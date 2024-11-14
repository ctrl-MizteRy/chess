package game.chessgame;

class ChessRules {
    private String[][] top;
    private String[][] bottom;
    private String player;
    private boolean isCheck = false;
    private final int[] checkPos = new int[2];
    private final ChessPieces chessPiece = new ChessPieces();

    public void setPosition(String[][]top, String[][]bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public void setPlayer(String player) {
        this.player = player;
    }


    public int[] findKing(String[][] opponent) {
        for (int i = 0; i < opponent.length; i++) {
            for (int j = 0; j < opponent[i].length; j++) {
                if (opponent[i][j].equals("king")) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1};
    }

    public boolean potentialCheckMate (String pieceColor, int pieceRow, int pieceCol, int oldRow, int oldCol){
        int[][] plusMoves = {
                {0,1},{0, -1},
                {1,0}, {-1,0}
        };
        int[][] crossMoves = {
                {1,1}, {1, -1},
                {-1,1}, {-1,-1}
        };
        int[][] knightMoves = {
            {1, 2}, {2, 1}, {1, -2}, {2, -1},
            {-1, 2}, {-2, 1}, {-1, -2}, {-2, -1},
        };
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
        for (int[] move : crossMoves) {
            int row = kingPos[0];
            int col = kingPos[1];
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
        for (int[] move : plusMoves){
            int row = kingPos[0];
            int col = kingPos[1];
            while (true){
                row += move[0];
                col += move[1];
                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    if (!side[row][col].isEmpty()) {break;}
                    else if(!oppSide[row][col].isEmpty()) {
                        if (oppSide[row][col].equals("queen") || oppSide[row][col].equals("rook")) {return true;}
                    }
                }
                else {break;}
            }
        }
        for (int[] move : knightMoves) {
            int row = kingPos[0] + move[0];
            int col = kingPos[1] + move[1];
            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                if (!oppSide[row][col].isEmpty()) {
                    if (oppSide[row][col].equals("knight")) {return true;}
                }
            }
        }
        return false;
    }
}
