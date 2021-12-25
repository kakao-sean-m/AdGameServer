package com.fufumasi.AdGameServer.controllers;

import lombok.Getter;
import lombok.Setter;

public class responses {
    @Getter
    @Setter
    public static class loginResponse {
        private String token;
    }

    @Getter
    @Setter
    public static class userResponse {
        private String name;
    }

    @Getter
    @Setter
    public static class signupResponse {
        private String res;
    }
}
