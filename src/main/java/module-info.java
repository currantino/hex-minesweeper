module com.github.currantino {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.github.currantino to javafx.fxml;
    exports com.github.currantino;
}