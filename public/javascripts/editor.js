/**
 * Created by kain on 15/10/16.
 */

function newFile() {
    var id = $("#projectID").val();
    var name = $("#fileName").val();
    var prjName = $("#project").attr("_projectName");

    $.ajax({
        url: "/file/new",
        type: "post",
        data:{"projectID":id,"fileName":name,"projectName":prjName},
        error: function(data) {
            console.log(data);
        },
        success: function(data) {
            $("#fileName").val("");
            $("#newFileModal").modal('toggle');
        }
    });
}



window.fileDeleteEventListener= function(fileID,fileName) {

        window.activeEditors = 0;
        var modal = $("#emptyModal");
        modal.find("#modalTitle").text("Delete Referendum");
        modal.find("#referendumSubject").text("You are trying to remove the file : " + fileName + " for project " + $("#project").attr("_projectName"));
        modal.find("")
        modal.modal("show");

        console.log("************************ NEW REFERENDUM **************************");
        $.ajax({
            url: "/file/delete",
            type: "POST",
            data: {"author":window.editorID,
                   "projectID":$("#projectID").val(),
                   "project":$("#project").attr("_projectName"),
                   "file":fileName,
                   "fileID":fileID},
            error: function(data) {
                console.log(data);
                alert("Error removing the file!");
            },
            success: function() {

                /*removeFileEntry(id);
                if (currentFileID == id) {
                    // Reload page
                    var location = getLocation(window.location.href).host;
                    window.open("http://" + location, "_self");
                }*/
                console.log("openQuorum result");

            }
        });
}



function removeFileEntry(id) {
    var node = "#container-" + id;
    $(node).remove();
}