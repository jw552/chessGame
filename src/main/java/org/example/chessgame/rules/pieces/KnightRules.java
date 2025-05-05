package org.example.chessgame.rules.pieces;
import org.example.chessgame.model.Position;
import org.example.chessgame.rules.PieceRules;

/**
 * The KnightRules class implements the PieceRules interface and provides the logic for validating
 * the movement of a knight piece in a chess game.
 */

public class KnightRules implements PieceRules {
    public boolean isLegalMove(String[][] board, Position from, Position to) {
        int dr = Math.abs(to.getRow() - from.getRow());
        int dc = Math.abs(to.getCol() - from.getCol());
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }
}