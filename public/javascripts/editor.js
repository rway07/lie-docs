/**
 * Created by kain on 15/10/16.
 */

function newFile() {
    var id = $("#projectID").val();
    var name = $("#fileName").val();

    $.ajax({
        url: "/project/" + id + "/" + name,
        type: "post",
        error: function(data) {
            console.log(data);
        },
        success: function(data) {
            addFileEntry(id, data.id, name);
            $("#fileName").val("");
            $("#newFileModal").modal('toggle');
        }
    });
}

function getLocation(href) {
    var l = document.createElement("a");
    l.href = href;
    return l;
};

function fileDeleteEventListener() {
    $(document).on("click", ".remove_file", function() {
        var id = $(this).attr("id");
        var currentFileID = $("#idFile").val();

        $.ajax({
            url: "/file/" + id + "/delete",
            type: "POST",
            error: function(data) {
                console.log(data);
                alert("Error removing the file!");
            },
            success: function() {
                removeFileEntry(id);
                if (currentFileID == id) {
                    // Reload page
                    var location = getLocation(window.location.href).host;
                    window.open("http://" + location, "_self");
                }
            }
        });
    });
}

function addFileEntry(project_id, id, name) {
    $("#filesList")
        .append('<div id="container-' + id + '">' +
            '<li><strong><a href="/project/' + project_id + '/' + id + '">' +
             name + ' </a></strong></li><i id="' + id + '" onclick="fileDeleteEventListener();"' +
            'class="remove_file fa fa-trash"></i>' +
            '</div>');
}

function removeFileEntry(id) {
    var node = "#container-" + id;
    $(node).remove();
}