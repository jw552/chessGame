package org.example.chessgame.rules.pieces;
import org.example.chessgame.model.Position;
import org.example.chessgame.rules.PieceRules;

public class KingRules implements PieceRules {
    public boolean isLegalMove(String[][] board, Position from, Position to) {
        int dr = Math.abs(to.getRow() - from.getRow());
        int dc = Math.abs(to.getCol() - from.getCol());
        return dr <= 1 && dc <= 1;
    }
}