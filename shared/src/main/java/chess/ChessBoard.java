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

    public void movePiece(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board[start.getRow() - 1][start.getColumn() - 1];
        board[start.getRow() - 1][start.getColumn() - 1] = null;
        board[end.getRow() - 1][end.getColumn() - 1] = piece;
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
                            case 0, 7:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                                break;
                            case 1, 6:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                                break;
                            case 2, 5:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                                break;
                            case 3:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
                                break;
                            case 4:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KING);
                                break;
                        }
                    } else {
                        board[i][j] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
                    }

                }
                if (i == 6 || i == 7) {
                    var color = ChessGame.TeamColor.BLACK;
                    if (i == 7) {
                        switch (j) {
                            case 0, 7:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                                break;
                            case 1, 6:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                                break;
                            case 2, 5:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                                break;
                            case 3:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
                                break;
                            case 4:
                                board[i][j] = new ChessPiece(color, ChessPiece.PieceType.KING);
                                break;
                        }
                    } else {
                        board[i][j] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
                    }

                }
            }
        }
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

    public void outputBoard() {
        for (int i = 7; i >= 0; --i) {
            System.out.print("|");
            for (int j = 0; j < 8; ++j) {
                var currPiece = getPiece(new ChessPosition(i + 1, j + 1));
                if (currPiece != null) {
                    if (currPiece.pieceColor == ChessGame.TeamColor.WHITE) {
                        switch (currPiece.type) {
                            case ChessPiece.PieceType.BISHOP:
                                System.out.print("B");
                                break;
                            case ChessPiece.PieceType.ROOK:
                                System.out.print("R");
                                break;
                            case ChessPiece.PieceType.KING:
                                System.out.print("K");
                                break;
                            case ChessPiece.PieceType.PAWN:
                                System.out.print("P");
                                break;
                            case ChessPiece.PieceType.KNIGHT:
                                System.out.print("N");
                                break;
                            case ChessPiece.PieceType.QUEEN:
                                System.out.print("Q");
                                break;
                        }
                    } else if (currPiece.pieceColor == ChessGame.TeamColor.BLACK) {
                        switch (currPiece.type) {
                            case ChessPiece.PieceType.BISHOP:
                                System.out.print("b");
                                break;
                            case ChessPiece.PieceType.ROOK:
                                System.out.print("r");
                                break;
                            case ChessPiece.PieceType.KING:
                                System.out.print("k");
                                break;
                            case ChessPiece.PieceType.PAWN:
                                System.out.print("p");
                                break;
                            case ChessPiece.PieceType.KNIGHT:
                                System.out.print("n");
                                break;
                            case ChessPiece.PieceType.QUEEN:
                                System.out.print("q");
                                break;
                        }
                    }
                } else {
                    System.out.print(" ");
                }
                System.out.print("|");
            }
            System.out.print("\n");
        }
    }

    public ChessPosition findPiece(ChessPiece pieceToFind) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (getPiece(new ChessPosition(i, j)) == pieceToFind) {
                    return new ChessPosition(i, j);
                }
            }
        }
        return null;
    }
}
