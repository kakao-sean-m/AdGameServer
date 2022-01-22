package com.fufumasi.AdGameServer.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GameVO {
    private String player1;
    private String player2;
    private Timestamp gameTime;
    private String winner;

    // for GameHandler
    private WebSocketSession session1;
    private WebSocketSession session2;
    private int[] choice1;
    private int[] choice2;
    private boolean ready;
    private int stage;
}
