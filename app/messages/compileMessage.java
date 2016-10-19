package messages;

import akka.actor.ActorRef;
import scala.Serializable;

/**
 * Created by enrico on 18/10/16.
 */
public class compileMessage implements Serializable{

    private ActorRef sender;

    public String getProject() {
        return project;
    }

    public compileMessage setProject(String project) {
        this.project = project;
        return this;
    }

    private String project;

    public ActorRef getSender() {
        return sender;
    }

    public compileMessage setSender(ActorRef sender) {
        this.sender = sender;
        return this;
    }




}
