package com.fufumasi.AdGameServer;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/board")
public class mysqlTest {
    // 루트컨테이너에서 빈(Bean)을 받아와서 자동으로 넣어줌
    @Inject
    private SqlSessionFactory sqlFactory;

    // "/list" URI에 대한 요청 처리
    @RequestMapping(value = "/list")
    public String home2(@ModelAttribute ResolverUtil.Test test) {
        try {
            SqlSession session = sqlFactory.openSession();
            System.out.println("성공 : " + session);
        } catch (Exception ex){
            System.out.println("실패..!");
            ex.printStackTrace();
        }

        return "/board/list";
    }
}