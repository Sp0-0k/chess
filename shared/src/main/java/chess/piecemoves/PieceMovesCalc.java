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
//            case ROOK -> {
//                return Rook.rookMoveCalc(myPiece, myPosition);
//            }
//            case KNIGHT -> {
//                return Knight.knightMoveCalc(myPiece, myPosition);
//            }
//            case KING -> {
//                return King.kingMoveCalc(myPiece, myPosition);
//            }
            case PAWN -> {
                return Pawn.pawnMoveCalc(curPiece, curPosition, board);
            }
//            case QUEEN -> {
//                return Queen.queenMoveCalc(myPiece, myPosition);
//            }
//            case BISHOP -> {
//                return Bishop.bishopMoveCalc(myPiece, myPosition);
//            }

        }

        return List.of();
    }


}
