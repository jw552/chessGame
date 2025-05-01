package org.example.chessgame.model;

public class MoveResponse {
    private boolean success;
    private MoveRequest move;

    public MoveResponse() {
    }

    public MoveResponse(boolean success, MoveRequest move) {
        this.success = success;
        this.move = move;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MoveRequest getMove() {
        return move;
    }

    public void setMove(MoveRequest move) {
        this.move = move;
    }
}
