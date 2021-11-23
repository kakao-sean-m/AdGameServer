package com.fufumasi.AdGameServer.controllers;

import lombok.Getter;
import lombok.Setter;

public class responses {
    @Getter
    @Setter
    public static class loginResponse {
        private String token;
    }
}
