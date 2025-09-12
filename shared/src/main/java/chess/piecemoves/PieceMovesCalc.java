package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessMove;

import java.util.List;

public class PieceMovesCalc {
    ChessPosition curPosition;
    ChessPiece curPiece;
    ChessBoard board;

    public PieceMovesCalc(ChessPosition curPosition, ChessPiece curPiece, ChessBoard board) {
        this.curPiece = curPiece;
        this.curPosition = curPosition;
        this.board = board;
    }

    public List<ChessMove> getPossiblePositions() {
        switch (curPiece.getPieceType()) {
            case ROOK -> {
                return Rook.rookMoveCalc(curPiece, curPosition, board);
            }
//            case KNIGHT -> {
//                return Knight.knightMoveCalc(curPiece, curPosition, board);
//            }
            case KING -> {
                return King.kingMoveCalc(curPiece, curPosition, board);
            }
            case PAWN -> {
                return Pawn.pawnMoveCalc(curPiece, curPosition, board);
            }
            case QUEEN -> {
                return Queen.queenMoveCalc(curPiece, curPosition, board);
            }
            case BISHOP -> {
                return Bishop.bishopMoveCalc(curPiece, curPosition, board);
            }

        }

        return List.of();
    }


}
