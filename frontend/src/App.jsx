import React, { useEffect, useRef } from 'react';
import ChessBoard from './components/ChessBoard';
import Sidebar from './components/Sidebar';
import useChessGame from './hooks/useChessGame';
import { pieceToUnicode } from './utils/pieceToUnicode';
import { formatMoveHistory } from './utils/formatMoveHistory';
import './index.css';

function App() {
    const {
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
        setWhiteTime,
        setBlackTime,
        setTurn,
        setBoard,
        setError,
        setSelected,
        fetchStatus,
        setPlayerIsWhite,
        setHistory
    } = useChessGame();

    const whiteRef = useRef(600000);
    const blackRef = useRef(600000);
    const timerRef = useRef(null);

    useEffect(() => {
        fetch('/api/chess/state')
            .then(res => res.json())
            .then(data => {
                const unicodeBoard = data.board.map(row =>
                    row.map(piece => pieceToUnicode(piece))
                );
                setBoard(unicodeBoard);
                setTurn(data.whiteTurn ? 'White' : 'Black');
                setWhiteTime(data.whiteTime);
                setBlackTime(data.blackTime);
                whiteRef.current = data.whiteTime;
                blackRef.current = data.blackTime;

                const formattedHistory = formatMoveHistory(data.history || []);
                setHistory(formattedHistory);

                setPlayerIsWhite(data.playerIsWhite);

                fetchStatus();
            })
            .catch(() => setError('Failed to load game.'));
    }, []);

    useEffect(() => {
        if (isCheckmate) {
            clearInterval(timerRef.current);
            return;
        }

        clearInterval(timerRef.current);
        timerRef.current = setInterval(() => {
            if (turn === 'White') {
                whiteRef.current -= 100;
                setWhiteTime(whiteRef.current);
            } else {
                blackRef.current -= 100;
                setBlackTime(blackRef.current);
            }
        }, 100);
        return () => clearInterval(timerRef.current);
    }, [turn, isCheckmate]);

    const handleReset = () => {
        fetch('/api/chess/reset', { method: 'POST' })
            .then(() => {
                clearInterval(timerRef.current);
                setSelected(null);
                setWhiteTime(600000);
                setBlackTime(600000);

                return fetch('/api/chess/state');
            })
            .then(res => res.json())
            .then(data => {
                const unicodeBoard = data.board.map(row =>
                    row.map(piece => pieceToUnicode(piece))
                );
                setBoard(unicodeBoard);
                setTurn(data.whiteTurn ? 'White' : 'Black');
                setWhiteTime(data.whiteTime);
                setBlackTime(data.blackTime);
                whiteRef.current = data.whiteTime;
                blackRef.current = data.blackTime;

                const formattedHistory = formatMoveHistory(data.history || []);
                setHistory(formattedHistory);
                setPlayerIsWhite(data.playerIsWhite);
                fetchStatus();
            })
            .catch((err) => {
                console.error("Reset failed.", err);
                setError('Reset failed.');
            });
    };

    return (
        <div className="app-container">
            <div className="board-and-captures">
                <div className="capture-zone captured-pieces">
                    {blackCaptures.map((p, i) => (
                        <span key={i} className="black-piece">{p}</span>
                    ))}
                </div>
                <ChessBoard
                    board={board}
                    selected={selected}
                    turn={turn}
                    onSquareClick={handleSquareClick}
                    isFrozen={isCheckmate}
                    playerIsWhite={playerIsWhite}
                />
                <div className="capture-zone captured-pieces">
                    {whiteCaptures.map((p, i) => (
                        <span key={i} className="white-piece">{p}</span>
                    ))}
                </div>
            </div>

            <Sidebar
                turn={turn}
                history={history}
                whiteTime={whiteTime}
                blackTime={blackTime}
                onReset={handleReset}
                whiteCaptures={whiteCaptures}
                blackCaptures={blackCaptures}
            />

            {isCheckmate && (
                <div className="popup">
                    <div className="popup-content">
                        <h2>Checkmate</h2>
                        <p>{winner} wins the game.</p>
                        <button onClick={handleReset}>Play Again</button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default App;
