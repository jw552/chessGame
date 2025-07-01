import React, { useState, useMemo } from 'react';
import './ChessBoard.css';

function getPieceColorClass(symbol) {
    const whitePieces = ['♙', '♖', '♘', '♗', '♕', '♔'];
    const blackPieces = ['♟', '♜', '♞', '♝', '♛', '♚'];
    if (whitePieces.includes(symbol)) return 'white-piece';
    if (blackPieces.includes(symbol)) return 'black-piece';
    return '';
}

function ChessBoard({ board, turn, selected, onSquareClick, playerIsWhite }) {
    const [validMoves, setValidMoves] = useState([]);

    const isYourPiece = (symbol) => {
        const isWhiteTurn = turn === 'White';
        const isHumanTurn =
            (playerIsWhite && isWhiteTurn) || (!playerIsWhite && !isWhiteTurn);

        if (!isHumanTurn) return false;

        const whitePieces = ['♙', '♖', '♘', '♗', '♕', '♔'];
        const blackPieces = ['♟', '♜', '♞', '♝', '♛', '♚'];

        return playerIsWhite
            ? whitePieces.includes(symbol)
            : blackPieces.includes(symbol);
    };

    const fetchValidMoves = async (row, col) => {
        try {
            const sessionId = localStorage.getItem("sessionId"); // safely retrieve
            const res = await fetch('/api/chess/valid-moves', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ sessionId, row, col })
            });

            const data = await res.json();
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

    const displayBoard = playerIsWhite ? board : [...board].reverse();

    const boardSquares = useMemo(() =>
        board.map((_, row) =>
            board[row].map((_, col) => {
                const displayRow = playerIsWhite ? row : 7 - row;
                const displayCol = playerIsWhite ? col : 7 - col;
                const cell = board[displayRow][displayCol];

                const isLight = (row + col) % 2 === 0;
                const isSelected = selected?.row === displayRow && selected?.col === displayCol;
                const isValid = isValidMoveSquare(displayRow, displayCol);
                const yourPiece = isYourPiece(cell);

                const hoverClass = cell ? (yourPiece ? 'hover-green' : 'hover-red') : '';

                return (
                    <div
                        key={`${row}-${col}`}
                        className={`square ${isLight ? 'light' : 'dark'} ${isSelected ? 'selected' : ''} ${isValid ? 'valid-move' : ''} ${hoverClass}`}
                        onClick={() => {
                            onSquareClick(displayRow, displayCol);
                            if (yourPiece) {
                                if (!selected || selected.row !== displayRow || selected.col !== displayCol) {
                                    fetchValidMoves(displayRow, displayCol);
                                }
                            } else {
                                setValidMoves([]);
                            }
                        }}
                    >
                        <span className={getPieceColorClass(cell)}>{cell || ''}</span>
                    </div>
                );
            })
        ), [board, selected, validMoves, turn, playerIsWhite]
    );

    return <div className="chessboard">{boardSquares}</div>;
}

export default React.memo(ChessBoard);
