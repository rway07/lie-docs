package actors;

import akka.dispatch.*;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.cluster.pubsub.*;
import scala.concurrent.Future;
import utils.*;

import play.Logger;

import java.sql.ResultSet;

public class editor extends UntypedActor {


    private final ActorSystem system = getContext().system();
    private final ActorRef socket;
    private final ActorRef router;
    private String room;
    private String editorID;
    private String editorColor;

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static Props props(ActorRef out) {
        return Props.create(editor.class, out);
    }


    public editor(ActorRef out) {

      this.router =  DistributedPubSub.get(getContext().system()).mediator();
      this.socket = out;

    }

    @Override
    public void onReceive(Object message) {

        try{

            if (message instanceof DistributedPubSubMediator.SubscribeAck){
                Logger.error("NEW EDITOR : for room: {}",this.room);
                jsonUtil msg = new jsonUtil("");
                msg.put("fn","join");
                msg.put("editorID",this.editorID);
                msg.put("editorColor",this.editorColor);
                msg.put("action","join");
                msg.put("ack","");
                router.tell(new DistributedPubSubMediator.Publish(this.room, msg.toString()),getSelf());

            }
            else if (message instanceof documentChanges ){
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
                                 Logger.info("ack join dico a tutti");
                                String project = (String)jsonMsg.get("project");
                                String file = (String)jsonMsg.get("file");
                                this.editorColor = (String)jsonMsg.get("editorColor");
                                this.room = project + " " + file;
                                this.editorID = (String)jsonMsg.get("editorID");
                                router.tell(new DistributedPubSubMediator.Subscribe(room, getSelf()), getSelf());
                                break;
                            }
                            else if(!this.editorID.equals((String)jsonMsg.get("editorID")))
                            {
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
                    switch((String)jsonMsg.get("action"))
                    {
                        case "updatePosition":
                        case "addChar":
                        case "removeChar":
                        {
                            break;
                        }
                        case "addRowNoMoveText":
                        {
                            jsonMsg.put("_subindex","0");
                            jsonMsg.put("_index","0");
                            break;
                        }
                        case "addRowMoveText": {
                            jsonMsg.put("_subindex","0");
                            jsonMsg.put("_index","0");
                            break;
                        }
                        case "removeRow":
                        case "addRow":
                        {
                            jsonMsg.put("_subindex","0");
                            jsonMsg.put("_index","0");
                            break;
                        }
                    }
                    //publish message

                    //Future<ResultSet> f = db.q("select * from projects");

                    Logger.warn("dico a tutti:" + jsonMsg.toString());
                    router.tell(new DistributedPubSubMediator.Publish(this.room, new documentChanges(jsonMsg.toString())),getSelf());

                }


                //******************************
            /*
            f.onSuccess(new OnSuccess<ResultSet>(){
                public void onSuccess(ResultSet result) {

                    //append something to json
                    //  ....

                    )), getSelf());
                }
            },system.dispatcher());
            */




            }
            else
                unhandled(message);
        }catch (Exception e){
          Logger.error(e.getMessage() + " - " + e.getCause());
        }

    }
}