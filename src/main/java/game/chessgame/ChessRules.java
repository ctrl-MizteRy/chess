package game.chessgame;

class ChessRules {
    private String[][] top;
    private String[][] bottom;
    private String player;
    private boolean isCheck = false;
    private int[] checkPos = new int[2];
    private final ChessPieces chessPiece = new ChessPieces();

    public void setPosition(String[][]top, String[][]bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public boolean checkMate(String pieceColor, String piece, int row, int col) {
        int[][] possibleMoves = switch(piece){
            case "bishop" -> chessPiece.Bishop(row, col, top, bottom, pieceColor, player);
            case "rook" -> chessPiece.Rook(row, col, top, bottom, pieceColor, player);
            case "knight" -> chessPiece.Knight(row, col, top, bottom, pieceColor, player);
            case "queen" -> chessPiece.Queen(row, col, top, bottom, pieceColor, player);
            default -> chessPiece.Pawn(row, col, player, top, bottom, pieceColor);
        };
        int[] kingPos;
        if (player.equals(pieceColor)) {
            kingPos = findKing(top);
            if (kingPos[0] == -1){ return false; }
            for (int[] move : possibleMoves) {
                if (move[0] == kingPos[0] && move[1] == kingPos[1]) {
                    isCheck = true;
                    checkPos[0] = row;
                    checkPos[1] = col;
                    return true;
                }
            }
        }
        else{
            kingPos = findKing(bottom);
            if (kingPos[0] == -1){ return false; }
            for (int[] move : possibleMoves) {
                if (move[0] == kingPos[0] && move[1] == kingPos[1]) {
                    isCheck = true;
                    checkPos[0] = row;
                    checkPos[1] = col;
                    return true;
                }
            }
        }
        return false;
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

    public boolean isCheckmate(int[][] moves, int row, int col){
        if (isCheck){
            if (checkPos[0] == row && checkPos[1] == col) { return false;}
        }
        for (int[] move : moves){
            if (move[0] == row && move[1] == col) { return false;}
        }
        return true;
    }
}
