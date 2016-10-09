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

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static Props props(ActorRef out) {
        return Props.create(editor.class, out);
    }


    public editor(ActorRef out) {

      this.router =  DistributedPubSub.get(getContext().system()).mediator();

      // subscribe to the document named "content"
      router.tell(new DistributedPubSubMediator.Subscribe("content", getSelf()), getSelf());
      this.socket = out;

    }

    @Override
    public void onReceive(Object message) {

        try{
            if (message instanceof DistributedPubSubMediator.SubscribeAck);
            else if (message instanceof documentChanges ){
                socket.tell(((documentChanges)message).getMsg(),self());
            }else if (message instanceof String) {


                Logger.info("GOT: " + (String)message);
                jsonUtil jsonMsg = new jsonUtil((String)message);

                //asking db for parameter
                //dbUtil   db = new dbUtil(system);

                switch((String)jsonMsg.get("action"))
                {
                    case "addChar":
                    case "removeChar":
                    {
                        jsonMsg.put("rd",((Long)jsonMsg.get("r")).toString());
                        Long pos = (Long)jsonMsg.get("c");
                        pos +=1;
                        jsonMsg.put("cd",pos.toString());
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
                router.tell(new DistributedPubSubMediator.Publish("content", new documentChanges(jsonMsg.toString())),getSelf());

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