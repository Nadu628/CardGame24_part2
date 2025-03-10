module org.example.cardgame24_part2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.cardgame24_part2 to javafx.fxml;
    exports org.example.cardgame24_part2;
}