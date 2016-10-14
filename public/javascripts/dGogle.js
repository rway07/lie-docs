"use strict";
var ws= null;
var stream = null;

window.onload = function(){
 ws = new WebSocket('ws://localhost:9001/ws');
 stream = {
      send: function(msg){ ws.send(msg);},
      bye: function(){
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
 window.caret = "<div class=\"caret\" z-index=\"100\"></div>";
 window.elementRow = "<tr><td contenteditable=\"true\"></td></tr>";
 window.viewFn = [];

 window.randomColor = function () {
   var letters = '0123456789ABCDEF';
   var color = '#';
   for (var i = 0; i < 6; i++ ) {
      color += letters[Math.floor(Math.random() * 16)];
   }
   return color;
 };

 window.editorColor = randomColor();
 window.getText = function(row)
 {
   var currRow = $('tr:eq('+(parseInt(row))+')').get(0);
   var text = "";
   var nodes = $(currRow).children("td").contents();

   for(var i=0;i<nodes.length; i++){
        //nodo tipo testo
        if(nodes[i].nodeType == 3)
        {
          text += nodes[i].nodeValue;
        }
   }

   return text;

 }
 window.removeVirtualCaret = function(actionAuthor,action){

     if(action != "join" && action != "ping" && action != "leave")
     {
       $(window.documentized).find("tr").each(function(rowID,currRow){
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
             window.restoreCaret.push({"index": text.length,"obj":obj, "row":rowID});
             $(obj).remove();
          }
         }

         //creo text node solo testo
         $(currRow).children("td").text(text);
       });
     }
 }

 window.restoreCarets = function(data){
  var virtualCarets = window.restoreCaret;
  window.restoreCaret = [];

  if(data.fn != "join" && data.fn != "ping" && data.fn != "leave"){

      for(var i=0; i<virtualCarets.length;i++)
           window.addCaret(virtualCarets[i].row,parseInt(virtualCarets[i].index),virtualCarets[i].obj,$(virtualCarets[i].obj).attr("id"));


      $($('tr:eq('+(parseInt(window.row))+')')).focusEditable(parseInt(window.col));

  }

 }
 window.min = function(a,b){return (a<=b)?a:b;}

 window.appendEmptyTextNode = function(row)
 {
   var currRow = $('tr:eq('+(parseInt(row))+')').get(0);
   var domTD = $(currRow).children("td").get(0);
   if(typeof domTD == "undefined")
     console.log("ti ho beccato");
     
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

 window.normalizeTextNode = function(row){
    var currRow = $('tr:eq('+(parseInt(row))+')').get(0);
    $(currRow).children("td")
               .contents()
               .filter(function(){return this.nodeType == 3 && this.nodeValue == "";})
               .each(function(idx,obj){$(obj).remove();});
 }
 window.addCaret = function(row,col,caret,id,color){
      var currTextNode = null;
      var currRow = $('tr:eq('+(parseInt(row))+')').get(0);
      var range = document.createRange();

      if( caret == null)
      {

        caret = $.parseHTML(window.caret);
        $(caret).attr("id",id);
        $(caret).css("border-left-color",color);
        caret = $(caret).get(0);
      }

      var nodeSelected = selectTextNodeOnCol(row,col);

      col = nodeSelected.col;
      currTextNode = nodeSelected.textNode;

      range.selectNode(currTextNode);
      range.setStart(currTextNode,min(col,currTextNode.length));
      range.setEnd(currTextNode,min(col,currTextNode.length));
      range.insertNode(caret);

      window.normalizeTextNode(row);

 }
 window.subOrZero = function(a,b)
 {
   return (a-b >= 0)?(a-b):0;
 }
 window.findVirtualCaret = function(id){
   for(var i=0; i<window.restoreCaret.length; i++)
     if($(window.restoreCaret[i].obj).attr("id") == id) return i;
 }

 window.findRightVirtualCaret = function(row,col){
    var retCarets = [];
    for(var i=0; i<window.restoreCaret.length; i++)
      if(window.restoreCaret[i].row == row && window.restoreCaret[i].index > col) retCarets.push(i);

    return retCarets;
  }

 window.viewFn['execEnter1'] = function(param){

    var currElem = $('tr:eq('+parseInt(param.r)+')');
    var elem = $.parseHTML(window.elementRow);
   
    $(elem).insertAfter(currElem)
        .on('keydown keyup mouseup keypress',function (e){$.fn.fn(e,stream.send);})
        .on('keypress',function (e){$.fn.streamChar(e,stream.send);})
        .attr("_subindex",param._subindex)
        .attr("_index",param._index);


    //update real caret
    if((window.row == param.r && window.col >= param.c) || window.row > param.r){
      window.col = window.subOrZero(window.col,parseInt(param.c));
      window.row++;
    }
    //update virtual carets
    for(var i=0; i<restoreCaret.length;i++){
      if((restoreCaret[i].row == param.r && restoreCaret[i].index >= param.c) || restoreCaret[i].row > param.r){
        restoreCaret[i].index = window.subOrZero(restoreCaret[i].index,parseInt(param.c));
        restoreCaret[i].row++;
      }
    }
 };

 window.viewFn['execEnter2'] = function(param){

      var currRow = $('tr:eq('+parseInt(param.r)+')');
      var elem = $.parseHTML(window.elementRow);
      var text = getText(param.r);

      currRow.children("td").text(text.substr(0,parseInt(param.c)));

      $(elem).children("td").text(text.substr(parseInt(param.c)));
      $(elem).insertAfter(currRow)
             .on('keydown keyup mouseup',function (e){$.fn.fn(e,stream.send);})
             .attr("_subindex",param._subindex)
             .attr("_index",param._index);

      //update real caret
      if((window.row == param.r && window.col >= param.c) || window.row > param.r){
        window.col =(parseInt(window.row) == parseInt(param.r))?window.subOrZero(window.col,parseInt(window.col)):window.col;
        window.row++;
      }
      //update virtual carets
      for(var i=0; i<restoreCaret.length;i++){
        if((restoreCaret[i].row == param.r && restoreCaret[i].index >= param.c) || restoreCaret[i].row > param.r){
          restoreCaret[i].index = (parseInt(restoreCaret[i].row) == parseInt(param.r))?window.subOrZero(restoreCaret[i].index,parseInt(param.c)):restoreCaret[i].index;
          restoreCaret[i].row++;
        }
      }
 };

 window.viewFn["execBackspace"] = function(param){

      var text = window.getText(param.r);
      var text_before = window.getText(parseInt(param.r)-1);

      $('tr:eq('+(parseInt(param.r))+')').remove();
      $('tr:eq('+(parseInt(param.r)-1)+')').children("td").text(text_before+text);

      //update real caret
      if((window.row == param.r && window.col >= param.c) || window.row > param.r){
          window.col =(parseInt(window.row) == parseInt(param.r))?(text_before.length + parseInt(param.c)):window.col;
          window.row=window.subOrZero(window.row,1);
      }
      //update virtual carets
      for(var i=0; i<restoreCaret.length;i++){
          if((restoreCaret[i].row == param.r && restoreCaret[i].index >= param.c) || restoreCaret[i].row > param.r){
            restoreCaret[i].index = (parseInt(restoreCaret[i].row) == parseInt(param.r))?(text_before.length + parseInt(param.c)):restoreCaret[i].index;
            restoreCaret[i].row=window.subOrZero(restoreCaret[i].row,1);
          }
      }

 }

 window.viewFn["execBackspaceChar"] = function(param){
      var currRow = $($('tr:eq('+(parseInt(param.r))+')').get(0));
      $(currRow).focusEditable(parseInt(param.c));
      document.execCommand('delete', true, null);

      var virtualCarets = window.restoreCaret;
      for(var i=0; i<virtualCarets.length;i++)
      {
           if(virtualCarets[i].row == parseInt(param.r))
           {
             var offset = (virtualCarets[i].index >= param.c || $(virtualCarets[i].obj).attr("id") == param.author)?-1:0;
             if($(virtualCarets[i].obj).attr("id") == param.author)
                virtualCarets[i].index = (parseInt(param.c)) + offset;
               else
                virtualCarets[i].index += offset ;
           }
      }
      window.col = (window.col >= param.c && window.row == param.r)?window.subOrZero(window.col,1):window.col;
 }

 window.viewFn['execCanc'] = function(param){

      var nextText = window.getText(parseInt(param.r)+1);
      var currText = window.getText(parseInt(param.r));
      $('tr:eq('+(parseInt(param.r))+')').children("td").text(currText + nextText);
      $('tr:eq('+(parseInt(param.r)+1)+')').remove();

      //update real caret
      if((window.row == param.r && window.col >= param.c) || window.row > param.r){
            window.col =(parseInt(window.row) == parseInt(param.r))?window.col:currText.length;
            window.row = (parseInt(window.row) == parseInt(param.r))?window.row:window.subOrZero(window.row,1);
      }

      //update virtual carets
      for(var i=0; i<restoreCaret.length;i++){
            if((restoreCaret[i].row == param.r && restoreCaret[i].index >= param.c) || restoreCaret[i].row > param.r){
              restoreCaret[i].index = (parseInt(restoreCaret[i].row) == parseInt(param.r))?restoreCaret[i].index:currText.length;
              restoreCaret[i].row = (parseInt(restoreCaret[i].row) == parseInt(param.r))?restoreCaret[i].index:window.subOrZero(restoreCaret[i].index,1);
            }
      }
}

 window.viewFn['execCancChar']=function(param){
      var currRow = $($('tr:eq('+(parseInt(param.r))+')').get(0));
      $(currRow).focusEditable(parseInt(param.c+1));
      document.execCommand('delete', false, null);
 }

 window.viewFn['execAddChar']=function(param){
     var currRow = $('tr:eq('+parseInt(param.r)+')');
     var currText = currRow.children("td").text();
     var char = param.chr;
     var newText = currText.substring(0,parseInt(param.c))+char+ currText.substring(parseInt(param.c));
     currRow.children("td").text(newText);

     var virtualCarets = window.restoreCaret; 
     for(var i=0; i<virtualCarets.length;i++)
     {
         if(virtualCarets[i].row == parseInt(param.r))
         {
           var offset = (virtualCarets[i].index >= param.c || $(virtualCarets[i].obj).attr("id") == param.author)?1:0;
           if($(virtualCarets[i].obj).attr("id") == param.author)
              virtualCarets[i].index = (parseInt(param.c)) + offset;
             else
              virtualCarets[i].index += offset ;
         }
     }
     window.col = (window.col >= param.c && window.row == param.r)?window.col+1:window.col;
 }

 window.viewFn['join'] = function(param){

     if(param.editorID != window.editorID)
     {
        var pingData = {"action":"ping","editorID": window.editorID, "editorColor": window.editorColor};

        if(typeof $("#" + param.editorID).get(0) == "undefined")
          window.addCaret(0,0,null,param.editorID,param.editorColor);

        stream.send(JSON.stringify(pingData));
     }

 }

 window.viewFn['leave'] = function(param){

      $("#"+param.editorID).remove();
 }

 window.viewFn['ping'] = function(param){
     if(param.editorID != window.editorID && typeof $("#" + param.editorID).get(0) == "undefined")
     {
       window.addCaret(0,0,null,param.editorID,param.editorColor);
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

    switch(data.fn){


      case "execUpdatePosition":{
        if (data.author != window.editorID){
           var authorCaret = $("#" + data.author);
           $(authorCaret).remove();
           window.addCaret(data.r,data.c,authorCaret.get(0),data.author);
        }
        var currRow = $($('tr:eq('+(parseInt(data.r))+')').get(0));
        $(currRow).focusEditable(parseInt(data.c));
        break;
      }
      default:{
        window.removeVirtualCaret(data.editorID,data.fn);
        window.normalizeTextNode(data.r);
        window.viewFn[data.fn](data);
        window.restoreCarets(data);
      }
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
  var anchorNode = window.getSelection().anchorNode; //text
  var parentTD = $(anchorNode).closest("td");      //td
  var parentTR = $(parentTD).closest("tr");        //tr
  var parentNodeContents = $(parentTD).contents();
  var anchorOffset = window.getSelection().anchorOffset;
  
  var col = 0;

  if(parentNodeContents.length > 1){
    for(var i=0;i<parentNodeContents.length;i++){
        if(parentNodeContents[i] !== anchorNode){
         if(parentNodeContents[i].nodeType == 3)
          col += parentNodeContents[i].nodeValue.length;
        }  
        else
          if(parentNodeContents[i] === anchorNode)
            break;

    }
  }

  col += anchorOffset;

  var currRow =parentTR;
  var row =  $(currRow).index();

  //console.log("r:"+row+"c:"+col);
  var text = window.getText(row);

  window.row = parseInt(row);
  window.col = parseInt(col);

  if(e.type == "mouseup" && $.contains(window.documentized.get(0),e.target))
  {
       var streamPos = {
         'fn':'execUpdatePosition',
         'r':row,
         'c':col,
         'author':window.editorID,
         'action':'updatePosition'
       };

       notifyChange(JSON.stringify(streamPos));
  }


  if(e.type == "keyup" || e.type == "keydown")
  {
     if(e.type == "keyup")
       switch(e.keyCode){
         case 8:  {e.stopImmediatePropagation(); e.preventDefault(); return false;} //backspace
         case 13: {e.preventDefault(); break;} // enter
         case 46: {e.preventDefault(); break;} // canc
         case 38: {  //arrow up
             if($(currRow).is(":first-child"))
               return false;

             var streamPos = {
                 'fn':'execUpdatePosition',
                 'r':window.subOrZero(row,1),
                 'c':col,
                 'author':window.editorID,
                 'action':'updatePosition'
             };

             window.row = streamPos.r;
             notifyChange(JSON.stringify(streamPos));

             //currRow.prev().focusEditable(col);
             return false;
         }
         case 40: { //arrow down
           if($(currRow).is(":last-child"))
             return false;

           var streamPos = {
                'fn':'execUpdatePosition',
                'r':row+1,
                'c':col,
                'author':window.editorID,
                'action':'updatePosition'
           };

           window.row=streamPos.r;
           notifyChange(JSON.stringify(streamPos));
           //currRow.next().focusEditable(col);
           return false;
         }
         default: return;

       }

     //console.log(e.keyCode);
     if(e.type == "keydown")
       switch(e.keyCode){
          case 39: //right arrow
          {
            var streamPos = {
                    'fn':'execUpdatePosition',
                    'r':row,
                    'c':col+1,
                    'author':window.editorID,
                    'action':'updatePosition'
            };

            if(col < text.length)
            {
              notifyChange(JSON.stringify(streamPos));
              window.col = streamPos.c;
            }


            break;
          }
          case 37: //left arrow
          {
              var streamPos = {
                      'fn':'execUpdatePosition',
                      'r':row,
                      'c':(col>1)?window.subOrZero(col,1):col,
                      'author':window.editorID,
                      'action':'updatePosition'
              };
              if(col > 0)
              {
                notifyChange(JSON.stringify(streamPos));
                window.col = streamPos.c;
              }


              break;
          }
          case 13:
          { //enter
            e.preventDefault();

            if(text == "" || text.length == col)
            {
                var paramEnter1 = {
                   fn: "execEnter1",
                   r: row,
                   c: col,
                   author:window.editorID,
                };
                notifyChange($.fn.indices(currRow,'addRowNoMoveText',paramEnter1));
            }
            else{

                var paramEnter2 = {
                   fn: "execEnter2",
                   r:row,
                   c: col,
                   c_end: parseInt(text.length),
                   author:window.editorID,
                };

                notifyChange($.fn.indices(currRow,'addRowMoveText',paramEnter2));

            }
            return false;
          }
          case 8:
          {

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

            var currTr = parentTR; //$(window.getSelection().anchorNode).closest("tr");
            var currText = text;

            if(currText.length > col){

               var paramCancChar = {

                    fn:"execCancChar",
                    r:row,
                    c:col,
                    author:window.editorID

               };
               notifyChange($.fn.indices(currTr,'removeChar',paramCancChar,true,false));

            }else{
                if(currTr.is(":last-child")) return false;

                var paramCanc = {

                  fn: "execCanc",
                  r: row,
                  c: col,
                  author:window.editorID,
                };

                notifyChange($.fn.indices(currTr,'removeRow',paramCanc,false,true));
            }

            return false;
          }
          case 38:
          case 40: {e.preventDefault(); return false; }
          default:
            return true;
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
  window.documentized = doc;
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
