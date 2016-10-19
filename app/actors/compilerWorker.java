package actors;

import akka.actor.UntypedActor;
import messages.compileMessage;
import messages.updateCompile;
import play.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by enrico on 18/10/16.
 */
public class compilerWorker extends UntypedActor{

    public compilerWorker(){
            Logger.debug("SONO NATO");
    }
    @Override
    public void onReceive(Object message) throws Throwable {


        Logger.error("*******************msg ricevuto");
        if(message instanceof compileMessage)
        {
            Logger.info("-------CAZZO MI STANNO SCHIAVIZZANDO " + getSelf().toString());
        }



        ProcessBuilder pb = new ProcessBuilder("conta");

        Map<String, String> env = pb.environment();
        // If you want clean environment, call env.clear() first
        // env.clear()
        env.put("VAR1", "myValue");
        env.remove("OTHERVAR");
        env.put("VAR2", env.get("VAR1") + "suffix");

        File workingFolder = new File("/home/enrico");
        pb.directory(workingFolder);

        Process proc = pb.start();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        // read the output from the command
        String s = null;
        while ((s = stdInput.readLine()) != null)
        {
            Logger.warn(s);
        }

        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null)
        {
            Logger.error(s);
        }


        //getSelf().tell("sono un compilatore",getSender());
    }
}
