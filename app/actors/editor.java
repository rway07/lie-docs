package actors;

import akka.actor.*;
import play.Logger;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import play.Logger;

import controllers.HomeController;

public class editor extends UntypedActor {

    public static Props props(ActorRef out) {


        //return Akka.system().actorOf(Props.create(editor.class, out),"editor")
        return Props.create(editor.class, out);
    }

    private final ActorRef out;
    private final ActorRef router;

    public editor(ActorRef out) {
        this.out = out;
        this.router = HomeController.router;

    }

    @Override
    public void preStart() {
        router.tell(new registerMsg(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String)
        {
            if(!getSender().toString().equals("Actor[akka://application/deadLetters]"))
              router.tell(message, getSelf());
            else
              out.tell(message, null);

        } else {
            unhandled(message);
        }
    }
}