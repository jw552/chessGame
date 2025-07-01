import React, { useEffect, useRef } from 'react';
import ChessBoard from './components/ChessBoard';
import Sidebar from './components/Sidebar.jsx';
import useChessGame from './hooks/useChessGame';
import { pieceToUnicode } from './utils/pieceToUnicode';
import { formatMoveHistory } from './utils/formatMoveHistory';
import PromotionModal from './components/PromotionModal';
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
        setHistory,
        setRawBoard,
        setWhiteCaptures,
        setBlackCaptures,
        showPromotion,
        setShowPromotion,
        promotionSquare,
        setPromotionSquare,
        sessionId
    } = useChessGame();

    const whiteRef = useRef(600000);
    const blackRef = useRef(600000);
    const timerRef = useRef(null);

    useEffect(() => {
        fetch('${import.meta.env.VITE_API_BASE}/api/chess/state', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sessionId })
        })
            .then(res => res.json())
            .then(data => {
                setRawBoard(data.board);
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
                setWhiteCaptures(data.whiteCaptures || []);
                setBlackCaptures(data.blackCaptures || []);

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
        fetch('${import.meta.env.VITE_API_BASE}/api/chess/reset', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sessionId })
        })
            .then(() => {
                clearInterval(timerRef.current);
                setSelected(null);
                setWhiteTime(600000);
                setBlackTime(600000);
                setHistory([]);

                return fetch('${import.meta.env.VITE_API_BASE}/api/chess/state', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ sessionId })
                })
            })
            .then(res => res.json())
            .then(data => {
                setRawBoard(data.board);
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
                setWhiteCaptures(data.whiteCaptures || []);
                setBlackCaptures(data.blackCaptures || []);
                fetchStatus();

                const isAITurn = (data.whiteTurn && !data.playerIsWhite) ||
                    (!data.whiteTurn && data.playerIsWhite);
                if (isAITurn) {
                    setTimeout(() => {
                        fetch('${import.meta.env.VITE_API_BASE}/api/chess/ai-move', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ sessionId })
                        })
                            .then(() => fetch('${import.meta.env.VITE_API_BASE}/api/chess/state', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ sessionId })
                            }))
                            .then(res => res.json())
                            .then(aiState => {
                                setRawBoard(aiState.board);
                                const unicode = aiState.board.map(row =>
                                    row.map(piece => pieceToUnicode(piece))
                                );
                                setBoard(unicode);
                                setWhiteCaptures(aiState.whiteCaptures || []);
                                setBlackCaptures(aiState.blackCaptures || []);
                                setTurn(aiState.whiteTurn ? 'White' : 'Black');
                                setWhiteTime(aiState.whiteTime);
                                setBlackTime(aiState.blackTime);
                                setHistory(Array.isArray(aiState.history)
                                    ? formatMoveHistory(aiState.history)
                                    : []);
                                setPlayerIsWhite(aiState.playerIsWhite);
                                setSelected(null);
                                fetchStatus();
                            });
                    }, 200);
                }
            });
    };

    return (
        <div className="app-container">
            <div className="board-and-captures">
                {playerIsWhite ? (
                    <>
                        <div className="capture-zone captured-pieces">
                            {blackCaptures.map((p, i) => (
                                <span key={i} className="black-piece">{pieceToUnicode(p)}</span>
                            ))}
                        </div>
                        <div className="board-wrapper">
                            <ChessBoard
                                board={board}
                                selected={selected}
                                turn={turn}
                                onSquareClick={handleSquareClick}
                                isFrozen={isCheckmate}
                                playerIsWhite={playerIsWhite}
                            />
                        </div>
                        <div className="capture-zone captured-pieces">
                            {whiteCaptures.map((p, i) => (
                                <span key={i} className="white-piece">{pieceToUnicode(p)}</span>
                            ))}
                        </div>
                    </>
                ) : (
                    <>
                        <div className="capture-zone captured-pieces">
                            {whiteCaptures.map((p, i) => (
                                <span key={i} className="white-piece">{pieceToUnicode(p)}</span>
                            ))}
                        </div>
                        <div className="board-wrapper">
                            <ChessBoard
                                board={board}
                                selected={selected}
                                turn={turn}
                                onSquareClick={handleSquareClick}
                                isFrozen={isCheckmate}
                                playerIsWhite={playerIsWhite}
                            />
                        </div>
                        <div className="capture-zone captured-pieces">
                            {blackCaptures.map((p, i) => (
                                <span key={i} className="black-piece">{pieceToUnicode(p)}</span>
                            ))}
                        </div>
                    </>
                )}

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

            {showPromotion && (
                <PromotionModal
                    onSelect={piece => {
                        fetch('${import.meta.env.VITE_API_BASE}/api/chess/promote', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({
                                sessionId,
                                piece
                            })
                        }).
                        then(() => {
                            setShowPromotion(false);
                            setPromotionSquare(null);

                            fetch('${import.meta.env.VITE_API_BASE}/api/chess/state', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ sessionId })
                            })
                                .then(res => res.json())
                                .then(data => {
                                    setRawBoard(data.board);
                                    const unicode = data.board.map(row =>
                                        row.map(p => pieceToUnicode(p))
                                    );
                                    setBoard(unicode);
                                    setWhiteCaptures(data.whiteCaptures || []);
                                    setBlackCaptures(data.blackCaptures || []);
                                    setTurn(data.whiteTurn ? 'White' : 'Black');
                                    setWhiteTime(data.whiteTime);
                                    setBlackTime(data.blackTime);
                                    setHistory(Array.isArray(data.history)
                                        ? formatMoveHistory(data.history)
                                        : []);
                                    setPlayerIsWhite(data.playerIsWhite);
                                    fetchStatus();
                                });
                        });
                    }}
                />
            )}

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
