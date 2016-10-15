/**
 * Created by kain on 15/10/16.
 */
$(document).ready(function() {
    $("#newFileButton").on("click", function() {
        var id = $("#projectID").val();
        var name = $("#fileName").val();

        $.ajax({
            url: "/project/" + id + "/file/" + name,
            type: "post",
            error: function(data) {
                console.log(data);
            },
            success: function(data) {
                addFileEntry(data.id, name);
                $("#fileName").val("");
                $("#newFileModal").modal('toggle');
            }
        });
    });

    fileDeleteEventListener();
});

function fileDeleteEventListener() {
    $(document).on("click", ".remove_file", function() {
        var id = $(this).attr("id");

        $.ajax({
            url: "/file/" + id + "/delete",
            type: "POST",
            error: function(data) {
                console.log(data);
                alert("Error removing the file!");
            },
            success: function() {
                removeFileEntry(id);
            }
        });
    });
}

function addFileEntry(id, name) {
    $("#filesList")
        .append('<div id="container-' + id + '">' +
            '<li><strong> ' + name + ' </strong></li><i id="' + id + '" class="remove_file fa fa-trash"></i>' +
            '</div>');
}

function removeFileEntry(id) {
    var node = "#container-" + id;
    $(node).remove();
}