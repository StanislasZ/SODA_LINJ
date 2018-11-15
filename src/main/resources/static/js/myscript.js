var status_running="running";
var status_pause="paused";
var status_stop="ready";
var status_error="error";


//定义一个avalonjs的控制器
var viewmodel = avalon.define({
    //id必须和页面上定义的ms-controller名字相同，否则无法控制页面
    $id: "viewmodel",
    datalist: {},
    datalist_field:{},
    text: "show task list",
    text_field:"show field list",
    cache:false,





    request: function () {

        $.ajax({
            type: "post",
            url: "/html/rowdata",    //向后端请求数据的url
            data: {},
            success: function (data) {
                //console.log('bingo........');
                //$('button').removeClass("btn-primary").addClass("btn-success").attr('disabled', true);
                //console.log(data);
                viewmodel.datalist = data;
                //console.log('succuess'+new Date());


                //修改status内容的颜色
                $("#tasklist").find("td").each(function(index, elem) {
                    if(elem.id=="statusinlist"){
                        //console.log('elem text is '+elem.innerText);
                        //console.log('status_stop is '+status_stop);
                        //console.log('elem==status_stop is '+(elem.innerText==status_stop));
                        //console.log('elem.trim==status_stop is '+(elem.innerText.trim()==status_stop));
                        if(elem.innerText.trim()==status_running){
                            //$(this).attr("class","status_running td_in_tasklist  align-middle");
                            var old_span=elem.firstChild;
                            old_span.setAttribute('class','badge badge-pill badge-success');

                            // var span_node=document.createElement("span");
                            // span_node.className="badge badge-pill badge-success";
                            // span_node.innerText="running";
                            // elem.replaceChild(span_node,old_span);

                        }else if(elem.innerText.trim()==status_pause){
                            //$(this).attr("class","status_pause td_in_tasklist  align-middle");
                            var old_span=elem.firstChild;
                            old_span.setAttribute('class','badge badge-pill badge-warning');

                            // var span_node=document.createElement("span");
                            // span_node.className="badge badge-pill badge-warning";
                            // span_node.innerText="pause";
                            // elem.replaceChild(span_node,old_span);
                        }
                        else if(elem.innerText.trim()==status_stop){

                            //console.log('here is stop... ');
                            var old_span=elem.firstChild;
                            old_span.setAttribute('class','badge badge-pill badge-secondary');
                            // var span_node=document.createElement("span");
                            // span_node.className="badge badge-pill badge-danger";
                            // span_node.innerText="stop";
                            // elem.replaceChild(span_node,old_span)

                        }
                        else if(elem.innerText.trim()==status_error){
                            //$(this).attr("class","status_error td_in_tasklist  align-middle");
                            var old_span=elem.firstChild;
                            old_span.setAttribute('class','badge badge-pill badge-dark');
                            // var span_node=document.createElement("span");
                            // span_node.className="badge badge-pill badge-dark";
                            // span_node.innerText="error";
                            // elem.replaceChild(span_node,old_span);

                        }
                        else{
                            console.log('nothing found....');
                        }
                    }
                });
                //console.log('0823');

                a_list=document.getElementsByName("a_button");


                var action_num;
                var td_ainlist=document.getElementById("ainlist");
                if(td_ainlist!=null){
                    action_num=td_ainlist.childElementCount;
                }

                //var action_num=document.getElementById("ainlist").childElementCount;


                for(var i=0;i<a_list.length;i++){
                    var status=getStatus(a_list[i]).trim();
                    //console.log('status='+status+'...');

                    if(i%action_num==0){  //a_start
                        if(status==status_running||status==status_error){
                            a_list[i].style.display="none";
                        }else{
                            a_list[i].style.display="inline";
                        }
                    }else if(i%action_num==1){   //a_pause
                        if(status==status_running){
                            a_list[i].style.display="inline";
                        }else{
                            a_list[i].style.display="none";

                        }
                    }else if(i%action_num==2){  //a_stop
                        if(status==status_running||status==status_pause){
                            a_list[i].style.display="inline";
                        }else{
                            a_list[i].style.display="none";
                        }
                    }else if(i%action_num==3){   //a_delete
                        a_list[i].style.display="inline";
                        // if(status==status_stop||status==status_error){
                        //     a_list[i].style.display="inline";
                        // }else{
                        //     a_list[i].style.display="none";
                        // }
                    }else if(i%action_num==4){  //a_edit
                        if(status==status_stop||status==status_error){
                            a_list[i].style.display="inline";
                        }else{
                            a_list[i].style.display="none";
                        }
                    }










                }


            }
        });




    },




    sortnum:function () {
        console.log('only sort number.... start');
        //获取table序号
        var tab=document.getElementById("tasklist");
        //获取行数
        var rows=tab.rows;
        //遍历行
        for(var i=1;i<rows.length;i++)
        {
            rows[i].cells[0].innerHTML=i;

        }

        console.log('only sort number.... stops');
    }


});

function getStatus(r) {
    var td_list=r.parentNode.parentNode.cells;
    for(var k=0;k<td_list.length;k++) {

        if (td_list[k].id == "statusinlist") {
            return td_list[k].innerText;
        }
    }
}