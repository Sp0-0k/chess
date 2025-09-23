package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public abstract class LargeMovement extends PieceMovesCalc {
    List<ChessMove> moves = new ArrayList<>();

    public LargeMovement(ChessPosition curPosition, ChessPiece curPiece, ChessBoard board) {
        super(curPosition, curPiece, board);
    }


    public List<ChessMove> searchDiagonals() {
        moves.clear();
        for (int i = curPosition.getRow() + 1; i < 9; ++i) {
            int j = i - curPosition.getRow();
            if (curPosition.getColumn() + j > 8 || j < 1 || i < 1) {
                break;
            }
            var nextDiagonal = new ChessPosition(i, curPosition.getColumn() + j);
            var isValid = new CheckPosition(curPiece, nextDiagonal, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(curPosition, nextDiagonal, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        for (int i = curPosition.getRow() + 1; i < 9; ++i) {
            int j = i - curPosition.getRow();
            if (j > 8 || curPosition.getColumn() - j < 1 || i < 1) {
                break;
            }
            var nextDiagonal = new ChessPosition(i, curPosition.getColumn() - j);
            var isValid = new CheckPosition(curPiece, nextDiagonal, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(curPosition, nextDiagonal, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        for (int i = curPosition.getRow() - 1; i > 0; --i) {
            int j = i - curPosition.getRow();

            if ((curPosition.getColumn() - Math.abs(j)) < 1 || i > 8) {
                break;
            }
            var nextDiagonal = new ChessPosition(i, curPosition.getColumn() - Math.abs(j));
            var isValid = new CheckPosition(curPiece, nextDiagonal, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(curPosition, nextDiagonal, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        for (int i = curPosition.getRow() - 1; i > 0; --i) {
            int j = i - curPosition.getRow();
            if ((curPosition.getColumn() + Math.abs(j)) > 8 || i > 8) {
                break;
            }
            var nextDiagonal = new ChessPosition(i, curPosition.getColumn() + Math.abs(j));
            var isValid = new CheckPosition(curPiece, nextDiagonal, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(curPosition, nextDiagonal, null));
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


    public List<ChessMove> searchHorizontals() {
        moves.clear();

        //Upwards
        for (int i = curPosition.getRow() + 1; i < 9; ++i) {
            var nextLocation = new ChessPosition(i, curPosition.getColumn());
            var isValid = new CheckPosition(curPiece, nextLocation, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(curPosition, nextLocation, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        //Downwards
        for (int i = curPosition.getRow() - 1; i > 0; --i) {
            var nextLocation = new ChessPosition(i, curPosition.getColumn());
            var isValid = new CheckPosition(curPiece, nextLocation, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(curPosition, nextLocation, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        //Right
        for (int i = curPosition.getColumn() + 1; i < 9; ++i) {
            var nextLocation = new ChessPosition(curPosition.getRow(), i);
            var isValid = new CheckPosition(curPiece, nextLocation, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(curPosition, nextLocation, null));
                if (isValid.isCapturable()) {
                    break;
                }
            }
            if (isValid.isOccupied() && !isValid.isCapturable()) {
                break;
            }
        }
        //Left
        for (int i = curPosition.getColumn() - 1; i > 0; --i) {
            var nextLocation = new ChessPosition(curPosition.getRow(), i);
            var isValid = new CheckPosition(curPiece, nextLocation, board);
            if (!isValid.isOccupied() || isValid.isCapturable()) {
                moves.add(new ChessMove(curPosition, nextLocation, null));
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
