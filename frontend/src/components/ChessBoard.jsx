import React, { useState } from 'react';
import './ChessBoard.css';

function getPieceColorClass(symbol) {
    const whitePieces = ['♙', '♖', '♘', '♗', '♕', '♔'];
    const blackPieces = ['♟', '♜', '♞', '♝', '♛', '♚'];
    if (whitePieces.includes(symbol)) return 'white-piece';
    if (blackPieces.includes(symbol)) return 'black-piece';
    return '';
}

function ChessBoard({ board, turn, selected, onSquareClick }) {
    const [hovered, setHovered] = useState(null);
    const [validMoves, setValidMoves] = useState([]);

    const isYourPiece = (symbol) => {
        const isWhite = turn === 'White';
        return isWhite
            ? ['♙', '♖', '♘', '♗', '♕', '♔'].includes(symbol)
            : ['♟', '♜', '♞', '♝', '♛', '♚'].includes(symbol);
    };

    const fetchValidMoves = async (row, col) => {
        try {
            const res = await fetch(`/api/chess/valid-moves?row=${row}&col=${col}`);
            const data = await res.json();
            console.log('Valid moves response:', data);

            if (Array.isArray(data)) {
                setValidMoves(data);
            } else {
                setValidMoves([]);
            }
        } catch (err) {
            console.error('Error fetching valid moves:', err);
            setValidMoves([]);
        }
    };

    const isValidMoveSquare = (row, col) =>
        validMoves.some(move => move.row === row && move.col === col);

    return (
        <div className="chessboard">
            {board.map((rowData, row) =>
                rowData.map((cell, col) => {
                    const isLight = (row + col) % 2 === 0;
                    const isSelected = selected?.row === row && selected?.col === col;

                    let extraClass = '';
                    if (hovered?.row === row && hovered?.col === col) {
                        extraClass = isYourPiece(cell) ? 'hover-valid' : 'hover-invalid';
                    } else if (isValidMoveSquare(row, col)) {
                        extraClass = 'valid-move';
                    }

                    return (
                        <div
                            key={`${row}-${col}`}
                            className={`square ${isLight ? 'light' : 'dark'} ${
                                isSelected ? 'selected' : ''
                            } ${extraClass}`}
                            onClick={() => {
                                onSquareClick(row, col);
                                if (isYourPiece(cell)) {
                                    fetchValidMoves(row, col);
                                } else {
                                    setValidMoves([]);
                                }
                            }}
                            onMouseEnter={() => setHovered({ row, col })}
                            onMouseLeave={() => setHovered(null)}
                        >
                            <span className={getPieceColorClass(cell)}>{cell || ''}</span>
                        </div>
                    );
                })
            )}
        </div>
    );
}

export default ChessBoard;
