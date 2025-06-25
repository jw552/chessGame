import React from 'react';
import './SideBar.css';

function Sidebar({ turn, history, whiteTime, blackTime, onReset }) {
    const formatTime = (ms) => {
        const minutes = Math.floor(ms / 60000);
        const seconds = Math.floor((ms % 60000) / 1000)
            .toString()
            .padStart(2, '0');
        return `${minutes}:${seconds}`;
    };

    return (
        <div className="sidebar">
            <div className="sidebar-section turn">
                {turn}'s Turn
            </div>

            <div className="sidebar-section timers">
                <div className="timer-box">White:<br />{formatTime(whiteTime)}</div>
                <div className="timer-box">Black:<br />{formatTime(blackTime)}</div>
            </div>

            <div className="sidebar-section move-history">
                <h4>Move History</h4>
                <ol className="history">
                    {history.map((move, index) => (
                        <li key={index}>{move}</li>
                    ))}
                </ol>
            </div>

            <button className="reset-button" onClick={onReset}>Reset Game</button>
        </div>
    );
}

export default Sidebar;
