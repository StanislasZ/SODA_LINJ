$(document).ready(function () {

    $('#sidebar_roadcheck_a').on('click',stu_menu_click());
    $('#sidebar_safe_generate_a').on('click',stu_menu_click());

    $('#sidebar_roadcheck2_a').on('click',police_menu_click());
    $('#sidebar_maxv_a').on('click',police_menu_click());



});


function stu_menu_click() {
    console.log('sfsefsef');

    $(".nav").find(".active").removeClass("active");
    $('#sidebar_student_a').addClass("active");
}

function police_menu_click() {
    console.log('sfsefsef');
    $(".nav").find(".active").removeClass("active");
    $('#sidebar_police_a').addClass("active");

}