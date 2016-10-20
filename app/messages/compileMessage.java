package messages;

import akka.actor.ActorRef;
import scala.Serializable;
import utils.cProject;

/**
 * Created by enrico on 18/10/16.
 */
public class compileMessage implements Serializable{

    private ActorRef sender;
    private cProject prjSource=null;
    private String project;

    public compileMessage(){
        prjSource = new cProject();
    }

    public boolean projectCreated = false;

    public cProject getCProject(){return prjSource;}
    public void setCProject(cProject p){prjSource = p;}

    public String getProject() {
        return project;
    }

    public compileMessage setProject(String project) {
        this.project = project;
        return this;
    }

    public ActorRef getSender() {
        return sender;
    }

    public compileMessage setSender(ActorRef sender) {
        this.sender = sender;
        return this;
    }




}
