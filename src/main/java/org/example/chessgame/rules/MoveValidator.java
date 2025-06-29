package org.example.chessgame.rules;
import org.example.chessgame.model.Position;
import org.example.chessgame.rules.pieces.*;
import java.util.ArrayList;
import java.util.List;

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
    public static boolean isCheckmate(String[][] board, boolean isWhite) {
        if (!isInCheck(board, isWhite)) return false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board[row][col];
                if (piece == null || piece.isEmpty()) continue;
                if (Character.isUpperCase(piece.charAt(0)) != isWhite) continue;

                Position from = new Position(row, col);
                List<Position> moves = getLegalDestinations(board, from, isWhite);
                if (!moves.isEmpty()) return false;
            }
        }
        return true;
    }

    public static boolean isStalemate(String[][] board, boolean isWhite) {
        if (isInCheck(board, isWhite)) return false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board[row][col];
                if (piece == null || piece.isEmpty()) continue;
                if (Character.isUpperCase(piece.charAt(0)) != isWhite) continue;

                Position from = new Position(row, col);
                List<Position> moves = getLegalDestinations(board, from, isWhite);
                if (!moves.isEmpty()) return false;
            }
        }
        return true;
    }

    public static List<Position> getLegalDestinations(String[][] board, Position from, boolean isWhite) {
        List<Position> moves = new ArrayList<>();
        String piece = board[from.getRow()][from.getCol()];
        if (piece == null || piece.isEmpty()) return moves;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position to = new Position(row, col);
                if (!isLegalMove(board, piece, from, to)) continue;

                String target = board[to.getRow()][to.getCol()];
                if (target != null && !target.isEmpty() &&
                        Character.isUpperCase(piece.charAt(0)) == Character.isUpperCase(target.charAt(0))) {
                    continue;
                }

                String originalFrom = board[from.getRow()][from.getCol()];
                String originalTo = board[to.getRow()][to.getCol()];
                board[to.getRow()][to.getCol()] = piece;
                board[from.getRow()][from.getCol()] = "";

                boolean stillInCheck = isInCheck(board, isWhite);

                board[from.getRow()][from.getCol()] = originalFrom;
                board[to.getRow()][to.getCol()] = originalTo;

                if (!stillInCheck) {
                    moves.add(to);
                }
            }
        }
        return moves;
    }
}
