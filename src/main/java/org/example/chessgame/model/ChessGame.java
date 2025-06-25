package org.example.chessgame.model;

import org.example.chessgame.rules.MoveValidator;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {

    private String[][] board;
    private boolean whiteTurn;
    private int whiteTimeMillis = 600000;
    private int blackTimeMillis = 600000;
    private long lastMoveTime = System.currentTimeMillis();
    private final List<String> moveHistory = new ArrayList<>();

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
        if (piece == null || piece.isEmpty()) return false;

        boolean isWhitePiece = Character.isUpperCase(piece.charAt(0));
        if ((whiteTurn && !isWhitePiece) || (!whiteTurn && isWhitePiece)) {
            return false;
        }

        // Prevent capturing your own piece
        String target = board[to.getRow()][to.getCol()];
        if (target != null && !target.isEmpty()) {
            boolean isWhiteTarget = Character.isUpperCase(target.charAt(0));
            if (isWhitePiece == isWhiteTarget) {
                return false;
            }
        }

        boolean legal = MoveValidator.isLegalMove(board, piece, from, to);
        if (!legal) return false;

        board[to.getRow()][to.getCol()] = piece;
        board[from.getRow()][from.getCol()] = "";

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

    public List<String> getMoveHistory() {
        return moveHistory;
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

    public List<Position> getValidMoves(Position from) {
        List<Position> moves = new ArrayList<>();
        String piece = board[from.getRow()][from.getCol()];
        if (piece == null || piece.isEmpty()) return moves;

        boolean isWhite = Character.isUpperCase(piece.charAt(0));
        if ((whiteTurn && !isWhite) || (!whiteTurn && isWhite)) return moves;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position to = new Position(row, col);
                if (MoveValidator.isLegalMove(board, piece, from, to)) {
                    String target = board[to.getRow()][to.getCol()];
                    if (target == null || target.isEmpty() ||
                            Character.isUpperCase(piece.charAt(0)) != Character.isUpperCase(target.charAt(0))) {
                        moves.add(to);
                    }
                }
            }
        }
        return moves;
    }

    // -----------------------------
    // ✅ Added Check/Checkmate Logic
    // -----------------------------

    public GameStatus getStatus() {
        boolean inCheck = isKingInCheck(getCurrentTurn());
        boolean hasMoves = hasAnyLegalMoves(getCurrentTurn());
        boolean isCheckmate = inCheck && !hasMoves;
        String winner = isCheckmate ? (getCurrentTurn().equals("White") ? "Black" : "White") : null;

        return new GameStatus(inCheck, isCheckmate, winner);
    }

    private String getCurrentTurn() {
        return whiteTurn ? "White" : "Black";
    }

    private boolean isKingInCheck(String color) {
        Position kingPos = findKing(color);
        return isSquareAttacked(kingPos, color.equals("White") ? "Black" : "White");
    }

    private Position findKing(String color) {
        String kingSymbol = color.equals("White") ? "K" : "k";
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (kingSymbol.equals(board[r][c])) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    private boolean isSquareAttacked(Position pos, String byColor) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String attacker = board[r][c];
                if (attacker == null || attacker.isEmpty()) continue;

                boolean isWhite = Character.isUpperCase(attacker.charAt(0));
                if ((byColor.equals("White") && isWhite) || (byColor.equals("Black") && !isWhite)) {
                    Position from = new Position(r, c);
                    if (MoveValidator.isLegalMove(board, attacker, from, pos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasAnyLegalMoves(String color) {
        for (int r1 = 0; r1 < 8; r1++) {
            for (int c1 = 0; c1 < 8; c1++) {
                String piece = board[r1][c1];
                if (piece == null || piece.isEmpty()) continue;

                boolean isWhite = Character.isUpperCase(piece.charAt(0));
                if ((color.equals("White") && !isWhite) || (color.equals("Black") && isWhite)) continue;

                Position from = new Position(r1, c1);
                List<Position> moves = getValidMoves(from);
                for (Position to : moves) {
                    String temp = board[to.getRow()][to.getCol()];
                    board[to.getRow()][to.getCol()] = piece;
                    board[r1][c1] = "";

                    boolean kingSafe = !isKingInCheck(color);

                    board[r1][c1] = piece;
                    board[to.getRow()][to.getCol()] = temp;

                    if (kingSafe) return true;
                }
            }
        }
        return false;
    }
}
