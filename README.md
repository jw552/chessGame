♟️ ChessGame

A full-stack, full-featured online chess game where players can play from either side of the board, with real-time board updates, move validation, timers, and win detection.
Built with Java Spring Boot backend and React frontend, and deployed on Azure.

Live Demo: [https://white-field-02255590f.1.azurestaticapps.net](https://white-field-02255590f.1.azurestaticapps.net)

---

Features

Player vs AI Mode — Player is randomly assigned white or black; AI plays the opposite side.
Legal Move Validation — Rules enforced server-side, including check and checkmate logic.
Auto Board Flip — Board rotates so human player is always at the bottom.
Chess Timers — Separate countdown timers for each player.
Move History — Scrollable list of moves with algebraic notation and piece symbols.
Containerized — Docker-ready for deployment anywhere.
Cross-Origin Ready — CORS configured for frontend-backend interaction during local and cloud deployment.

---

Tech Stack

| Layer       | Technology                         |
|------------|-------------------------------------|
| Frontend   | React, Vite, JavaScript, CSS        |
| Backend    | Java 17, Spring Boot                |
| Build Tool | Maven                               |
| Deployment | Azure Static Web Apps + Azure App Service |
| Container  | Docker                              |
