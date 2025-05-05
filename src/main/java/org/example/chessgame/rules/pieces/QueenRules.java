package org.example.chessgame.rules.pieces;
import org.example.chessgame.model.Position;
import org.example.chessgame.rules.PieceRules;

/**
 * The QueenRules class implements the PieceRules interface and provides the logic for validating
 * the movement of a queen piece in a chess game.
 */

public class QueenRules implements PieceRules {
    public boolean isLegalMove(String[][] board, Position from, Position to) {
        return new RookRules().isLegalMove(board, from, to)
                || new BishopRules().isLegalMove(board, from, to);
    }
}
