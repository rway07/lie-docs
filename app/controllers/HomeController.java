package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.cluster.pubsub.DistributedPubSub;
import akka.dispatch.OnComplete;
import akka.routing.Broadcast;
import akka.util.Timeout;
import com.fasterxml.jackson.databind.node.ObjectNode;
import messages.deleteProject;
import play.Logger;
import play.mvc.*;
import scala.concurrent.Future;
import views.html.*;
import java.util.ArrayList;

import utils.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    ActorSystem system;

    @Inject
    public HomeController(ActorSystem system) {

       this.system = system;
    }
    // Create a new project
    public Result newProject(String name) {
        String queryText = "insert into projects (name) values ('" + name + "');";
        int  result = dbUtil.executeUpdate(queryText);

        if (result != 0) {
            ObjectNode node = play.libs.Json.newObject();
            ArrayList<HashMap<String, Object>> data = dbUtil.executeQuery("select max(id) as id from projects;");
            int id = (int)data.get(0).get("id");
            node.put("projectID", id);

            dbUtil.query("insert into files (`project`,`name`) values ("+id+",'main.c')");
            data = dbUtil.executeQuery("select max(id) as id from files where project = " + id);
            id = (int)data.get(0).get("id");
            node.put("fileID", id);

            return ok(node);
        } else {
            return internalServerError("Error during project creation!");
        }
    }

    // Remove a project
    public Result removeProject(String idProject) {
        HashMap r = ((ArrayList<HashMap>)dbUtil.query("select * from projects where id = " + idProject)).get(0);
        ActorSelection sel = system.actorSelection("akka://application/user/DB" + (String)r.get("name"));
        Future<ActorRef> future = sel.resolveOne(new Timeout(1, TimeUnit.SECONDS));
        // Wait for the completion of task to be completed.
        future.onComplete(new OnComplete<ActorRef>() {
            @Override
            public void onComplete(Throwable excp, ActorRef child)
                    throws Throwable {
                if (excp != null) {
                    int result = 0;
                    String query = "delete from files where project = " + idProject + ";";

                    // remove all files in the project
                    result = dbUtil.executeUpdate(query);

                    // remove the project
                    query = "delete from projects where id = " + idProject + ";";
                    result = dbUtil.executeUpdate(query);

                } else {
                    child.tell(new deleteProject(Integer.parseInt(idProject),(String)r.get("name")),ActorRef.noSender());
                }
            }
        }, system.dispatcher());

        return ok("Project will be deleted when no editors stay there");
    }

    // Return the main view with the project list
    public Result index() {

        //Logger.info("sono qui "+router.toString());
        //router.tell(new Broadcast("Watch out for Davy Jones' locker"),ActorRef.noSender());

        ArrayList projectslist = dbUtil.executeQuery("select id, name from projects");
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        projectslist.forEach((item) -> {
            // Get the current project HashMap
            HashMap<String, Object> current = (HashMap)item;

            // Obtain the files list for the current project
            ArrayList filesList = dbUtil.executeQuery(
                "select id, name from files where project = " + current.get("id").toString());
            HashMap<String, Object> entry = new HashMap<>();
            entry.put("project", current);
            entry.put("files", filesList);
            list.add(entry);

        });
        return ok(index.render("lie-docs prototype", "", list));
    }


}
