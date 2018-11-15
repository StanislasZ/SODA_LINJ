$(document).ready(function () {

    //加载task页面后，马上让task_management加上active
    //console.log('find click.....');
    $(".nav").find(".active").removeClass("active");
    $('#sidebar_task_a').addClass("active");


});