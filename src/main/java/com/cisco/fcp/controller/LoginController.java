package com.cisco.fcp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController extends BasicController {

    @RequestMapping("/login")
    public String login(Model model){
        //返回  templates/login.html
        return "login";
    }

    @RequestMapping("/home")
    public String forward_home(Model model){
        //返回  templates/home.html
        return "home";

    }

}
