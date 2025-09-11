package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class CheckPosition {

    private final ChessPiece myPiece;
    private final ChessPosition positionToCheck;
    private final ChessBoard board;

    public CheckPosition(ChessPiece myPiece, ChessPosition positionToCheck, ChessBoard board) {
        this.myPiece = myPiece;
        this.positionToCheck = positionToCheck;
        this.board = board;
    }

    public boolean isOccupied() {
        ChessPiece piece = board.getPiece(positionToCheck);
        return piece != null;
    }

    public boolean isSameColor() {
        ChessPiece piece = board.getPiece(positionToCheck);
        return myPiece.getTeamColor() == piece.getTeamColor();
    }

    public boolean isCapturable() {
        return isOccupied() && !isSameColor();
    }

}
