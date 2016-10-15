package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.mvc.Controller;
import play.mvc.Result;
import utils.dbUtil;
import java.util.ArrayList;
import java.util.HashMap;
import utils.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class EditorController extends Controller {
    // Create a new file
    public Result newFile(String idProject, String name) {
        ArrayList<HashMap<String, Object>> data;
        int result =
            dbUtil.executeUpdate("insert into files (project, name) values (" + idProject + ", '" + name + "')");

        if (result != 0) {
            data = dbUtil.executeQuery("select id from files where name = '" + name + "' and project = " + idProject + ";");

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

}
