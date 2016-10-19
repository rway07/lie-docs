package actors;

import akka.dispatch.*;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.cluster.pubsub.*;
import akka.util.Timeout;
import messages.compileMessage;
import messages.documentChanges;
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

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static Props props(ActorRef out) {
        return Props.create(editor.class, out);
    }


    public editor(ActorRef out) {
        this.router =  DistributedPubSub.get(system).mediator();
        this.socket = out;

        ActorSelection sel = system.actorSelection("akka://application/user/compilerManager");
        Future<ActorRef> future = sel.resolveOne(new Timeout(5, TimeUnit.SECONDS));

        future.onComplete(new OnComplete<ActorRef>() {
            @Override
            public void onComplete(Throwable excp, ActorRef child) throws Throwable {
                if (excp != null) {
                    Logger.info("compilerManager non esisteto, lo creo");
                    compilerManager = system.actorOf(Props.create(compilerManager.class),"compilerManager");
                    Logger.debug("il manager si chiama: "+compilerManager.path().toString());
                } else {
                    compilerManager = child;
                }
            }
        }, system.dispatcher());

    }

    @Override
    public void onReceive(Object message) {

            if (message instanceof DistributedPubSubMediator.SubscribeAck){
                Logger.info("conferma subscribe");
                Logger.error("NEW EDITOR : for room: {}",this.room);
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


                    // subscribe to the document named "content"

                    switch((String)jsonMsg.get("action"))
                    {

                        case "join":
                        {
                            if(jsonMsg.get("ack") == null)
                            {
                                Logger.info("richiesta join da ws");
                                String project = (String)jsonMsg.get("project");
                                String file = (String)jsonMsg.get("file");
                                this.editorColor = (String)jsonMsg.get("editorColor");
                                this.project = project;
                                this.file = file;
                                this.room = project + " " + file;

                                ActorSelection sel = system.actorSelection("akka://application/user/"+"DB"+project+file);
                                Future<ActorRef> future = sel.resolveOne(new Timeout(5, TimeUnit.SECONDS));

                                future.onComplete(new OnComplete<ActorRef>() {
                                    @Override
                                    public void onComplete(Throwable excp, ActorRef child) throws Throwable {
                                        // ActorNotFound will be the Throwable if actor not exists
                                        Logger.info("getto attore e mi sottoscrivo");

                                        if (excp != null) {
                                            Logger.info("attore non esisteto, lo creo");
                                            db = system.actorOf(dbActor.props(),"DB"+project+file);
                                            Logger.info(db.path().toString());
                                            Logger.info("nuovo attore db: " + db.toString());

                                        } else {
                                            db = child;
                                            Logger.info("attore trovato: " + db.toString());

                                        }

                                        jsonUtil initMsg = new jsonUtil("");
                                        initMsg.put("action","init");
                                        initMsg.put("project",project);
                                        initMsg.put("file",file);

                                        db.tell(initMsg.toString(),getSelf());
                                        editorID = (String)jsonMsg.get("editorID");
                                        Logger.info("mi sottoscrivo alla room");
                                        router.tell(new DistributedPubSubMediator.Subscribe(room, getSelf()), getSelf());
                                    }
                                }, system.dispatcher());
                                //initialize db actor if require, one for each room


                                break;
                            }
                            else if(!this.editorID.equals((String)jsonMsg.get("editorID")))
                            {
                                Logger.info("mi annuncio come nuovo editor");
                                jsonUtil join = new jsonUtil("");
                                join.put("editorID",jsonMsg.get("editorID"));
                                join.put("editorColor",jsonMsg.get("editorColor"));
                                join.put("fn","join");
                                Logger.warn("comunico avvenuto join");
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
                            Logger.info("mi annuncio:  " + msg.toString());
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
                                compilerManager.tell(new compileMessage(),getSelf());
                                break;
                            }
                            case "open": {
                                //read all documents line
                                //build command for actorDB
                            jsonUtil openCmd = new jsonUtil("");
                            openCmd.put("action","open");
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
                unhandled(message);


    }
}