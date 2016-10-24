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

        var active = Object.keys(window.activeEditors).length;
        var modal = $("#emptyModal");
        modal.find("#modalTitle").text("Delete Referendum");

        modal.find(".referendumDescription").each(function(idx,obj){$(obj).remove();});

        modal.find("#refDescr").append("<p class=\"referendumDescription\" id=\"referendumSubject\">You are trying to remove the file : " + fileName + " for project " + $("#project").attr("_projectName")+"</p>");
        modal.find("#refDescr").append("<p class=\"referendumDescription\">Since it may be an harmfull action, I have announced a new referedum among all active editors.</p>");
        modal.find("#refDescr").append("<p class=\"referendumDescription\">If the qourum will be get you can proceed otherwise you should ask new votation</p>");

        modal.find("#activeEditors").text(parseInt(active));
        modal.find("#quorum").text(Math.ceil(parseInt(active)/2));

        modal.find("#acceptReferendum").text(0);
        modal.find("#discardReferendum").text(0);

        modal.find("#refResult").html("");

        modal.find("#fail").hide();

        if(active > 0)
          modal.find("#success").hide();
        else
            modal.find("#success").show();

        modal.find("#success").text("Proceed Delete");
        modal.find("#fileID").attr("fileID", fileID);
        modal.modal("show");


        $.ajax({
            url: "/file/delete",
            type: "POST",
            data: {"author":window.editorID,
                   "projectID":$("#projectID").val(),
                   "project":$("#project").attr("_projectName"),
                   "file":fileName,
                   "fileID":fileID},

            success: function() {}
        });
}



function removeFileEntry(id) {
    var node = "#container-" + id;
    $(node).remove();
}