package org.example.chessgame.model;

public class GameStatus {
    private boolean check;
    private boolean checkmate;
    private String winner;

    public GameStatus(boolean check, boolean checkmate, String winner) {
        this.check = check;
        this.checkmate = checkmate;
        this.winner = winner;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public String getWinner() {
        return winner;
    }
}
