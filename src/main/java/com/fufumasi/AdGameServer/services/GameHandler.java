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
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

/*
 * 동기화 문제 해결 필요.
 */
@Service
@Log4j2
public class GameHandler extends TextWebSocketHandler {
    private static final List<WebSocketSession> waitingQueue = new ArrayList<>();
    private static final List<GameVO> gameList = new ArrayList<>();

    @Inject
    private TokenHandler tokenHandler;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JSONObject req = new JSONObject(message.getPayload());
            JSONObject res = new JSONObject();
            GameVO game;
            String r = req.getString("r");

            switch (r) {
                case "ready":
                    game = lookupGame(session);
                    if (game == null) {
                        res.put("r", "NotFound");
                        session.sendMessage(new TextMessage(res.toString()));
                        return;
                    }

                    // if player2 is bot OR opponent ready
                    if (game.getSession2() == null || game.isReady()) {
                        Timer t = new Timer();
                        GameManager pg = new GameManager(game);

                        res.put("op", "BOT");
                        session.sendMessage(new TextMessage(res.toString()));
                        t.schedule(pg, new Date(System.currentTimeMillis()+5000));
                    } else {
                        game.setReady(true);
                    }
                    break;
                case "card":
                    game = lookupGame(session);
                    if (game == null || !game.isReady()) {
                        res.put("r", "NotFound");
                        session.sendMessage(new TextMessage(res.toString()));
                        return;
                    }
                    int choice = Integer.parseInt(req.getString("choice"));
                    if (choice > 5 || choice < 1)
                        return;
                    if (game.getSession1() == session) {
                        int[] choices = game.getChoice1();
                        if (IntStream.of(choices).anyMatch(x -> x == choice)) // if already chosen, return
                            return;
                        choices[game.getStage()] = choice;
                        game.setChoice1(choices);
                    } else {
                        int[] choices = game.getChoice2();
                        if (IntStream.of(choices).anyMatch(x -> x == choice))
                            return;
                        choices[game.getStage()] = choice;
                        game.setChoice2(choices);
                    }

                    break;
                case "single":
                    waitingQueue.remove(session);
                    Claims claims = tokenHandler.parseJwtToken(req.getString("token"));
                    Date date = new Date();

                    int[] choice1 = {0, 0, 0, 0, 0};
                    int[] choice2 = {0, 0, 0, 0, 0};
                    gameList.add(new GameVO((String) claims.get("nickname"),
                                            "BOT",
                                            new java.sql.Timestamp(date.getTime()),
                                            0,
                                            session,
                                            null,
                                            choice1,
                                            choice2,
                                            false,
                                            0));
                    res.append("r", "ok");
                    session.sendMessage(new TextMessage(res.toString()));
                    break;
                default:
                    res.append("r", "NotFound");
                    session.sendMessage(new TextMessage(res.toString()));
                    break;
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

class GameManager extends TimerTask {
    private final GameVO game;

    GameManager(GameVO game) {
        super();
        this.game = game;
    }

    @Override
    public void run() {
        Timer t = new Timer();
        int tLeft;
        JSONObject object = new JSONObject();
        Random rd = new Random();

        try {
            while (game.getStage() < 5) {
                tLeft = 10;
                while (tLeft >= 0) {
                    object.clear();
                    object.put("timeleft", String.format("%d", tLeft--));
                    game.getSession1().sendMessage(new TextMessage(object.toString()));
                    if (game.getSession2() != null)
                        game.getSession2().sendMessage(new TextMessage(object.toString()));
                    Thread.sleep(1000);
                }
                if (game.getChoice1()[game.getStage()] == 0) {
                    ArrayList<Integer> cLeft = new ArrayList<>();
                    int[] choices;
                    for (int i = 1; i <= 5; i++)
                        cLeft.add(i);
                    choices = game.getChoice1();
                    for (int c : choices)
                        cLeft.remove((Integer) c);
                    choices[game.getStage()] = cLeft.get(rd.nextInt(cLeft.size()));
                    game.setChoice1(choices);
                }
                if (game.getChoice2()[game.getStage()] == 0) {
                    ArrayList<Integer> cLeft = new ArrayList<>();
                    int[] choices;
                    for (int i = 1; i <= 5; i++)
                        cLeft.add(i);
                    choices = game.getChoice2();
                    for (int c : choices)
                        cLeft.remove((Integer) c);
                    choices[game.getStage()] = cLeft.get(rd.nextInt(cLeft.size()));
                    game.setChoice2(choices);
                }

                if (game.getChoice1()[game.getStage()] > game.getChoice2()[game.getStage()])
                    game.setWinner(game.getWinner() + 1);
                else if (game.getChoice1()[game.getStage()] < game.getChoice2()[game.getStage()])
                    game.setWinner(game.getWinner() - 1);

                object.clear();
                object.put("player", game.getChoice1()[game.getStage()]);
                object.put("opponent", game.getChoice2()[game.getStage()]);
                game.getSession1().sendMessage(new TextMessage(object.toString()));
                if (game.getSession2() != null) {
                    object.clear();
                    object.put("player", game.getChoice2()[game.getStage()]);
                    object.put("opponent", game.getChoice1()[game.getStage()]);
                    game.getSession2().sendMessage(new TextMessage(object.toString()));
                }
                game.setStage(game.getStage() + 1);
                Thread.sleep(4500);
            }
            if (game.getWinner() > 0) {
                object.clear();
                object.put("result", "win");
                game.getSession1().sendMessage(new TextMessage(object.toString()));
                if (game.getSession2() != null) {
                    object.put("result", "lose");
                    game.getSession2().sendMessage(new TextMessage(object.toString()));
                }
            }
            else if (game.getWinner() < 0) {
                object.clear();
                object.put("result", "lose");
                game.getSession1().sendMessage(new TextMessage(object.toString()));
                if (game.getSession2() != null) {
                    object.put("result", "win");
                    game.getSession2().sendMessage(new TextMessage(object.toString()));
                }
            }
            else {
                object.clear();
                object.put("result", "draw");
                game.getSession1().sendMessage(new TextMessage(object.toString()));
                if (game.getSession2() != null)
                    game.getSession2().sendMessage(new TextMessage(object.toString()));
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
