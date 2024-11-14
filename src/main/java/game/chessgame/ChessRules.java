package game.chessgame;

class ChessRules {
    private String[][] top;
    private String[][] bottom;
    private String player;
    private boolean isCheck = false;
    private final int[] checkPos = new int[2];
    private final ChessPieces chessPiece = new ChessPieces();
    private final int[][] plusMoves = {
            {0,1},{0, -1},
            {1,0}, {-1,0}
    };
    private final int[][] crossMoves = {
            {1,1}, {1, -1},
            {-1,1}, {-1,-1}
    };
    private final int[][] knightMoves = {
            {1, 2}, {2, 1}, {1, -2}, {2, -1},
            {-1, 2}, {-2, 1}, {-1, -2}, {-2, -1},
    };

    protected void setPosition(String[][]top, String[][]bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    protected void setPlayer(String player) {
        this.player = player;
    }


    protected int[] findKing(String[][] opponent) {
        for (int i = 0; i < opponent.length; i++) {
            for (int j = 0; j < opponent[i].length; j++) {
                if (opponent[i][j].equals("king")) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1};
    }

    protected boolean potentialCheckMate (String pieceColor, int pieceRow, int pieceCol, int oldRow, int oldCol){
        String[][] side = new String[8][];
        String[][] oppSide = new String[8][];
        for (int i = 0; i < 8; i++){
            if (player.equals(pieceColor)){
                side[i] = java.util.Arrays.copyOf(bottom[i], 8);
                oppSide[i] = java.util.Arrays.copyOf(top[i], 8);
            }
            else{
                side[i] = java.util.Arrays.copyOf(top[i], 8);
                oppSide[i] = java.util.Arrays.copyOf(bottom[i], 8);
            }
        }

        side[pieceRow][pieceCol] = side[oldRow][oldCol];
        side[oldRow][oldCol] = "";

        int[] kingPos = findKing(side);

        if (findCrossMoves(side, oppSide, kingPos[0], kingPos[1])) {return true;}
        else if (findPlusMoves(side, oppSide, kingPos[0], kingPos[1])){ return true;}
        else return findKnightMoves(oppSide, kingPos[0], kingPos[1]);
    }

    protected boolean potentialCastling (String[][] side, String[][] oppSide, int row, int col){
        if (findPlusMoves(side, oppSide, row, col)){ return true;}
        else if (findCrossMoves(side, oppSide, row, col)){ return true;}
        else return findKnightMoves(oppSide, row, col);
    }

    protected boolean findPlusMoves(String[][] side, String[][] oppSide, int row, int col){
        for (int[] move : plusMoves){
            int newRow = row;
            int newCol = col;
            while (true){
                newRow += move[0];
                newCol += move[1];
                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                    if (!side[newRow][newCol].isEmpty()) {break;}
                    else if(!oppSide[newRow][newCol].isEmpty()) {
                        if (oppSide[newRow][newCol].equals("queen") || oppSide[newRow][newCol].equals("rook")) {return true;}
                    }
                }
                else {break;}
            }
        }
        return false;
    }

    protected boolean findCrossMoves(String[][] side, String[][] oppSide, int oldRow, int oldCol){
        for (int[] move : crossMoves) {
            int row = oldRow;
            int col = oldCol;
            while (true){
                row += move[0];
                col += move[1];
                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    if (!side[row][col].isEmpty()) {break;}
                    else if(!oppSide[row][col].isEmpty()) {
                        if (oppSide[row][col].equals("queen") || oppSide[row][col].equals("bishop")) {return true;}
                    }
                }
                else {break;}
            }
        }
        return false;
    }

    protected boolean findKnightMoves( String[][] oppSide, int oldRow, int oldCol){
        for (int[] move : knightMoves) {
            int row = oldRow + move[0];
            int col = oldCol + move[1];
            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                if (!oppSide[row][col].isEmpty()) {
                    if (oppSide[row][col].equals("knight")) {return true;}
                }
            }
        }
        return false;
    }
}
