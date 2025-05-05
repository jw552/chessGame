package org.example.chessgame.model;
import org.example.chessgame.rules.MoveValidator;

public class ChessGame {

    private String[][] board;
    private boolean whiteTurn;

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

        if (piece == null || piece.isEmpty()) {
            return false;
        }

        // Validate turn
        if (whiteTurn && !Character.isUpperCase(piece.charAt(0))) return false;
        if (!whiteTurn && !Character.isLowerCase(piece.charAt(0))) return false;

        if (target != null && !target.isEmpty()) {
            if (Character.isUpperCase(piece.charAt(0)) == Character.isUpperCase(target.charAt(0))) {
                return false;
            }
        }

        // Basic movement: allow any move
        board[to.getRow()][to.getCol()] = piece;
        board[from.getRow()][from.getCol()] = "";

        whiteTurn = !whiteTurn; // switch turns
        return true;
    }

    public String[][] getBoard() {
        return board;
    }
}
