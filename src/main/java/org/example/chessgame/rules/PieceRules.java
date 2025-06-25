package org.example.chessgame.rules;

import org.example.chessgame.model.Position;

public interface PieceRules {
    boolean isLegalMove(String[][] board, Position from, Position to);
}
