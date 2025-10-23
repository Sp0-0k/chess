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
        var curRow = curPosition.getRow();
        var curCol = curPosition.getColumn();
        for (int i = -1; i < 2; ++i) {
            if (curRow + 1 != 9 && curRow - 1 != 0 && curCol + i != 9 && curCol + i != 0) {
                var frontPosition = new ChessPosition(curRow + 1, curCol + i);
                var isValid = new CheckPosition(curPiece, frontPosition, board);
                if (!isValid.isOccupied() || isValid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, frontPosition, null));
                }
            }

            if (curCol + i != 9 && curCol + i != 0) {
                var midPosition = new ChessPosition(curRow, curCol + i);
                var isValidMid = new CheckPosition(curPiece, midPosition, board);
                if (!isValidMid.isOccupied() || isValidMid.isCapturable()) {
                    moves.add(new ChessMove(curPosition, midPosition, null));
                }
            }


            if (curRow - 1 != 0 && curCol + i != 9 && curCol + i != 0) {
                var backPosition = new ChessPosition(curRow - 1, curCol + i);
                var isValidBack = new CheckPosition(curPiece, backPosition, board);
                if (!isValidBack.isOccupied() || isValidBack.isCapturable()) {
                    moves.add(new ChessMove(curPosition, backPosition, null));
                }

            }
        }

        return moves;
    }
}
