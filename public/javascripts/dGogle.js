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



