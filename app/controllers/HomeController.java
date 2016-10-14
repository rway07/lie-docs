package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.*;
import play.mvc.*;
import views.html.*;
import java.util.ArrayList;
import play.Logger;
import play.api.libs.json.*;
import utils.*;
import java.util.HashMap;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    // Create a new project
    public Result newProject(String name) {
        String queryText = "insert into projects (name) values ('" + name + "');";
        int  result = dbUtil.executeUpdate(queryText);

        if (result != 0) {
            ArrayList<HashMap<String, Object>> data = dbUtil.executeQuery("select max(id) as id from projects;");
            int id = (int)data.get(0).get("id");

            ObjectNode node = play.libs.Json.newObject();
            node.put("id", id);

            return ok(node);
        } else {
            return internalServerError("Error during project creation!");
        }
    }

    // Remove a project
    public Result removeProject(String idProject) {
        int result = 0;
        String query = "delete from files where project = " + idProject + ";";

        // remove all files in the project
        result = dbUtil.executeUpdate(query);

        // remove the project
        query = "delete from projects where id = " + idProject + ";";
        result = dbUtil.executeUpdate(query);

        return ok();
    }

    // Return the main view with the project list
    public Result index() {
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

    // Return the view for file's editing
    public Result editor(String idProject) {
        String projectName;

        // Get the project name
        ArrayList<HashMap<String, Object>> data =
                dbUtil.executeQuery("select name from projects where id = " + idProject + ";");
        projectName = (String)data.get(0).get("name");

        // Get the project's files list
        ArrayList<HashMap<String, Object>> list =
                dbUtil.executeQuery("select id, name from files where project = " + idProject + ";");

        return ok(editor.render("lie-docs prototype", "", idProject, projectName, list));
    }
}
