package org.example.chessgame.model;

import org.example.chessgame.rules.MoveValidator;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {

    private String[][] board;
    private boolean whiteTurn;
    private int whiteTimeMillis = 600000;
    private int blackTimeMillis = 600000;
    private long lastMoveTime = System.currentTimeMillis();
    private final List<String> moveHistory = new ArrayList<>();

    public ChessGame() {
        board = new String[][]{
                {"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}
        };
        whiteTurn = true;
    }

    public boolean movePiece(Position from, Position to) {
        String piece = board[from.getRow()][from.getCol()];
        if (piece == null || piece.isEmpty()) return false;

        boolean isWhitePiece = Character.isUpperCase(piece.charAt(0));
        if ((whiteTurn && !isWhitePiece) || (!whiteTurn && isWhitePiece)) {
            return false;
        }

        String target = board[to.getRow()][to.getCol()];
        if (target != null && !target.isEmpty()) {
            if (Character.isUpperCase(piece.charAt(0)) == Character.isUpperCase(target.charAt(0))) {
                return false;
            }
        }

        if (!MoveValidator.isLegalMove(board, piece, from, to)) {
            return false;
        }

        String originalFrom = board[from.getRow()][from.getCol()];
        String originalTo = board[to.getRow()][to.getCol()];
        board[to.getRow()][to.getCol()] = piece;
        board[from.getRow()][from.getCol()] = "";

        boolean stillInCheck = MoveValidator.isInCheck(board, whiteTurn);
        if (stillInCheck) {
            board[from.getRow()][from.getCol()] = originalFrom;
            board[to.getRow()][to.getCol()] = originalTo;
            return false;
        }

        long now = System.currentTimeMillis();
        if (whiteTurn) {
            whiteTimeMillis -= (now - lastMoveTime);
        } else {
            blackTimeMillis -= (now - lastMoveTime);
        }

        lastMoveTime = now;
        whiteTurn = !whiteTurn;

        moveHistory.add(String.format("%s %s%d → %s%d",
                piece,
                (char) ('a' + from.getCol()), 8 - from.getRow(),
                (char) ('a' + to.getCol()), 8 - to.getRow()
        ));

        return true;
    }

    public String[][] getBoard() {
        return board;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public int getWhiteTimeMillis() {
        return whiteTimeMillis;
    }

    public int getBlackTimeMillis() {
        return blackTimeMillis;
    }

    public List<String> getMoveHistory() {
        return moveHistory;
    }

    public List<Position> getValidMoves(Position from) {
        List<Position> moves = new ArrayList<>();
        String piece = board[from.getRow()][from.getCol()];
        if (piece == null || piece.isEmpty()) return moves;

        boolean isWhite = Character.isUpperCase(piece.charAt(0));
        if ((whiteTurn && !isWhite) || (!whiteTurn && isWhite)) return moves;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position to = new Position(row, col);
                if (!MoveValidator.isLegalMove(board, piece, from, to)) continue;

                String target = board[to.getRow()][to.getCol()];
                if (target != null && !target.isEmpty() &&
                        Character.isUpperCase(piece.charAt(0)) == Character.isUpperCase(target.charAt(0))) {
                    continue;
                }

                String originalFrom = board[from.getRow()][from.getCol()];
                String originalTo = board[to.getRow()][to.getCol()];
                board[to.getRow()][to.getCol()] = piece;
                board[from.getRow()][from.getCol()] = "";

                boolean stillInCheck = MoveValidator.isInCheck(board, isWhite);

                board[from.getRow()][from.getCol()] = originalFrom;
                board[to.getRow()][to.getCol()] = originalTo;

                if (!stillInCheck) {
                    moves.add(to);
                }
            }
        }
        return moves;
    }

    public GameStatus getStatus() {
        boolean isWhite = whiteTurn;
        boolean inCheck = MoveValidator.isInCheck(board, isWhite);

        boolean hasLegalMove = false;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board[row][col];
                if (piece == null || piece.isEmpty()) continue;
                if (Character.isUpperCase(piece.charAt(0)) != isWhite) continue;

                Position pos = new Position(row, col);
                if (!getValidMoves(pos).isEmpty()) {
                    hasLegalMove = true;
                    break;
                }
            }
            if (hasLegalMove) break;
        }

        boolean isCheckmate = inCheck && !hasLegalMove;
        String winner = isCheckmate ? (isWhite ? "Black" : "White") : null;

        return new GameStatus(inCheck, isCheckmate, winner);
    }

}
