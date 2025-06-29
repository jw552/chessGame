package org.example.chessgame.model;

public record MoveAI(Position from, Position to) {

    @Override
    public String toString() {
        return from + " -> " + to;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MoveAI)) return false;
        MoveAI other = (MoveAI) obj;
        return from.equals(other.from) && to.equals(other.to);
    }

}
