package org.example.chessgame.ai;

import org.example.chessgame.model.ChessGame;
import org.example.chessgame.model.MoveAI;
import java.util.List;

public class ChessAI {
    private static final int MAX_DEPTH = 3;

    public static MoveAI findBestMove(ChessGame game) {
        return minimax(game, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true).move();
    }

    private static MoveScore minimax(ChessGame game, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || game.isGameOver()) {
            return new MoveScore(null, BoardEvaluator.evaluate(game));
        }

        List<MoveAI> legalMoves = game.getAllLegalMoves();
        MoveAI bestMove = null;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (MoveAI move : legalMoves) {
                boolean moved = game.makeMove(move);
                if (!moved) continue;

                int eval = minimax(game, depth - 1, alpha, beta, false).score();
                game.undoMove();
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                } else if (eval == maxEval && Math.random() < 0.5) {
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return new MoveScore(bestMove, maxEval);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (MoveAI move : legalMoves) {
                boolean moved = game.makeMove(move);
                if (!moved) continue;

                int eval = minimax(game, depth - 1, alpha, beta, true).score();
                game.undoMove();
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                } else if (eval == minEval && Math.random() < 0.5) {
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return new MoveScore(bestMove, minEval);
        }
    }
}
