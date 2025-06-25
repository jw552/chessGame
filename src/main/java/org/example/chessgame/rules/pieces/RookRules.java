package org.example.chessgame.rules.pieces;
import org.example.chessgame.model.Position;
import org.example.chessgame.rules.PieceRules;

public class RookRules implements PieceRules {
    public boolean isLegalMove(String[][] board, Position from, Position to) {
        if (from.getRow() != to.getRow() && from.getCol() != to.getCol()) {
            return false; // Rook can only move in straight lines
        }

        int stepRow = Integer.compare(to.getRow(), from.getRow());
        int stepCol = Integer.compare(to.getCol(), from.getCol());
        int r = from.getRow() + stepRow;
        int c = from.getCol() + stepCol;

        while (r != to.getRow() || c != to.getCol()) {
            if (!board[r][c].isEmpty()) {
                return false; // Rook cannot jump over pieces
            }
            r += stepRow;
            c += stepCol;
        }
        return true; // Rook can move to the target position
    }
}
