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
        String target = board[to.getRow()][to.getCol()];

        if (!MoveValidator.isLegalMove(board, piece, from, to)) {
            return false;
        }

        if (piece == null || piece.isEmpty()) return false;

        if (whiteTurn && !Character.isUpperCase(piece.charAt(0))) return false;
        if (!whiteTurn && !Character.isLowerCase(piece.charAt(0))) return false;

        if (target != null && !target.isEmpty()) {
            if (Character.isUpperCase(piece.charAt(0)) == Character.isUpperCase(target.charAt(0))) {
                return false;
            }
        }

        // Deduct time
        long now = System.currentTimeMillis();
        long elapsed = now - lastMoveTime;
        if (whiteTurn) whiteTimeMillis -= elapsed;
        else blackTimeMillis -= elapsed;

        board[to.getRow()][to.getCol()] = piece;
        board[from.getRow()][from.getCol()] = "";

        moveHistory.add(String.format("%s %s%d → %s%d",
                piece,
                (char) ('a' + from.getCol()), 8 - from.getRow(),
                (char) ('a' + to.getCol()), 8 - to.getRow()
        ));

        lastMoveTime = now;
        whiteTurn = !whiteTurn;
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
                if (MoveValidator.isLegalMove(board, piece, from, to)) {
                    String target = board[to.getRow()][to.getCol()];
                    if (target == null || target.isEmpty() ||
                            Character.isUpperCase(piece.charAt(0)) != Character.isUpperCase(target.charAt(0))) {
                        moves.add(to);
                    }
                }
            }
        }
        return moves;
    }
}
