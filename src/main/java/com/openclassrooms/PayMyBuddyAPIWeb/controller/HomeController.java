package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String showHomePage() {
        return "home"; // correspond à home.html dans /templates
    }
}
