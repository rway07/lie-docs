"use strict";
var ws= null;
var stream = null;

window.onload = function(){
 ws = new WebSocket('ws://localhost:9001/ws');
 stream = {
      send: function(msg){ ws.send(msg);},
      bye: function(){
                       console.log("i say ");
                       ws.send(JSON.stringify({"editorID":window.editorID,
                                  "action":"leave",
                                  "project":$("#project").attr("_projectName"),
                                  "file":$("#file").attr("_fileName")
                               }));
                       ws.close();
                     },

      registerCallback: function(func){ws.onmessage = func},
 };

  ws.onopen = function(){
    ws.send(JSON.stringify({"editorID":window.editorID,
               "action":"join",
               "editorColor":window.editorColor,
               "project":$("#project").attr("_projectName"),
               "file":$("#file").attr("_fileName")})
 )};

 window.onbeforeunload = function (e) {
         stream.bye();
  };

 window.row=0;
 window.col=0;
 window.editorID = String.prototype.makeid(128);
 window.restoreCaret =[];

 window.randomColor = function () {
   var letters = '0123456789ABCDEF';
   var color = '#';
   for (var i = 0; i < 6; i++ ) {
      color += letters[Math.floor(Math.random() * 16)];
   }
   return color;
 };

 window.editorColor = randomColor();

 window.removeVirtualCaret = function(rowID,actionAuthor,action){

     if(action != "join" && action != "ping" && action != "leave")
     {
       var currRow = $('tr:eq('+(parseInt(rowID))+')').get(0);
       var text = "";
       var nodes = $(currRow).children("td").contents();

       var obj = null;
       for(var i=0;i<nodes.length; i++){
         obj = nodes[i];
         //nodo tipo testo
         if(obj.nodeType == 3)
         {
           text += obj.nodeValue;
         }
         //salvo le posizioni di tutti i caret virtuali
         else
         {
            window.restoreCaret.push({"index": text.length,"obj":obj});
            $(obj).remove();
         }
       }

       //creo text node solo testo
       $(currRow).children("td").text(text);
     }
 }

 window.restoreCarets = function(data){
  var virtualCarets = window.restoreCaret;
  window.restoreCaret = [];

  if(data.fn != "join" && data.fn != "ping" && data.fn != "leave"){

      var currRow = $('tr:eq('+(parseInt(data.r))+')').get(0);
      var onLeftRealCaret = 0;

      for(var i=0; i<virtualCarets.length;i++)
      {
           var onLeftCurrentVirtual = 0;
           var offset = 0;


           if( data.fn == "execAddChar" && (virtualCarets[i].index >= data.c || $(virtualCarets[i].obj).attr("id") == data.author))
             offset = 1;
           else if ((data.fn == "execBackspaceChar" || data.fn == "execCancChar") && (virtualCarets[i].index >= data.c || $(virtualCarets[i].obj).attr("id") == data.author ))
             offset = -1;

           if($(virtualCarets[i].obj).attr("id") == data.author)
             virtualCarets[i].index = (parseInt(data.c)) + offset;
           else
             virtualCarets[i].index += offset ;

           onLeftRealCaret += ((virtualCarets[i].index) <= window.col)?1:0;

           for(var j = 0 ; j<=i; j++)
           {
             if(virtualCarets[j].index < virtualCarets[i])
               onLeftCurrentVirtual++;
           }

           if($(virtualCarets[i].obj).attr("id") == data.author)
             console.log("set col = " + parseInt(virtualCarets[i].index));

           window.addCaret(parseInt(data.r),parseInt(virtualCarets[i].index),virtualCarets[i].obj,$(virtualCarets[i].obj).attr("id"),onLeftCurrentVirtual);
      };

      var realCaretIndex = window.col;
      var offset = 0;
      if( data.fn == "execAddChar" && realCaretIndex >= data.c)
        offset = 1;
      else if ((data.fn == "execBackspaceChar" || data.fn == "execCancChar") && realCaretIndex >= data.c)
        offset = -1;

      window.col = parseInt(realCaretIndex) + parseInt(offset);

      console.log("window owner update focus");
      $($('tr:eq('+(parseInt(window.row))+')')).focusEditable(parseInt(window.col));

  }

 }
 window.min = function(a,b){return (a<=b)?a:b;}

 window.appendEmptyTextNode = function(row)
 {
   var currRow = $('tr:eq('+(parseInt(row))+')').get(0);
   var domTD = $(currRow).children("td").get(0);
   var text = document.createTextNode("");
   domTD.appendChild(text);

   return text;
 } 

 window.selectTextNodeOnCol=function(row,col){
   var currRow = $('tr:eq('+(parseInt(row))+')').get(0);
   var currTextNode = $(currRow).children("td").contents();
   var currentLength = 0;
   var residualLength = col;
   var currNode = null;
   if(currTextNode.length >1)
   {
      for(var i = 0; i< currTextNode.length; i++)
      {
        if(currTextNode[i].nodeType == "3")
        {
          currNode = currTextNode[i];
          currentLength += currTextNode[i].nodeValue.length;

          if(currentLength >= col)
            return {"textNode":currTextNode[i],"col":residualLength};
          else
            residualLength -= currTextNode[i].nodeValue.length;
        }
      }
      if(currNode != null)
        return {"textNode":currNode, "col":currNode.length};
      else
        return {"textNode":window.appendEmptyTextNode(row), "col":0};
        
   }
   else if(currTextNode.length == 1)
   {
     if(currTextNode[0].nodeType != 3)
     {
        return {"textNode":window.appendEmptyTextNode(row), "col":0};
     }
     else
       currTextNode = {"textNode":currTextNode[0],"col":col};
   }else{
        return {"textNode":window.appendEmptyTextNode(row), "col":0};
   }
     

   return currTextNode;

 }

 window.createSelection=function(row,col,selStart,selEnd){
     var range = document.createRange();
     var selectTextNode = selectTextNodeOnCol(row,col);
     var selection = window.getSelection();

     console.log("su questo textnode: " + selectTextNode.textNode + " seleziono da: " + selStart + " fino a : " + selEnd);

     range.selectNode(selectTextNode.textNode);
     range.setStart(selectTextNode.textNode,selStart);
     range.setEnd(selectTextNode.textNode,selEnd);

 }

 window.normalizeTextNode = function(row){
    var currRow = $('tr:eq('+(parseInt(row))+')').get(0);
    $(currRow).children("td")
               .contents()
               .filter(function(){return this.nodeType == 3 && this.nodeValue == "";})
               .each(function(idx,obj){$(obj).remove();});
 }
 window.addCaret = function(row,col,caret_,id,color,leftCaret){
      var caret = caret_;
      var currTextNode = null;
      var currRow = $('tr:eq('+(parseInt(row))+')').get(0);
      var range = document.createRange();

      if( caret == null)
      {

        caret = $.parseHTML("<div class=\"caret\" z-index=\"100\">|</div>");
        $(caret).attr("id",id);
        $(caret).css("color",color);
        caret = $(caret).get(0);
      }


      var nodeSelected = selectTextNodeOnCol(row,col);

      col = nodeSelected.col;
      currTextNode = nodeSelected.textNode;
      console.log(currTextNode);

      range.selectNode(currTextNode);
      range.setStart(currTextNode,min(col,currTextNode.length));
      range.setEnd(currTextNode,min(col,currTextNode.length));
      console.log(caret);
      range.insertNode(caret);

      window.normalizeTextNode(row);

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
        var row = $('tr:eq('+(parseInt(window.row)+1)+')').get(0);
        var idx = parseInt(window.col) - parseInt(param.c);
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

 window.viewFn["execBackspaceChar"] = function(param){
      var currRow = $($('tr:eq('+(parseInt(param.r))+')').get(0));
      $(currRow).focusEditable(parseInt(param.c));
      document.execCommand('delete', true, null);
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
     var currText = currRow.children("td").text();
     var char = param.chr;
     var newText = currText.substring(0,parseInt(param.c))+char+ currText.substring(parseInt(param.c));
     currRow.children("td").text(newText);
 }

 window.viewFn['join'] = function(param){

     if(param.editorID != window.editorID)
     {
        var pingData = {"action":"ping","editorID": window.editorID, "editorColor": window.editorColor};

        if(typeof $("#" + param.editorID).get(0) == "undefined")
          window.addCaret(0,0,null,param.editorID,param.editorColor,null);

        stream.send(JSON.stringify(pingData));
     }

 }

 window.viewFn['leave'] = function(param){

      $("#"+param.editorID).remove();
 }

 window.viewFn['ping'] = function(param){
     if(param.editorID != window.editorID && typeof $("#" + param.editorID).get(0) == "undefined")
     {
       window.addCaret(0,0,null,param.editorID,param.editorColor,null);
     }
 }

};

$(document).ready(function(){

  $("#editorColor").css("background-color",window.editorColor);
  $("#page").documentize(stream.send);
  stream.registerCallback(exec);

});

var exec = function(resp){
    console.log("Server Say: " + resp.data);
    var data = JSON.parse(resp.data);

    window.removeVirtualCaret(data.r,data.author,data.fn);
    window.normalizeTextNode(data.r);
    
    window.viewFn[data.fn](data);
    window.restoreCarets(data);

}


String.prototype.makeid = function(len)
{
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < len; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;
}


$.fn.focusEditable = function(col)
{
  var c = (typeof col === 'undefined')?0:col;
  var currRow = $(this).children("td");

  var selectedNode = selectTextNodeOnCol($(this).index(),col);
  var c  = selectedNode.col;
  var currTextNode = selectedNode.textNode;
 
  var s = window.getSelection();
  var r = document.createRange();

  if(typeof currTextNode != "undefined" && currTextNode.length != 0){

    var actualLength =  currTextNode.length;
    r.setStart(currTextNode, min(c,actualLength));
    r.setEnd(currTextNode, min(c,actualLength));
    s.removeAllRanges();
    s.addRange(r);

  } else {

     currRow.get(0).focus();

  }
}

$.fn.fn = function(e,notifyChange){
  
  var anchorNode = window.getSelection().anchorNode;
  var parentNode = anchorNode.parentNode;
  var parentNodeContents = $(parentNode).contents();
  //var leftCaret = countLeftCaret
  var anchorOffset = window.getSelection().anchorOffset;

  var col = 0;
  if(parentNodeContents.length > 1)
  {
    for(var i=0;i<parentNodeContents.length && parentNodeContents[i] !== anchorNode;i++)
    { 
        if(parentNodeContents[i].nodeType == 3)
          col += parentNodeContents[i].nodeValue.length;
    }
  }
  
  col += window.getSelection().anchorOffset;
  var currRow = $(window.getSelection().anchorNode).parent().parent();
  var row = currRow.index();

  console.log("r:"+row+"c:"+col);
  var text = currRow.contents();


  window.row = parseInt(row);
  window.col = parseInt(col);

  if(e.type == "keyup" || e.type == "keydown")
  {
     if(e.type == "keyup")
       switch(e.keyCode){

         case 8:  {console.log("sono in keyup handleer" + e.which); console.log("cancellabile: " + e.cancelable); e.stopImmediatePropagation(); e.preventDefault(); return false;} //backspace
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
            console.log("sono in keydown handleer" + e.which);

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
              if(!currRow.is(":first-child"))
              {
                var paramBackspace = {
                    fn: "execBackspace",
                    r: row,
                    c: col,
                    author:window.editorID
                };

                notifyChange($.fn.indices(currRow,'removeRow',paramBackspace,true,false));
              }

            }
            e.stopImmediatePropagation(); e.preventDefault();
            return false;
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
  console.log("sono in keypress handleer" + e.which);

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
    var currRow = $('tr:eq('+parseInt(row)+')');

    notifyChange($.fn.indices(currRow,'addChar',paramAddChar,true));

    return;
  }

}

$.fn.indices = function(currTr,action,func,prev,next) {

   var addPrev = (typeof prev == 'undefined' || prev == true)?true:false;
   var addNext = (typeof next == 'undefined' || next == true)?true:false;

   var domCurr = currTr.get(0);
   var domNext = (currTr.is(":last-child"))?null:currTr.next().get(0);

   func['prevIndex'] = (typeof domCurr != 'undefined' && addPrev)?$(domCurr).attr('_index'):"null";
   func['prevSubIndex']= (typeof domCurr != 'undefined' && addPrev)?$(domCurr).attr('_subindex'):"null";
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
