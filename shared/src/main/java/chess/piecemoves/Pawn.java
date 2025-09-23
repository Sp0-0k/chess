package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class Pawn {
    static List<ChessMove> moves = new ArrayList<>();

    public static List<ChessMove> pawnMoveCalc(ChessPiece myPiece, ChessPosition myPosition, ChessBoard board) {
        moves.clear();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int colorDif;
        var myColumn = myPosition.getColumn();
        var myRow = myPosition.getRow();
        var myIndex = new ChessPosition(myRow, myColumn);
        if (color == ChessGame.TeamColor.WHITE) {
            colorDif = 1;
        } else {
            colorDif = -1;
        }

        if (myColumn != 1) {
            var leftCapture = new ChessPosition(myRow + colorDif, myColumn - 1);
            var leftValidSpot = new CheckPosition(myPiece, leftCapture, board);
            if (leftValidSpot.isCapturable()) {
                addMove(myIndex, leftCapture);
            }
        }

        var oneAhead = new ChessPosition(myRow + colorDif, myColumn);
        var forwardValidSpot = new CheckPosition(myPiece, oneAhead, board);
        if (!forwardValidSpot.isOccupied()) {
            addMove(myIndex, oneAhead);
            if ((myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) && (myRow == 2) || (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) && (myRow == 7)) {
                var twoAhead = new ChessPosition(myRow + (colorDif * 2), myColumn);
                var nextValidSpot = new CheckPosition(myPiece, twoAhead, board);
                if (!nextValidSpot.isOccupied()) {
                    addMove(myIndex, twoAhead);
                }
            }
        }

        if (myColumn != 8) {
            var rightCapture = new ChessPosition(myRow + colorDif, myColumn + 1);
            var rightValidSpot = new CheckPosition(myPiece, rightCapture, board);
            if (rightValidSpot.isCapturable()) {
                addMove(myIndex, rightCapture);
            }

        }


        return moves;
    }

    private static void addMove(ChessPosition myIndex, ChessPosition endSpot) {
        if ((endSpot.getRow() == 8) || (endSpot.getRow() == 1)) {
            moves.add(new ChessMove(myIndex, endSpot, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myIndex, endSpot, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myIndex, endSpot, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myIndex, endSpot, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(myIndex, endSpot, null));
        }
    }
}
