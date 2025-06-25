export function pieceToUnicode(piece) {
    const isWhite = piece === piece.toUpperCase();
    switch (piece.toLowerCase()) {
        case 'p': return isWhite ? '♙' : '♟';
        case 'r': return isWhite ? '♖' : '♜';
        case 'n': return isWhite ? '♘' : '♞';
        case 'b': return isWhite ? '♗' : '♝';
        case 'q': return isWhite ? '♕' : '♛';
        case 'k': return isWhite ? '♔' : '♚';
        default: return '';
    }
}
