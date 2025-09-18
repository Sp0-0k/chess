package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class Queen extends LargeMovement {


    public Queen(ChessPosition curPosition, ChessPiece curPiece, ChessBoard board) {
        super(curPosition, curPiece, board);
    }

    @Override
    public List<ChessMove> getPossiblePositions() {
        var listToReturn = new ArrayList<ChessMove>();
        listToReturn.addAll(searchDiagonals());
        listToReturn.addAll(searchHorizonatals());
        return listToReturn;
    }
}
