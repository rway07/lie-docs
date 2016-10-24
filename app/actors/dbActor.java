package actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import messages.compileMessage;
import messages.controllerMessage;
import messages.referendumMessage;
import messages.updateReferendum;
import messages.voteReferendum;
import utils.cProject;
import utils.jsonUtil;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by enrico on 17/10/16.
 */
public class dbActor extends UntypedActor {

    public static Props props() {
        return Props.create(dbActor.class);
    }
    private boolean initialized = false;
    private editorModel db = null;


    @Override
    public void onReceive(Object msg) {

        if(msg instanceof compileMessage)
        {
            cProject p = new cProject();
            p = new cProject();

            //getting source files from db
            Iterator sources = db.getSources();
            while(sources.hasNext())
            {
                String srcName = (String)((HashMap)sources.next()).get("name");
                p.addSource(srcName,db.getSource(srcName));
            }
            //carico tutti gli h necessari
            Iterator<String> reqHeaders = p.getHeaders();
            while(reqHeaders.hasNext())
            {
                String header = reqHeaders.next();
                p.addHeaderContent(header,db.getSource(header));
            }

            ((compileMessage) msg).projectCreated = true;
            ((compileMessage) msg).setCProject(p);
            getSender().tell(msg,getSelf());

            return;
        }

        jsonUtil m = new jsonUtil((String)msg);
        jsonUtil resp = new jsonUtil("");

        switch((String)m.get("action")){
            case "addChar": {
                db.addChar((String)m.get("file"),(int)(long) m.get("r"),(int)(long) m.get("c"),(String)m.get("chr"));
                break;
            }
            case "removeChar":{
                db.removeChar((String)m.get("file"),(int)(long) m.get("r"),(int)(long) m.get("c"));
                break;
            }
            case "removeRowBackspace":{
                db.removeRowBackspace((String)m.get("file"),(int)(long) m.get("r"),(int)(long) m.get("c"));
                break;
            }
            case "removeRowCanc":{
                db.removeRowCanc((String)m.get("file"),(int)(long) m.get("r"),(int)(long) m.get("c"));
                break;
            }
            case "addRowMoveText":{
               db.addRowMoveText((String)m.get("file"),(int)(long) m.get("r"),(int)(long) m.get("c"));
               break;
            }
            case "addRowNoMoveText":{
                db.addRowNoMoveText((String)m.get("file"),(int)(long) m.get("r"));
                break;
            }
            case "init":{
                if(!initialized)
                {
                    initialized = true;
                    db = new editorModel((String)m.get("project"));
                }
                resp.put("action","init");
                resp.put("ack","");
                break;
            }
            case "open":{
                int id = 0;
                ArrayList  rows = db.getRows((String)m.get("file"));
                if(rows.size() > 0){
                    Iterator irow = rows.iterator();
                    resp.put("fn","open");
                    while(irow.hasNext())
                    {
                        HashMap row = (HashMap) irow.next();
                        id = (int)row.get("id");
                        ArrayList rowChars = db.getChars(id);
                        Iterator ichar = rowChars.iterator();
                        String str = "";
                        while(ichar.hasNext())
                        {
                            HashMap c = (HashMap) ichar.next();
                            str += (String)c.get("value");
                        }
                        jsonUtil jrow = new jsonUtil("");
                        jrow.put("idx",row.get("idx"));
                        jrow.put("str",str);

                        resp.add("rows",jrow.getObject());
                    }
                }else{
                    //file vuoto oppure nuovo
                    db.addRowNoMoveText((String)m.get("file"),0);
                    jsonUtil jrow = new jsonUtil("");
                    jrow.put("idx","0");
                    jrow.put("str","");
                    resp.add("rows",jrow.getObject());
                }

            }
        }
        getSender().tell(resp.toString(),getSelf());
    }


}
