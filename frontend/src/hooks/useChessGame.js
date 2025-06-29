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

    const handleSquareClick = (row, col) => {
        if (isCheckmate) return;

        if (!selected) {
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
                            const unicodeBoard = state.board.map(row =>
                                row.map(piece => pieceToUnicode(piece))
                            );
                            setBoard(unicodeBoard);
                            setTurn(state.whiteTurn ? 'White' : 'Black');
                            setWhiteTime(state.whiteTime);
                            setBlackTime(state.blackTime);
                            if (Array.isArray(state.history) && state.history.length > 0) {
                                setHistory(formatMoveHistory(state.history));
                            } else {
                                setHistory([]);
                            }
                            setPlayerIsWhite(state.playerIsWhite);
                            setSelected(null);
                            fetchStatus();
                        });
                } else {
                    setError('Illegal move');
                    setSelected(null);
                }
            })
            .catch(err => {
                console.error(err);
                setError(err.message || 'Move failed');
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
        setHistory
    };
}

export default useChessGame;
