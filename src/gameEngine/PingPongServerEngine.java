package gameEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

import menu.Menu;

import screens.*;
import inetConnection.Server;

public class PingPongServerEngine implements Runnable, KeyListener,
		GameConstants {
	private PingPongGreenTable table;
	private int serverRacket_Y = PLAYER_RACKET_Y_START; // server
	private int clientRacket_Y = PLAYER2_RACKET_Y_START; // client
	private int serverScore;
	private int clientScore;
	private int ballX;
	private int ballY;
	private boolean movingLeft = true;
	private boolean ballServed = true;
	private boolean canServe = false;
	private boolean clientServe = false;
	private boolean isActive = true;
	// Параметр который не допускает начала новой игры пока не завершилась
	// текущая
	private boolean gameRun = false;
	// Нажатие клавиш
	private boolean serverPressUp = false;
	private boolean serverPressDown = false;
	// Значение вертикального передвижения мяча
	private int verticalSlide;
	// server socket
	Socket socket = null;

	// Конструктор
	public PingPongServerEngine(PingPongGreenTable greenTable)
			throws IOException {
		table = greenTable;
		socket = Server.createServer();
		Thread worker = new Thread(this);
		worker.start();
	}

	// Server works
	public void workServer(Socket socket) throws IOException {
		char request = Server.getRequestClient(socket);
		System.out.println(request);
		switch (request) {
		case 'b':
			Server.sendBall(socket, ballX, ballY);
			System.out.println("Server.sendBall(socket, ballX, ballY);");
			break;
		case 'p':
			Server.sendServerY(socket, serverRacket_Y);
			System.out.println("Server.sendServerY(socket, playerRacket_Y);");
			break;
		case 'i':
			clientRacket_Y = Server.getClientY(socket);
			table.setPlayer2Racket_Y(clientRacket_Y);
			System.out.println("player2Racket_Y = Server.getClientY(socket);");
			break;
		case 's':
			Server.sendScore(socket, clientScore, serverScore);
			System.out
					.println("Server.sendScore(socket, player2Score, playerScore);");
			break;
		case 'g':
			canServe = true;
			playerServe();
			break;
		}
	}

	public void serverRacketMove(int direction) {
		switch (direction) {
		case 1:
			if (serverRacket_Y > TABLE_TOP) {
				serverRacket_Y -= RACKET_INCREMENT;
			}
			break;
		case -1:
			if (serverRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
				serverRacket_Y += RACKET_INCREMENT;
			}
			break;
		}
		table.setPlayerRacket_Y(serverRacket_Y);
	}

	public void setDelay() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setBounce() {
		// целое число в диапазоне [-5, 5]
		Random r = new Random();
		int k = -5 + r.nextInt(10 + 1);
		if (k > 0) {
			verticalSlide = 1;
		}
		if (k < 0) {
			verticalSlide = -1;
		}
	}

	// Обязательные методы интерфейса KeyListener
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		int directionUp = 1;
		int directionDown = -1;
		if (e.getKeyCode() == e.VK_UP || serverPressUp) {
			serverRacketMove(directionUp);
			serverPressUp = true;
		} else if (e.getKeyCode() == e.VK_DOWN || serverPressDown) {
			serverRacketMove(directionDown);
			serverPressDown = true;
		} else if ('n' == key || 'N' == key) {
			if (!gameRun) {
				startNewGame();
				gameRun = true;
			}
		} else if ('q' == key || 'Q' == key) {
			endGame();
		} else if ('s' == key || 'S' == key) {
			playerServe();
		} else if ('b' == key || 'B' == key) {
			backToMenu();
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == e.VK_UP) {
			serverPressUp = false;
		}
		if (e.getKeyCode() == e.VK_DOWN) {
			serverPressDown = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	// Начать новую игру
	private void startNewGame() {
		clientScore = 0;
		serverScore = 0;
		table.setMessageText("Score Player 2: 0 Player 1: 0");
		canServe = true;
		playerServe();
	}

	// Завершить игру
	public void endGame() {
		System.exit(0);
	}

	// Вернуться в меню
	public void backToMenu() {
		PingPongGreenTable.f.dispose();
		try {
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		isActive = false;
		int codeMenu = 0;
		new Menu(codeMenu);
	}

	private void goLeft(boolean canBounce) {
		if (movingLeft && ballX > BALL_MIN_X) {
			canBounce = (ballY >= clientRacket_Y && ballY < (clientRacket_Y + RACKET_LENGTH));
			ballX -= BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
		}
		// Отскок
		if (ballX <= LEFT_RACKET_X + RACKET_WIDTH && canBounce) {
			movingLeft = false;
			setBounce();
		}
	}

	private void goRight(boolean canBounce) {
		if (!movingLeft && ballX <= BALL_MAX_X) {
			canBounce = (ballY >= serverRacket_Y
					&& ballY <= (serverRacket_Y + RACKET_LENGTH) && (ballX + BALL_RADIUS) <= PLAYER_RACKET_X);
			ballX += BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
			// Отскок
			if (ballX + BALL_RADIUS >= PLAYER_RACKET_X && canBounce) {
				movingLeft = true;
				setBounce();
			}
		}
	}

	private void updateScore() {
		if (isBallOnTheTable()) {
			if (ballX > BALL_MAX_X && ballX < BALL_MAX_X + 100) {
				clientScore++;
				canServe = true;
				clientServe = false;
				ballX += 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			} else if (ballX <= BALL_MIN_X && ballX > BALL_MIN_X - 100) {
				serverScore++;
				clientServe = true;
				ballX -= 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			}
		}
	}

	private void ballBounce() {
		if (ballY <= TABLE_TOP) {
			verticalSlide = -1;
		}
		if (ballY >= TABLE_BOTTOM) {
			verticalSlide = 1;
		}
	}

	public void run() {
		boolean canBounce = false;
		while (true) {
			if (socket == null) {
				JOptionPane.showMessageDialog(null,
						"No clients connected. Back to menu");
				backToMenu();
			}
			try {
				if (socket.isClosed()) {
					JOptionPane.showMessageDialog(null,
							"Connection is lost. Back to menu");
					backToMenu();
				}
			} catch (Exception e) {
				// NULL Exception
			}

			if (!isActive) {
				return;
			}
			// work to client
			try {
				workServer(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (ballServed) {
				// Мяч движется влево?
				goLeft(canBounce);
				// Мяч движется вправо?
				goRight(canBounce);
				// Приостановить
				setDelay();
				// Обновить счет
				updateScore();
				// Отскок мяча
				ballBounce();
			}
		}
	}

	// Подать мяч
	private void playerServe() {
		if (canServe) {
			ballServed = true;
			if (!clientServe) {
				movingLeft = true;
				ballX = PLAYER_RACKET_X - 1;
				ballY = serverRacket_Y;
				table.setPlayerRacket_Y(serverRacket_Y);
			} else {
				movingLeft = false;
				ballX = LEFT_RACKET_X + 1;
				ballY = clientRacket_Y;
				table.setPlayer2Racket_Y(clientRacket_Y);
			}
			table.setBallPosition(ballX, ballY);
			setBounce();
			canServe = false;
		}
	}

	private void displayScore() {
		if (clientScore == WINNING_SCORE) {
			table.setMessageText("Player 2 won! " + clientScore + ":"
					+ serverScore);
			ballServed = false;
			gameRun = false;
		} else if (serverScore == WINNING_SCORE) {
			table.setMessageText("Player 1 won! " + serverScore + ":"
					+ clientScore);
			ballServed = false;
			gameRun = false;
		} else {
			table.setMessageText("Player 2: " + clientScore + "   "
					+ "Player 1: " + serverScore);
		}
	}

	private boolean isBallOnTheTable() {
		if (ballY >= BALL_MIN_Y && ballY <= BALL_MAX_Y) {
			return true;
		} else {
			return false;
		}
	}

}
