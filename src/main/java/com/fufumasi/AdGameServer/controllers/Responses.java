package com.fufumasi.AdGameServer.controllers;

import lombok.Getter;
import lombok.Setter;

public class Responses {
    @Getter
    @Setter
    public static class LoginResponse {
        private String token;
    }

    @Getter
    @Setter
    public static class UserResponse {
        private String nickname;
    }

    @Getter
    @Setter
    public static class SignupResponse {
        private String res;
    }

    @Getter
    @Setter
    public static class GameResponse {
        private String res;
    }
}
