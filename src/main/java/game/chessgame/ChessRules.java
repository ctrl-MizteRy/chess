package game.chessgame;

import java.util.Arrays;

class ChessRules {
    private String[][] top;
    private String[][] bottom;
    private String player;
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
    private ChessPieces chess = new ChessPieces();
    private String checkDirection = "";

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
        setSide(side, oppSide, pieceColor, player);

        side[pieceRow][pieceCol] = side[oldRow][oldCol];
        side[oldRow][oldCol] = "";

        int[] kingPos = findKing(side);

        if (findCross(side, oppSide, kingPos[0], kingPos[1])) {
            checkDirection = "cross";
            return true;
        }
        else if (findPlus(side, oppSide, kingPos[0], kingPos[1])){
            checkDirection = "plus";
            return true;
        }
        else return findKnight(oppSide, kingPos[0], kingPos[1]);
    }

    protected boolean potentialCastling (String[][] side, String[][] oppSide, int row, int col){
        if (findPlus(side, oppSide, row, col)){ return true;}
        else if (findCross(side, oppSide, row, col)){ return true;}
        else return findKnight(oppSide, row, col);
    }

    protected boolean findPlus(String[][] side, String[][] oppSide, int row, int col){
        int[][] moves = findPlusMoves(side, oppSide, row, col);
        return moves[0][0] != -1;
    }

    protected boolean findCross(String[][] side, String[][] oppSide, int oldRow, int oldCol){
        int[][] moves = findCrossMoves(side, oppSide, oldRow, oldCol);
        return moves[0][0] != -1;
    }

    protected boolean findKnight( String[][] oppSide, int oldRow, int oldCol){
        int[][] moves = findKnightMoves(oppSide, oldRow, oldCol);
        return moves[0][0] != -1;
    }

    protected int[][] findPlusMoves (String[][] side, String[][] oppSide, int row, int col){
        int[][] moves = new int[4][];
        int index = 0;
        for (int[] move : plusMoves){
            int newRow = row;
            int newCol = col;
            while (true){
                newRow += move[0];
                newCol += move[1];
                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                    if (!side[newRow][newCol].isEmpty()) {break;}
                    else if(!oppSide[newRow][newCol].isEmpty()) {
                        if (oppSide[newRow][newCol].equals("queen") || oppSide[newRow][newCol].equals("rook")) {
                            moves[index++] = new int[] {newRow, newCol};
                            break;
                        }
                    }
                }
                else {break;}
            }
        }
        return chess.resize(moves, index);
    }

    protected int[][] findCrossMoves(String[][] side, String[][] oppSide, int oldRow, int oldCol){
        int[][] moves = new int[4][2];
        int index = 0;
        for (int[] move : crossMoves) {
            int row = oldRow;
            int col = oldCol;
            while (true){
                row += move[0];
                col += move[1];
                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    if (!side[row][col].isEmpty()) {break;}
                    else if(!oppSide[row][col].isEmpty()) {
                        if (oppSide[row][col].equals("queen") || oppSide[row][col].equals("bishop")) {
                            moves[index++] = new int[]{row, col};
                            break;
                        }
                    }
                }
                else {break;}
            }
        }
        return chess.resize(moves, index);
    }

    protected int[][] findKnightMoves( String[][] oppSide, int oldRow, int oldCol){
        int[][] threatenPos = new int[8][2];
        int index = 0;
        for (int[] move : knightMoves) {
            int row = oldRow + move[0];
            int col = oldCol + move[1];
            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                if (!oppSide[row][col].isEmpty()) {
                    if (oppSide[row][col].equals("knight")) {
                        threatenPos[index++] = new int[] {row, col};
                        break;
                    }
                }
            }
        }
        return chess.resize(threatenPos, index);
    }

    protected void setSide (String[][] side, String[][] oppSide, String pieceColor, String player){
        for (int i = 0; i < 8; i++){
            if (player.equals(pieceColor)){
                side[i] = Arrays.copyOf(bottom[i], 8);
                oppSide[i] = Arrays.copyOf(top[i], 8);
            }
            else{
                side[i] = Arrays.copyOf(top[i], 8);
                oppSide[i] = Arrays.copyOf(bottom[i], 8);
            }
        }
    }

    protected boolean isCheckMate(String player, String color){
        String[][] side = (player.equals(color))? bottom : top;
        int[] kingPos = findKing(side);
        int[][] kingMoves = chess.King(kingPos[0], kingPos[1], top, bottom, color, player, false, false, false);
        if (kingMoves[0][0] == -1){
            int[][] check = new int[8][2];
            check[0] = switch (checkDirection){
               case "cross" -> findCrossMoves(bottom, top, kingPos[0], kingPos[1])[0];
               case "plus" -> findPlusMoves(bottom, top, kingPos[0], kingPos[1])[0];
               case "knight" -> findKnightMoves(top, kingPos[0], kingPos[1])[0];
               default -> new int[] {-1,0};
            };
            if (check[0][0] == -1) {return false;}
            if (!checkDirection.equals("knight")) {
                getCheckDirection(check, kingPos[0], kingPos[1]);
            }
            return PossibleDefenseCheck(side, check, color);
        }
         return false;
    }

    protected void getCheckDirection(int[][] check, int KingRow, int KingCol){
        int row = Integer.compare(KingRow, check[0][0]);
        int col = Integer.compare(KingCol, check[0][1]);
        int index = 0, nextIndex = 1;
        while (KingRow != check[index][0] && KingCol != check[index][1]){
            check[nextIndex][0] = KingRow + row;
            check[nextIndex][1] = KingCol + col;
            index = nextIndex;
            nextIndex++;
        }
    }

    protected boolean PossibleDefenseCheck(String[][] side, int[][] check, String color){
        int row = 0, col;
        for (String[] rows : side){
            col = 0;
            for(String piece : rows){
                if (!piece.isEmpty()){
                    int[][] moves = getPieceMoves(piece, row, col, color);
                    if (checkDefense(check, moves)) {return true;}
                }
                col++;
            }
            row++;
        }
        return false;
    }

    protected int[][] getPieceMoves(String piece,int row, int col, String color){
        boolean[] pawn = new boolean[8];
        Arrays.fill(pawn, true);
        return switch(piece){
            case "knight" -> chess.Knight(row, col, top, bottom, color, player);
            case "bishop" -> chess.Bishop(row, col, top, bottom, color, player);
            case "queen" -> chess.Queen(row, col, top, bottom, color, player);
            case "rook" -> chess.Rook(row, col, top, bottom, color, player);
            default -> chess.Pawn(row, col, player, top, bottom, color, pawn, pawn);
        };
    }

    protected boolean checkDefense(int[][] check, int[][] moves){
        for (int[] move : moves){
            for (int[] checkMove : check){
                if (move[0] == checkMove[0] && move[1] == checkMove[1]){ return true;}
            }
        }
        return false;
    }
}
