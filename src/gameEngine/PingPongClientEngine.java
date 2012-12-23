package gameEngine;

import java.awt.event.KeyEvent;
import java.util.TimerTask;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import menu.Menu;
import screens.*;
import inetConnection.Client;

public class PingPongClientEngine implements Runnable, KeyListener,
		GameConstants {
	private PingPongGreenTable table;
	private int serverScore = 0;
	private int clientScore = 0;
	private int clientRacket_Y = PLAYER2_RACKET_Y_START; // Клиент
	//
	private boolean isActive = false;
	//
	private boolean pressUp = false;
	private boolean pressDown = false;
	private boolean clientServe = false;
	// client socket
	Socket socket = null;

	// Конструктор
	public PingPongClientEngine(PingPongGreenTable greenTable)
			throws IOException {
		table = greenTable;
		socket = Client.createClient();
		Thread worker = new Thread(this);
		worker.start();
	}

	java.util.Timer timerC = new java.util.Timer();
	TimerTask work = new TimerTask() {
		public void run() {
			try {
				workClient(socket);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Connection is lost. Back to menu");
				backToMenu();
			}
		}

	};

	// Работа клиента
	public void workClient(final Socket socket) throws IOException {
		Client.sendRequestServer(socket, clientRacket_Y + " " + clientServe);
		clientServe = false;
		// 0-serverRacket_Y 1-ballX 2-ballY 3-wasScore 4-clientScore
		// 5-serverScore
		String request = Client.getRequestServer(socket);
		if (request != null) {
			String[] data = request.split(" ");
			table.setPlayerRacket_Y(Integer.parseInt(data[0]));
			table.setBallPosition(Integer.parseInt(data[1]),
					Integer.parseInt(data[2]));
			if (Boolean.parseBoolean(data[3])) {
				clientScore = Integer.parseInt(data[4]);
				serverScore = Integer.parseInt(data[5]);
				displayScore();
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
		if (('e' == key || 'E' == key) && !pressDown) {
			pressUp = true;
		} else if (('d' == key || 'D' == key) && !pressUp) {
			pressDown = true;
		} else if ('q' == key || 'Q' == key) {
			endGame();
		} else if ('s' == key || 'S' == key) {
			clientServe = true;
		} else if ('b' == key || 'B' == key) {
			backToMenu();
		}
	}

	public void keyReleased(KeyEvent e) {
		char key = e.getKeyChar();
		if (('e' == key || 'E' == key) && pressUp) {
			pressUp = false;
		}
		if (('d' == key || 'D' == key) && pressDown) {
			pressDown = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	// Завершить игру
	public void endGame() {
		try {
			socket.close();
		} catch (Exception e) {
		}
		timerC.cancel();
		System.exit(0);
	}

	// Вернуться в меню
	public void backToMenu() {
		timerC.cancel();
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

	// Задержка для быстрых компьютеров
	public void setDelay() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void displayScore() {
		if (clientScore == WINNING_SCORE) {
			table.setMessageText("Player 2 won! " + clientScore + ":"
					+ serverScore);
		} else if (serverScore == WINNING_SCORE) {
			table.setMessageText("Player 1 won! " + serverScore + ":"
					+ clientScore);
		} else {
			table.setMessageText("Player 2: " + clientScore + "   "
					+ "Player 1: " + serverScore);
		}
	}

	public void run() {
		if (socket == null) {
			JOptionPane.showMessageDialog(null, "Server not found");
			isActive = true;
			backToMenu();
		} else {
			try {
				if (Client.getRequestServer(socket).equals("start game")) {
					isActive = true;
				}
			} catch (IOException e2) {
			}
			timerC.schedule(work, 0, 5);
		}
		while (isActive) {
			if (pressUp) {
				if (clientRacket_Y > TABLE_TOP) {
					clientRacket_Y -= RACKET_INCREMENT;
				}
				table.setPlayer2Racket_Y(clientRacket_Y);
			}
			if (pressDown) {
				if (clientRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + TABLE_TOP) {
					clientRacket_Y += RACKET_INCREMENT;
				}
				table.setPlayer2Racket_Y(clientRacket_Y);
			}
			// Приостановить
			setDelay();
		}
	}
}
