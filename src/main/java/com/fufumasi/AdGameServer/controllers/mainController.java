package com.fufumasi.AdGameServer.controllers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class mainController {
    //access '/'
    @GetMapping(value = "/")
    @ResponseBody
    public String unautorizedResponse() {
        return "Unauthorized Access";
    }

    //login api
    @GetMapping(value = "/login")
    @ResponseBody
    public loginResponse loginResponse() {
        loginResponse res = new loginResponse();
        res.token = "testToken";
        return res;
    }

    @Getter
    @Setter
    public static class loginResponse {
        private String token;
    }
}