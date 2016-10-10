var app = angular.module('dGogle',['angular-websocket']);

app.factory('$streamModule',function streamModuleFactory ($websocket) {

  var ws = $websocket('ws://localhost:9001/ws');
  var messages = [];

  ws.onOpen(function(){
    ws.send({"editorID":window.editorID,
             "action":"join",
             "project":$("#project").attr("_projectName"),
             "file":$("#file").attr("_fileName")});
  });

  var dataStreamClass = {
    messages : messages,
    send: function(msg){ ws.send(msg);},
    bye: function(){
                     ws.send({  "editorID":window.editorID,
                                "action":"leave",
                                "project":$("#project").attr("_projectName"),
                                "file":$("#file").attr("_fileName")
                             });
                     ws.close();
                   },

    registerCallback: function(func){ws.onMessage(func)},
  };

  return dataStreamClass;

});

app.run(['$rootScope','$streamModule',function(scope,stream){

   window.onbeforeunload = function (e) {
     stream.bye();
   };

   window.row=0;
   window.col=0;
   window.editorID = String.prototype.makeid(128);

   window.addCaret = function(row,col,caret,id){
      if( caret == null)
      {
        randomColor = function () {
            var letters = '0123456789ABCDEF';
            var color = '#';
            for (var i = 0; i < 6; i++ ) {
                color += letters[Math.floor(Math.random() * 16)];
            }
            return color;
        };


        caret = $.parseHTML("<div class=\"caret\" z-index=\"100\">|</div>");
        $(caret).attr("id",id);
        //$(caret).css("color",randomColor());
        caret = $(caret).get(0);
      }

      
      var currRow = $('tr:eq('+(parseInt(row))+')').get(0);
      var currTextNode = $(currRow).children("td").contents();
      var range = document.createRange();

      tdString = "";
      textNodes = [];

      if(currTextNode.length >1)
      { 
        for(var i = 0; i< currTextNode.length; i++)
        {
          obj = currTextNode[i];
          if(obj.nodeType == 3) 
          {
            tdString += obj.nodeValue;
            textNodes.push(obj);
          }
          else tdString += "|"; 
        }  
      
      tdString = tdString.substr(0,col+1);
      
      leftCaret = (tdString.match(/\|/g) || []).length;
      currTextNode = textNodes[leftCaret];
      newC = col - (tdString.lastIndexOf("|")>0?tdString.lastIndexOf("|"):0);
      col = newC;
    }
    else
      currTextNode = currTextNode[0];

      range.selectNode(currTextNode);
      range.setStart(currTextNode,col);
      range.setEnd(currTextNode,col);
      console.log(caret);
      range.insertNode(caret);

      //myRow = $('tr:eq('+(parseInt(window.row))+')').get(0);
      //$(myRow).focusEditable(parseInt(window.col));

   }

   window.viewFn = [];
   window.viewFn['execEnter1'] = function(param){

        var currElem = $('tr:eq('+parseInt(param.r)+')');
        var elem = $(param.elem);

        elem.insertAfter(currElem)
            .on('keydown keyup mouseup keypress',function (e){$.fn.fn(e,stream.send);})
            .on('keypress',function (e){$.fn.streamChar(e,stream.send);})
            .attr("_subindex",param._subindex)
            .attr("_index",param._index);

        if(param.author == window.editorID)
          elem.focusEditable();
        else if(parseInt(window.row) > parseInt(param.r)) {
            $($('tr:eq('+(parseInt(window.row)+1)+')').get(0)).focusEditable(parseInt(window.col));
        }
   };

   window.viewFn['execEnter2'] = function(param){

      var currRow = $('tr:eq('+parseInt(param.r)+')');
      var elem = $(param.elem);
      var text = currRow.text();

      var text =  currRow.text();
      currRow.children().text(text.substr(0,parseInt(param.c)));

      elem.children().text(text.substr(parseInt(param.c)));
      elem.insertAfter(currRow)
          .on('keydown keyup mouseup',function (e){$.fn.fn(e,stream.send);})
          .attr("_subindex",param._subindex)
          .attr("_index",param._index);

      if(param.author == window.editorID)
        elem.focusEditable();
      else if(parseInt(window.col) >= parseInt(param.c) && parseInt(window.row) == parseInt(param.r)){
        row = $('tr:eq('+(parseInt(window.row)+1)+')').get(0);
        idx = parseInt(window.col) - parseInt(param.c);
        $(row).focusEditable(idx);
      } else if(parseInt(window.row) > parseInt(param.r) ){
        $($('tr:eq('+(parseInt(window.row)+1)+')').get(0)).focusEditable((parseInt(window.col)));
      }
   };

   window.viewFn["execBackspace"] = function(param){

      var currRow = $($('tr:eq('+(parseInt(param.r))+')').get(0));
      var prevRow = $($('tr:eq('+(parseInt(param.r)-1)+')').get(0));
      var cursorPos = prevRow.children("td").text().length;
      var text = currRow.children("td").text();

      prevRow.children("td").text(prevRow.children("td").text() + text);
      currRow.remove();

      if(param.author == window.editorID)
        prevRow.focusEditable(cursorPos);
      else if(parseInt(window.row) == parseInt(param.r) && parseInt(window.col) >= parseInt(param.c)){
        var row =$('tr:eq('+(parseInt(window.row)-1)+')').get(0); 
        $(row).focusEditable(parseInt(parseInt(window.col) + parseInt(cursorPos)));
      }else if(parseInt(window.row) > parseInt(param.r)){
       $($('tr:eq('+(parseInt(window.row)-1)+')').get(0)).focusEditable((parseInt(window.col)));
      }
   }

   window.viewFn["execBackspaceChar"] = function(param)
   {
      var currRow = $($('tr:eq('+(parseInt(param.r))+')').get(0));
      $(currRow).focusEditable(parseInt(param.c));
      document.execCommand('delete', false, null);
      if(param.author != window.editorID)
      {
        $($('tr:eq('+(parseInt(window.row))+')').get(0)).focusEditable(parseInt(window.col));
      }

   }

   window.viewFn['execCanc'] = function(param){

      var nextRow = $($('tr:eq('+(parseInt(param.r)+1)+')').get(0));
      var currRow = $($('tr:eq('+(parseInt(param.r))+')').get(0));
      var cursorPos = currRow.children("td").text().length;
      var text = nextRow.children("td").text();

      currRow.children("td").text(currRow.children("td").text() + text);
      nextRow.remove();

      if(window.editorID == param.author){
        currRow.focusEditable(cursorPos);
      } else if(parseInt(window.row) == parseInt(param.r)){
        $($('tr:eq('+parseInt(window.row)+')').get(0)).focusEditable((parseInt(window.col)));
      } else if(parseInt(window.row) > parseInt(param.r)) {
        $($('tr:eq('+(parseInt(window.row)-1)+')').get(0)).focusEditable((parseInt(window.col)));
      }
   }
   window.viewFn['execCancChar']=function(param){
      var currRow = $($('tr:eq('+(parseInt(param.r))+')').get(0));
      $(currRow).focusEditable(parseInt(param.c+1));
      document.execCommand('delete', false, null);
      if(param.author != window.editorID)
      {
        $($('tr:eq('+(parseInt(window.row))+')').get(0)).focusEditable(parseInt(window.col));
      }
   }

   window.viewFn['execAddChar']=function(param){
     var currRow = $('tr:eq('+parseInt(param.rd)+')');
     currText = currRow.children("td").text();
     char = param.chr;
     newText = currText.substr(0,parseInt(param.c))+char+ currText.substr(parseInt(param.c));
     currRow.children("td").text(newText);
     
     if(param.author == window.editorID)
     {
       //$($('tr:eq('+(parseInt(window.row))+')').get(0)).focusEditable(parseInt(param.cd));
       //leftCaret = currText.substr(0,param.cd).lastIndexOf("|").length
       window.col = parseInt(param.cd);
     }
   }

   window.viewFn['join'] = function(param)
   {

     if(param.editorID != window.editorID)
     {
        var pingData = {"action":"ping","editorID": window.editorID};

        if(typeof $("#" + param.editorID).get(0) == "undefined")
          window.addCaret(0,0,null,param.editorID);

        stream.send(JSON.stringify(pingData));
     }

   }
   window.viewFn['leave'] = function(param){

      $("#"+param.editorID).remove();
   }
   window.viewFn['ping'] = function(param){
     if(param.editorID != window.editorID && typeof $("#" + param.editorID).get(0) == "undefined")
     {
       window.addCaret(0,0,null,param.editorID);
     }
   }

   $("#page").documentize(stream.send);

   stream.registerCallback(exec);

}]);

var exec = function(resp){
    console.log("Server Say: " + resp.data);
    data = JSON.parse(resp.data);
    
    offset = 0;
    switch(data.fn)
    {
     case "execAddChar": {offset = 1; break;}
     case "execCancChar":{offset = 0; break;}
     case "execBackspaceChar":{offset = -1; break;};
    } 
    
    restoreCaret = [];
    leftCaret = 0;
    if(data.fn != "join" && data.fn != "ping" && data.fn != "leave")
    {

          var currRow = $('tr:eq('+(parseInt(data.r))+')').get(0);
          //replacing current row text with caret placeholder
          text = "";
          nodes = $(currRow).children("td").contents();
          leftCaret = countLeftCaret($(currRow).children("td"),data.c);
 
          for(var i=0;i<nodes.length; i++){
            obj = nodes[i];
            if(obj.nodeType == 3)
            {
              text += obj.nodeValue;
            }
            else if($(obj).attr("id") != data.author)
            {
               restoreCaret.push({"strIndex": text.length,"caret":obj});
               $(obj).remove();
            }
          }

          if(data.author != window.editorID) 
            $("#"+data.author).remove();

          $(currRow).children("td").text(text);

    }

    window.viewFn[data.fn](data);

    if(data.fn != "join" && data.fn != "ping" && data.fn != "leave")
    {
        
        for(var i=0; i<restoreCaret.length;i++)
        {
             console.log("aggiungo restore");
             if(offset == 1 && restoreCaret[i].strIndex > data.c)
               restOffset =1;
             else if(((offset == 0 || offset == -1) && restoreCaret[i].strIndex > data.c))  
               restOffset = -1;
             else
               restOffset = 0;
             
             window.addCaret(parseInt(data.r),parseInt(restoreCaret[i].strIndex)+ restOffset,restoreCaret[i].caret);
        };

        console.log("wincol:"+window.col);
        if(data.author != window.editorID)
        {
          console.log("author caret add");
          window.addCaret(parseInt(data.rd),parseInt(data.cd)+offset,null,data.author);  
          window.col += (offset);
          console.log("wincol_dopo:" +window.col);
        }
        else
          window.col = parseInt(data.cd)+offset; 
        
        console.log("window owner update focus");
        $($('tr:eq('+(parseInt(window.row))+')')).focusEditable(parseInt(window.col));
        
        }
}


String.prototype.makeid = function(len)
{
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < len; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;
}

String.prototype.hashCode = function() {
  var hash = 0, i, chr, len;
  if (this.length === 0) return hash;
  for (i = 0, len = this.length; i < len; i++) {
    chr   = this.charCodeAt(i);
    hash  = ((hash << 5) - hash) + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return hash;
};

var countLeftCaret = function(node,pivot){
  tdString = caretToString(node,"|");
  tdString = tdString.substr(0,pivot);
  return tdString.match("|/g").length -1;

}

var caretToString = function(node,char)
{
  char = (typeof char == 'undefined')?"|":char;

  var currTextNode = $(node).contents();
  var tdString = "";

  if(currTextNode.length >1)
  {
    for(var i = 0; i< currTextNode.length; i++)
    {
      obj = currTextNode[i];
      if(obj.nodeType == 3)
      {
        tdString += obj.nodeValue;
      }
      else tdString += char;
    };
  }
  return tdString;
}


$.fn.focusEditable = function(col)
{
  var min = function(a,b){return (a<=b)?a:b;}

  var c = (typeof col === 'undefined')?0:col;
  var currRow = $(this).children("td");
  var currTextNode = currRow.contents();
 
  var s = window.getSelection();
  var r = document.createRange();

  if(c != 0){

    tdString = "";
    textNodes = [];

    if(currTextNode.length >1)
    { 
      for(var i = 0; i< currTextNode.length; i++)
      {
        obj = currTextNode[i];
        if(obj.nodeType == 3) 
        {
          tdString += obj.nodeValue;
          textNodes.push(obj);
        }
        else tdString += "|"; 
      };  
      
      tdString = tdString.substr(0,c+1);
      
      leftCaret = (tdString.match(/\|/g) || []).length;
      currTextNode = textNodes[leftCaret];
      //TODO: unica condizione errata! verificare
      newC = c - (tdString.lastIndexOf("|")>0?tdString.lastIndexOf("|"):0);
      c = newC;
    }
    else
      currTextNode = currTextNode[0];      
    

    var actualLength =  currTextNode.length;
    r.setStart(currTextNode, min(c,actualLength));
    r.setEnd(currTextNode, min(c,actualLength));
    s.removeAllRanges();
    s.addRange(r);
  } else {

     if(currRow.text().length == 0){
       //currRow.get(0).innerHTML = '\u00a0';
       currRow.get(0).focus();
       document.execCommand('delete', false, null);
     }else{
        currRow.get(0).focus();
     }
  }
}

$.fn.fn = function(e,notifyChange){
  
  var anchorNode = window.getSelection().anchorNode;
  var parentNode = anchorNode.parentNode;
  var parentNodeContents = $(parentNode).contents();
  var leftCaret = countLeftCaret
  var anchorOffset = window.getSelection().anchorOffset;

  var col = 0;
  if(parentNodeContents.length > 1)
  {
    for(i=0;i<parentNodeContents.length && parentNodeContents[i] !== anchorNode;i++)
    { 
        if(parentNodeContents[i].nodeType == 3)
          col += parentNodeContents[i].nodeValue.length;
    }
  }
  
  col += window.getSelection().anchorOffset;
  var currRow = $(window.getSelection().anchorNode).parent().parent();
  var row = currRow.index();

  console.log("r:"+row+"c:"+col);
  text = currRow.contents();


  window.row = parseInt(row);
  window.col = parseInt(col);

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
                var paramEnter1 = {
                   fn: "execEnter1",
                   r: row,
                   c: col,
                   elem: "<tr><td contenteditable=\"true\"></td></tr>",
                   author:window.editorID,
                };
                notifyChange($.fn.indices(currRow,'addRow',paramEnter1));
            }
            else{

                var paramEnter2 = {
                   fn: "execEnter2",
                   r:row,
                   c: col,
                   author:window.editorID,
                   elem:"<tr><td contenteditable=\"true\"></td></tr>"
                };

                notifyChange($.fn.indices(currRow,'addRow',paramEnter2));

            }
            return;
          }
          case 8:
          {
            e.preventDefault();

            //var currRow = $(window.getSelection().anchorNode).parent().parent();
            if(col > 0)
            {

               var paramBackspaceChar = {

                 fn:"execBackspaceChar",
                 r:row,
                 c:col,
                 author:window.editorID

               };
               notifyChange($.fn.indices(currRow,'removeChar',paramBackspaceChar,true,false));

            }
            else
            {
              if(currRow.is(":first-child")) return;
              var paramBackspace = {
                fn: "execBackspace",
                r: row,
                c: col,
                author:window.editorID
              };

              notifyChange($.fn.indices(currRow,'removeRow',paramBackspace,true,false));

              return;
            }



          }
          case 46:
          {
            e.preventDefault();

            var currTr = $(window.getSelection().anchorNode).parent().parent();
            var currText = currRow.children("td").text();

            if(currText.length > col){

               var paramCancChar = {

                    fn:"execCancChar",
                    r:row,
                    c:col,
                    author:window.editorID

               };
               notifyChange($.fn.indices(currRow,'removeChar',paramCancChar,true,false));

            }else{
                if(currTr.is(":last-child")) return;

                var paramCanc = {

                  fn: "execCanc",
                  r: row,
                  c: col,
                  author:window.editorID,
                };

                notifyChange($.fn.indices(currRow,'removeRow',paramCanc,false,true));
            }

            return;
          }
          case 38:
          case 40: {e.preventDefault(); return; }
          default:
            return
       }
  }

};

$.fn.streamChar = function(e,notifyChange)
{
  if(e.type=="keypress" && e.which !== 0 && !e.ctrlKey && !e.metaKey && !e.altKey)
  {
    e.preventDefault();

    var char = String.fromCharCode(e.keyCode);

    var paramAddChar = {
      fn: "execAddChar",
      r: row,
      c: col,
      author:window.editorID,
      chr: char
    };
    var currRow = $(window.getSelection().anchorNode).parent().parent();
    notifyChange($.fn.indices(currRow,'addChar',paramAddChar,true));

    return;
  }

}

$.fn.indices = function(currTr,action,func,prev,next) {

   addPrev = (typeof prev == 'undefined' || prev == true)?true:false;
   addNext = (typeof next == 'undefined' || next == true)?true:false;

   domCurr = currTr.get(0);
   domNext = (currTr.is(":last-child"))?null:currTr.next().get(0);

   func['prevIndex'] = (typeof domCurr != 'undefined' && addPrev)?currTr.attr('_index'):"null";
   func['prevSubIndex']= (typeof domCurr != 'undefined' && addPrev)?currTr.attr('_subindex'):"null";
   func['nextIndex']= (domNext != null && addNext)?$(domNext).attr('_index'):"null";
   func['nextSubIndex']=(domNext != null && addNext)?$(domNext).attr('_subindex'):"null";
   func['action'] = action;

   return JSON.stringify(func);

 }

$.fn.documentize = function(callBackChange){

  var doc = $(this); 
  $(this).find("td").on('keydown keyup mouseup',function (e){$.fn.fn(e,callBackChange);});
  $(this).find("td").on('keypress',function (e){$.fn.streamChar(e,callBackChange);});

  $(document).click(function(e){

      var distance = function(x1,x2,y1,y2){
          return Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2))
      }

      var selectedNode = null;
      var selectedDistance = null;

      if(e.target.nodeName != "TD")
      {
        doc.find("tr").each(function(idx,obj){

                 var dist = distance($(obj).position().left,e.clientX,$(obj).position().top,e.clientY);
                 if(selectedNode == null || dist <= selectedDistance)
                 {
                   selectedDistance = dist;
                   selectedNode = obj;
                 }
        });

        $(selectedNode).focusEditable($(selectedNode).children("td").text().length);

      }
    });
}
