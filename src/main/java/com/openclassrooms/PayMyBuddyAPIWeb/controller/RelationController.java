package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RelationController {

    @GetMapping("/relation")
    public String showRelationPage() {
        return "relation"; // correspond à relation.html dans /templates
    }
}