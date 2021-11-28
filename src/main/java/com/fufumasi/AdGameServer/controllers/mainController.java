package com.fufumasi.AdGameServer.controllers;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;
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

    @Resource(name = "userDAO")
    private com.fufumasi.AdGameServer.db.userDAO dao;
    @GetMapping(value = "/test")
    @ResponseBody
    public String testResponse() {
        return "/test";
    }

    // login api
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

        res.setToken(token.makeJwtToken(user.getName(), user.getEmail()));
        return res;
    }

    // login api
    @PostMapping(value = "/login")
    @ResponseBody
    public responses.userResponse userResponse(HttpServletRequest req) {
        Claims claims = (Claims) req.getAttribute("claims");

        // System.out.println(email + " " + pw + " reached /login");
        responses.userResponse res = new responses.userResponse();

        userVO user = new userVO();
        user.setEmail((String) claims.get("email"));
        user.setName((String) claims.get("name"));
        user = dao.selectUserInfo(user);
        if (user == null)
            return res;

        res.setName(user.getName());
        return res;
    }
}