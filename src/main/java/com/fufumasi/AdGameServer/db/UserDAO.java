package com.fufumasi.AdGameServer.db;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository("userDAO")
public class UserDAO {
    @Resource(name="sqlSessionTemplate")
    private SqlSessionTemplate session;

    public UserVO selectUserLogin(UserVO queryUser) {
        return session.selectOne("fufumasi.selectUserLogin", queryUser);
    }

    public UserVO selectUserInfo(UserVO queryUser) {
        return session.selectOne("fufumasi.selectUserInfo", queryUser);
    }

    public int insertUser(UserVO queryUser) {
        return session.insert("fufumasi.insertUser", queryUser);
    }
}
