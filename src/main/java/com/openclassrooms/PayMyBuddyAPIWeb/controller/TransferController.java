package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransferController {

    @GetMapping("/transfer")
    public String showTransferPage() {
        return "transfer"; // correspond à transfer.html dans /templates
    }
}
