package org.example.chessgame.rules;
import org.example.chessgame.model.Position;
import org.example.chessgame.rules.pieces.*;

/**
 * The MoveValidator class is responsible for validating the legality of a move in a chess game.
 * It uses the PieceRules interface to delegate the validation to specific piece rule classes.
 */

public class MoveValidator {
    public static boolean isLegalMove(String[][] board, String piece, Position from, Position to) {
        PieceRules rules;

        switch (piece.toUpperCase()) {
            case "P" -> rules = new PawnRules();
            case "R" -> rules = new RookRules();
            case "N" -> rules = new KnightRules();
            case "B" -> rules = new BishopRules();
            case "Q" -> rules = new QueenRules();
            case "K" -> rules = new KingRules();
            default -> {
                return false;
            }
        }

        return rules.isLegalMove(board, from, to);
    }
}
