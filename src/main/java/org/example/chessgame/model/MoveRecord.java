package org.example.chessgame.model;

public record MoveRecord(Position from, Position to, String capturedPiece) {
}
