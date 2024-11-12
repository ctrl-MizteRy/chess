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
    private String player;
    private final ChessRules rules = new ChessRules();
    private boolean checkmate = false;
    private int[][] checkMove;
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
    private Pane pane;
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Scene scene = firstScene();
        primaryStage.setScene(scene);
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
            checkSide(player);
            startGame();
        });
        Button white = new Button("White");
        white.setOnAction(event ->{
            player = "white";
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
        Scene scene = new Scene(pane, 700, 700);
        primaryStage.setScene(scene);
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
                    piece.setFitWidth(50); piece.setFitHeight(50);
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

            System.out.println(color);
            System.out.println(chessPiece);
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
        int col = (int) Math.floor(sceneX / 75) - 1;
        int row = (int) Math.floor(sceneY / 75) - 1 ;

        StackPane destination = (StackPane) board.getChildren().get((row * 8) + col);
        int destinationNodes = destination.getChildren().size();
        if (getMoves(oldRow, oldCol, row, col, chessPiece, color)) {
            if (checkmate) {
                checkmate = rules.isCheckmate(checkMove, row, col);
            }
            if (!checkmate && !rules.potentialCheckMate(color, row, col, oldRow, oldCol)) {
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
                System.out.println("Dropping off: " + row + " " + col);
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
                firstMove = !firstMove;
                secondMove = !secondMove;
                if (rules.checkMate(color, chessPiece, row, col)) {
                    checkmate = true;
                    checkMove = possibleMoves(row, col, chessPiece, color);
                }
            }
        }
        img.setLayoutX(0);
        img.setLayoutY(0);
        startGame();
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
        return switch (piece) {
            case "king" -> chessMoves.King(row, col, top, bottom, color, player);
            case "knight" -> chessMoves.Knight(row, col, top, bottom, color, player);
            case "bishop" -> chessMoves.Bishop(row, col, top, bottom, color, player);
            case "rook" -> chessMoves.Rook(row, col, top, bottom, color, player);
            case "queen" -> chessMoves.Queen(row, col, top, bottom, color, player);
            default -> chessMoves.Pawn(row, col, player, top, bottom, color);
        };
    }

    private StackPane makeBorder(int row, int col){
        StackPane pane = new StackPane();
        Rectangle rec = new Rectangle(75,75);
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
        catch (FileNotFoundException _){

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
        catch (FileNotFoundException _){

        }
        return piece;
    }

    public static void main(String[] args) {Application.launch(args);}
}