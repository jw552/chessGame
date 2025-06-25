package org.example.chessgame.rules.pieces;
import org.example.chessgame.model.Position;
import org.example.chessgame.rules.PieceRules;

public class PawnRules implements PieceRules {
    public boolean isLegalMove(String[][] board, Position from, Position to) {

        String piece = board[from.getRow()][from.getCol()];

        int dr = to.getRow() - from.getRow();
        int dc = to.getCol() - from.getCol();

        String target = board[to.getRow()][to.getCol()];

        if (piece.equals("P")) {
            if (dc == 0 && dr == -1 && target.isEmpty()) {
                return true; // Pawn move forward
            }
            if (dc == 0 && dr == -2 && from.getRow() == 6 && board[from.getRow() - 1][from.getCol()].isEmpty() && target.isEmpty()) {
                return true; // Pawn double move
            }
            if (Math.abs(dc) == 1 && dr == -1 && !target.isEmpty() && Character.isLowerCase(target.charAt(0))) {
                return true; // Pawn capture
            }
            return false; // Pawn capture
        }

        if (piece.equals("p")) { // Black pawn
            if (dc == 0 && dr == 1 && target.isEmpty()) {
                return true;
            }
            if (dc == 0 && dr == 2 && from.getRow() == 1 && board[from.getRow() + 1][from.getCol()].isEmpty() && target.isEmpty()) {
                return true;
            }
            if (Math.abs(dc) == 1 && dr == 1 && !target.isEmpty() && Character.isUpperCase(target.charAt(0))) {
                return true;
            }
            return false;
        }
        return false;
    }
}
