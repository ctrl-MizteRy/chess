package game.chessgame;

class ChessPieces {
    private int index = 0;
    protected int[][] Pawn(int row, int col, String player, String[][] top, String[][] bottom, String pieceColor) {
        int[][] moves = new int[4][2];
        int index = 0;
        if (player.equals(pieceColor) && row == 6){
            moves[index++] = new int[]{row - 1, col};
            moves[index++] = new int[] {row -2 , col};
        }
        else if (player.equals(pieceColor) && row != 0){
            if (top[row-1][col].isEmpty()) {
                moves[index++] = new int[]{row - 1, col};
            }
        }
        else if (!player.equals(pieceColor) && row == 1){
            if (bottom[row+1][col].isEmpty()) {
                moves[index++] = new int[]{row + 1, col};
                if (bottom[row+2][col].isEmpty()) {
                    moves[index++] = new int[]{row + 2, col};
                }
            }
        }
        else {
            if (bottom[row+1][col].isEmpty()) {
                moves[index++] = new int[]{row + 1, col};
            }
        }
        if (player.equals(pieceColor)){
            if (!top[row-1][col-1].isEmpty()){
                if (!top[row-1][col+1].isEmpty()){
                    moves[index++] = new int[] {row-1, col+1};
                }
                moves[index++] = new int[] {row-1, col-1};
            }
            else if (!top[row-1][col+1].isEmpty()){
                moves[index++] =  new int[] {row-1, col+1};
            }
        }
        else{
            if (!bottom[row+1][col-1].isEmpty()){
                if (!bottom[row+1][col+1].isEmpty()){
                    moves[index++] = new int[] {row+1, col+1};
                }
                moves[index++] = new int[] {row+1, col-1};
            }
            else if (!bottom[row+1][col+1].isEmpty()){
                moves[index++] = new int[] {row+1, col+1};
            }
        }
        return resize(moves, index);
    }

    protected int[][] Knight(int row, int col, String[][] top, String[][] bottom, String pieceColor, String player) {
        int[][] moves = new int[8][2];
        index = 0;
        int[][] possibleMoves = {
                {1, 2}, {2, 1}, {1, -2}, {2, -1},
                {-1, 2}, {-2, 1}, {-1, -2}, {-2, -1},
        };
        for (int[] position : possibleMoves) {
            int newRow = row + position[0];
            int newCol = col + position[1];

            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                if (player.equals(pieceColor) && bottom[newRow][newCol].isEmpty()) {
                    moves[index] = new int[]{newRow, newCol};
                    index++;
                }
                else if (!player.equals(pieceColor) && top[newRow][newCol].isEmpty()) {
                    moves[index] = new int[]{newRow, newCol};
                    index++;
                }
            }
        }
        if (index < 7){
            return resize(moves, index);
        }
        return moves;
    }

    protected int[][] Bishop(int row, int col, String[][]top, String[][] bottom, String pieceColor, String player) {
        int[][] moves = new int[13][2];
        int moveB = 13;
        int[][] possibleMoves = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1},
        };
        moves = getMoves(row, col, moves, possibleMoves, top, bottom, pieceColor, player, moveB);
        return moves;
    }

    protected int[][] Rook(int row, int col, String[][] top, String[][] bottom, String pieceColor, String player) {
        int[][] moves = new int[14][2];
        int moveR = 14;
        int[][] possibleMoves = {
                {1,0}, {-1,0}, {0,1}, {0,-1}
        };
        moves = getMoves(row, col, moves, possibleMoves, top, bottom, pieceColor, player, moveR);
        return moves;
    }

    protected int[][] King(int row, int col, String[][] top, String[][] bottom, String pieceColor, String player, boolean kingOrigiPos, boolean kingSide, boolean queenSide) {
        int[][] moves = new int[10][2];
        index = 0;
        int[][] possibleMoves = {
                {1,1}, {1, -1}, {1, 0},
                {0,1}, {0,-1},
                {-1,1}, {-1, -1}, {-1, 0},
        };
        for (int[] position : possibleMoves) {
            int newRow = row + position[0];
            int newCol = col + position[1];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                if (pieceColor.equals(player) && bottom[newRow][newCol].isEmpty()){
                    moves[index] = new int[]{newRow, newCol};
                    index++;
                }
                else if (!pieceColor.equals(player) && top[newRow][newCol].isEmpty()){
                    moves[index] = new int[]{newRow, newCol};
                    index++;
                }
            }
        }

        ChessRules rules = new ChessRules();
        if (kingOrigiPos && kingSide) {
            if (kingSideCastle(top, bottom, player, pieceColor, rules)) {
                if (player.equals("white")) {
                    moves[index++] = (player.equals(pieceColor)) ? new int[]{7, 6} : new int[]{0, 6};
                } else {
                    moves[index++] = (player.equals(pieceColor)) ? new int[]{7, 1} : new int[]{0, 1};
                }
            }
        }
        if (kingOrigiPos && queenSide) {
            if (queenSideCastle(top, bottom, player, pieceColor, rules)) {
                if (player.equals("white")) {
                    moves[index++] = (player.equals(pieceColor)) ? new int[]{7, 2} : new int[]{0, 2};
                } else {
                    moves[index++] = (player.equals(pieceColor)) ? new int[]{7, 5} : new int[]{0, 5};
                }
            }
        }
        return resize(moves, index);
    }

    protected int[][] Queen(int row, int col, String[][] top, String[][] bottom, String pieceColor, String player) {
        int[][] moves = new int[27][2];
        int moveQ = 27;
        int[][] possibleMoves = {
                {1,1}, {1, -1}, {1, 0},
                {0,1}, {0,-1},
                {-1,1}, {-1, -1}, {-1, 0},
        };
        moves = getMoves(row, col, moves, possibleMoves, top, bottom, pieceColor, player, moveQ);
        return moves;
    }

    protected int[][] resize(int[][] moves, int len) {
        if (len == 0){
            moves = new int[1][2];
            moves[0] = new int[]{-1, 0};
            return moves;
        }
        else {
            int[][] newMoves = new int[len][2];
            System.arraycopy(moves, 0, newMoves, 0, len);
            return newMoves;
        }
    }

    protected int[][] getMoves(int row, int col, int[][] moves, int[][] possibleMoves, String[][] top, String[][] bottom, String pieceColor, String player, int maxMove) {
        index = 0;
        for (int[] position : possibleMoves) {
            int newRow = row, newCol = col;
            while (true){
                newRow += position[0];
                newCol +=  position[1];
                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                    if (pieceColor.equals(player) && !bottom[newRow][newCol].isEmpty()){ break;}
                    else if (!pieceColor.equals(player) && !top[newRow][newCol].isEmpty()) { break;}
                    moves[index] = new int[]{newRow, newCol};
                    index++;
                    if (!top[newRow][newCol].isEmpty() || !bottom[newRow][newCol].isEmpty()){
                        break;
                    }
                }
                else {break;}
            }
        }
        if (index < maxMove){
            return resize(moves, index);
        }
        return moves;
    }

    protected boolean kingSideCastle(String[][] oppSide, String[][] side, String player, String pieceColor, ChessRules rules) {
        int n = (player.equals("white"))? 5 : 1;
        int m = (player.equals("white"))? 7 : 3;
        for (int i = n; i < m; i++) {
            if (player.equals(pieceColor)) {
                if (!side[7][i].isEmpty()) { return false;}
                else if (rules.potentialCastling(side, oppSide, 7, i)) { return false;}
            }
            else{
                if (!oppSide[0][i].isEmpty()) { return false;}
                else if (rules.potentialCastling(oppSide, side, 0, i)) { return false;}
            }
        }
        return true;
    }

    protected boolean queenSideCastle(String[][] oppSide, String[][] side, String player, String pieceColor, ChessRules rules){
        int n = (player.equals("white"))? 1 : 4;
        int m = (player.equals("white"))? 4 : 7;
        for (int i = n; i < m; i++) {
            if (player.equals(pieceColor)) {
                if (!side[7][i].isEmpty()) { return false;}
                else if (rules.potentialCastling(side, oppSide, 7, i)) { return false;}
            }
            else {
                if (!oppSide[0][i].isEmpty()) { return false;}
                else if (rules.potentialCastling(oppSide, side, 0, i)) { return false;}
            }
        }
        return true;
    }

}
