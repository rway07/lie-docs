/**
 * Created by enrico on 25/10/16.
 */
$.fn.notify = function(param)
{
    param["who"]=this;

    $(this).removeClass($(this).attr("class")).addClass("col-md-8 col-md-offset-3 fixedDialog alert alert-" + param.class)
    $(this).html(param.html);
    $(this).slideDown();

    if(param.duration > 0)
      setTimeout(function(param){ $(param.who).slideUp(); },param.duration,param);
};