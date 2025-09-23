package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

public class Bishop extends LargeMovement {


    public Bishop(ChessPosition curPosition, ChessPiece curPiece, ChessBoard board) {
        super(curPosition, curPiece, board);
    }

    @Override
    public List<ChessMove> getPossiblePositions() {
        return searchDiagonals();
    }
}
