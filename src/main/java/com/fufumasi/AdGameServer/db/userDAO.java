package com.fufumasi.AdGameServer.db;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository("userDAO")
public class userDAO {
    @Resource(name="sqlSessionTemplate")
    private SqlSessionTemplate session;

    public userVO selectUserLogin(userVO queryUser) {
        return session.selectOne("fufumasi.selectUserLogin", queryUser);
    }

    public userVO selectUserInfo(userVO queryUser) {
        return session.selectOne("fufumasi.selectUserInfo", queryUser);
    }

    public int insertUser(userVO queryUser) {
        return session.insert("fufumasi.insertUser", queryUser);
    }
}
