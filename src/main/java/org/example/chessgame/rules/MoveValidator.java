package org.example.chessgame.rules;
import org.example.chessgame.model.Position;
import org.example.chessgame.rules.pieces.*;

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

    public static boolean isInCheck(String[][] board, boolean isWhite) {
        Position kingPos = null;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board[row][col];
                if (piece == null || piece.isEmpty()) continue;
                if (isWhite && piece.equals("K")) kingPos = new Position(row, col);
                if (!isWhite && piece.equals("k")) kingPos = new Position(row, col);
            }
        }

        if (kingPos == null) return true;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board[row][col];
                if (piece == null || piece.isEmpty()) continue;

                boolean enemy = isWhite ? Character.isLowerCase(piece.charAt(0)) : Character.isUpperCase(piece.charAt(0));
                if (!enemy) continue;

                Position from = new Position(row, col);
                if (isLegalMove(board, piece, from, kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

}
