package org.example.chessgame.model;

public class SessionGame {
    private ChessGame game;
    private long lastAccessTime;

    public SessionGame(ChessGame game) {
        this.game = game;
        this.lastAccessTime = System.currentTimeMillis();
    }

    public ChessGame getGame() {
        this.lastAccessTime = System.currentTimeMillis();
        return game;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }
}
