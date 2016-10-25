/**
 * Created by kain on 11/10/16.
 */
$(document).ready(function() {
    $("#newProjectButton").on("click", function(){
        var projectName = $("#projectName").val();

        $.ajax({
            url: '/project/' + projectName + '/new',
            type: 'POST',

            error: function (data) {
                console.log('Error:', data);
                $("#errordiv").html("").append(
                    "<div class='alert alert-danger'><h5><strong>ERRORE!</strong> " + data.status + " "
                    + data.statusText + "</h5></div>");
            },
            success: function (data) {
                window.open('/project/' + data.projectID + '/' + data.fileID, "_self");
            }
        });
    });

    $(".remove_me").on("click", function() {
        var id = $(this).attr('id');

        $.ajax({
            url: '/project/' + id + '/delete',
            type: "DELETE",

            error: function(data) {
                console.log('Error: ' + data);
            },
            success: function(data) {
                element = "#panel-" + id;
                $("#notice").notify({"duration":2000,"class":"success","html":"<i class=\"fa fa-check-circle\"></i>Project marked as post delayed. Will be deleted when no editor are inside"});
                $(element+" *").attr("disabled", "disabled").off('click');
                $(element+" a").bind('click', false);
                $(element).addClass("disabled");
                $(element + " i").removeClass("fa-trash-o").addClass("fa-ban");
            }
        });
    });



});
