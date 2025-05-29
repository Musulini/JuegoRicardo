module com.example.juegoricardo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
	requires java.sql;
	requires jdk.jdi;

	opens com.example.juegoricardo to javafx.fxml;
    exports com.example.juegoricardo;
}