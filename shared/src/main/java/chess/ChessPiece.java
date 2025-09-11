package chess;

import chess.piecemoves.PieceMovesCalc;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public List<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        ChessPiece currPiece = board.getPiece(myPosition);
        switch (currPiece.type) {
            case ChessPiece.PieceType.BISHOP:
                break;
            case ChessPiece.PieceType.ROOK:
                break;
            case ChessPiece.PieceType.KING:
                break;
            case ChessPiece.PieceType.PAWN:
                var pawnCalc = new PieceMovesCalc(myPosition, currPiece, board);
                return pawnCalc.getPossiblePositions();
            case ChessPiece.PieceType.KNIGHT:
                break;
            case ChessPiece.PieceType.QUEEN:
                break;
        }

        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
