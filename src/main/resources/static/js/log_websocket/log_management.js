var stompClient = null;



$(document).ready(function () {
    //$('#btn_open_socket').on("click",openSocket());  //给按钮绑定事件
    //$('#btn_close_socket').on("click",closeSocket());
    openSocket();
});




function openSocket() {
    if(stompClient==null){
        var hostname = location.hostname;
        var port = location.port;
        var socket = new SockJS("http://"+hostname+":"+port+"/html/home/log?token=kl");
        stompClient = Stomp.over(socket);
        stompClient.connect({token:"kl"}, function(frame) {

            stompClient.subscribe('/topic/pullFileLogger', function(event) {
                var content=event.body;
                $("#filelog-container div").append(content).append("<br/>");
                $("#filelog-container").scrollTop($("#filelog-container div").height() - $("#filelog-container").height());
            },{
                token:"kltoen"
            });
        });
    }
}

function closeSocket() {
    if (stompClient != null) {
        stompClient.disconnect();
        stompClient=null;
    }
}






