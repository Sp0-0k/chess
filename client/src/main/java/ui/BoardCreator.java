package ui;

import chess.*;
import datamodel.*;

import java.util.Objects;

public class BoardCreator {
    private final ChessBoard board;
    private final ChessGame.TeamColor viewerColor;


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
        String darkGray = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        String white = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String black = EscapeSequences.SET_BG_COLOR_BLACK;

        if (viewerColor == ChessGame.TeamColor.WHITE) {
            print(darkGray + "     a    b    c    d    e    f    g    h ");
            String outputString = "";
            for (int j = 8; j > 0; --j) {
                for (int i = 0; i < 10; ++i) {
                    if (i == 0 || i == 9) {
                        outputString = outputString.concat(darkGray + " " + j + " ");
                    } else {
                        if ((i + j) % 2 == 0) {
                            var whiteSquare = white + " " + getPieceType(j, i) + " ";
                            outputString = outputString.concat(whiteSquare);
                        } else {
                            var blackSquare = black + " " + getPieceType(j, i) + " ";
                            outputString = outputString.concat(blackSquare);
                        }
                    }
                }
                outputString = outputString.concat("\n");
            }
            outputString = outputString.concat(darkGray + "     a    b    c    d    e    f    g    h ");
            print(outputString);

        } else {
            print(darkGray + "     h    g    f    e    d    c    b    a ");
            String outputString = "";
            for (int j = 1; j < 9; ++j) {
                for (int i = 9; i >= 0; --i) {
                    if (i == 0 || i == 9) {
                        outputString = outputString.concat(darkGray + " " + j + " ");
                    } else {
                        if ((i + j) % 2 == 0) {
                            var whiteSquare = white + " " + getPieceType(j, i) + " ";
                            outputString = outputString.concat(whiteSquare);
                        } else {
                            var blackSquare = black + " " + getPieceType(j, i) + " ";
                            outputString = outputString.concat(blackSquare);
                        }
                    }
                }
                outputString = outputString.concat("\n");
            }
            outputString = outputString.concat(darkGray + "     h    g    f    e    d    c    b    a ");
            print(outputString);
        }


    }

    private String getPieceType(int row, int col) {
        var piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return "   ";
        }
        var type = piece.getPieceType();
        var color = piece.getTeamColor();
        var white = ChessGame.TeamColor.WHITE;
        return switch (type) {
            case ChessPiece.PieceType.BISHOP ->
                    color == white ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case ChessPiece.PieceType.ROOK -> color == white ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case ChessPiece.PieceType.KING -> color == white ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case ChessPiece.PieceType.PAWN -> color == white ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            case ChessPiece.PieceType.KNIGHT ->
                    color == white ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ChessPiece.PieceType.QUEEN ->
                    color == white ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
        };
    }

    private void print(String string) {
        System.out.println(string);
    }


}
