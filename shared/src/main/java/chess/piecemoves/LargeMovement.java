package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public abstract class LargeMovement extends PieceMovesCalc {
    private ChessPiece myPiece;
    private ChessPosition myPosition;
    private ChessBoard board;
    List<ChessMove> moves = new ArrayList<>();

    public LargeMovement(ChessPosition curPosition, ChessPiece curPiece, ChessBoard board) {
        super(curPosition, curPiece, board);
    }


    public List<ChessMove> searchDiagonals() {
        moves.clear();
        for (int i = myPosition.getRow() + 1; i < 9; ++i) {
            int j = i - myPosition.getRow();
            if (myPosition.getColumn() + j > 8 || j < 1 || i < 1) {
                break;
            }
            var nextDiagonal = new ChessPosition(i, myPosition.getColumn() + j);
            var isValid = new CheckPosition(myPiece, nextDiagonal, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(myPosition, nextDiagonal, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        for (int i = myPosition.getRow() + 1; i < 9; ++i) {
            int j = i - myPosition.getRow();
            if (j > 8 || myPosition.getColumn() - j < 1 || i < 1) {
                break;
            }
            var nextDiagonal = new ChessPosition(i, myPosition.getColumn() - j);
            var isValid = new CheckPosition(myPiece, nextDiagonal, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(myPosition, nextDiagonal, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        for (int i = myPosition.getRow() - 1; i > 0; --i) {
            int j = i - myPosition.getRow();

            if ((myPosition.getColumn() - Math.abs(j)) < 1 || i > 8) {
                break;
            }
            var nextDiagonal = new ChessPosition(i, myPosition.getColumn() - Math.abs(j));
            var isValid = new CheckPosition(myPiece, nextDiagonal, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(myPosition, nextDiagonal, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        for (int i = myPosition.getRow() - 1; i > 0; --i) {
            int j = i - myPosition.getRow();
            if ((myPosition.getColumn() + Math.abs(j)) > 8 || i > 8) {
                break;
            }
            var nextDiagonal = new ChessPosition(i, myPosition.getColumn() + Math.abs(j));
            var isValid = new CheckPosition(myPiece, nextDiagonal, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(myPosition, nextDiagonal, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }

        return moves;
    }


    public List<ChessMove> searchHorizonatals() {
        moves.clear();

        //Upwards
        for (int i = myPosition.getRow() + 1; i < 9; ++i) {
            var nextLocation = new ChessPosition(i, myPosition.getColumn());
            var isValid = new CheckPosition(myPiece, nextLocation, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(myPosition, nextLocation, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        //Downwards
        for (int i = myPosition.getRow() - 1; i > 0; --i) {
            var nextLocation = new ChessPosition(i, myPosition.getColumn());
            var isValid = new CheckPosition(myPiece, nextLocation, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(myPosition, nextLocation, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        //Right
        for (int i = myPosition.getColumn() + 1; i < 9; ++i) {
            var nextLocation = new ChessPosition(myPosition.getRow(), i);
            var isValid = new CheckPosition(myPiece, nextLocation, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(myPosition, nextLocation, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        //Left
        for (int i = myPosition.getColumn() - 1; i > 0; --i) {
            var nextLocation = new ChessPosition(myPosition.getRow(), i);
            var isValid = new CheckPosition(myPiece, nextLocation, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(myPosition, nextLocation, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }


        return moves;
    }
}
