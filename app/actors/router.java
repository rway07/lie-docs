package actors;


import akka.actor.*;
import java.util.*;
import actors.*;
import play.Logger;

public class router extends UntypedActor {

    private final Set<ActorRef> editors = new HashSet<>();

    private void addEditor(ActorRef actorRef){
        editors.add(actorRef);
    }

    private void removeEditor(ActorRef actorRef){
        editors.remove(actorRef);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof registerMsg) {
            addEditor(sender());
            getContext().watch(sender()); // Watch sender so we can detect when they die.
        } else if (message instanceof Terminated) {
            // One of our watched senders has died.
            removeEditor(sender());
        } else if (message instanceof String) {
            for (ActorRef editor : editors) {
                  editor.tell(message, null);
            }
        }
    }
}