package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class Knight {
    public static List<ChessMove> knightMoveCalc(ChessPiece curPiece, ChessPosition curPosition, ChessBoard board) {
        List<ChessMove> moves = new ArrayList<>();
        if (curPosition.getRow() < 7) {
            //UpRight
            if (curPosition.getColumn() != 8) {
                var frontRight = new ChessPosition(curPosition.getRow() + 2, curPosition.getColumn() + 1);
                var isValid = new CheckPosition(curPiece, frontRight, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, frontRight, null));
                }
            }
            //UpLeft
            if (curPosition.getColumn() != 1) {
                var frontLeft = new ChessPosition(curPosition.getRow() + 2, curPosition.getColumn() - 1);
                var isValid = new CheckPosition(curPiece, frontLeft, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, frontLeft, null));
                }
            }
        }
        if (curPosition.getColumn() < 7) {
            //RightUp
            if (curPosition.getRow() != 8) {
                var rightFront = new ChessPosition(curPosition.getRow() + 1, curPosition.getColumn() + 2);
                var isValid = new CheckPosition(curPiece, rightFront, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, rightFront, null));
                }
            }
            //RightDown
            if (curPosition.getRow() != 1) {
                var rightBack = new ChessPosition(curPosition.getRow() - 1, curPosition.getColumn() + 2);
                var isValid = new CheckPosition(curPiece, rightBack, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, rightBack, null));
                }
            }
        }
        if (curPosition.getRow() > 2) {
            //BackRight
            if (curPosition.getColumn() != 8) {
                var backRight = new ChessPosition(curPosition.getRow() - 2, curPosition.getColumn() + 1);
                var isValid = new CheckPosition(curPiece, backRight, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, backRight, null));
                }
            }
            //BackLeft
            if (curPosition.getColumn() != 1) {
                var backLeft = new ChessPosition(curPosition.getRow() - 2, curPosition.getColumn() + 1);
                var isValid = new CheckPosition(curPiece, backLeft, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, backLeft, null));
                }
            }
        }
        if (curPosition.getColumn() > 2) {
            //LeftUp
            if (curPosition.getRow() != 8) {
                var leftUp = new ChessPosition(curPosition.getRow() + 1, curPosition.getColumn() - 2);
                var isValid = new CheckPosition(curPiece, leftUp, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, leftUp, null));
                }
            }
            //LeftDown
            if (curPosition.getRow() != 1) {
                var leftDown = new ChessPosition(curPosition.getRow() - 1, curPosition.getColumn() - 2);
                var isValid = new CheckPosition(curPiece, leftDown, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, leftDown, null));
                }
            }
        }

        return moves;
    }
}
