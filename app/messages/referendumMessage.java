package messages;

import akka.actor.ActorRef;
import scala.Serializable;

/**
 * Created by enrico on 22/10/16.
 */
public class referendumMessage extends controllerMessage implements Serializable {

    public referendumMessage()
    {
        super("");
    }
    private ActorRef sender;

    public ActorRef getSender() {
        return sender;
    }

    public referendumMessage setSender(ActorRef sender) {
        this.sender = sender;
        return this;
    }
}
