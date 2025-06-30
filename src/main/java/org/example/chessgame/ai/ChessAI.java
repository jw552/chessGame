package org.example.chessgame.ai;

import org.example.chessgame.model.ChessGame;
import org.example.chessgame.model.MoveAI;

import java.util.List;
import java.util.Random;

public class ChessAI {
    private static final int MAX_DEPTH = 4;
    private static int nodesEvaluated;

    public static MoveAI findBestMove(ChessGame game) {
        nodesEvaluated = 0;
        MoveScore best = minimax(game, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        try {
            int cappedNodes = Math.min(nodesEvaluated, 10000);
            long delay = 300 + (cappedNodes / 10L);
            delay += new Random().nextInt(400);

            if (best.secondBestScore != null && Math.abs(best.score() - best.secondBestScore) < 50) {
                delay += 500;
            }

            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return best.move();
    }

    private static MoveScore minimax(ChessGame game, int depth, int alpha, int beta, boolean maximizingPlayer) {
        nodesEvaluated++;

        if (depth == 0 || game.isGameOver()) {
            return new MoveScore(null, BoardEvaluator.evaluate(game));
        }

        List<MoveAI> legalMoves = game.getAllLegalMoves();
        MoveAI bestMove = null;
        Integer bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Integer secondBestScore = null;

        for (MoveAI move : legalMoves) {
            ChessGame clonedGame = new ChessGame();
            clonedGame.copyFrom(game);
            if (!clonedGame.makeMove(move)) continue;

            int eval = minimax(clonedGame, depth - 1, alpha, beta, !maximizingPlayer).score();

            if (maximizingPlayer) {
                if (eval > bestScore) {
                    secondBestScore = bestScore;
                    bestScore = eval;
                    bestMove = move;
                } else if (eval == bestScore && Math.random() < 0.5) {
                    secondBestScore = bestScore;
                    bestMove = move;
                } else if (secondBestScore == null || eval > secondBestScore) {
                    secondBestScore = eval;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            } else {
                if (eval < bestScore) {
                    secondBestScore = bestScore;
                    bestScore = eval;
                    bestMove = move;
                } else if (eval == bestScore && Math.random() < 0.5) {
                    secondBestScore = bestScore;
                    bestMove = move;
                } else if (secondBestScore == null || eval < secondBestScore) {
                    secondBestScore = eval;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
        }

        return new MoveScore(bestMove, bestScore, secondBestScore);
    }

    private record MoveScore(MoveAI move, int score, Integer secondBestScore) {
        public MoveScore(MoveAI move, int score) {
            this(move, score, null);
        }
    }
}
