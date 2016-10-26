package controllers;


import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import messages.controllerMessage;
import messages.referendumMessage;
import play.api.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.fileSystem;
import views.html.*;
import utils.dbUtil;
import java.util.ArrayList;
import java.util.HashMap;

import akka.actor.*;
import javax.inject.*;
/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class EditorController extends Controller {

    final ActorRef projectActor;

    @Inject public EditorController(ActorSystem system) {
        projectActor  = DistributedPubSub.get(system).mediator();
    }

    public Result download(String project){
        String publicFolder = Play.current().getFile("/public/executables").toString();
        return ok(fileSystem.readBinary(publicFolder + "/" + project + "/" + project)).as("application/octet-stream");
    }



    // Create a new file
    public Result newFile() {

        DynamicForm form = Form.form().bindFromRequest();
        String projectName = form.data().get("projectName");
        int projectID = Integer.parseInt(form.data().get("projectID"));
        String fileName = form.data().get("fileName");

        ArrayList<HashMap<String, Object>> data;
        int result =
            dbUtil.executeUpdate("insert into files (project, name) values (" + projectID + ", '" + fileName + "')");

        if (result != 0) {
            data = dbUtil.executeQuery("select id from files where name = '" + fileName + "' and project = " + projectID + ";");

            int id = (int)data.get(0).get("id");

            projectActor.tell(new DistributedPubSubMediator.Publish(projectName,new controllerMessage(projectName + " " + fileName)
                    .setAction(controllerMessage.actionEnum.ADD)
                    .setTarget(controllerMessage.targetEnum.FILE)
                    .setTargetID(id)
                    .setContainerName(projectName)
                    .setContainerID(projectID)
                    .setTargetName(fileName)),ActorRef.noSender());

            // Creating json node
            ObjectNode node = play.libs.Json.newObject();
            node.put("id", id);

            return ok(node);
        } else {
            return internalServerError("Error during file creation!");
        }
    }

    // Remove a file
    public Result removeFile() {

        DynamicForm form = Form.form().bindFromRequest();
        String author = form.data().get("author");
        String project = form.data().get("project");
        String file = form.data().get("file");
        int fileID = Integer.parseInt(form.data().get("fileID"));
        int projectID =Integer.parseInt(form.data().get("projectID"));

        HashMap f = (HashMap) (((ArrayList)dbUtil.query("select project from files where id = " + fileID)).get(0));

        projectActor.tell(new DistributedPubSubMediator.Publish(project,new controllerMessage(project + " " + file)
                .setAction(controllerMessage.actionEnum.DELETE)
                .setTarget(controllerMessage.targetEnum.FILE)
                .setTargetID(fileID)
                .setContainerID(projectID)
                .setRequireVote(true)
                .setContainerName(project)
                .setAuthor(author)
                .setTargetName(file)),ActorRef.noSender());

        return ok("DONE");
    }

    public Result execDelete(){
        DynamicForm form = Form.form().bindFromRequest();
        int fileID = Integer.parseInt(form.data().get("fileID"));
        String project = form.data().get("project");
        String file = form.data().get("file");

        int result = dbUtil.executeUpdate("delete from files where id = " + fileID + ";");

        if (result != 0) {
            projectActor.tell(new DistributedPubSubMediator.Publish(project,new controllerMessage(project + " " + file)
                    .setAction(controllerMessage.actionEnum.DELETE)
                    .setTarget(controllerMessage.targetEnum.FILE)
                    .setTargetID(fileID)
                    .setContainerName(project)
                    .setAck(true)),ActorRef.noSender());

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