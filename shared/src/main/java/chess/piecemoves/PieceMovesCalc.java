package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessMove;

import java.util.List;

public abstract class PieceMovesCalc {
    ChessPosition curPosition;
    ChessPiece curPiece;
    ChessBoard board;

    public PieceMovesCalc(ChessPosition curPosition, ChessPiece curPiece, ChessBoard board) {
        this.curPiece = curPiece;
        this.curPosition = curPosition;
        this.board = board;
    }

    public abstract List<ChessMove> getPossiblePositions();

}
