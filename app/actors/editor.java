package actors;

import akka.dispatch.*;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.cluster.pubsub.*;
import akka.util.Timeout;
import messages.*;
import scala.concurrent.Future;
import static akka.pattern.Patterns.ask;

import utils.*;

import play.Logger;


import java.util.concurrent.TimeUnit;

public class editor extends UntypedActor {


    private final ActorSystem system = getContext().system();

    private final ActorRef socket;
    private final ActorRef router;
    private ActorRef db;

    private String room;
    private String project;
    private String file;
    private String editorID;
    private String editorColor;
    private ActorRef compilerManager = null;
    private ActorRef broadCaster = null;

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static Props props(ActorRef out) {
        return Props.create(editor.class, out);
    }


    public editor(ActorRef out) {

        this.router =  DistributedPubSub.get(system).mediator();
        this.socket = out;

        ActorSelection s = system.actorSelection("akka://application/user/controllerActor");
        Future<ActorRef> f = s.resolveOne(new Timeout(5, TimeUnit.SECONDS));

        f.onComplete(new OnComplete<ActorRef>() {
            @Override
            public void onComplete(Throwable excp, ActorRef child) throws Throwable {
                if (excp != null)
                  broadCaster = system.actorOf(Props.create(controllerActor.class),"controllerActor");
                else
                  broadCaster = child;
            }
        }, system.dispatcher());



    }

    @Override
    public void onReceive(Object message) {
            Logger.debug("RICEVUTO QUALCOSA: " + message.getClass().getName());
            if(message instanceof updateReferendum) {
                Logger.error("NEW UPDATE REFERENDUM");
                jsonUtil ret = new jsonUtil("");
                if((((updateReferendum) message).getObject() == updateReferendum.enumObject.VOTER))
                {
                    Logger.error("NEW VOTER");
                    ret.put("fn","addVoter");
                    ret.put("containerName",((updateReferendum) message).getContainerName());
                    ret.put("targetName",((updateReferendum) message).getTargetName());
                }
                else
                {
                    Logger.error("NEW VOTE");
                    ret.put("fn","vote");
                    ret.put("containerName",((updateReferendum) message).getContainerName());
                    ret.put("targetName",((updateReferendum) message).getTargetName());
                    ret.put("value",((updateReferendum) message).getValue());
                }
                socket.tell(ret.toString(),getSelf());

            }
            else if(message instanceof referendumMessage  && !(((referendumMessage) message).getAuthor().equals(editorID))){

                    Logger.debug("NEW REFERENDUM");
                    if(((referendumMessage) message).getTarget() == referendumMessage.targetEnum.FILE) {
                        Logger.debug("FOR A FILE");
                        if (((referendumMessage) message).getContainerName().equals(project)) {
                            Logger.debug("I'M INTERESTED");
                            referendumMessage msg = (referendumMessage) message;

                            msg.getSender().tell(new updateReferendum()
                                    .setObject(updateReferendum.enumObject.VOTER)
                                    .setTargetName(msg.getTargetName())
                                    .setContainerName(msg.getContainerName()), getSelf());

                            jsonUtil ref = new jsonUtil("");
                            ref.put("author", msg.getAuthor());
                            ref.put("target", msg.getTarget().toString());
                            ref.put("targetName", msg.getTargetName());
                            ref.put("act", msg.getAction().toString());
                            ref.put("containerID", msg.getContainerID());
                            ref.put("containerName", msg.getContainerName());
                            ref.put("targetID", msg.getTargetID());
                            ref.put("fn", "referendum");
                            socket.tell(ref.toString(), getSelf());
                        }
                    }
            }else if(message instanceof referendumMessage && ((referendumMessage) message).getAuthor().equals(editorID)) {
                Logger.error("RICEVUTO MIO REFEREDUM");
                return;
            }else if(message instanceof controllerMessage) {
                if (((controllerMessage) message).getAction() == controllerMessage.actionEnum.ADD) {
                    if(((controllerMessage) message).getTarget() == controllerMessage.targetEnum.FILE){
                        if(((controllerMessage) message).getContainerName().equals(project))
                        {
                            jsonUtil ret = new jsonUtil("");
                            ret.put("fn","addFile");
                            ret.put("fileName",((controllerMessage) message).getTargetName());
                            ret.put("fileID",((controllerMessage) message).getTargetID());
                            ret.put("projectID",((controllerMessage) message).getContainerID());
                            socket.tell(ret.toString(),getSelf());
                        }
                        return ;

                    }else if(((controllerMessage) message).getTarget()== controllerMessage.targetEnum.PROJECT){
                        return ;
                    }
                } else if (((controllerMessage) message).getAction() == controllerMessage.actionEnum.DELETE){
                    if(((controllerMessage) message).getTarget() == controllerMessage.targetEnum.FILE){
                        if(((controllerMessage) message).getContainerName().equals(project) && ((controllerMessage) message).getAuthor().equals(editorID) ){

                            Logger.info("RICHIESTO NUOVO REFERENDUM");
                            referendumMessage ref = ((controllerMessage)message).toReferedumMessage();
                            ref.setSender(getSelf()).setForwarded(false).setAuthor(editorID);
                            Logger.info("AUTORE: " + ref.getAuthor());
                            broadCaster.tell(ref,getSelf());
                            return;
                        }
                    }else if(((controllerMessage) message).getTarget()== controllerMessage.targetEnum.PROJECT){

                    }
                }
            }
            else if( message instanceof updateCompile){
               socket.tell(((updateCompile) message).toString(),getSelf());
            }
            else if (message instanceof DistributedPubSubMediator.SubscribeAck){
                jsonUtil msg = new jsonUtil("");
                msg.put("fn","join");
                msg.put("editorID",this.editorID);
                msg.put("editorColor",this.editorColor);
                msg.put("action","join");
                msg.put("ack","");
                router.tell(new DistributedPubSubMediator.Publish(this.room, msg.toString()),getSelf());

            }
            else if (message instanceof documentChanges){
                socket.tell(((documentChanges)message).getMsg(),self());
            }else if (message instanceof String) {


                Logger.info("GOT: " + (String)message);
                jsonUtil jsonMsg = new jsonUtil((String)message);

                //asking db for parameter
                //dbUtil   db = new dbUtil(system);
                if((String)jsonMsg.get("editorID") != null)
                {
                    switch((String)jsonMsg.get("action"))
                    {

                        case "join":
                        {
                            if(jsonMsg.get("ack") == null)
                            {
                                String project = (String)jsonMsg.get("project");
                                String file = (String)jsonMsg.get("file");
                                this.editorColor = (String)jsonMsg.get("editorColor");
                                this.project = project;
                                this.file = file;
                                this.room = project + " " + file;

                                ActorSelection sel = system.actorSelection("akka://application/user/"+"DB"+project);
                                Future<ActorRef> future = sel.resolveOne(new Timeout(5, TimeUnit.SECONDS));

                                future.onComplete(new OnComplete<ActorRef>() {
                                    @Override
                                    public void onComplete(Throwable excp, ActorRef child) throws Throwable {
                                        if (excp != null) {
                                            db = system.actorOf(dbActor.props(),"DB"+project);
                                        } else {
                                            db = child;
                                        }

                                        ActorSelection s = system.actorSelection("akka://application/user/compilerManager" + project);
                                        Future<ActorRef> f = s.resolveOne(new Timeout(5, TimeUnit.SECONDS));

                                        f.onComplete(new OnComplete<ActorRef>() {
                                            @Override
                                            public void onComplete(Throwable excp, ActorRef child) throws Throwable {
                                                if (excp != null) {
                                                    compilerManager = system.actorOf(Props.create(compilerManager.class,project,db),"compilerManager" + project);
                                                } else {
                                                    compilerManager = child;
                                                }
                                            }
                                        }, system.dispatcher());

                                        jsonUtil initMsg = new jsonUtil("");
                                        initMsg.put("action","init");
                                        initMsg.put("project",project);
                                        initMsg.put("file",file);

                                        db.tell(initMsg.toString(),getSelf());
                                        editorID = (String)jsonMsg.get("editorID");
                                        router.tell(new DistributedPubSubMediator.Subscribe(room, getSelf()), getSelf());
                                    }
                                }, system.dispatcher());
                                //initialize db actor if require, one for each room


                                break;
                            }
                            else if(!this.editorID.equals((String)jsonMsg.get("editorID")))
                            {
                                jsonUtil join = new jsonUtil("");
                                join.put("editorID",jsonMsg.get("editorID"));
                                join.put("editorColor",jsonMsg.get("editorColor"));
                                join.put("fn","join");
                                socket.tell(join.toString(),self());
                            }
                            break;

                        }
                        case "leave":
                        {
                            Logger.info("LEAVE EDITOR : leaving room {}",this.room);
                            jsonUtil msg = new jsonUtil("");
                            msg.put("fn","leave");
                            msg.put("editorID",this.editorID);
                            router.tell(new DistributedPubSubMediator.Publish(this.room, new documentChanges(msg.toString())),getSelf());
                            router.tell(new DistributedPubSubMediator.Unsubscribe(room,getSelf()), getSelf());
                            break;
                        }
                        case "ping":
                        {

                            jsonUtil msg = new jsonUtil("");
                            msg.put("fn","ping");
                            msg.put("editorID",this.editorID);
                            msg.put("editorColor",this.editorColor);
                            router.tell(new DistributedPubSubMediator.Publish(this.room, new documentChanges(msg.toString())),getSelf());
                            break;

                        }

                    }


                }
                else
                {
                    if(((String)jsonMsg.get("action")).equals("init")) {
                         jsonMsg.put("fn","init");
                         socket.tell(jsonMsg.toString(),getSelf());
                         return;
                    }else {
                        Future f = null;
                        switch ((String) jsonMsg.get("action")) {
                            //editor's view function helper
                            case "compile":{
                                compilerManager.tell(new compileMessage().setSender(getSelf()).setProject(project),getSelf());
                                break;
                            }
                            case "open": {
                                //read all documents line
                                //build command for actorDB
                            jsonUtil openCmd = new jsonUtil("");
                            openCmd.put("action","open");
                            openCmd.put("file",file);
                            f = ask(db,(Object)openCmd.toString(),1000);
                            f.onSuccess(new OnSuccess<String>(){
                                public void onSuccess(String result) {

                                    socket.tell((Object)result,getSelf());
                                }
                            },system.dispatcher());
                                return;
                            }

                            case "updatePosition":{
                                router.tell(new DistributedPubSubMediator.Publish(room, new documentChanges(jsonMsg.toString())), getSelf());
                                break;
                            }
                            default: {


                                jsonMsg.put("file",file);
                                f = ask(db,(Object)jsonMsg.toString(),1000);
                                f.onSuccess(new OnSuccess() {
                                    @Override
                                    public void onSuccess(Object result) throws Throwable {
                                        router.tell(new DistributedPubSubMediator.Publish(room, new documentChanges(jsonMsg.toString())), getSelf());

                                    }
                                },system.dispatcher());
                                break;
                            }

                        }

                    }
                }


                //******************************


            }
            else
            {

                unhandled(message);
            }



    }
}