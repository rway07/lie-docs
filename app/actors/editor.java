package actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.cluster.pubsub.*;
import play.Logger;


public class editor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static Props props(ActorRef out) {
        Logger.debug("static editor");
        return Props.create(editor.class, out);
    }

    private final ActorRef socket;
    private final ActorRef router;

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
          log.info("Got new msg from websocket: {}", message);
          router.tell(new DistributedPubSubMediator.Publish("content", new documentChanges(message)), getSelf());
        }
        else
          unhandled(message);
    }
}