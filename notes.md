# My Notes:

### September 16th Notes:
#### Good software:
* Solves the customer's problem today
* Also solves tomorrow's problem

We do this via Abstraction, Immutability, and Encapsulation

#### Abstraction: Appearing to the need
Giving the information need to know in the context you are in.
#### Polymorphism: many forms -- How we achieve abstraction
Achieved with interfaces (implements) and inheritance (extends)

An interface defines the role of something, the inheritance utilizes it. See exampleCode.txt for examples.

Compare an interface to a game controller and an inheritance to the game code that "assigns" the buttons



## Old code for outputting board:
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
