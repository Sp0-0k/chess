package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

public class Bishop {


    public static List<ChessMove> bishopMoveCalc(ChessPiece curPiece, ChessPosition curPosition, ChessBoard board) {
        var moves = new LargeMovement(curPiece, curPosition, board);
        return moves.searchDiagonals();
    }
}
