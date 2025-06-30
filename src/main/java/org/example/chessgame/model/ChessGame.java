package org.example.chessgame.model;

import org.example.chessgame.rules.MoveValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Deque;

public class ChessGame {

    private String[][] board;
    private String[][] deepCopyBoard(String[][] original) {
        String[][] copy = new String[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, 8);
        }
        return copy;
    }
    private boolean whiteTurn;
    private int whiteTimeMillis = 600000;
    private int blackTimeMillis = 600000;
    private long lastMoveTime = System.currentTimeMillis();
    private List<String> moveHistory;
    private Deque<MoveRecord> history;
    private boolean vsAI;
    private boolean playerIsWhite;
    private final List<Character> whiteCaptures = new ArrayList<>();
    private final List<Character> blackCaptures = new ArrayList<>();

    public ChessGame() {
        this.board = new String[8][8];
        setupBoard();

        this.moveHistory = new ArrayList<>();
        this.history = new ArrayDeque<>();
        this.whiteTurn = true;
        this.whiteTimeMillis = 10 * 60 * 1000;
        this.blackTimeMillis = 10 * 60 * 1000;
        this.lastMoveTime = System.currentTimeMillis();
        this.vsAI = true;
        this.playerIsWhite = true;

        System.out.println("NEW ChessGame initialized.");
        System.out.println("CONSTRUCTOR: this = " + this);
        System.out.println("CONSTRUCTOR: moveHistory.size() = " + moveHistory.size());
    }

    public void copyFrom(ChessGame other) {
        this.board = deepCopyBoard(other.board);
        this.whiteTurn = other.whiteTurn;
        this.whiteTimeMillis = other.whiteTimeMillis;
        this.blackTimeMillis = other.blackTimeMillis;
        this.vsAI = other.vsAI;
        this.playerIsWhite = other.playerIsWhite;
        this.moveHistory = new ArrayList<>(other.moveHistory);
        this.history = new ArrayDeque<>(other.history);
    }

    private void setupBoard() {
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
    }

    public boolean movePiece(Position from, Position to) {
        String piece = board[from.getRow()][from.getCol()];
        if (piece == null || piece.isEmpty()) return false;

        boolean isWhitePiece = Character.isUpperCase(piece.charAt(0));
        if ((whiteTurn && !isWhitePiece) || (!whiteTurn && isWhitePiece)) {
            return false;
        }

        String target = board[to.getRow()][to.getCol()];
        if (target != null && !target.isEmpty()) {
            if (Character.isUpperCase(piece.charAt(0)) == Character.isUpperCase(target.charAt(0))) {
                return false;
            }
        }

        if (!MoveValidator.isLegalMove(board, piece, from, to)) {
            return false;
        }

        String originalFrom = board[from.getRow()][from.getCol()];
        String originalTo = board[to.getRow()][to.getCol()];
        if (target != null && !target.isEmpty()) {
            char captured = target.charAt(0);
            if (whiteTurn) {
                whiteCaptures.add(captured);
            } else {
                blackCaptures.add(captured);
            }
        }
        board[to.getRow()][to.getCol()] = piece;
        board[from.getRow()][from.getCol()] = "";

        boolean stillInCheck = MoveValidator.isInCheck(board, whiteTurn);
        if (stillInCheck) {
            board[from.getRow()][from.getCol()] = originalFrom;
            board[to.getRow()][to.getCol()] = originalTo;
            return false;
        }

        history.add(new MoveRecord(from, to, originalTo));

        long now = System.currentTimeMillis();
        if (whiteTurn) {
            whiteTimeMillis -= (now - lastMoveTime);
        } else {
            blackTimeMillis -= (now - lastMoveTime);
        }

        lastMoveTime = now;
        whiteTurn = !whiteTurn;

        moveHistory.add(String.format("%s %s%d → %s%d",
                piece,
                (char) ('a' + from.getCol()), 8 - from.getRow(),
                (char) ('a' + to.getCol()), 8 - to.getRow()
        ));

        return true;
    }

    public void setVsAI(boolean vsAI) {
        this.vsAI = vsAI;
    }

    public boolean isVsAI() {
        return vsAI;
    }

    public void setPlayerIsWhite(boolean isWhite) {
        this.playerIsWhite = isWhite;
    }

    public boolean isPlayerIsWhite() {
        return playerIsWhite;
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

    public List<Character> getWhiteCaptures() {
        return whiteCaptures;
    }

    public List<Character> getBlackCaptures() {
        return blackCaptures;
    }



    public List<String> getMoveHistory() {
        System.out.println("⚠️ getMoveHistory called. Current size: " + moveHistory.size());
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
                if (!MoveValidator.isLegalMove(board, piece, from, to)) continue;

                String target = board[to.getRow()][to.getCol()];
                if (target != null && !target.isEmpty() &&
                        Character.isUpperCase(piece.charAt(0)) == Character.isUpperCase(target.charAt(0))) {
                    continue;
                }

                String originalFrom = board[from.getRow()][from.getCol()];
                String originalTo = board[to.getRow()][to.getCol()];
                board[to.getRow()][to.getCol()] = piece;
                board[from.getRow()][from.getCol()] = "";

                boolean stillInCheck = MoveValidator.isInCheck(board, isWhite);

                board[from.getRow()][from.getCol()] = originalFrom;
                board[to.getRow()][to.getCol()] = originalTo;

                if (!stillInCheck) {
                    moves.add(to);
                }
            }
        }
        return moves;
    }

    public GameStatus getStatus() {
        boolean isWhite = whiteTurn;
        boolean inCheck = MoveValidator.isInCheck(board, isWhite);

        boolean hasLegalMove = false;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board[row][col];
                if (piece == null || piece.isEmpty()) continue;
                if (Character.isUpperCase(piece.charAt(0)) != isWhite) continue;

                Position pos = new Position(row, col);
                if (!getValidMoves(pos).isEmpty()) {
                    hasLegalMove = true;
                    break;
                }
            }
            if (hasLegalMove) break;
        }

        boolean isCheckmate = inCheck && !hasLegalMove;
        String winner = isCheckmate ? (isWhite ? "Black" : "White") : null;

        return new GameStatus(inCheck, isCheckmate, winner);
    }

    public boolean isGameOver() {
        return MoveValidator.isCheckmate(board, whiteTurn) || MoveValidator.isStalemate(board, whiteTurn);
    }

    public List<MoveAI> getAllLegalMoves() {
        List<MoveAI> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position from = new Position(row, col);
                String piece = board[row][col];
                if (piece == null || piece.isEmpty()) continue;

                boolean isWhitePiece = Character.isUpperCase(piece.charAt(0));
                if ((whiteTurn && !isWhitePiece) || (!whiteTurn && isWhitePiece)) continue;

                List<Position> destinations = MoveValidator.getLegalDestinations(board, from, whiteTurn);
                for (Position to : destinations) {
                    moves.add(new MoveAI(from, to));
                }
            }
        }
        return moves;
    }

    public boolean makeMove(MoveAI move) {
        return movePiece(move.from(), move.to());
    }

    public MoveResponse makeMove(MoveRequest move) {
        if ((isWhiteTurn() && !playerIsWhite) || (!isWhiteTurn() && playerIsWhite)) {
            return new MoveResponse(false, null);
        }

        boolean success = movePiece(move.getFrom(), move.getTo());
        return new MoveResponse(success, success ? move : null);
    }

    public void clearInternalHistory() {
        if (history != null) history.clear();
    }

    public void undoMove() {
        if (history.isEmpty()) {
            throw new IllegalStateException("No moves to undo.");
        }

        MoveRecord last = history.removeLast();
        String movedPiece = board[last.to().getRow()][last.to().getCol()];

        board[last.from().getRow()][last.from().getCol()] = movedPiece;
        board[last.to().getRow()][last.to().getCol()] = last.capturedPiece();

        whiteTurn = !whiteTurn;
    }
}
