package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeamColor;
    private ChessBoard currBoard;

    @Override
    public String toString() {
        return "ChessGame{" +
                "currentTeamColor=" + currentTeamColor +
                ", currBoard=" + currBoard +
                '}';
    }

    public ChessGame() {
        currentTeamColor = TeamColor.WHITE;
        currBoard = new ChessBoard();
        currBoard.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> possibleMoves = currBoard.getPiece(startPosition).pieceMoves(currBoard, startPosition);
        TeamColor curColor = currBoard.getPiece(startPosition).getTeamColor();
        Collection<ChessMove> movesThatWork = new ArrayList<>();
        for (ChessMove move : new ArrayList<>(possibleMoves)) {
            ChessBoard tempBoard = new ChessBoard(currBoard);
            tempBoard.movePiece(move);
            if (!isInCheck(curColor, tempBoard)) {
                movesThatWork.add(move);
            }
        }
        return movesThatWork;
    }

    private boolean isPieceDanger(ChessPosition locationToCheck, TeamColor colorAttacking, ChessBoard board) {
        var attackerMoves = getTeamMoves(colorAttacking, board);
        for (ChessMove potentialMove : attackerMoves) {
            if (potentialMove.getEndPosition().equals(locationToCheck)) {
                return true;
            }
        }
        return false;
    }

    private Collection<ChessMove> getTeamMoves(TeamColor teamToSearch, ChessBoard board) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                var posToCheck = new ChessPosition(i + 1, j + 1);
                if (board.getPiece(posToCheck) != null) {
                    var curPiece = board.getPiece(posToCheck);
                    if (curPiece.getTeamColor() == teamToSearch) {
                        allMoves.addAll(curPiece.pieceMoves(board, posToCheck));
                    }
                }
            }
        }

        return allMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startingPosition = move.getStartPosition();
        if (currBoard.getPiece(startingPosition) == null) {
            throw new InvalidMoveException();
        }
        TeamColor teamMoving = currBoard.getPiece(startingPosition).getTeamColor();
        TeamColor teamToChangeTo = (teamMoving == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> possibleMoves = validMoves(startingPosition);
        if (teamMoving != currentTeamColor) {
            throw new InvalidMoveException();
        }

        for (ChessMove potentialMove : possibleMoves) {
            if (potentialMove.equals(move)) {
                currBoard.movePiece(move);

                setTeamTurn(teamToChangeTo);
                return;
            }
        }
        throw new InvalidMoveException();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor oppositeTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        ChessPosition kingLocation = currBoard.findPiece(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        return isPieceDanger(kingLocation, oppositeTeam, this.currBoard);
    }

    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        TeamColor oppositeTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        ChessPosition kingLocation = board.findPiece(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        return isPieceDanger(kingLocation, oppositeTeam, board);
    }

    public boolean noValidMoves(TeamColor teamToCheck) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                ChessPosition posToCheck = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = currBoard.getPiece(posToCheck);
                if (piece != null && piece.getTeamColor() == teamToCheck) {
                    Collection<ChessMove> moves = validMoves(posToCheck);
                    if (moves != null && !moves.isEmpty()) {
                        return false; // Found a valid move
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return noValidMoves(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return noValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param currBoard the new board to use
     */
    public void setBoard(ChessBoard currBoard) {
        this.currBoard = currBoard;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currBoard;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeamColor == chessGame.currentTeamColor && Objects.equals(currBoard, chessGame.currBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeamColor, currBoard);
    }
}
