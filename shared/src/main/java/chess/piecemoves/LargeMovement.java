package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public abstract class LargeMovement extends PieceMovesCalc {

    public LargeMovement(ChessPosition curPosition, ChessPiece curPiece, ChessBoard board) {
        super(curPosition, curPiece, board);
    }


    public List<ChessMove> searchDiagonals() {
        List<ChessMove> moves = new ArrayList<>();
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

    private boolean verticalMoveFailure(List<ChessMove> moves, int rowNum) {
        var nextLocation = new ChessPosition(rowNum, curPosition.getColumn());
        var isValid = new CheckPosition(curPiece, nextLocation, board);
        if (!isValid.isOccupied() || isValid.isCapturable()) {
            moves.add(new ChessMove(curPosition, nextLocation, null));
            if (isValid.isCapturable()) {
                return true;
            }
        }
        return isValid.isOccupied() && !isValid.isCapturable();
    }

    private boolean horizontalMoveFailure(List<ChessMove> moves, int colNum) {
        var nextLocation = new ChessPosition(curPosition.getRow(), colNum);
        var isValid = new CheckPosition(curPiece, nextLocation, board);
        if (!isValid.isOccupied() || isValid.isCapturable()) {
            moves.add(new ChessMove(curPosition, nextLocation, null));
            if (isValid.isCapturable()) {
                return true;
            }
        }
        return isValid.isOccupied() && !isValid.isCapturable();
    }


    public List<ChessMove> searchHorizontals() {
        List<ChessMove> moves = new ArrayList<>();

        //Upwards
        for (int i = curPosition.getRow() + 1; i < 9; ++i) {
            if (verticalMoveFailure(moves, i)) {
                break;
            }
        }
        //Downwards
        for (int i = curPosition.getRow() - 1; i > 0; --i) {
            if (verticalMoveFailure(moves, i)) {
                break;
            }
        }
        //Right
        for (int i = curPosition.getColumn() + 1; i < 9; ++i) {
            if (horizontalMoveFailure(moves, i)) {
                break;
            }
        }
        //Left
        for (int i = curPosition.getColumn() - 1; i > 0; --i) {
            if (horizontalMoveFailure(moves, i)) {
                break;
            }
        }


        return moves;
    }
}
