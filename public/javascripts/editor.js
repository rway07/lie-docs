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


        window.acceptRef = 0;
        window.declineRef = 0;
        window.quorum = Math.ceil(parseInt(active)/2);

        modal.find("#refStat").show();
        modal.find("#activeEditors").text(parseInt(active));
        modal.find("#quorum").text(quorum);


        modal.find("#acceptReferendum").text(acceptRef);
        modal.find("#discardReferendum").text(declineRef);

        modal.find("#refResult").html("").show();

        modal.find("#fail").unbind("click").on('click',function(){modal.modal('hide')}).text("Close").show();

        if(active > 0)
          modal.find("#success").hide();
        else
        {
            modal.find("#success").unbind('click').on("click",function(){

                $.ajax({
                    url: "/file/execDelete",
                    type: "POST",
                    data: {"fileID":parseInt(modal.find("#fileID").attr("fileid")),
                           "project":$("#project").attr("_projectname"),
                           "file" : fileName},
                    error: function(data) {
                        alert("Error removing the file!");
                        modal.modal("hide");
                    },
                    success: function() {

                        modal.modal("toggle");
                        //alert("File rimosso correttamente");

                    }
                });

            }).show();
        }

        modal.find("#success").text("Proceed Delete").hide();
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