package ui;

import chess.*;
import datamodel.*;

import java.util.Objects;

public class BoardCreator {
    private final ChessBoard board;
    private final ChessGame.TeamColor viewerColor;
    private final String resetBg = EscapeSequences.RESET_BG_COLOR;
    private ChessGame.TeamColor curPieceColor;

    public BoardCreator(GameData gameData, String playerName) {
        ChessGame game = gameData.game();
        this.board = game.getBoard();
        if (Objects.equals(gameData.blackUsername(), playerName)) {
            viewerColor = ChessGame.TeamColor.BLACK;
        } else {
            viewerColor = ChessGame.TeamColor.WHITE;
        }
    }

    public void drawBoard() {
        String bgGray = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        if (viewerColor == ChessGame.TeamColor.WHITE) {
            print(resetBg + "     a    b    c    d    e    f    g    h ");
            String outputString = "";
            for (int j = 8; j > 0; --j) {
                for (int i = 0; i < 10; ++i) {
                    outputString = outputTile(j, i, outputString);
                }
                outputString = outputString.concat("\n");
            }
            outputString = outputString.concat(resetBg + "     a    b    c    d    e    f    g    h ");
            print(outputString);

        } else {
            print(resetBg + "     h    g    f    e    d    c    b    a ");
            String outputString = "";
            for (int j = 1; j < 9; ++j) {
                for (int i = 9; i >= 0; --i) {
                    outputString = outputTile(j, i, outputString);
                }
                outputString = outputString.concat("\n");
            }
            outputString = outputString.concat(resetBg + "     h    g    f    e    d    c    b    a ");
            print(outputString);
            print(bgGray);
        }


    }

    private String assignColors(String piece, ChessGame.TeamColor color) {
        String brown = EscapeSequences.SET_TEXT_COLOR_BROWN;
        String white = EscapeSequences.SET_TEXT_COLOR_WHITE;
        String normal = EscapeSequences.RESET_TEXT_COLOR;
        if (color == ChessGame.TeamColor.WHITE) {
            return white + piece + normal;
        } else {
            return brown + piece + normal;
        }
    }

    private String getPieceType(int row, int col) {
        var piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        var type = piece.getPieceType();
        curPieceColor = piece.getTeamColor();
        return switch (type) {
            case ChessPiece.PieceType.BISHOP -> EscapeSequences.BLACK_BISHOP;
            case ChessPiece.PieceType.ROOK -> EscapeSequences.BLACK_ROOK;
            case ChessPiece.PieceType.KING -> EscapeSequences.BLACK_KING;
            case ChessPiece.PieceType.PAWN -> EscapeSequences.BLACK_PAWN;
            case ChessPiece.PieceType.KNIGHT -> EscapeSequences.BLACK_KNIGHT;
            case ChessPiece.PieceType.QUEEN -> EscapeSequences.BLACK_QUEEN;
        };
    }

    private String outputTile(int row, int col, String curOutput) {
        String whiteTile = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String blackTile = EscapeSequences.SET_BG_COLOR_DARK_GREY;


        if (col == 0 || col == 9) {
            return curOutput.concat(resetBg + " " + row + " ");
        } else {
            if ((row + col) % 2 == 1) {
                var whiteSquare = whiteTile + " " + assignColors(getPieceType(row, col), curPieceColor) + " ";
                return curOutput.concat(whiteSquare);
            } else {
                var blackSquare = blackTile + " " + assignColors(getPieceType(row, col), curPieceColor) + " ";
                return curOutput.concat(blackSquare);
            }
        }
    }

    private void print(String string) {
        System.out.println(string);
    }


}
