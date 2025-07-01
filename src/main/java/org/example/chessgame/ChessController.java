package org.example.chessgame;

import org.example.chessgame.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.chessgame.ai.ChessAI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;



@RestController
@RequestMapping("/api/chess")
public class ChessController {

    private final Map<String, SessionGame> games = new ConcurrentHashMap<>();

    private SessionGame getOrCreateSession(String sessionId) {
        return games.computeIfAbsent(sessionId, id -> new SessionGame(new ChessGame()));
    }

    @PostMapping("/move")
    public ResponseEntity<?> makeMove(@RequestBody MoveRequest move) {
        String sessionId = move.getSessionId();
        ChessGame game = getOrCreateSession(sessionId).getGame();

        if (game.isGameOver()) {
            return ResponseEntity.badRequest().body("Game already over.");
        }

        if ((game.isWhiteTurn() && !game.isPlayerIsWhite()) ||
                (!game.isWhiteTurn() && game.isPlayerIsWhite())) {
            return ResponseEntity.badRequest().body("Not your turn.");
        }

        MoveResponse response = game.makeMove(move);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body("Illegal move.");
        }

        if (game.isPromotionPending()) {
            return ResponseEntity.ok(Map.of("promotion", true));
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/ai-move")
    public ResponseEntity<?> makeAIMove(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        ChessGame game = getOrCreateSession(sessionId).getGame();

        if (game.isGameOver()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Game is over"));
        }

        MoveAI aiMove = ChessAI.findBestMove(game);
        boolean moved = game.makeMove(aiMove);

        if (moved) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("AI move failed.");
        }
    }

    @PostMapping("/state")
    public Map<String, Object> getState(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        ChessGame game = getOrCreateSession(sessionId).getGame();

        Map<String, Object> state = new HashMap<>();
        state.put("board", game.getBoard());
        state.put("playerIsWhite", game.isPlayerIsWhite());
        state.put("whiteTurn", game.isWhiteTurn());
        state.put("whiteTime", game.getWhiteTimeMillis());
        state.put("blackTime", game.getBlackTimeMillis());
        state.put("history", game.getMoveHistory());
        state.put("whiteCaptures", game.getWhiteCaptures());
        state.put("blackCaptures", game.getBlackCaptures());
        return state;
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetGame(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        SessionGame session = getOrCreateSession(sessionId);
        ChessGame game = new ChessGame();
        session = new SessionGame(game);
        games.put(sessionId, session);

        game.setVsAI(true);
        boolean playerIsWhite = Math.random() < 0.5;
        game.setPlayerIsWhite(playerIsWhite);

        if (!playerIsWhite) {
            MoveAI aiMove = ChessAI.findBestMove(game);
            game.makeMove(aiMove);
        }

        Map<String, Object> state = new HashMap<>();
        state.put("board", game.getBoard());
        state.put("playerIsWhite", game.isPlayerIsWhite());
        state.put("whiteTurn", game.isWhiteTurn());
        state.put("whiteTime", game.getWhiteTimeMillis());
        state.put("blackTime", game.getBlackTimeMillis());
        state.put("history", game.getMoveHistory());

        return ResponseEntity.ok(state);
    }

    @PostMapping("/valid-moves")
    public List<Position> getValidMoves(@RequestBody Map<String, Object> body) {
        String sessionId = (String) body.get("sessionId");
        int row = (int) body.get("row");
        int col = (int) body.get("col");

        ChessGame game = getOrCreateSession(sessionId).getGame();
        return game.getValidMoves(new Position(row, col));
    }

    @PostMapping("/status")
    public GameStatus getStatus(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        ChessGame game = getOrCreateSession(sessionId).getGame();
        return game.getStatus();
    }

    @PostMapping("/promote")
    public ResponseEntity<?> promotePawn(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        String piece = body.get("piece");

        if (!Set.of("Q", "R", "B", "N").contains(piece)) {
            return ResponseEntity.badRequest().body("Invalid promotion piece.");
        }

        ChessGame game = getOrCreateSession(sessionId).getGame();
        game.promotePawn(piece);
        return ResponseEntity.ok().build();
    }
}
