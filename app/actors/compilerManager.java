package actors;

import akka.actor.*;
import akka.dispatch.OnComplete;
import akka.routing.FromConfig;
import akka.util.Timeout;
import messages.compileMessage;
import messages.updateCompile;
import play.Logger;
import scala.concurrent.Future;

import java.util.concurrent.TimeUnit;

/**
 * Created by enrico on 18/10/16.
 */
public class compilerManager extends UntypedActor{

    private ActorRef workerRouter = null;
    private ActorRef db = null;


    public compilerManager()
    {
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

        if(message instanceof compileMessage)
        {
            Logger.info("DEVO COMPILARE, CHE PALLE");
            workerRouter.tell(message,getSelf());
        }else if( message instanceof updateCompile)
        {
            Logger.debug(((updateCompile) message).getStatus());
        }


    }
}
