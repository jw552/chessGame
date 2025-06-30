import React from 'react';
import './PromotionModal.css';

function PromotionModal({ onSelect }) {
    const pieces = ['Q', 'R', 'B', 'N'];

    return (
        <div className="promotion-modal">
            <div className="promotion-modal-content">
                <h3>Promote your pawn:</h3>
                {pieces.map(p => (
                    <button key={p} onClick={() => onSelect(p)}>
                        {p}
                    </button>
                ))}
            </div>
        </div>
    );
}

export default PromotionModal;
