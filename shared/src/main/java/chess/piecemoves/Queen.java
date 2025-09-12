package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class Queen {

    public static List<ChessMove> queenMoveCalc(ChessPiece curPiece, ChessPosition curPosition, ChessBoard board) {
        var diagonalMoves = new LargeMovement(curPiece, curPosition, board);
        var totalMoves = diagonalMoves.searchHorizonatals();
        var horizontalMoves = new LargeMovement(curPiece, curPosition, board);
        totalMoves.addAll(horizontalMoves.searchDiagonals());
        return totalMoves;
    }


}
