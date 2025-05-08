package org.example.chessgame.controller;

import org.example.chessgame.model.ChessGame;
import org.example.chessgame.model.MoveRequest;
import org.example.chessgame.model.MoveResponse;
import org.example.chessgame.model.Position;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chess")
public class ChessController {

    private final ChessGame game = new ChessGame();

    @PostMapping("/move")
    public ResponseEntity<MoveResponse> move(@RequestBody MoveRequest moveRequest) {
        boolean success = game.movePiece(moveRequest.getFrom(), moveRequest.getTo());
        if (success) {
            return ResponseEntity.ok(new MoveResponse(true, moveRequest));
        } else {
            return ResponseEntity.ok(new MoveResponse(false, null));
        }
    }

    @GetMapping("/state")
    public Map<String, Object> getState() {
        Map<String, Object> state = new HashMap<>();
        state.put("board", game.getBoard());
        state.put("whiteTurn", game.isWhiteTurn());
        state.put("whiteTime", game.getWhiteTimeMillis());
        state.put("blackTime", game.getBlackTimeMillis());
        state.put("history", game.getMoveHistory());
        return state;
    }

    @GetMapping("/valid-moves")
    public List<Position> getValidMoves(@RequestParam int row, @RequestParam int col) {
        return game.getValidMoves(new Position(row, col));
    }
}
