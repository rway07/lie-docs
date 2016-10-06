package controllers;

import actors.editor;
import akka.actor.*;
import play.mvc.LegacyWebSocket;
import play.mvc.WebSocket;

public class WebsocketController {

    public LegacyWebSocket<String> ws() {return WebSocket.withActor(editor::props);}

}
