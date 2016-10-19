package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import utils.dbUtil;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class EditorController extends Controller {
    // Create a new file
    public Result newFile(String idProject, String fileName) {
        ArrayList<HashMap<String, Object>> data;
        int result =
            dbUtil.executeUpdate("insert into files (project, name) values (" + idProject + ", '" + fileName + "')");

        if (result != 0) {
            data = dbUtil.executeQuery("select id from files where name = '" + fileName + "' and project = " + idProject + ";");

            int id = (int)data.get(0).get("id");

            // Creating json node
            ObjectNode node = play.libs.Json.newObject();
            node.put("id", id);

            return ok(node);
        } else {
            return internalServerError("Error during file creation!");
        }
    }

    // Remove a file
    public Result removeFile(String idFile) {
        int result = dbUtil.executeUpdate("delete from files where id = " + idFile + ";");

        if (result != 0) {
            return ok("DONE");
        } else {
            return internalServerError("Errore during file deletion!");
        }
    }

    // Return the view for file's editing
    public Result editor(String idProject, String idFile) {
        String projectName;
        String fileName;

        // Get the project name
        ArrayList<HashMap<String, Object>> projectData =
                dbUtil.executeQuery("select name from projects where id = " + idProject + ";");
        projectName = (String)projectData.get(0).get("name");

        // Get the file name
        ArrayList<HashMap<String, Object>> fileData =
                dbUtil.executeQuery("select name from files where id = " + idFile + ";");
        fileName = fileData.get(0).get("name").toString();

        // Get the project's files list
        ArrayList<HashMap<String, Object>> list =
                dbUtil.executeQuery("select id, name from files where project = " + idProject + ";");

        return ok(editor.render("lie-docs prototype", idProject, projectName, idFile, fileName, list));
    }
}