package gameEngine;

import java.awt.event.KeyEvent;
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
	private int clientRacket_Y = PLAYER2_RACKET_Y_START; // client
	private int serverScore;
	private int clientScore;
	private boolean canServe = false;
	// Нажатие клавиш
	private boolean clientPressUp = false;
	private boolean clientPressDown = false;
	//
	private boolean isActive = true;
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

	// Client works
	public void workClient(Socket socket) throws IOException {
		Coord ball;
		ball = Client.getBall(socket);
		System.out.println("ball = Client.getBall(socket);");
		table.setBallPosition(ball.x, ball.y);
		table.setPlayerRacket_Y(Client.getServerY(socket));
		Client.sendClientY(socket, clientRacket_Y);
		System.out.println("Client.sendClientY(socket, player2Racket_Y);");
		Score score;
		score = Client.getScore(socket);
		if (serverScore != score.scoreServer
				|| clientScore != score.scoreClient) {
			if (serverScore != score.scoreServer) {
				canServe = true;
			}
			serverScore = score.scoreServer;
			clientScore = score.scoreClient;
			displayScore();
		}
	}

	public void clientRacketMove(int direction) {
		switch (direction) {
		case 1:
			if (clientRacket_Y > TABLE_TOP) {
				clientRacket_Y -= RACKET_INCREMENT;
			}
			break;
		case -1:
			if (clientRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
				clientRacket_Y += RACKET_INCREMENT;
			}
			break;
		}
		table.setPlayer2Racket_Y(clientRacket_Y);
	}

	public void setDelay() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Обязательные методы интерфейса KeyListener
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		int directionUp = 1;
		int directionDown = -1;
		if ('e' == key || 'E' == key || clientPressUp) {
			clientRacketMove(directionUp);
			clientPressUp = true;
		} else if ('d' == key || 'D' == key || clientPressDown) {
			clientRacketMove(directionDown);
			clientPressDown = true;
		} else if ('q' == key || 'Q' == key) {
			endGame();
		} else if ('s' == key || 'S' == key) {
			if (canServe) {
				try {
					Client.sendServe(socket);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					canServe = false;
				}
			}
		} else if ('b' == key || 'B' == key) {
			backToMenu();
		}
	}

	public void keyReleased(KeyEvent e) {
		char key = e.getKeyChar();
		if ('e' == key || 'E' == key) {
			clientPressUp = false;
		}
		if ('d' == key || 'D' == key) {
			clientPressDown = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	// Завершить игру
	public void endGame() {
		System.exit(0);
	}

	// Вернуться в меню
	public void backToMenu() {
		PingPongGreenTable.f.dispose();
		isActive = false;
		int codeMenu = 0;
		new Menu(codeMenu);
	}

	public void run() {
		clientScore = 0;
		serverScore = 0;
		while (true) {
			if (socket == null) {
				JOptionPane.showMessageDialog(null, "Server not found");
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
			try {
				workClient(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

}
