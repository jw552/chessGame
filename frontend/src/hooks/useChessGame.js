import { useState } from 'react';
import { pieceToUnicode } from '../utils/pieceToUnicode'; // adjust path as needed
import { formatMoveHistory } from '../utils/formatMoveHistory'; // adjust path as needed

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
            .then(res => res.json())
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
                            setHistory(formatMoveHistory(state.history));
                            setSelected(null);
                            fetchStatus();
                        });
                } else {
                    setError('Illegal move');
                    setSelected(null);
                }
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
        handleSquareClick,
        setBoard,
        setTurn,
        setWhiteTime,
        setBlackTime,
        setSelected,
        setError,
        fetchStatus
    };
}

export default useChessGame;
