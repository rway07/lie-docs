package actors;

import akka.actor.UntypedActor;
import messages.compileMessage;
import messages.sourceCompiled;
import messages.updateCompile;
import play.Logger;
import utils.cProject;
import utils.fileSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by enrico on 18/10/16.
 */
public class compilerWorker extends UntypedActor{

    private final String me = "WORKER (" + getSelf().path().name() + ")";
    private boolean errors = false;

    @Override
    public void onReceive(Object message) throws Throwable {



        //comandi gcc:
        // gcc -c file.c -o file.o per ogni file c
        // gcc -o prog_name *.o

        if(message instanceof cProject) {
            String tmpDir = fileSystem.getTempDir();
            String workingDir = tmpDir + "/"+ getSelf().path().name() + "/" + ((cProject) message).getProjectName();
            File cwd = fileSystem.getWorkingDir(getSelf().path().name() + "/" + ((cProject) message).getProjectName());


            //printing soucers
            String sourceFileName = "";
            Iterator<String> sources = ((cProject) message).getSources();
            while (sources.hasNext())
            {
                sourceFileName = sources.next();
                fileSystem.writeText(workingDir + "/" + sourceFileName,((cProject) message).getSource(sourceFileName));
            }

            //printing headers
            sources = ((cProject) message).getHeaders();
            while (sources.hasNext())
            {
                String fileName = sources.next();
                fileSystem.writeText(workingDir + "/" + fileName,((cProject) message).getHeader(fileName));
            }

            getSender().tell(new updateCompile()
                                       .setStatus("Compiling... " + sourceFileName)
                                       .setSender(((cProject) message).getSender())
                                       .setSenderName(me)
                                       .setMsgType(updateCompile.type.Info)
                                       .setTotalSteps(0)
                                       .setCurrentStep(0),getSelf());

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("gcc -c " + workingDir+"/"+sourceFileName + " -o " + workingDir+"/"+sourceFileName.substring(0,sourceFileName.length()-2) + ".o",null,cwd);

            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            String s = null;

            while (proc.isAlive()) {

                if(stdError.ready())
                {
                    errors = true;
                    s = stdError.readLine();
                    getSender().tell(new updateCompile()
                                     .setStatus(s)
                                     .setSender(((cProject) message).getSender())
                                     .setSenderName(me)
                                     .setMsgType(updateCompile.type.Error)
                                     .setTotalSteps(0)
                                     .setCurrentStep(0),getSelf());
                }
            }

            if(!errors){
                getSender().tell(new updateCompile()
                                     .setStatus("Compilation successfull!")
                                     .setSender(((cProject) message).getSender())
                                     .setSenderName(me)
                                     .setMsgType(updateCompile.type.Success)
                                     .setTotalSteps(0)
                                     .setCurrentStep(0),getSelf());

                byte[] data = fileSystem.readBinary(workingDir+"/"+sourceFileName.substring(0,sourceFileName.length()-2) + ".o");

                getSender().tell(new sourceCompiled()
                           .setWorkerName(getSelf().path().name())
                           .setSender(((cProject) message).getSender())
                           .setCompilationFailed(false)
                           .setBinData(data)
                           .setProjectName(((cProject) message).getProjectName())
                           .setObjName(sourceFileName.substring(0,sourceFileName.length()-2) + ".o"),getSelf());
            }else{
                getSender().tell(new sourceCompiled()
                           .setWorkerName(getSelf().path().name())
                           .setSender(((cProject) message).getSender())
                           .setCompilationFailed(true)
                           .setBinData(null)
                           .setProjectName(((cProject) message).getProjectName())
                           .setObjName(sourceFileName.substring(0,sourceFileName.length()-2) + ".o"),getSelf());
            }
        }

        //getSelf().tell("sono un compilatore",getSender());
    }
}
