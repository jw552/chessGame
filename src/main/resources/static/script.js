document.addEventListener('DOMContentLoaded', () => {
    const board = document.getElementById('chessboard');
    const turnDisplay = document.getElementById('turn-display');
    const moveHistory = document.getElementById('move-history');
    const whiteTimerDisplay = document.getElementById('white-timer');
    const blackTimerDisplay = document.getElementById('black-timer');
    const whiteCapturesBar = document.getElementById('white-captures');
    const blackCapturesBar = document.getElementById('black-captures');

    let selected = null;
    let currentTurn = 'White';
    let boardState = [];

    let whiteTime = 600000;
    let blackTime = 600000;
    let whiteTimerInterval = null;
    let blackTimerInterval = null;

    let whiteCaptures = [];
    let blackCaptures = [];

    function pieceToUnicode(piece) {
        const pieces = {
            'K': '♔', 'Q': '♕', 'R': '♖', 'B': '♗', 'N': '♘', 'P': '♙',
            'k': '♚', 'q': '♛', 'r': '♜', 'b': '♝', 'n': '♞', 'p': '♟'
        };
        return pieces[piece] || '';
    }

    function formatTime(ms) {
        const totalSeconds = Math.floor(ms / 1000);
        const minutes = Math.floor(totalSeconds / 60);
        const seconds = totalSeconds % 60;
        const milliseconds = Math.floor((ms % 1000) / 10);
        return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}:${String(milliseconds).padStart(2, '0')}`;
    }

    function updateTimerDisplay() {
        whiteTimerDisplay.textContent = formatTime(whiteTime);
        blackTimerDisplay.textContent = formatTime(blackTime);
    }

    function stopTimers() {
        clearInterval(whiteTimerInterval);
        clearInterval(blackTimerInterval);
    }

    function startTimer(color) {
        stopTimers();
        if (color === 'White') {
            whiteTimerInterval = setInterval(() => {
                whiteTime -= 100;
                updateTimerDisplay();
            }, 100);
        } else {
            blackTimerInterval = setInterval(() => {
                blackTime -= 100;
                updateTimerDisplay();
            }, 100);
        }
    }

    function drawBoard() {
        board.innerHTML = '';
        for (let row = 0; row < 8; row++) {
            for (let col = 0; col < 8; col++) {
                const square = document.createElement('div');
                square.classList.add('square');
                square.classList.add((row + col) % 2 === 0 ? 'light' : 'dark');
                square.dataset.row = row;
                square.dataset.col = col;
                square.innerText = pieceToUnicode(boardState[row][col]);

                square.addEventListener('click', () => handleClick(row, col));

                square.addEventListener('mouseenter', () => {
                    const piece = boardState[row][col];
                    if (piece && ((currentTurn === 'White' && piece === piece.toUpperCase()) ||
                        (currentTurn === 'Black' && piece === piece.toLowerCase()))) {
                        square.classList.add('hover-valid');
                    } else {
                        square.classList.add('hover-invalid');
                    }
                });

                square.addEventListener('mouseleave', () => {
                    square.classList.remove('hover-valid');
                    square.classList.remove('hover-invalid');
                });

                board.appendChild(square);
            }
        }
    }

    function handleClick(row, col) {
        if (selected) {
            const move = {
                from: selected,
                to: { row, col }
            };

            fetch('/api/chess/move', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(move)
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        const from = data.move.from;
                        const to = data.move.to;
                        const piece = boardState[from.row][from.col];
                        const capturedPiece = boardState[to.row][to.col];
                        const pieceSymbol = pieceToUnicode(piece);
                        const moveText = `${pieceSymbol} ${String.fromCharCode(97 + from.col)}${8 - from.row} → ${String.fromCharCode(97 + to.col)}${8 - to.row}`;
                        const entry = document.createElement('li');
                        entry.textContent = moveText;
                        moveHistory.appendChild(entry);

                        if (capturedPiece && capturedPiece !== '') {
                            if (currentTurn === 'White') {
                                blackCaptures.push(capturedPiece);
                                blackCapturesBar.innerHTML += pieceToUnicode(capturedPiece);
                            } else {
                                whiteCaptures.push(capturedPiece);
                                whiteCapturesBar.innerHTML += pieceToUnicode(capturedPiece);
                            }
                        }

                        boardState[to.row][to.col] = piece;
                        boardState[from.row][from.col] = '';

                        clearValidHighlights();
                        selected = null; // ✅ Clear before redraw to avoid leftover highlight
                        drawBoard();

                        currentTurn = currentTurn === 'White' ? 'Black' : 'White';
                        turnDisplay.textContent = `${currentTurn}'s Turn`;
                        startTimer(currentTurn);
                    } else {
                        alert("Invalid move");
                        clearValidHighlights();
                        selected = null;
                    }
                });
        } else {
            selected = { row, col };
            clearValidHighlights();
            highlightValidMoves(row, col);
        }
    }

    function clearValidHighlights() {
        document.querySelectorAll('.valid-move').forEach(square => {
            square.classList.remove('valid-move');
        });
    }

    function highlightValidMoves(fromRow, fromCol) {
        fetch(`/api/chess/valid-moves?row=${fromRow}&col=${fromCol}`)
            .then(res => res.json())
            .then(validMoves => {
                validMoves.forEach(move => {
                    const index = move.row * 8 + move.col;
                    const square = board.children[index];
                    if (square) {
                        square.classList.add('valid-move');
                    }
                });
            });
    }

    fetch('/api/chess/state')
        .then(res => res.json())
        .then(data => {
            whiteCaptures = [];
            blackCaptures = [];
            whiteCapturesBar.innerHTML = '';
            blackCapturesBar.innerHTML = '';
            moveHistory.innerHTML = '';

            boardState = data.board;
            currentTurn = data.whiteTurn ? 'White' : 'Black';
            whiteTime = data.whiteTime;
            blackTime = data.blackTime;

            turnDisplay.textContent = `${currentTurn}'s Turn`;
            updateTimerDisplay();
            startTimer(currentTurn);

            data.history.forEach(move => {
                const entry = document.createElement('li');
                entry.textContent = move;
                moveHistory.appendChild(entry);
            });

            drawBoard();
        });
});
