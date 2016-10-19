package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
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

        jsonUtil m = new jsonUtil((String)msg);
        jsonUtil resp = new jsonUtil("");

        switch((String)m.get("action")){
            case "addChar": {
                Logger.debug("STEA: char = " + m.get("chr").toString());
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
               Logger.info("dentro");
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

                Logger.info("sono actorDB ricevo OPEN");
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
