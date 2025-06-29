package org.example.chessgame.ai;

import org.example.chessgame.model.ChessGame;

public class BoardEvaluator {

    public static int evaluate(ChessGame game) {
        String[][] board = game.getBoard();
        int score = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board[row][col];
                if (piece == null || piece.isEmpty()) continue;

                int baseValue = getPieceValue(piece);
                int positionBonus = getPositionalBonus(piece, row, col);

                score += Character.isUpperCase(piece.charAt(0))
                        ? (baseValue + positionBonus)
                        : -(baseValue + positionBonus);
            }
        }

        if (game.isGameOver()) {
            if (game.getStatus().isCheckmate()) {
                return game.isWhiteTurn() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }
        }

        return score;
    }

    private static int getPieceValue(String piece) {
        return switch (piece.toUpperCase()) {
            case "P" -> 100;
            case "N", "B" -> 300;
            case "R" -> 500;
            case "Q" -> 900;
            case "K" -> 10000;
            default -> 0;
        };
    }

    private static int getPositionalBonus(String piece, int row, int col) {
        if (piece.equalsIgnoreCase("P")) {
            return (Character.isUpperCase(piece.charAt(0))) ? (6 - row) * 10 : (row - 1) * 10;
        }

        if (piece.equalsIgnoreCase("N")) {
            return (3 - Math.abs(3 - row)) + (3 - Math.abs(3 - col));
        }

        return 0;
    }
}
