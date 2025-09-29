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
    private ChessBoard currentBoard;

    public ChessGame() {
        currentTeamColor = TeamColor.WHITE;
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();

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
        Collection<ChessMove> possibleMoves = currentBoard.getPiece(startPosition).pieceMoves(currentBoard, startPosition);
        return possibleMoves;
    }

    private Collection<ChessMove> getTeamMoves(TeamColor teamToSearch) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (currentBoard.getPiece(new ChessPosition(i + 1, j + 1)) != null) {
                    if (currentBoard.getPiece(new ChessPosition(i + 1, j + 1)).getTeamColor() == teamToSearch) {
                        allMoves.addAll(validMoves(new ChessPosition(i + 1, j + 1)));
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
        if (currentBoard.getPiece(startingPosition) == null) {
            throw new InvalidMoveException();
        }
        TeamColor teamMoving = currentBoard.getPiece(startingPosition).getTeamColor();
        TeamColor teamToChangeTo = (teamMoving == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> possibleMoves = validMoves(startingPosition);
        if (teamMoving != currentTeamColor) {
            throw new InvalidMoveException();
        }

        for (ChessMove potentialMove : possibleMoves) {
            if (potentialMove.equals(move)) {
                currentBoard.movePiece(move);

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
        ChessPosition kingLocation = currentBoard.findPiece(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Collection<ChessMove> possibleMoves = getTeamMoves(oppositeTeam);
        for (ChessMove move : possibleMoves) {
            if (move.getEndPosition().equals(kingLocation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return true;
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
        if (!isInCheck(teamColor)) {
            return validMoves(currentBoard.findPiece(new ChessPiece(teamColor, ChessPiece.PieceType.KING))) == null;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeamColor == chessGame.currentTeamColor && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeamColor, currentBoard);
    }
}
