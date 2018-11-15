package com.cisco.fcp.controller;

import com.cisco.fcp.entity.FieldForSimulator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController extends BasicController {
    
    @RequestMapping("/main")
    public String login(Model model){
        return "login";
    }

    @RequestMapping("/")
    public String index(Model model){

        return "home";
    }

//    @RequestMapping("/home/service")
//    public String service_management(Model model){
//
//        return "service";
//    }

//    @RequestMapping("/home/log")
//    public String log_management(Model model){
//
//        return "log";
//    }


    @RequestMapping("/home/student")
    public String student_management(){

        return "student";
    }

    @RequestMapping("/home/police")
    public String police_management(Model model){

        return "police";
    }

    @RequestMapping("/home/maptest")
    public String maptest(Model model) {

//        return "/trace/test";
        return "test";
    }



}
