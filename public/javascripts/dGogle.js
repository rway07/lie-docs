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

$(document).ready(function(){

    $("#editable").on('keydown keyup mousedown mouseup',function(e){

        if($(window.getSelection().anchorNode).is($(this))){
        console.log(0);
        }else{
        console.log(window.getSelection().anchorOffset);
        }
        });

$.fn.caret = function (begin, end)
    {
        if (this.length == 0) return;
        if (typeof begin == 'number')
        {
            end = (typeof end == 'number') ? end : begin;
            return this.each(function ()
            {
                if (this.setSelectionRange)
                {
                    this.setSelectionRange(begin, end);
                } else if (this.createTextRange)
                {
                    var range = this.createTextRange();
                    range.collapse(true);
                    range.moveEnd('character', end);
                    range.moveStart('character', begin);
                    try { range.select(); } catch (ex) { }
                }
            });
        } else
        {
            if (this[0].setSelectionRange)
            {
                begin = this[0].selectionStart;
                end = this[0].selectionEnd;
            } else if (document.selection && document.selection.createRange)
            {
                var range = document.selection.createRange();
                begin = 0 - range.duplicate().moveStart('character', -100000);
                end = begin + range.text.length;
            }
            return { begin: begin, end: end };
        }
    }

});

