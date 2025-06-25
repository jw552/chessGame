export function formatMoveHistory(history) {
    const unicodeMap = {
        'p': '♟', 'r': '♜', 'n': '♞', 'b': '♝', 'q': '♛', 'k': '♚',
        'P': '♙', 'R': '♖', 'N': '♘', 'B': '♗', 'Q': '♕', 'K': '♔',
    };

    const toAlg = (pos) => {
        if (!pos || typeof pos.col !== 'number' || typeof pos.row !== 'number') return '?';
        return String.fromCharCode(97 + pos.col) + (8 - pos.row);
    };

    return (history || []).map((move, i) => {
        if (typeof move === 'string') return move;
        const piece = unicodeMap[move.piece] || move.piece || '?';
        return `${i + 1}. ${piece} ${toAlg(move.from)} → ${toAlg(move.to)}`;
    });
}
