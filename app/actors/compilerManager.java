package actors;

import akka.actor.*;
import akka.cluster.routing.ClusterRouterPool;
import akka.cluster.routing.ClusterRouterPoolSettings;
import akka.dispatch.OnComplete;
import akka.remote.routing.RemoteRouterConfig;
import akka.routing.BalancingPool;
import akka.routing.RoundRobinPool;
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

    public compilerManager()
    {
        Address[] addresses = {
                AddressFromURIString.parse("akka.tcp://application@127.0.0.1:2551"),
                AddressFromURIString.parse("akka.tcp://application@127.0.0.1:2552"),
        };

        ActorSelection sel = getContext().system().actorSelection("akka://application/user/workerRouter");
        Future<ActorRef> future = sel.resolveOne(new Timeout(5, TimeUnit.SECONDS));

        future.onComplete(new OnComplete<ActorRef>() {
            @Override
            public void onComplete(Throwable excp, ActorRef child) throws Throwable {
                if (excp != null) {
                    Logger.info("routerWorker non esisteto, lo creo");
                    workerRouter = getContext().system().actorOf(new ClusterRouterPool(new BalancingPool(0),
                            new ClusterRouterPoolSettings(10,5,true,"compiler")).props(
                            Props.create(compilerWorker.class)),"workerRouter");

                    Logger.debug("*****" + workerRouter.path().toString());
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
