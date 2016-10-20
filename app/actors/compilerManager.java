package actors;

import akka.actor.*;
import akka.dispatch.OnComplete;
import akka.routing.FromConfig;
import akka.util.Timeout;
import messages.compileMessage;
import messages.updateCompile;
import play.Logger;
import scala.concurrent.Future;
import utils.cProject;
import static akka.pattern.Patterns.ask;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by enrico on 18/10/16.
 */
public class compilerManager extends UntypedActor{

    private ActorRef workerRouter = null;
    private ActorRef db = null;


    //manager: 1- ottiene tutti i c del progetto
    //         2- analizza gli include locali
    //         3- distribuisce file c e include necessari al worker
    //         4- attende file o da tutti i worker
    //         5- linka il risultato

    public compilerManager(String project, ActorRef db) {
        this.db = db;

        ActorSelection sel = getContext().system().actorSelection("akka://application/user/workerRouter");
        Future<ActorRef> future = sel.resolveOne(new Timeout(5, TimeUnit.SECONDS));

        future.onComplete(new OnComplete<ActorRef>() {
            @Override
            public void onComplete(Throwable excp, ActorRef child) throws Throwable {
                if (excp != null) {
                    Logger.info("routerWorker non esisteto, lo creo");
                    workerRouter = getContext().system().actorOf(Props.create(compilerWorker.class).withRouter(new FromConfig()),"workerRouter");
                } else {
                    workerRouter = child;
                }
            }
        }, getContext().system().dispatcher());
    }


    @Override
    public void onReceive(Object message) throws Throwable {

        if(message instanceof compileMessage && ((compileMessage) message).projectCreated)
        {
            Logger.info("MANAGER: RICEVUTO SOURCE PROGETTO");
            cProject project = (cProject)((compileMessage) message).getCProject();
            //ottengo tutto cio che devo compilare
            Iterator<String> sources = project.getSources();
            while(sources.hasNext())
            {
                cProject f = project.getSourceHeaders(sources.next());
                workerRouter.tell(f,getSelf());
            }
        }
        else if(message instanceof compileMessage)
        {
            Logger.info("MANAGER: ricevuta richiesa di compilazione per " + ((compileMessage) message).getProject());
            db.tell(message,getSelf());
        }else if( message instanceof updateCompile)
        {
            Logger.info("MANAGER: forwardo aggiornamento");
            ((updateCompile) message).getSender().tell(message,getSelf());
        }


    }
}
