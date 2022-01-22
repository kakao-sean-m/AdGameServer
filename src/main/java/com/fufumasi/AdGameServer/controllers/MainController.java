package com.fufumasi.AdGameServer.controllers;

import com.fufumasi.AdGameServer.services.MainService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@RestController
public class MainController {
    @Inject
    private MainService mainService;

    @GetMapping(value = "/test")
    @ResponseBody
    public String testResponse() {
        this.mainService.test();
        return "null";
    }

    /***
     * Get /login
     * login with email and password
     */
    @GetMapping(value = "/login")
    @ResponseBody
    public Responses.LoginResponse loginResponse(HttpServletRequest req) {
        String email = req.getParameter("email");
        String pw = req.getParameter("pw");

        Responses.LoginResponse res = new Responses.LoginResponse();
        res.setToken(this.mainService.login_idpw(email, pw));
        return res;
    }

    /***
     * Post /login
     * sign up user
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public Responses.SignupResponse signupResponse(HttpServletRequest req) {
        String email = req.getParameter("email");
        String pw = req.getParameter("pw");
        String name = req.getParameter("name");
        String mobileNum = req.getParameter("phoneNum");

        Responses.SignupResponse res = new Responses.SignupResponse();
        res.setRes(this.mainService.signup(name, email, pw, mobileNum));
        return res;
    }

    /***
     * Get /main
     * login with a token
     */
    @GetMapping(value = "/main")
    @ResponseBody
    public Responses.UserResponse userResponse(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);

        Responses.UserResponse res = new Responses.UserResponse();
        res.setNickname(this.mainService.login_token(authorizationHeader));
        return res;
    }

    @GetMapping(value = "/game")
    @ResponseBody
    public Responses.GameResponse gameResponse(HttpServletRequest req) {

        Responses.GameResponse res = new Responses.GameResponse();
        res.setRes("");
        return res;
    }
}