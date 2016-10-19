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


        //comandi gcc:
        // gcc -c file.c -o file.o per ogni file c
        // gcc -o prog_name *.o

        if(message instanceof compileMessage)
        {

            ProcessBuilder pb = new ProcessBuilder("/home/enrico/conta");

            Map<String, String> env = pb.environment();
            // If you want clean environment, call env.clear() first
            // env.clear()
            //env.put("VAR1", "myValue");
            //env.remove("OTHERVAR");
            //env.put("VAR2", env.get("VAR1") + "suffix");

            //File workingFolder = new File("/home/enrico");
            //pb.directory(workingFolder);

            Process proc = pb.start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            Logger.info("Inizio lettura da stdInput");
            String s = null;

            while (proc.isAlive()) {

                if(stdInput.ready())
                {
                    Logger.info("WORKER: invio aggiornamento");
                    s = stdInput.readLine();
                    getSender().tell(new updateCompile().setStatus(s).setSender(((compileMessage) message).getSender()),getSelf());
                }
            }

            Logger.info("PROCESSO terminato");
            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null)
            {
                Logger.error(s);
            }

        }

        //getSelf().tell("sono un compilatore",getSender());
    }
}
