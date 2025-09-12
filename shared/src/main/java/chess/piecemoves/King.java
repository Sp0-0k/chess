package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class King {

    public static List<ChessMove> kingMoveCalc(ChessPiece curPiece, ChessPosition curPosition, ChessBoard board) {
        List<ChessMove> moves = new ArrayList<>();
        for (int i = -1; i < 2; ++i) {
            if (curPosition.getRow() + 1 != 9 && curPosition.getRow() - 1 != 0 && curPosition.getColumn() + i != 9 && curPosition.getColumn() + i != 0) {
                var frontPosition = new ChessPosition(curPosition.getRow() + 1, curPosition.getColumn() + i);
                var isValid = new CheckPosition(curPiece, frontPosition, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, frontPosition, null));
                }
            }

            if (curPosition.getColumn() + i != 9 && curPosition.getColumn() + i != 0) {
                var midPosition = new ChessPosition(curPosition.getRow(), curPosition.getColumn() + i);
                var isValidMid = new CheckPosition(curPiece, midPosition, board);
                if (!isValidMid.isOccupied() || isValidMid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, midPosition, null));
                }
            }


            if (curPosition.getRow() - 1 != 0 && curPosition.getColumn() + i != 9 && curPosition.getColumn() + i != 0) {
                var backPosition = new ChessPosition(curPosition.getRow() - 1, curPosition.getColumn() + i);
                var isValidBack = new CheckPosition(curPiece, backPosition, board);
                if (!isValidBack.isOccupied() || isValidBack.isCapturable()) {
                    moves.add(new ChessMove(curPosition, backPosition, null));
                }

            }
        }

        return moves;
    }
}
