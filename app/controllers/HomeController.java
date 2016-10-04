package controllers;

import play.mvc.*;
import play.*;
import views.html.*;

import akka.actor.*;
import play.libs.F.*;
import play.mvc.WebSocket;
import play.mvc.LegacyWebSocket;


import play.Logger.*;

import actors.*;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


    private final static ActorSystem system = ActorSystem.create("MySystem");
    public final static ActorRef router = system.actorOf(Props.create(router.class),"router");


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(index.render("chinese Gogle Docs","","Unamed Document"));
    }

    public LegacyWebSocket<String> ws() {
        return WebSocket.withActor(editor::props);
    }
}
