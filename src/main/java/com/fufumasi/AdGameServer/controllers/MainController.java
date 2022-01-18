package com.fufumasi.AdGameServer.controllers;

import com.fufumasi.AdGameServer.db.UserDAO;
import com.fufumasi.AdGameServer.services.EmailHandler;
import com.fufumasi.AdGameServer.services.MainService;
import com.fufumasi.AdGameServer.services.TokenHandler;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import com.fufumasi.AdGameServer.db.UserVO;

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
    public Responses.loginResponse loginResponse(HttpServletRequest req) {
        String email = req.getParameter("email");
        String pw = req.getParameter("pw");

        Responses.loginResponse res = new Responses.loginResponse();
        res.setToken(this.mainService.login_idpw(email, pw));
        return res;
    }

    /***
     * Post /login
     * sign up user
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public Responses.signupResponse signupResponse(HttpServletRequest req) {
        String email = req.getParameter("email");
        String pw = req.getParameter("pw");
        String name = req.getParameter("name");
        String mobileNum = req.getParameter("phoneNum");

        Responses.signupResponse res = new Responses.signupResponse();
        res.setRes(this.mainService.signup(name, email, pw, mobileNum));
        return res;
    }

    /***
     * Get /main
     * login with a token
     */
    @GetMapping(value = "/main")
    @ResponseBody
    public Responses.userResponse userResponse(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);

        Responses.userResponse res = new Responses.userResponse();
        res.setName(this.mainService.login_token(authorizationHeader));
        return res;
    }
}