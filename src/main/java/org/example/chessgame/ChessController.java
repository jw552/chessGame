package org.example.chessgame;

import org.example.chessgame.model.ChessGame;
import org.example.chessgame.model.MoveRequest;
import org.example.chessgame.model.MoveResponse;
import org.example.chessgame.model.Position;
import org.example.chessgame.model.GameStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.chessgame.ai.ChessAI;
import org.example.chessgame.model.MoveAI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import java.util.Set;



@RestController
@RequestMapping("/api/chess")
public class ChessController {

    private ChessGame game = new ChessGame();

    @PostMapping("/move")
    public ResponseEntity<?> makeMove(@RequestBody MoveRequest move) {
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
    public ResponseEntity<?> makeAIMove() {
        if (game.isGameOver()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Game is over"));
        }

        MoveAI aiMove = ChessAI.findBestMove(game);
        boolean moved = game.makeMove(aiMove);

        if (moved) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("AI move failed.");
        }
    }

    @GetMapping("/state")
    public Map<String, Object> getState() {
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
    public ResponseEntity<Map<String, Object>> resetGame() {
        game.getMoveHistory().clear();
        game.clearInternalHistory();

        game = new ChessGame();
        game.setVsAI(true);

        boolean playerIsWhite = Math.random() < 0.5;
        game.setPlayerIsWhite(playerIsWhite);

        if (!playerIsWhite) {
            MoveAI aiMove = ChessAI.findBestMove(game);
            game.makeMove(aiMove);
        }

        System.out.println("CONTROLLER: game reference = " + game);
        System.out.println("CONTROLLER: AFTER NEW GAME: moveHistory = " + game.getMoveHistory().size());

        Map<String, Object> state = new HashMap<>();
        state.put("board", game.getBoard());
        state.put("playerIsWhite", game.isPlayerIsWhite());
        state.put("whiteTurn", game.isWhiteTurn());
        state.put("whiteTime", game.getWhiteTimeMillis());
        state.put("blackTime", game.getBlackTimeMillis());
        state.put("history", game.getMoveHistory());

        return ResponseEntity.ok(state);
    }


    @GetMapping("/valid-moves")
    public List<Position> getValidMoves(@RequestParam int row, @RequestParam int col) {
        return game.getValidMoves(new Position(row, col));
    }

    @GetMapping("/status")
    public GameStatus getStatus() {
        return game.getStatus();
    }

    @PostMapping("/promote")
    public ResponseEntity<?> promotePawn(@RequestBody Map<String, String> body) {
        String piece = body.get("piece");
        if (!Set.of("Q", "R", "B", "N").contains(piece)) {
            return ResponseEntity.badRequest().body("Invalid promotion piece.");
        }

        game.promotePawn(piece);
        return ResponseEntity.ok().build();
    }

}
