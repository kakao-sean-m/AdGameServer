package com.fufumasi.AdGameServer.controllers;

import com.fufumasi.AdGameServer.db.UserDAO;
import com.fufumasi.AdGameServer.handlers.EmailHandler;
import com.fufumasi.AdGameServer.handlers.TokenHandler;
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
    // access '/'
    @GetMapping(value = "/")
    @ResponseBody
    public String unauthorizedResponse() {
        return "0";
    }

    @Inject
    private EmailHandler email;
    @Resource(name = "userDAO")
    private UserDAO dao;
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
    public TokenHandler token;
    @GetMapping(value = "/login")
    @ResponseBody
    public Responses.loginResponse loginResponse(HttpServletRequest req) {
        String email = req.getParameter("email");
        String pw = req.getParameter("pw");
        // System.out.println(email + " " + pw + " reached /login");
        Responses.loginResponse res = new Responses.loginResponse();
        if (email == null || pw == null) {
            return res;
        }
        UserVO user = new UserVO();
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
    public Responses.userResponse userResponse(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        Claims claims = token.parseJwtToken(authorizationHeader);
        Responses.userResponse res = new Responses.userResponse();
        if (claims == null) {
            System.out.println("claims NULL");
            return res;
        }
        // System.out.printf("token claims email: %s name: %s%n", claims.get("email"), claims.get("name"));

        UserVO user = new UserVO();
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
    public Responses.signupResponse signupResponse(HttpServletRequest req) {
        Responses.signupResponse res = new Responses.signupResponse();
        String email = req.getParameter("email");
        String pw = req.getParameter("pw");
        String name = req.getParameter("name");
        String mobileNum = req.getParameter("phoneNum");

        if (email.length() > 30)
            res.setRes("EMAIL");

        UserVO user = new UserVO();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(pw);
        user.setMobileNum(mobileNum);
        int ret = dao.insertUser(user);

        res.setRes("OK");
        return res;
    }
}