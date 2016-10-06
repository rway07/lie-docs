var app = angular.module('dGogle',['angular-websocket']);

app.factory('$streamModule',function streamModuleFactory ($websocket) {

  var ws = $websocket('ws://localhost:9001/ws');
  var messages = [];

  var append = function(msg){messages.push(msg.data); console.log("got msg - " + msg.data)};
  ws.onMessage(append);

  var dataStreamClass = {
    messages : messages,
    send: function(msg){console.log("invio"); ws.send(msg); console.log("sending: " + msg);},
    close: function(){console.log("chiudo"); ws.close();},
    registerCallback: function(func){ws.onMessage(func)},
  };

  return dataStreamClass;

});

app.run(['$rootScope','$streamModule',function(scope,stream){

  window.callbackRecall = []; //id - jquery istance // creazione
  $("#page").documentize(stream.send);
        //(action,prevIndex,prevSubIndex,nextIndex,nextSubIndex)
  scope.data = ['a','b'];
  scope.input = "";

  var appendResponse = function(msg){
    scope.data.push(msg.data);
  }
  stream.registerCallback(appendResponse);
  scope.send = function(){stream.send(scope.input);}

}]);

app.controller('filenameCtrl',function($scope){
  $scope.filename="Unnamed Document";
});


$.fn.focusEditable = function(col)
{
  var min = function(a,b){return (a<=b)?a:b;}

  var c = (typeof col === 'undefined')?0:col;
  var currRow = $(this).children("td");
  var currTextNode = currRow.contents();

  var s = window.getSelection();
  var r = document.createRange();

  if(c != 0){
    console.log(currRow.text());
    var actualLength =  currRow.text().length;
    r.setStart(currTextNode.get(0), min(c,actualLength));
    r.setEnd(currTextNode.get(0), min(c,actualLength));
    s.removeAllRanges();
    s.addRange(r);
  } else {

     if(currRow.text().length == 0){
       currRow.get(0).innerHTML = '\u00a0';
       currRow.get(0).focus();
       document.execCommand('delete', false, null);
     }else{
        currRow.get(0).focus();
     }
  }
}

$.fn.documentize = function(callBackChange){

  var indices = function(currTr,action,instance,prev,next){

    addPrev = (typeof prev == 'undefined' || prev == true)?true:false;
    addNext = (typeof next == 'undefined' || next == true)?true:false;

    domCurr = currTr.get(0);
    domNext = (currTr.is(":last-child"))?null:currTr.next().get(0);
    console.log(domNext);

    index =  window.callbackRecall.length;
    window.callbackRecall[index] = instance;

    return {
      'prevIndex': (typeof domCurr != 'undefined' && addPrev)?currTr.attr('_index'):"null",
      'prevSubIndex': (typeof domCurr != 'undefined' && addPrev)?currTr.attr('_subindex'):"null",
      'nextIndex': (domNext != null && addNext)?$(domNext).attr('_index'):"null",
      'nextSubIndex':(domNext != null && addNext)?$(domNext).attr('_subindex'):"null",
      'idRecall' : index,
      'action' : action
    };
  }

  var fn = function(e){
              var col = ($(window.getSelection().anchorNode).is($(this)))?0:window.getSelection().anchorOffset;
              var currRow = $(window.getSelection().anchorNode).parent().parent();
              var row = currRow.index();

              //console.log("riga: " + row + "; col: " + col);

              if(e.type == "keyup" || e.type == "keydown")
              {
                 if(e.type == "keyup")
                   switch(e.keyCode){

                     case 8:  {e.preventDefault(); break;} //backspace
                     case 13: {e.preventDefault(); break;} // enter
                     case 46: {e.preventDefault(); break;} // canc
                     case 38: { if($(currRow).is(":first-child")) return; currRow.prev().focusEditable(col); break;} //arrow up
                     case 40: { if($(currRow).is(":last-child")) return; currRow.next().focusEditable(col); break;} //arrow down
                     default: return;

                   }

                 if(e.type == "keydown")
                   switch(e.keyCode){
                      case 13:
                      { //enter
                        e.preventDefault();



                        if(currRow.text() == "" || currRow.text().length == col)
                        {

                            var newElem = $("<tr><td contenteditable=\"true\"></td></tr>");
                            callBackChange(indices(currRow,'add',newElem));

                            newElem.insertAfter(currRow)
                                   .on('keydown keyup mouseup',function (e){fn(e);})
                                   .focusEditable();
                        }
                        else{
                            var text =  currRow.text();
                            currRow.children().text(text.substr(0,col));

                            var newElem = $("<tr><td contenteditable=\"true\">"+text.substr(col)+"</td></tr>");
                            callBackChange(indices(currRow,'add',newElem));

                            newElem.insertAfter(currRow)
                               .on('keydown keyup mouseup',function (e){fn(e);})
                               .focusEditable();
                        }
                        return;
                      }
                      case 8:
                      {
                        if(col > 0 ) return;
                        var currTr = $(window.getSelection().anchorNode).parent().parent();
                        console.log(currTr);
                        if(currTr.is(":first-child")) return;

                        var prevRow = currTr.prev();
                        var cursorPos = prevRow.children("td").text().length;
                        var text = currTr.children("td").text();

                        callBackChange(indices(prevRow,'remove',null,false,true));

                        prevRow.children("td").text(prevRow.children("td").text() + text);
                        console.log(prevRow.children("td").text());
                        currTr.remove();
                        prevRow.focusEditable(cursorPos);
                        e.preventDefault();

                        return;
                      }
                      case 46:
                      {
                        var currTr = $(window.getSelection().anchorNode).parent().parent();
                        if(currTr.is(":last-child")) return;


                        var nextRow = currTr.next();
                        var cursorPos = currRow.children("td").text().length;
                        var text = nextRow.children("td").text();
                        var currText = currRow.children("td").text();
                        if(currText.length > col) return;

                        currTr.children("td").text(currTr.children("td").text() + text);

                        callBackChange(indices(currRow,'remove',null,false,true));

                        nextRow.remove();
                        currRow.focusEditable(cursorPos);

                        e.preventDefault();
                        return;
                      }
                      case 38:
                      case 40: {e.preventDefault(); return; }
                      default: return;
                   }


              }
  };

  $(this).find("td").on('keydown keyup mouseup',function (e){fn(e);});
};






