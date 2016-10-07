package actors;

import akka.dispatch.*;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.cluster.pubsub.*;
import com.google.inject.Inject;
import com.sun.rowset.internal.Row;
import scala.concurrent.Future;


import play.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Callable;
import static akka.dispatch.Futures.future;
import static play.db.DB.getConnection;

import play.db.*;




public class editor extends UntypedActor {


    private final ActorSystem system = getContext().system();
    private final ActorRef socket;
    private final ActorRef router;
    private DB db;


    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static Props props(ActorRef out) {
        Logger.debug("static editor");
        return Props.create(editor.class, out);
    }


    public editor(ActorRef out) {

      Logger.debug("instance editor");
      this.router =  DistributedPubSub.get(getContext().system()).mediator();

      // subscribe to the document named "content"
      router.tell(new DistributedPubSubMediator.Subscribe("content", getSelf()), getSelf());
      this.socket = out;

    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof DistributedPubSubMediator.SubscribeAck)
          log.info("Editor is now: SUBSCRIBER");
        else if (message instanceof documentChanges ){
          log.info("message from others");
          socket.tell(((documentChanges)message).getMsg(),self());
        }else if (message instanceof String) {

          log.info("Got new msg from websocket: {} START FUTURE EVENT : query db", message);
          Future<String> f = future(new Callable<String>() {
                public String call() {

                    Connection conn =  db.getConnection();
                    try{
                        Statement stm = conn.createStatement();
                        ResultSet r = stm.executeQuery("select name from projects");
                        r.first();
                        
                        Logger.info("enrico " + r.getString("name"));

                    }catch (Exception e){
                        Logger.error(e.getMessage() + ":" + e.getCause());
                    }

                    return "Hello" + "World";

                }
          }, system.dispatcher());

          f.onSuccess(new OnSuccess<String>(){
              public void onSuccess(String result) {
                  router.tell(new DistributedPubSubMediator.Publish("content", new documentChanges(message)), getSelf());
              }
          },system.dispatcher());


        }
        else
          unhandled(message);
    }
}