module game.chessgame {
    requires javafx.controls;
    requires javafx.fxml;


    opens game.chessgame to javafx.fxml;
    exports game.chessgame;
}