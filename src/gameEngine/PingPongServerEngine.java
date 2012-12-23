package gameEngine;

import java.awt.event.KeyEvent;
import java.util.TimerTask;
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
	private int serverRacket_Y = PLAYER_RACKET_Y_START; // Сервер
	private int clientRacket_Y = PLAYER2_RACKET_Y_START; // Клиент
	private int serverScore;
	private int clientScore;
	private boolean wasScore = false;
	private int ballX = -BALL_START_X;
	private int ballY = -BALL_START_Y;
	private boolean movingLeft = true;
	private boolean ballServed = true;
	private boolean canServe = false;
	boolean canBounce = false;
	private boolean clientServe = false;
	//
	private boolean pressUpServer = false;
	private boolean pressDownServer = false;
	private boolean isActive = true;
	// Параметр который не допускает начала новой игры пока не завершилась
	// текущая
	private boolean gameRun = false;
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

	java.util.Timer timerS = new java.util.Timer();
	TimerTask work = new TimerTask() {
		public void run() {
			try {
				workServer(socket);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Connection is lost. Back to menu");
				backToMenu();
			}
		}

	};

	// Работа сервера
	public void workServer(final Socket socket) throws IOException {
		// 0-clientRacket_Y 1-clientServe
		String request = Server.getRequestClient(socket);
		if (request != null) {
			String[] data = request.split(" ");
			if (wasScore) {
				Server.sendRequestClient(socket, serverRacket_Y + " " + ballX
						+ " " + ballY + " " + wasScore + " " + clientScore
						+ " " + serverScore);
			} else {
				Server.sendRequestClient(socket, serverRacket_Y + " " + ballX
						+ " " + ballY + " " + wasScore);
			}
			clientRacket_Y = Integer.parseInt(data[0]);
			table.setPlayer2Racket_Y(clientRacket_Y);
			if (Boolean.parseBoolean(data[1]) && clientServe && canServe) {
				setServe();
				playerServe();
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"Connection is lost. Back to menu");
			backToMenu();
		}
	}

	// Обязательные методы интерфейса KeyListener
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		if (e.getKeyCode() == KeyEvent.VK_UP && !pressUpServer && !pressDownServer) {
			pressUpServer = true;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN && !pressUpServer
				&& !pressDownServer) {
			pressDownServer = true;
		} else if ('n' == key || 'N' == key) {
			if (!gameRun) {
				startNewGame();
				gameRun = true;
			}
		} else if ('q' == key || 'Q' == key) {
			endGame();
		} else if ('s' == key || 'S' == key) {
			if (!clientServe) {
				playerServe();
			}
		} else if ('b' == key || 'B' == key) {
			backToMenu();
		}
	}

	public void keyReleased(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_UP) && pressUpServer) {
			pressUpServer = false;
		}
		if ((e.getKeyCode() == KeyEvent.VK_DOWN) && pressDownServer) {
			pressDownServer = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	private void setServe() {
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

	// Подать мяч
	private void playerServe() {
		if (canServe) {
			ballServed = true;
			if (!clientServe) {
				movingLeft = true;
				ballX = PLAYER_RACKET_X - BALL_RADIUS;
				ballY = serverRacket_Y + RACKET_LENGTH / 2 - BALL_RADIUS / 2;
				table.setPlayerRacket_Y(serverRacket_Y);
			} else {
				movingLeft = false;
				ballX = LEFT_RACKET_X + RACKET_WIDTH;
				ballY = clientRacket_Y + RACKET_LENGTH / 2 - BALL_RADIUS / 2;
				table.setPlayer2Racket_Y(clientRacket_Y);
			}
			table.setBallPosition(ballX, ballY);
			setServe();
			canServe = false;
		}
	}

	// Начать новую игру
	private void startNewGame() {
		clientScore = 0;
		serverScore = 0;
		table.setMessageText("Score Player 2: 0 Player 1: 0");
		canServe = true;
		clientServe = false;
		wasScore = true;
		setServe();
		playerServe();
	}

	// Завершить игру
	public void endGame() {
		try {
			socket.close();
		} catch (Exception e) {
		}
		timerS.cancel();
		System.exit(0);
	}

	// Вернуться в меню
	public void backToMenu() {
		timerS.cancel();
		PingPongGreenTable.f.dispose();
		try {
			socket.close();
		} catch (Exception e) {
		}
		if (isActive) {
			isActive = false;
			int codeMenu = 0;
			new Menu(codeMenu);
		}
	}

	private void goLeft() {
		if (movingLeft && ballX > BALL_MIN_X) {
			canBounce = (ballY + BALL_RADIUS >= clientRacket_Y && ballY <= clientRacket_Y
					+ RACKET_LENGTH);
			ballX -= BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
		}
		// Отскок
		if (ballX <= LEFT_RACKET_X + RACKET_WIDTH && canBounce) {
			movingLeft = false;
		}
	}

	private void goRight() {
		if (!movingLeft && ballX <= BALL_MAX_X) {
			canBounce = (ballY + BALL_RADIUS >= serverRacket_Y && ballY <= serverRacket_Y
					+ RACKET_LENGTH);
			ballX += BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
			// Отскок
			if (ballX + BALL_RADIUS >= PLAYER_RACKET_X && canBounce
					&& ballX < PLAYER_RACKET_X) {
				movingLeft = true;
			}
		}
	}

	// Задержка для быстрых компьютеров
	public void setDelay() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean isBallOnTheTable() {
		if (ballY >= BALL_MIN_Y && ballY <= BALL_MAX_Y) {
			return true;
		} else {
			return false;
		}
	}

	private void updateScore() {
		if (isBallOnTheTable()) {
			if (ballX > BALL_MAX_X && ballX < BALL_MAX_X + 100) {
				clientScore++;
				wasScore = true;
				canServe = true;
				clientServe = false;
				ballX += 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			} else if (ballX <= BALL_MIN_X && ballX > BALL_MIN_X - 100) {
				serverScore++;
				wasScore = true;
				canServe = true;
				clientServe = true;
				ballX -= 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			}
		}
	}

	private void displayScore() {
		if (clientScore == WINNING_SCORE) {
			table.setMessageText("Player 2 won! " + clientScore + ":"
					+ serverScore);
			ballServed = false;
			gameRun = false;
			canServe = false;
		} else if (serverScore == WINNING_SCORE) {
			table.setMessageText("Player 1 won! " + serverScore + ":"
					+ clientScore);
			ballServed = false;
			gameRun = false;
			canServe = false;
		} else {
			table.setMessageText("Player 2: " + clientScore + "   "
					+ "Player 1: " + serverScore);
		}
	}

	private void ballBounce() {
		if (ballY <= TABLE_TOP
				|| ballY + BALL_RADIUS >= TABLE_BOTTOM + TABLE_TOP) {

			verticalSlide *= -1;
		}
	}

	public void run() {
		table.setBallPosition(ballX, ballY);
		if (socket == null) {
			JOptionPane.showMessageDialog(null,
					"No clients connected. Back to menu");
			backToMenu();
		} else {
			Server.sendRequestClient(socket, "start game");
			timerS.schedule(work, 0, 5);
		}
		while (isActive) {
			if (pressUpServer) {
				if (serverRacket_Y > TABLE_TOP) {
					serverRacket_Y -= RACKET_INCREMENT;
				}
				table.setPlayerRacket_Y(serverRacket_Y);
			}
			if (pressDownServer) {
				if (serverRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + TABLE_TOP) {
					serverRacket_Y += RACKET_INCREMENT;
				}
				table.setPlayerRacket_Y(serverRacket_Y);
			}
			if (ballServed) {
				// Мяч движется влево?
				goLeft();
				// Мяч движется вправо?
				goRight();
				// Обновить счет
				updateScore();
				// Отскок мяча
				ballBounce();
			}
			// Приостановить
			setDelay();
		}
	}
}
