package com.fufumasi.AdGameServer.db;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository("userDAO")
public class userDAO {
    @Resource(name="sqlSessionTemplate")
    private SqlSessionTemplate session;

    public userVO select(userVO queryUser) {
        userVO user = session.selectOne("fufumasi.selectUser",queryUser);
        return user;
    }
}
