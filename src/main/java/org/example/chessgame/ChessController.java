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

        if (!game.isGameOver()) {
            MoveAI aiMove = ChessAI.findBestMove(game);
            game.makeMove(aiMove);
        }

        return ResponseEntity.ok(response);
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
        return state;
    }

    @PostMapping("/reset")
    public ResponseEntity<ChessGame> resetGame() {
        game = new ChessGame();
        game.setVsAI(true);

        boolean playerIsWhite = Math.random() < 0.5;
        game.setPlayerIsWhite(playerIsWhite);

        if (!playerIsWhite) {
            MoveAI aiMove = ChessAI.findBestMove(game);
            game.makeMove(aiMove);
        }

        System.out.println("CONTROLLER: AFTER NEW GAME: moveHistory = " + game.getMoveHistory().size());

        return ResponseEntity.ok(game);
    }

    @GetMapping("/valid-moves")
    public List<Position> getValidMoves(@RequestParam int row, @RequestParam int col) {
        return game.getValidMoves(new Position(row, col));
    }

    @GetMapping("/status")
    public GameStatus getStatus() {
        return game.getStatus();
    }
}
