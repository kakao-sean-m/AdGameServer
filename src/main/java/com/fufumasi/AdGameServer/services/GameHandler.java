package com.fufumasi.AdGameServer.services;

import com.fufumasi.AdGameServer.db.GameVO;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.inject.Inject;
import java.util.*;

/*
 * 동기화 문제 해결 필요.
 */
@Service
@Log4j2
public class GameHandler extends TextWebSocketHandler {
    private static List<WebSocketSession> waitingQueue = new ArrayList<>();
    private static List<GameVO> gameList = new ArrayList<>();

    @Inject
    private TokenHandler tokenHandler;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JSONObject req = new JSONObject(message.getPayload());
            JSONObject res = new JSONObject();
            String r = req.getString("r");

            // game ready
            if (r.equals("ready")) {
                GameVO game = lookupGame(session);

                // game for session not found
                if (game == null) {
                    res.append("r", "reset");
                    session.sendMessage(new TextMessage(res.toString()));
                    return;
                }
                // if player2 is bot OR opponent ready
                if (game.getSession2() == null || game.isReady()) {
                    Timer t = new Timer();
                    ProceedGame pg = new ProceedGame(game);

                    res.append("op", "BOT");
                    t.schedule(pg, new Date(System.currentTimeMillis()+5000));
                } else {
                    game.setReady(true);
                }
                session.sendMessage(new TextMessage(res.toString()));
            }

            // game with bot request
            else if (r.equals("single")) {
                waitingQueue.remove(session);
                Claims claims = tokenHandler.parseJwtToken(req.getString("token"));
                Date date = new Date();
                gameList.add(new GameVO((String) claims.get("nickname"),
                                        "BOT",
                                        new java.sql.Timestamp(date.getTime()),
                                        null,
                                        session,
                                        null,
                                        new int[5],
                                        new int[5],
                                        false,
                                        0));
                res.append("r", "ok");
                session.sendMessage(new TextMessage(res.toString()));
            }
        } catch (Exception e) { // JSON 파싱 에러, 등등 분할 필요
            log.error(e);
        }
    }

    /* Client 접속 시 호출되는 메소드 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(session + " connection established.");
        waitingQueue.add(session);
    }

    /* Client 접속 해제 시 호출되는 메소드 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(session + " connection closed.");
        waitingQueue.remove(session);
    }

    /* session 게임 반환 */
    private GameVO lookupGame(WebSocketSession session) {
        for (GameVO g : gameList)
            if (session == g.getSession1() || session == g.getSession2())
                return g;
        return null;
    }
}

class ProceedGame extends TimerTask {
    private GameVO game;

    ProceedGame(GameVO game) {
        super();
        this.game = game;
    }

    @Override
    public void run() {

    }
}
