package com.fufumasi.AdGameServer.controllers;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import com.fufumasi.AdGameServer.db.userVO;

@RestController
public class mainController {
    // access '/'
    @GetMapping(value = "/")
    @ResponseBody
    public String unauthorizedResponse() {
        return "0";
    }

    @Inject
    private emailHandler email;
    @Resource(name = "userDAO")
    private com.fufumasi.AdGameServer.db.userDAO dao;
    @GetMapping(value = "/test")
    @ResponseBody
    public String testResponse() {
        try {
            email.sendMail("secured8372@gmail.com");
        } catch (MessagingException e) {
            System.out.println(e);
        }
        return "null";
    }

    /***
     * Get /login
     * login with email and password
     */
    @Inject
    public tokenHandler token;
    @GetMapping(value = "/login")
    @ResponseBody
    public responses.loginResponse loginResponse(HttpServletRequest req) {
        String email = req.getParameter("email");
        String pw = req.getParameter("pw");
        // System.out.println(email + " " + pw + " reached /login");
        responses.loginResponse res = new responses.loginResponse();
        if (email == null || pw == null) {
            return res;
        }
        com.fufumasi.AdGameServer.db.userVO user = new com.fufumasi.AdGameServer.db.userVO();
        user.setEmail(email);
        user.setPassword(pw);
        user = dao.selectUserLogin(user);
        if (user == null)
            return res;

        res.setToken(token.makeJwtToken(user.getEmail(), user.getName()));
        return res;
    }

    /***
     * Get /main
     * login with a token
     */
    @GetMapping(value = "/main")
    @ResponseBody
    public responses.userResponse userResponse(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        Claims claims = token.parseJwtToken(authorizationHeader);
        responses.userResponse res = new responses.userResponse();
        if (claims == null) {
            System.out.println("claims NULL");
            return res;
        }
        // System.out.printf("token claims email: %s name: %s%n", claims.get("email"), claims.get("name"));

        userVO user = new userVO();
        user.setEmail((String) claims.get("email"));
        user.setName((String) claims.get("name"));
        user = dao.selectUserInfo(user);
        if (user == null)
            return null;
        // System.out.printf("db email: %s name: %s%n", user.getEmail(), user.getName());

        res.setName(user.getName());
        return res;
    }

    /***
     * Post /login
     * sign up user
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public responses.signupResponse signupResponse(HttpServletRequest req) {
        responses.signupResponse res = new responses.signupResponse();
        String email = req.getParameter("email");
        String pw = req.getParameter("pw");
        String name = req.getParameter("name");
        String mobileNum = req.getParameter("phoneNum");

        if (email.length() > 30)
            res.setRes("EMAIL");

        userVO user = new userVO();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(pw);
        user.setMobileNum(mobileNum);
        int ret = dao.insertUser(user);

        res.setRes("OK");
        return res;
    }
}