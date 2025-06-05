package com.example.juegoricardo.controller;

import com.example.juegoricardo.ConnectorDAO;
import com.example.juegoricardo.GameApp;
import com.example.juegoricardo.SQLConnection;
import com.example.juegoricardo.UserScore;
import com.example.juegoricardo.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LoginController {
	////////////////
	////Conexión////
	////////////////
	private ConnectorDAO connector = null;

	/////////////////
	////REACTIVOS////
	/////////////////
	@FXML
	private Button signUpBtn;
	@FXML
	private Button signInBtn;
	@FXML
	private AnchorPane signInContainer;
	@FXML
	private AnchorPane signUpContainer;

	////////////////////
	////SIGN UP DATA////
	////////////////////
	@FXML
	TextField nombre;
	@FXML
	TextField materno;
	@FXML
	TextField paterno;
	@FXML
	TextField username;
	@FXML
	PasswordField pass;
	@FXML
	PasswordField confPass;
	@FXML
	TextField email;

	///////////////////
	////LOG IN DATA////
	///////////////////
	@FXML
	TextField logUser;
	@FXML
	PasswordField logPass;

	//////////////////////
	////LISTA USUARIOS////
	//////////////////////
	@FXML
	ListView<VBox> userContainer;

	private void signUpUser() {
		User user = null;
		if (pass.getText().equals(confPass.getText())) {
			user = new User(nombre.getText(), paterno.getText(), materno.getText(), email.getText(), username.getText(), pass.getText());
			connector.createUser(user);
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Ok");
			alert.setHeaderText(null);
			alert.setContentText("Registro Exitoso");
			alert.showAndWait();
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Las contraseñas no coinciden");
			alert.showAndWait();
		}
	}

	private void signInUser() throws IOException, SQLException {
		String[] data = connector.getLoggedUser(logUser.getText());
		if (data[0] != null) {
			if (data[1].equals(logPass.getText())) {

				GameApp game = new GameApp();
				game.setData(logUser.getText());
				Stage stage = new Stage();
				game.start(stage);


//				FXMLLoader fxmlLoader = new FXMLLoader(JuegoApp.class.getResource("game.fxml"));
//
//				Scene scene = new Scene(fxmlLoader.load());
//				Stage newStage = new Stage();
//				newStage.setTitle("Mi lol");
//				newStage.setScene(scene);
//				newStage.show();

				/*Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Ok");
				alert.setHeaderText(null);
				alert.setContentText("Inicio de Sesión");
				alert.showAndWait();*/
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Contraseña Incorrecta");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Usuario no encontrado");
			alert.showAndWait();
		}
	}
	private void populateUserScores() {
		List<UserScore> scores = connector.getAllUserScores();
		userContainer.getItems().clear(); // Clear previous entries

		for (UserScore userScore : scores) {
			Label usernameLabel = new Label("Username: " + userScore.getUsername());
			usernameLabel.setTextFill(Color.WHITE);
			usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

			Label scoreLabel = new Label("Score: " + userScore.getScore());
			scoreLabel.setTextFill(Color.LIGHTGRAY);
			scoreLabel.setStyle("-fx-font-size: 13px;");

			VBox vbox = new VBox(4); // Spacing between labels
			vbox.getChildren().addAll(usernameLabel, scoreLabel);
			vbox.setStyle("-fx-background-color: #2e2e2e; -fx-padding: 8px; -fx-background-radius: 6px;");

			userContainer.getItems().add(vbox);
		}
	}


	@FXML
	public void initialize() throws SQLException {
		connector = new ConnectorDAO(SQLConnection.getConnection());
		signUpBtn.setOnAction(e -> {
			signUpUser();
		});
		signInBtn.setOnAction(e -> {
            try {
                signInUser();
				System.out.println("Juego");
            } catch (IOException | SQLException ex) {
				System.out.println(ex.getMessage() + "moomom " );
                throw new RuntimeException(ex);
            }
        });
		populateUserScores();
	}

}