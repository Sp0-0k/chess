package ui;

import chess.*;
import datamodel.*;

import java.util.Objects;

public class BoardCreator {
    private GameData data;
    private ChessGame game;
    private ChessBoard board;
    private String player;
    private ChessGame.TeamColor viewerColor;

    private String darkGray = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private String white = EscapeSequences.SET_BG_COLOR_WHITE;
    private String black = EscapeSequences.SET_BG_COLOR_BLACK;


    public BoardCreator(GameData gameData, String playerName) {
        this.data = gameData;
        this.game = gameData.game();
        this.board = game.getBoard();
        this.player = playerName;
        if (Objects.equals(gameData.blackUsername(), playerName)) {
            viewerColor = ChessGame.TeamColor.BLACK;
        } else {
            viewerColor = ChessGame.TeamColor.WHITE;
        }
    }

    public void drawBoard() {
        print(darkGray + "    a  b  c  d  e  f  g  h ");
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
        outputString = outputString.concat(darkGray + "    a  b  c  d  e  f  g  h ");
        print(outputString);
    }

    private String getPieceType(int row, int col) {
        var piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return " ";
        }
        var type = piece.getPieceType();
        return switch (type) {
            case ChessPiece.PieceType.BISHOP -> ("B");
            case ChessPiece.PieceType.ROOK -> ("R");
            case ChessPiece.PieceType.KING -> ("K");
            case ChessPiece.PieceType.PAWN -> ("P");
            case ChessPiece.PieceType.KNIGHT -> ("N");
            case ChessPiece.PieceType.QUEEN -> ("Q");
        };
    }

    private void print(String string) {
        System.out.println(string);
    }


}
