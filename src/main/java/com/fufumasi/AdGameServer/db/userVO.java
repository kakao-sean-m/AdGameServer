package com.fufumasi.AdGameServer.db;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class userVO {
    private String email;
    private String password;
    private String name;
    private String mobileNum;
    private Date registDate;
    private Date lastLogin;
}