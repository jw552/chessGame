package org.example.chessgame.rules.pieces;
import org.example.chessgame.model.Position;
import org.example.chessgame.rules.PieceRules;

/**
 * The BishopRules class implements the PieceRules interface and provides the logic for validating
 * the movement of a bishop piece in a chess game.
 */

public class BishopRules implements PieceRules {
    public boolean isLegalMove(String[][] board, Position from, Position to) {
        int dr = to.getRow() - from.getRow();
        int dc = to.getCol() - from.getCol();
        if (Math.abs(dr) != Math.abs(dc)) return false;

        int stepRow = Integer.compare(dr, 0);
        int stepCol = Integer.compare(dc, 0);
        int r = from.getRow() + stepRow;
        int c = from.getCol() + stepCol;

        while (r != to.getRow() && c != to.getCol()) {
            if (!board[r][c].isEmpty()) return false;
            r += stepRow;
            c += stepCol;
        }
        return true;
    }
}