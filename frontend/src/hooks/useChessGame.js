import { useState } from 'react';
import { pieceToUnicode } from '../utils/pieceToUnicode';
import { formatMoveHistory } from '../utils/formatMoveHistory';

function useChessGame() {
    const [board, setBoard] = useState([]);
    const [turn, setTurn] = useState('White');
    const [selected, setSelected] = useState(null);
    const [error, setError] = useState('');
    const [whiteTime, setWhiteTime] = useState(600000);
    const [blackTime, setBlackTime] = useState(600000);
    const [history, setHistory] = useState([]);
    const [whiteCaptures, setWhiteCaptures] = useState([]);
    const [blackCaptures, setBlackCaptures] = useState([]);
    const [isCheckmate, setIsCheckmate] = useState(false);
    const [winner, setWinner] = useState(null);
    const [playerIsWhite, setPlayerIsWhite] = useState(true);
    const [rawBoard, setRawBoard] = useState([]);



    const handleSquareClick = (row, col) => {
        if (isCheckmate) return;

        const clicked = rawBoard[row]?.[col];

        const isOwnPiece = clicked &&
            ((playerIsWhite && clicked === clicked.toUpperCase()) ||
                (!playerIsWhite && clicked === clicked.toLowerCase()));

        if (!selected) {
            if (isOwnPiece) {
                setSelected({ row, col });
            }
            return;
        }

        if (selected.row === row && selected.col === col) {
            setSelected(null);
            return;
        }

        if (isOwnPiece) {
            setSelected({ row, col });
            return;
        }

        fetch('/api/chess/move', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                from: selected,
                to: { row, col }
            }),
        })
            .then(async res => {
                if (!res.ok) {
                    const text = await res.text();
                    throw new Error(text);
                }
                return res.json();
            })
            .then(data => {
                if (data.success) {
                    fetch('/api/chess/state')
                        .then(res => res.json())
                        .then(state => {
                            setRawBoard(state.board);
                            const unicodeBoard = state.board.map(row =>
                                row.map(piece => pieceToUnicode(piece))
                            );
                            setBoard(unicodeBoard);
                            setWhiteCaptures(state.whiteCaptures || []);
                            setBlackCaptures(state.blackCaptures || []);
                            setTurn(state.whiteTurn ? 'White' : 'Black');
                            setWhiteTime(state.whiteTime);
                            setBlackTime(state.blackTime);
                            setHistory(Array.isArray(state.history)
                                ? formatMoveHistory(state.history)
                                : []);
                            setPlayerIsWhite(state.playerIsWhite);
                            setSelected(null);
                            fetchStatus();
                        });
                } else {
                    setSelected(null);
                }
            })
            .catch(err => {
                console.error(err);
                setSelected(null);
            });
    };

    const fetchStatus = () => {
        fetch('/api/chess/status')
            .then(res => res.json())
            .then(data => {
                setIsCheckmate(data.checkmate);
                setWinner(data.winner);
            });
    };

    return {
        board,
        turn,
        selected,
        error,
        whiteTime,
        blackTime,
        history,
        whiteCaptures,
        blackCaptures,
        isCheckmate,
        winner,
        playerIsWhite,
        handleSquareClick,
        setBoard,
        setTurn,
        setWhiteTime,
        setBlackTime,
        setSelected,
        setError,
        fetchStatus,
        setPlayerIsWhite,
        setHistory,
        setRawBoard,
        setWhiteCaptures,
        setBlackCaptures
    };
}

export default useChessGame;
