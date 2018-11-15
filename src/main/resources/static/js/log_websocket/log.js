$(document).ready(function () {

    //加载log页面后，马上让Log加上active

        //console.log('find click.....');
        $(".nav").find(".active").removeClass("active");
        $('#sidebar_log_a').addClass("active");
});


