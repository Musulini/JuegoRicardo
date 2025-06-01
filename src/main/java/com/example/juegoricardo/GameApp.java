package com.example.juegoricardo;

import com.example.juegoricardo.model.User;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Random;

public class GameApp extends Application {
	private ConnectorDAO sqlConnection = new ConnectorDAO(SQLConnection.getConnection());
	private int userId;

	private Pane root;
	private int score = 0;
	private boolean sKeyPressed = false;
	private Random random = new Random();

	private Label scoreLabel = new Label("Score: " + score);

	public GameApp() throws SQLException {
	}


	@Override
	public void start(Stage stage) {
		root = new Pane();
		root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a1a, #2c2c2c);");
		Scene scene = new Scene(root, 800, 600);

		scoreLabel.setTextFill(Color.WHITE);
		scoreLabel.setStyle("-fx-font-size: 24px;");
		scoreLabel.setLayoutX(10);
		scoreLabel.setLayoutY(10);
		root.getChildren().add(scoreLabel);

		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.S) sKeyPressed = true;
		});
		scene.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.S) sKeyPressed = false;
		});

		stage.setScene(scene);
		stage.setTitle("Juego de Figuras");
		stage.show();

		// Crear figuras
		for (int i = 0; i < 12; i++) createMovingFigure(false);
		createSpecialFigure();

		// Fin del juego tras 1 minuto
		PauseTransition endGame = new PauseTransition(Duration.minutes(1));
		endGame.setOnFinished(e -> {
			// Detener todas las animaciones
			Platform.runLater(() -> {
				sqlConnection.createScore(userId, score);
				showScoreAlertAndClose(stage);
			});
		});
		endGame.play();
	}

	private void showScoreAlertAndClose(Stage stage) {
		// Usa Platform.runLater *fuera* del proceso de la animación
		Platform.runLater(() -> {
			new Thread(() -> {
				try {
					// Dormir un momento para asegurar que no hay animaciones en ejecución
					Thread.sleep(100);
				} catch (InterruptedException ignored) {
				}

				// Mostrar el Alert en el hilo de JavaFX
				Platform.runLater(() -> {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Fin del Juego");
					alert.setHeaderText("¡Tiempo terminado!");
					alert.setContentText("Tu puntaje fue: " + score);
					alert.showAndWait();
					stage.close();
				});
			}).start();
		});
	}


	private void createMovingFigure(boolean isSpecial) {
		Circle circle = new Circle(20);
		circle.setFill(isSpecial ? Color.GOLD : Color.BLUE);
		root.getChildren().add(circle);

		moveFigure(circle, isSpecial);
	}

	private void moveFigure(Circle circle, boolean isSpecial) {
		double fromX, fromY, toX, toY;

		// Randomiza el punto de entrada y salida
		switch (random.nextInt(4)) {
			case 0 -> { // desde izquierda
				fromX = -40;
				toX = 800;
				fromY = random.nextDouble() * 600;
				toY = random.nextDouble() * 600;
			}
			case 1 -> { // desde derecha
				fromX = 840;
				toX = -40;
				fromY = random.nextDouble() * 600;
				toY = random.nextDouble() * 600;
			}
			case 2 -> { // desde arriba
				fromY = -40;
				toY = 600;
				fromX = random.nextDouble() * 800;
				toX = random.nextDouble() * 800;
			}
			default -> { // desde abajo
				fromY = 640;
				toY = -40;
				fromX = random.nextDouble() * 800;
				toX = random.nextDouble() * 800;
			}
		}

		circle.setTranslateX(fromX);
		circle.setTranslateY(fromY);

		TranslateTransition tt = new TranslateTransition(Duration.seconds(5 + random.nextInt(4)), circle);
		tt.setToX(toX);
		tt.setToY(toY);
		tt.setOnFinished(e -> {
			if (!isSpecial) {
				moveFigure(circle, false); // repetir movimiento si es normal
			} else {
				root.getChildren().remove(circle); // desaparece si es especial
			}
		});
		tt.play();

		// Detectar clics
		circle.setOnMouseClicked(e -> {
			if (isSpecial && sKeyPressed) {
				score += 5;
				System.out.println("¡Figura especial! Puntos: " + score);
				root.getChildren().remove(circle);
				updateScore();
			} else if (!isSpecial) {
				score += 1;
				System.out.println("Figura normal. Puntos: " + score);
				root.getChildren().remove(circle);
				createMovingFigure(false); // reemplazar figura normal
				updateScore();
			}
		});
	}

	private void createSpecialFigure() {
		PauseTransition specialFigurePause = new PauseTransition(Duration.seconds(10 + random.nextInt(30)));
		specialFigurePause.setOnFinished(e -> createMovingFigure(true));
		specialFigurePause.play();
	}

	private void updateScore() {
		scoreLabel.setText("Score: " + score);
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void setData(String loggedUser) {
		this.userId = sqlConnection.getId(loggedUser);
	}
}
