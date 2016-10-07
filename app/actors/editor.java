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
    public void onReceive(Object message) throws Exception {

        if (message instanceof DistributedPubSubMediator.SubscribeAck);
        else if (message instanceof documentChanges ){
          socket.tell(((documentChanges)message).getMsg(),self());
        }else if (message instanceof String) {

            jsonUtil jsonMsg = new jsonUtil((String)message);
            dbUtil   db = new dbUtil(system);

            Logger.error("sono quiiiiiiiiiii: " + (String)jsonMsg.get("action"));

            Future<ResultSet> f = db.q("select * from projects");

            f.onSuccess(new OnSuccess<ResultSet>(){
              public void onSuccess(ResultSet result) {
                  router.tell(new DistributedPubSubMediator.Publish("content", new documentChanges(message)), getSelf());
              }
            },system.dispatcher());

        }
        else
          unhandled(message);
    }
}