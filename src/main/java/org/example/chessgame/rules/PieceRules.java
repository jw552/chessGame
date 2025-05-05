package org.example.chessgame.rules;

import org.example.chessgame.model.Position;

/**
 * The PieceRules interface defines the contract for implementing movement rules for chess pieces.
 * Classes implementing this interface should provide the logic to determine if a move is legal
 * based on the piece's movement capabilities and the current state of the board.
 */

public interface PieceRules {
    boolean isLegalMove(String[][] board, Position from, Position to);
}
