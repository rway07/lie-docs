package actors;

import akka.actor.*;
import akka.dispatch.OnComplete;
import akka.routing.FromConfig;
import akka.util.Timeout;
import messages.compileMessage;
import messages.sourceCompiled;
import messages.updateCompile;
import play.Logger;
import scala.concurrent.Future;
import utils.cProject;
import utils.fileSystem;

import static akka.pattern.Patterns.ask;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by enrico on 18/10/16.
 */
public class compilerManager extends UntypedActor{

    private ActorRef workerRouter = null;
    private ActorRef db = null;
    private int missingCompiled = 0;
    private int totaleObject = 0;
    private boolean someFail = false;
    private File cwd = null;
    private String workingDir = "";
    private String project;
    private final String me = "MANAGER";
    private String objs = "";
    private boolean errors = false;

    //manager: 1- ottiene tutti i c del progetto
    //         2- analizza gli include locali
    //         3- distribuisce file c e include necessari al worker
    //         4- attende file o da tutti i worker
    //         5- linka il risultato

    public compilerManager(String project, ActorRef db) {
        this.db = db;

        this.project = project;
        this.workingDir = fileSystem.getTempDir() + "/" + getSelf().path().name() + "/" + project;

        ActorSelection sel = getContext().system().actorSelection("akka://application/user/workerRouter");
        Future<ActorRef> future = sel.resolveOne(new Timeout(5, TimeUnit.SECONDS));

        future.onComplete(new OnComplete<ActorRef>() {
            @Override
            public void onComplete(Throwable excp, ActorRef child) throws Throwable {
                if (excp != null) {
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

            cProject project = (cProject)((compileMessage) message).getCProject();

            //ottengo tutto cio che devo compilare
            Iterator<String> sources = project.getSources();
            someFail = false;
            cwd = fileSystem.getWorkingDir(getSelf().path().name()+ "/" + this.project);
            objs = "";
            errors = false;

            while(sources.hasNext())
            {
                missingCompiled++;
                cProject f = project.getSourceHeaders(sources.next())
                                    .setSender(((compileMessage) message).getSender())
                                    .setProjectName(((compileMessage) message).getProject());

                workerRouter.tell(f,getSelf());
            }
            totaleObject = missingCompiled;
        }
        else if(message instanceof sourceCompiled){
            someFail |= ((sourceCompiled) message).isCompilationFailed();
            missingCompiled--;


            if(!((sourceCompiled) message).isCompilationFailed()) {
                fileSystem.writeBinary(workingDir+"/"+((sourceCompiled) message).getObjName(),((sourceCompiled) message).getBinData());
                ((sourceCompiled)message).getSender().tell(new updateCompile()
                                                     .setStatus("Got compiled object " + ((sourceCompiled) message).getObjName() + " from " + ((sourceCompiled) message).getWorkerName() + " (Objects: "+(-missingCompiled + totaleObject)+"/"+totaleObject+")").setSenderName(me),getSelf());

                objs += " "+ workingDir + "/" + (((sourceCompiled) message).getObjName());
            }

            if(missingCompiled == 0 && !someFail) {


                ((sourceCompiled)message).getSender().tell(new updateCompile()
                        .setStatus("Linking...").setSenderName(me),getSelf());


                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec("gcc -o " + workingDir+"/" + this.project + " " + objs ,null,cwd);

                BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                String s = null;

                while (proc.isAlive()) {

                    if(stdError.ready())
                    {
                        errors = true;
                        s = stdError.readLine();
                        ((sourceCompiled) message).getSender().tell(new updateCompile()
                                                                        .setStatus(s)
                                                                        .setSender(((sourceCompiled) message).getSender())
                                                                        .setSenderName(me),getSelf());
                    }
                }

                ((sourceCompiled)message).getSender().tell(new updateCompile()
                        .setStatus("Linking Successfull").setSenderName(me),getSelf());

            }

        }
        else if(message instanceof compileMessage)
        {
            db.tell(message,getSelf());
        }else if( message instanceof updateCompile)
        {
            ((updateCompile) message).getSender().tell(message,getSelf());
        }


    }
}
