document.addEventListener('DOMContentLoaded', () => {
    const board = document.getElementById('chessboard');
    let selected = null;

    let boardState = [
        ['r','n','b','q','k','b','n','r'],
        ['p','p','p','p','p','p','p','p'],
        ['','','','','','','',''],
        ['','','','','','','',''],
        ['','','','','','','',''],
        ['','','','','','','',''],
        ['P','P','P','P','P','P','P','P'],
        ['R','N','B','Q','K','B','N','R']
    ];

    function pieceToUnicode(piece) {
        const pieces = {
            'K': '♔', 'Q': '♕', 'R': '♖', 'B': '♗', 'N': '♘', 'P': '♙',
            'k': '♚', 'q': '♛', 'r': '♜', 'b': '♝', 'n': '♞', 'p': '♟'
        };
        return pieces[piece] || '';
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
                        boardState[data.move.to.row][data.move.to.col] = boardState[data.move.from.row][data.move.from.col];
                        boardState[data.move.from.row][data.move.from.col] = '';
                        drawBoard();
                    } else {
                        alert("Invalid move");
                    }
                    selected = null;
                });
        } else {
            selected = { row, col };
        }
    }

    drawBoard();
});
