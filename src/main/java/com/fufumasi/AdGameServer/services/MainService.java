package com.fufumasi.AdGameServer.services;

import com.fufumasi.AdGameServer.controllers.Responses;
import com.fufumasi.AdGameServer.db.UserDAO;
import com.fufumasi.AdGameServer.db.UserVO;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.MessagingException;

@Service
@RequiredArgsConstructor
public class MainService {
    @Inject
    private EmailHandler email;
    @Inject
    public TokenHandler token;
    @Resource(name = "userDAO")
    private UserDAO dao;

    public void test() {
        try {
            email.sendMail("secured8372@gmail.com");
        } catch (MessagingException e) {
            System.out.println(e);
        }
    }

    public String login_idpw(String email, String pw) {
        if (email == null || pw == null)
            return "";
        UserVO user = new UserVO();
        user.setEmail(email);
        user.setPassword(pw);
        user = dao.selectUserLogin(user);
        return token.makeJwtToken(user.getEmail(), user.getName());
    }

    public String login_token(String auth) {
        Claims claims = token.parseJwtToken(auth);

        UserVO user = new UserVO();
        user.setEmail((String) claims.get("email"));
        user.setName((String) claims.get("name"));
        user = dao.selectUserInfo(user);
        if (user == null)
            return "";
        // System.out.printf("db email: %s name: %s%n", user.getEmail(), user.getName());

        return user.getName();
    }

    public String signup(String name, String email, String password, String mobile) {
        if (email.length() > 30)
            return "EMAIL";

        UserVO user = new UserVO();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setMobileNum(mobile);
        dao.insertUser(user);

        return "OK";
    }
}
