var app = angular.module('dGogle',['angular-websocket']);

app.factory('$streamModule',function streamModuleFactory ($websocket) {

  console.log("dentro");
  var ws = $websocket('ws://localhost:9001/ws');
  var messages = [];

  var append = function(msg){messages.push(msg.data); console.log("got msg - " + msg.data)};
  ws.onMessage(append);

  var dataStreamClass = {
    messages : messages,
    send: function(msg){console.log("invio"); ws.send(msg); console.log("sending: " + msg);},
    close: function(){console.log("chiudo"); ws.close();},
    registerCallback: function(func){ws.onMessage(func)}
  };

  return dataStreamClass;

});

app.run(['$rootScope','$streamModule',function(scope,stream){
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
  var c = (typeof col === 'undefined')?0:col;
  var currRow = $(this).children("td");
  var s = window.getSelection();
  var r = document.createRange();

  console.log("sono qui"+currRow.text());

  if(c != 0){
    r.setStart(currRow, c);
    r.setEnd(currRow, c);
    s.removeAllRanges();
    s.addRange(r);
  } else {
     console.log(currRow.text().length);
     if(currRow.text().length == 0){
       console.log("enrico dentro");
       currRow.get(0).innerHTML = '\u00a0';
       currRow.get(0).focus();
       document.execCommand('delete', false, null);
     }else{
        console.log(currRow);
        currRow.get(0).focus();
     }
  }
}

$.fn.documentize = function(){

  var fn = function(e){
              var col = ($(window.getSelection().anchorNode).is($(this)))?0:window.getSelection().anchorOffset;
              var currRow = $(window.getSelection().anchorNode).parent().parent();
              var row = currRow.index();

              console.log("riga: " + row + "; col: " + col);

              if(e.type == "keyup" || e.type == "keydown")
              {
                 if(e.type == "keyup")
                   switch(e.keyCode){
                     case 13:
                     case 38:
                     case 40: e.preventDefault();
                     default: return;
                   }

                 if(e.type == "keydown")
                   switch(e.keyCode){
                      case 13:{ //enter
                        e.preventDefault();

                        if(currRow.text() == "" || currRow.text().length == col)
                            $("<tr><td contenteditable=\"true\"></td></tr>")
                               .insertAfter(currRow)
                               .on('keydown keyup mouseup',function (e){fn(e);})
                               .focusEditable();
                        else{
                            var text =  currRow.text();
                            currRow.children().text(text.substr(0,col));

                            $("<tr><td contenteditable=\"true\">"+text.substr(col)+"</td></tr>")
                               .insertAfter(currRow)
                               .on('keydown keyup mouseup',function (e){fn(e);})
                               .focusEditable();
                        }
                      }
                      case 38:{ //arrow up
                        currRow.prev().focusEditable();

                      }
                      case 40:{
                        currRow.next().focusEditable();
                      }

                      default: return;
                   }


              }
  };

  $(this).find("td").on('keydown keyup mouseup',function (e){fn(e);});
};

$(document).ready(function(){

      $("#page").documentize();

});




