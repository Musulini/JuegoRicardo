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

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;



public class GameApp extends Application {
	private ConnectorDAO sqlConnection = new ConnectorDAO(SQLConnection.getConnection());
	private int userId;

	private Pane root;
	private int score = 0;
	private boolean sKeyPressed = false;
	private Random random = new Random();

	private Label scoreLabel = new Label("Score: " + score);
	private Label targetLabel = new Label();
	private Label userLabel = new Label();
	private String username;  // nuevo campo para guardar el nombre


	private boolean specialFigureShown = false;
	private final String[] figureTypes = {"circle", "square", "triangle"};
	private final Color[] colors = {
			Color.RED, Color.GREEN, Color.BLUE, Color.PURPLE,
			Color.ORANGE, Color.CYAN, Color.PINK, Color.BROWN
	};

	private String currentTargetType = "circle";

	public GameApp() throws SQLException {
	}

	@Override
	public void start(Stage stage) {
		root = new Pane();
		Scene scene = new Scene(root, 800, 600);
		root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a1a, #2c2c2c);");

		scoreLabel.setTextFill(Color.WHITE);
		scoreLabel.setStyle("-fx-font-size: 24px;");
		scoreLabel.setLayoutX(10);
		scoreLabel.setLayoutY(10);
		root.getChildren().add(scoreLabel);

		targetLabel.setText("Objetivo: " + currentTargetType);
		targetLabel.setTextFill(Color.WHITE);
		targetLabel.setStyle("-fx-font-size: 24px;");
		targetLabel.setLayoutX(300);
		targetLabel.setLayoutY(10);
		root.getChildren().add(targetLabel);

		// Etiqueta de usuario (parte inferior)
		userLabel.setText("Usuario: " + username);
		userLabel.setTextFill(Color.WHITE);
		userLabel.setStyle("-fx-font-size: 18px;");
		userLabel.setLayoutX(10);
		userLabel.setLayoutY(570);  // Parte inferior de una ventana de 600 px
		root.getChildren().add(userLabel);


		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.S) sKeyPressed = true;
		});
		scene.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.S) sKeyPressed = false;
		});

		stage.setScene(scene);
		stage.setTitle("Juego de Figuras");
		stage.show();

// Genera figuras normales cada 1 a 2 segundos durante 1 minuto
		generateFiguresRandomly();

// Crear figura especial una vez durante el minuto
		createSpecialFigure();


		// Fin del juego tras 1 minuto
		PauseTransition endGame = new PauseTransition(Duration.minutes(1));
		endGame.setOnFinished(e -> Platform.runLater(() -> {
			sqlConnection.createScore(userId, score);
			showScoreAlertAndClose(stage);
		}));
		endGame.play();
	}

	private void createMovingFigure(boolean isSpecial) {
		if (isSpecial && specialFigureShown) return;

		Shape shape;
		Color color = isSpecial ? Color.GOLD : colors[random.nextInt(colors.length)];
		String type;

		if (isSpecial) {
			shape = new Circle(20, color);
			type = "special";
			specialFigureShown = true;
		} else {
			type = figureTypes[random.nextInt(3)];
			switch (type) {
				case "circle" -> shape = new Circle(20, color);
				case "square" -> shape = new Rectangle(40, 40);
				default -> {
					Polygon triangle = new Polygon();
					triangle.getPoints().addAll(
							0.0, 40.0,
							20.0, 0.0,
							40.0, 40.0
					);
					triangle.setFill(color);
					shape = triangle;
				}
			}
			shape.setFill(color);
		}

		root.getChildren().add(shape);
		moveFigure(shape, type);
	}

	private void generateFiguresRandomly() {
		PauseTransition generator = new PauseTransition(Duration.seconds(1 + random.nextInt(2)));
		generator.setOnFinished(e -> {
			createMovingFigure(false);
			generateFiguresRandomly(); // llama recursivamente para seguir generando
		});

		// Detener creación después de 1 minuto
		PauseTransition stop = new PauseTransition(Duration.minutes(1));
		stop.setOnFinished(e2 -> generator.stop());

		generator.play();
		stop.play();
	}

	private void moveFigure(Shape shape, String type) {
		double fromX, fromY, toX, toY;

		switch (random.nextInt(4)) {
			case 0 -> {
				fromX = -40;
				toX = 800;
				fromY = random.nextDouble() * 600;
				toY = random.nextDouble() * 600;
			}
			case 1 -> {
				fromX = 840;
				toX = -40;
				fromY = random.nextDouble() * 600;
				toY = random.nextDouble() * 600;
			}
			case 2 -> {
				fromY = -40;
				toY = 600;
				fromX = random.nextDouble() * 800;
				toX = random.nextDouble() * 800;
			}
			default -> {
				fromY = 640;
				toY = -40;
				fromX = random.nextDouble() * 800;
				toX = random.nextDouble() * 800;
			}
		}

		shape.setTranslateX(fromX);
		shape.setTranslateY(fromY);

		TranslateTransition tt = new TranslateTransition(Duration.seconds(5 + random.nextInt(3)), shape);
		tt.setToX(toX);
		tt.setToY(toY);
		tt.setOnFinished(e -> {
			if (!type.equals("special")) {
				root.getChildren().remove(shape);
			} else {
				root.getChildren().remove(shape);
			}
		});
		tt.play();

		// Clic handler
		shape.setOnMouseClicked(e -> {
			if (type.equals("special") && sKeyPressed) {
				score += 5;
				System.out.println("¡Figura especial! Puntos: " + score);
			} else if (type.equals(currentTargetType)) {
				score++;
				System.out.println("¡Correcto! Puntos: " + score);
			} else {
				System.out.println("Figura equivocada.");
			}
			root.getChildren().remove(shape);
			updateScore();
		});
	}

	private void createSpecialFigure() {
		PauseTransition delay = new PauseTransition(Duration.seconds(10 + random.nextInt(20)));
		delay.setOnFinished(e -> createMovingFigure(true));
		delay.play();
	}

	private void updateScore() {
		scoreLabel.setText("Score: " + score);
		currentTargetType = figureTypes[random.nextInt(3)];
		targetLabel.setText("Objetivo: " + currentTargetType);
		System.out.println("Nuevo objetivo: " + currentTargetType);
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

	public static void main(String[] args) {
		launch(args);
	}

	public void setData(String loggedUser) {
		this.userId = sqlConnection.getId(loggedUser);
		this.username = loggedUser;
	}
}
