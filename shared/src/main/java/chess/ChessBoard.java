package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (i == 0 || i == 1) {
                    var color = ChessGame.TeamColor.WHITE;
                    if (i == 0) {
                        switch (j) {
                            case 0:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                            case 1:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                            case 2:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                            case 3:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
                            case 4:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KING);
                            case 5:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                            case 6:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                            case 7:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                        }
                    } else {
                        board[i][j] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
                    }

                }
                if (i == 6 || i == 7) {
                    var color = ChessGame.TeamColor.BLACK;
                    if (i == 7) {
                        switch (j) {
                            case 0:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                            case 1:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                            case 2:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                            case 3:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
                            case 4:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KING);
                            case 5:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                            case 6:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                            case 7:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                        }
                    } else {
                        board[i][j] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
                    }

                }
            }
        }
        System.out.println(toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }
}
