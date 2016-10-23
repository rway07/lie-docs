package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import messages.controllerMessage;
import messages.referendumMessage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by enrico on 22/10/16.
 */
public class controllerActor extends UntypedActor {

    Cluster cluster = Cluster.get(getContext().system());

    private HashMap<String,String> nodes = new HashMap<String,String>();

    //subscribe to cluster changes
    @Override
    public void preStart() {
        //#subscribe
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
        //#subscribe
    }

    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object message) {

        if((message instanceof controllerMessage) && !((controllerMessage) message).isForwarded()){

            ((controllerMessage) message).setForwarded(true);

            Set<String> remotes = nodes.keySet();
            Iterator<String> iremotes = remotes.iterator();
            while(iremotes.hasNext())
            {
                ActorSelection remoteEditor = getContext().system().actorSelection(iremotes.next() + "/user/*");
                remoteEditor.tell(message,ActorRef.noSender());
            }
        }
        if (message instanceof ClusterEvent.MemberUp) {
            ClusterEvent.MemberUp mUp = (ClusterEvent.MemberUp) message;
            nodes.put(mUp.member().address().toString(),mUp.member().address().toString());
        } else if (message instanceof ClusterEvent.UnreachableMember) {
            ClusterEvent.UnreachableMember mUnreachable = (ClusterEvent.UnreachableMember) message;
        } else if (message instanceof ClusterEvent.MemberRemoved) {
            ClusterEvent.MemberRemoved mRemoved = (ClusterEvent.MemberRemoved) message;
            nodes.remove(mRemoved.member().address().toString());
        } else if (message instanceof ClusterEvent.MemberEvent) {
            // ignore

        } else {
            unhandled(message);
        }

    }

}
