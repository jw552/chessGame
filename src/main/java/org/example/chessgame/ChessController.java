package org.example.chessgame.controller;

import org.example.chessgame.model.ChessGame;
import org.example.chessgame.model.MoveRequest;
import org.example.chessgame.model.MoveResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
