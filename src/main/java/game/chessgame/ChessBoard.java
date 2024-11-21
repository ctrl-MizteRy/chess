package game.chessgame;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.application.Application;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class ChessBoard extends Application {
    private final ChessPieces chessMoves = new ChessPieces();
    private boolean firstMove = true, secondMove = false;
    private String player, opp;
    private final ChessRules rules = new ChessRules();
    private final String[][] top = {
            {"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"},
            {"pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn"},
            {"","","","","","","",""},
            {"","","","","","","",""},
            {"","","","","","","",""},
            {"","","","","","","",""},
            {"","","","","","","",""},
            {"","","","","","","",""},
    };
    private final String[][] bottom = {
            {"","","","","","","",""},
            {"","","","","","","",""},
            {"","","","","","","",""},
            {"","","","","","","",""},
            {"","","","","","","",""},
            {"","","","","","","",""},
            {"pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn"},
            {"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"},
    };
    private Stage primaryStage;
    protected Pane pane;
    protected boolean playerSideKingOrigPos = true, oppSideKingOrigPos = true, playerRKing = true, playerRQueen = true, oppRKing = true, oppRQueen = true;
    protected boolean[] playerPawns = new boolean[8], oppPawns = new boolean[8];

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Scene scene = firstScene();
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Chess Board");
        primaryStage.show();
    }

    private Scene firstScene(){
        GridPane firstScene = new GridPane();
        firstScene.setAlignment(Pos.CENTER);
        firstScene.setHgap(10);
        firstScene.setVgap(10);
        Text welcome = new Text("Welcome to Chess, pick a color");
        welcome.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 15));
        firstScene.add(welcome, 0, 0);
        Button black = new Button("Black");
        black.setOnAction(event->{
            player = "black";
            opp = "white";
            checkSide(player);
            startGame();
        });
        Button white = new Button("White");
        white.setOnAction(event ->{
            player = "white";
            opp = "black";
            startGame();
        });
        firstScene.add(white, 0, 1);
        firstScene.add(black, 0, 2);
        pane = firstScene;
        pane.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, null, null)));
        return new Scene(pane, 300, 300);
    }

    private void startGame(){
        pane = getBoard();
        rules.setPlayer(player);
        rules.setPosition(top, bottom);
        checkPawn();
        Scene scene = new Scene(pane, 950, 950);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    private void checkSide(String player){
        if (player.equals("black")){
            String tempPiece = top[0][3];
            top[0][3] = top[0][4];
            top[0][4] = tempPiece;
            tempPiece = bottom[7][3];
            bottom[7][3] = bottom[7][4];
            bottom[7][4] = tempPiece;
        }
    }

    private GridPane getBoard(){
        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane pane = makeBorder(row, col);
                if (!top[row][col].isEmpty()|| !bottom[row][col].isEmpty()) {
                    String color, chessPiece;
                    if (player.equals("black")) {
                        color = (top[row][col].isEmpty()) ? "black" : "white";
                    }
                    else{
                        color = (top[row][col].isEmpty()) ? "white" : "black";
                    }
                    chessPiece = (top[row][col].isEmpty())? bottom[row][col]: top[row][col];
                    color += " " + chessPiece;
                    Image image = getChessPiece(row, col);
                    ImageView piece = new ImageView(image);
                    piece.setPickOnBounds(true);
                    piece.setFitWidth(65); piece.setFitHeight(65);
                    Label pieceColor = new Label(color);
                    pieceColor.setVisible(false);
                    pane.getChildren().addAll(piece, pieceColor);
                    board.add(pane, col, row);
                    draggingPiece(board, pane, row, col, piece);
                }
                else{
                    board.add(pane, col, row);
                }
            }
        }
        return board;
    }

    private void draggingPiece(GridPane board, StackPane piece, int oldRow, int oldCol, ImageView img){
        double[] offset = new double[2];
        String[] pieceColor = ((Label) piece.getChildren().get(2)).textProperty().getValue().split(" ");
        String color = pieceColor[0];
        String chessPiece = pieceColor[1];


        img.setOnMousePressed(event ->{
            offset[0] = event.getX();
            offset[1] = event.getY();

            if (color.equals("black") && secondMove){
                highlightMoves(oldRow, oldCol, chessPiece, color);
            }
            else if(color.equals("white") && firstMove){
                highlightMoves(oldRow, oldCol, chessPiece, color);
            }
        });

        img.setOnMouseDragged(event ->{
            img.setLayoutX(event.getSceneX() - offset[0]);
            img.setLayoutY(event.getSceneY() - offset[1]);
        });

        piece.setOnMouseReleased(event ->{
            if (color.equals("white") && firstMove){
                makeMove(event, board, piece, oldRow, oldCol, img, chessPiece, color);
            }
            else if (color.equals("black") && secondMove){
                makeMove(event, board, piece, oldRow, oldCol, img, chessPiece, color);
            }
        });
    }

    private void highlightMoves(int row, int col, String chessPiece, String pieceColor){
        int[][] highlightedMoves = possibleMoves(row, col, chessPiece, pieceColor);
        StackPane piece = (StackPane) pane.getChildren().get((row*8) + col);
        piece.setBackground(new Background(new BackgroundFill(Color.TEAL, null, null)));
        for(int[] moves : highlightedMoves){
            if (moves[0] == -1){
                break;
            }
            StackPane box = (StackPane) pane.getChildren().get((moves[0]*8) + moves[1]);
            box.setBackground(new Background(new BackgroundFill(Color.TEAL, null, null)));
        }
    }

    private void makeMove(MouseEvent event, GridPane board, StackPane piece, int oldRow, int oldCol, ImageView img, String chessPiece, String color){
        double sceneX = event.getSceneX();
        double sceneY = event.getSceneY() + 35;
        int col = (int) Math.floor(sceneX / 100) - 1;
        int row = (int) Math.floor(sceneY / 100) - 1 ;

        StackPane destination = (StackPane) board.getChildren().get((row * 8) + col);
        int destinationNodes = destination.getChildren().size();
        if (getMoves(oldRow, oldCol, row, col, chessPiece, color)) {
            if (!rules.potentialCheckMate(color, row, col, oldRow, oldCol)) {
                if (destinationNodes > 1) {
                    if (firstMove) {
                        top[row][col] = "";
                    } else {
                        bottom[row][col] = "";
                    }
                }
                if (row != oldRow && col != oldCol) {
                    piece.getChildren().remove(img);
                    destination.getChildren().add(img);
                }
                if (player.equals("white")) {
                    if (color.equals("white")) {
                        bottom[row][col] = bottom[oldRow][oldCol];
                        bottom[oldRow][oldCol] = "";
                    } else {
                        top[row][col] = top[oldRow][oldCol];
                        top[oldRow][oldCol] = "";
                    }
                } else if (player.equals("black")) {
                    if (color.equals("black")) {
                        bottom[row][col] = bottom[oldRow][oldCol];
                        bottom[oldRow][oldCol] = "";
                    } else {
                        top[row][col] = top[oldRow][oldCol];
                        top[oldRow][oldCol] = "";
                    }
                }
                castling(player, color, chessPiece, oldRow, oldCol, row, col);
                if (player.equals(color) && chessPiece.equals("pawn")){
                    if (top[row+1][col].equals("pawn") && oppPawns[col]){
                        top[row+1][col] = "";
                    }
                }
                else if (!player.equals(color) && chessPiece.equals("pawn")){
                    if (bottom[row-1][col].equals("pawn") && playerPawns[col]){
                        bottom[row-1][col] = "";
                    }
                }
                firstMove = !firstMove;
                secondMove = !secondMove;
            }
        }
        img.setLayoutX(0);
        img.setLayoutY(0);
        startGame();
    }

    private void checkPawn(){
        for (int i = 0; i < 8; i++){
            if (!firstMove){
                if (bottom[0][i].equals("pawn")){
                    checkPromotion(player, 0, i);
                }
            }
            else{
                if (top[7][i].equals("pawn")){
                    checkPromotion(opp, 7, i);
                }
            }
        }
    }

    private void checkPromotion( String color, int row, int col){
        if (player.equals(color)){
            if (row == 0){
                getPromotion(row, col, color);
            }
        }
        else {
            if (row == 7){
                getPromotion(row, col, color);
            }
        }
    }

    private void getPromotion(int row,int col, String color){
        StackPane piece = (StackPane) pane.getChildren().get((row * 8) + col);
        GridPane promotionPieces = promotionPieces(color, row, col);
        piece.getChildren().add(promotionPieces);
    }

    private GridPane promotionPieces(String color, int row, int col){
        GridPane promotion = new GridPane();
        for (int i = 0; i < 4; i++){
            StackPane stack = new StackPane();
            Rectangle rec = new Rectangle(25,25);
            rec.setFill(Color.TEAL);
            Image piece = setPromotionImg(i, color);
            int num = i;
            ImageView img = new ImageView(piece);
            img.setFitWidth(25);
            img.setFitHeight(25);
            img.setPickOnBounds(true);
            img.setOnMouseClicked(event-> {
                    String[][] side = (player.equals(color))? bottom : top;
                    side[row][col] = switch(num) {
                        case 0 -> "rook";
                        case 1 -> "knight";
                        case 2 -> "bishop";
                        default -> "queen";
                    };
                    startGame();
            });
            stack.getChildren().add(rec);
            stack.getChildren().add(img);
            promotion.add(stack, i, 0);
        }
        return promotion;
    }

    private Image setPromotionImg(int i, String color){
        try {
            return switch (i) {
                case 0 ->
                        (color.equals("white")) ? new Image(new FileInputStream("src/main/ChessPics/wr.png")) : new Image(new FileInputStream("src/main/ChessPics/br.png"));
                case 1 ->
                        (color.equals("white")) ? new Image(new FileInputStream("src/main/ChessPics/wkni.png")) : new Image(new FileInputStream("src/main/ChessPics/bkni.png"));
                case 2 ->
                        (color.equals("white")) ? new Image(new FileInputStream("src/main/ChessPics/wb.png")) : new Image(new FileInputStream("src/main/ChessPics/bb.png"));
                default ->
                        (color.equals("white")) ? new Image(new FileInputStream("src/main/ChessPics/wq.png")) : new Image(new FileInputStream("src/main/ChessPics/bq.png"));
            };
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void castling (String player, String color, String chessPiece, int oldRow, int oldCol, int row, int col){
        if (chessPiece.equals("king") && (playerSideKingOrigPos|| oppSideKingOrigPos)){
            if (player.equals(color)) {
                if (color.equals("white")){
                    if (row == 7){
                        if (col == 2){
                            bottom[7][3] = bottom[7][0];
                            bottom[7][0] = "";
                            playerRQueen = false;
                        }
                        else if (col == 6){
                            bottom[7][5] = bottom[7][7];
                            bottom[7][7] = "";
                            playerRKing = false;
                        }
                    }
                }
                else{
                    if (row == 7){
                        if (col == 1){
                            bottom[7][2] = bottom[7][0];
                            bottom[7][0] = "";
                            playerRKing = false;
                        }
                        else if (col == 5){
                            bottom[7][4] = bottom[7][7];
                            bottom[7][7] = "";
                            playerRQueen = false;
                        }
                    }
                }
                playerSideKingOrigPos = false;
            }
            else{
                if (color.equals("white")){
                    if (row == 0){
                        if (col == 1){
                            top[0][2] = top[0][0];
                            top[0][0] = "";
                            oppRQueen = false;
                        }
                        else if (col == 5){
                            top[0][4] = top[0][7];
                            top[0][7] = "";
                            oppRKing = false;
                        }
                    }
                }
                else{
                    if (row == 0){
                        if (col == 2){
                            top[0][3] = top[0][0];
                            top[0][0] = "";
                            oppRKing = false;
                        }
                        else if (col == 6){
                            top[0][5] = top[0][7];
                            top[0][7] = "";
                            oppRQueen = false;
                        }
                    }
                }
                oppSideKingOrigPos = false;
            }
        }
        if (chessPiece.equals("rook")){
            if (player.equals("white")){
                if (player.equals(color)){
                    if (oldRow == 7 && oldCol == 0){ playerRQueen = false;}
                    else if (oldRow == 7 && oldCol == 7){ playerRKing = false;}
                }
                else{
                    if (oldRow == 0 && oldCol == 0){ oppRQueen = false;}
                    else if (oldRow == 0 && oldCol == 7){ oppRKing = false;}
                }
            }
            if (player.equals("black")){
                if (player.equals(color)){
                    if (oldRow == 7 && oldCol == 0){ playerRKing = false;}
                    else if (oldRow == 7 && oldCol == 7){ playerRQueen = false;}
                }
                else{
                    if (oldRow == 0 && oldCol == 0){ oppRKing = false;}
                    else if (oldRow == 0 && oldCol == 7){ oppRQueen = false;}
                }
            }
        }
    }

    private boolean getMoves(int oldRow, int oldCol, int row, int col, String piece, String color){
        int[][] moves = possibleMoves(oldRow, oldCol, piece, color);
        return checkMoves(moves, row, col);
    }

    private boolean checkMoves(int[][] moves, int row, int col){
        for (int[] move : moves) {
            if (move[0] == row && move[1] == col) { return true;}
            if (move[0] == -1){return false;}
        }
        return false;
    }

    private int[][] possibleMoves(int row, int col, String piece, String color){
        boolean kingOrigiPos = (player.equals(color))? playerSideKingOrigPos : oppSideKingOrigPos;
        boolean kingSideCastle = (player.equals(color))? playerRKing : oppRKing;
        boolean queenSideCastle = (player.equals(color))? playerRQueen : oppRQueen;
        return switch (piece) {
            case "king" -> chessMoves.King(row, col, top, bottom, color, player, kingOrigiPos, kingSideCastle, queenSideCastle);
            case "knight" -> chessMoves.Knight(row, col, top, bottom, color, player);
            case "bishop" -> chessMoves.Bishop(row, col, top, bottom, color, player);
            case "rook" -> chessMoves.Rook(row, col, top, bottom, color, player);
            case "queen" -> chessMoves.Queen(row, col, top, bottom, color, player);
            default -> chessMoves.Pawn(row, col, player, top, bottom, color, playerPawns, oppPawns);
        };
    }

    private StackPane makeBorder(int row, int col){
        StackPane pane = new StackPane();
        Rectangle rec = new Rectangle(100,100);
        rec.setStroke(Color.BLACK);
        rec.setFill(Color.TRANSPARENT);
        if ((row + col) % 2 == 0) {
            pane.setBackground(new Background( new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, null)));
        }
        else {
            pane.setBackground(new Background( new BackgroundFill(Color.BURLYWOOD, CornerRadii.EMPTY, null)));
        }
        pane.getChildren().add(rec);
        return pane;
    }

    private Image getChessPiece(int row, int col){
        return (player.equals("white"))? getChessPieceWhite(row, col) : getChessPieceBlack(row, col);
    }

    private Image getChessPieceBlack(int row, int col){
        Image piece = null;
        try {
            if (!top[row][col].isEmpty()) {
                switch (top[row][col]) {
                    case "rook": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wr.png"));
                        break;
                    }
                    case "knight": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wkni.png"));
                        break;
                    }
                    case "bishop": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wb.png"));
                        break;
                    }
                    case "queen": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wq.png"));
                        break;
                    }
                    case "king": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wk.png"));
                        break;
                    }
                    case "pawn": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wp.png"));
                        break;
                    }
                }
            } else if (!bottom[row][col].isEmpty()){
                switch (bottom[row][col]) {
                    case "rook": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/br.png"));
                        break;
                    }

                    case "knight": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bkni.png"));
                        break;
                    }
                    case "bishop": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bb.png"));
                        break;
                    }
                    case "queen": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bq.png"));
                        break;
                    }
                    case "king": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bk.png"));
                        break;
                    }
                    case "pawn": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bp.png"));
                        break;
                    }
                }
            }
        }
        catch (FileNotFoundException ignored){

        }
        return piece;
    }

    private Image getChessPieceWhite(int row, int col){
        Image piece = null;
        try {
            if (!top[row][col].isEmpty()) {
                switch (top[row][col]) {
                    case "rook": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/br.png"));
                        break;
                    }

                    case "knight": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bkni.png"));
                        break;
                    }
                    case "bishop": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bb.png"));
                        break;
                    }
                    case "queen": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bq.png"));
                        break;
                    }
                    case "king": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bk.png"));
                        break;
                    }
                    case "pawn": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/bp.png"));
                        break;
                    }
                }
            } else if (!bottom[row][col].isEmpty()){
                switch (bottom[row][col]) {
                    case "rook": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wr.png"));
                        break;
                    }
                    case "knight": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wkni.png"));
                        break;
                    }
                    case "bishop": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wb.png"));
                        break;
                    }
                    case "queen": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wq.png"));
                        break;
                    }
                    case "king": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wk.png"));
                        break;
                    }
                    case "pawn": {
                        piece = new Image( new FileInputStream("src/main/ChessPics/wp.png"));
                        break;
                    }
                }
            }
        }
        catch (FileNotFoundException ignored){

        }
        return piece;
    }

    public static void main(String[] args) {Application.launch(args);}
}