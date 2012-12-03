package gameEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.TimerTask;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

import menu.Menu;

import screens.*;
import inetConnection.Client;
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
	boolean canBounce = false;
	private boolean clientServe = false;
	//
	private boolean pressUp = false;
	private boolean pressDown = false;
	private boolean isActive = true;
	// �������� ������� �� ��������� ������ ����� ���� ���� �� �����������
	// �������
	private boolean gameRun = false;
	// �������� ������������� ������������ ����
	private int verticalSlide;
	// server socket
	Socket socket = null;
	// Timer
	private boolean startTimer = false;
	private boolean startTimerS = false;
	// Request
	private char request = 0;

	// �����������
	public PingPongServerEngine(PingPongGreenTable greenTable)
			throws IOException {
		table = greenTable;
		socket = Server.createServer();
		Thread worker = new Thread(this);
		worker.start();
	}

	java.util.Timer timer = new java.util.Timer();
	java.util.Timer timerBall = new java.util.Timer();
	java.util.Timer timerS = new java.util.Timer();
	java.util.Timer timerR = new java.util.Timer();

	TimerTask getRequest = new TimerTask() {
		public void run() {
			try {
				request = Server.getRequestClient(socket);
			} catch (IOException e) {
			}
			getRequest.cancel();
		}
	};

	TimerTask goUp = new TimerTask() {
		public void run() {
			if (clientRacket_Y > TABLE_TOP) {
				clientRacket_Y -= RACKET_INCREMENT;
			}
			table.setPlayer2Racket_Y(clientRacket_Y);
		}
	};

	TimerTask goDown = new TimerTask() {
		public void run() {
			if (clientRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
				clientRacket_Y += RACKET_INCREMENT;
			}
			table.setPlayer2Racket_Y(clientRacket_Y);
		}
	};

	TimerTask goUpS = new TimerTask() {
		public void run() {
			if (serverRacket_Y > TABLE_TOP) {
				serverRacket_Y -= RACKET_INCREMENT;
			}
			table.setPlayerRacket_Y(serverRacket_Y);
		}
	};

	TimerTask goDownS = new TimerTask() {
		public void run() {
			if (serverRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
				serverRacket_Y += RACKET_INCREMENT;
			}
			table.setPlayerRacket_Y(serverRacket_Y);
		}
	};

	// Server works
	public void workServer(final Socket socket) throws IOException {
		if (request != 0) {
			switch (request) {
			case 'b':
				Server.sendBall(socket, ballX, ballY);
				break;
			case 'u':
				if (!startTimer) {
					goUp = new TimerTask() {
						public void run() {
							if (clientRacket_Y > TABLE_TOP) {
								clientRacket_Y -= RACKET_INCREMENT;
							}
							table.setPlayer2Racket_Y(clientRacket_Y);
						}
					};
					timer.schedule(goUp, 0, timeMove);
					startTimer = true;
					pressUp = true;
				} else if (pressUp) {
					goUp.cancel();
					startTimer = false;
					pressUp = false;
				}
				break;
			case 'd':
				if (!startTimer) {
					goDown = new TimerTask() {
						public void run() {
							if (clientRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
								clientRacket_Y += RACKET_INCREMENT;
							}
							table.setPlayer2Racket_Y(clientRacket_Y);
						}
					};
					timer.schedule(goDown, 0, timeMove);
					startTimer = true;
					pressDown = true;
				} else if (pressDown) {
					goDown.cancel();
					startTimer = false;
					pressDown = false;
				}
				break;
			case 'g':
				canServe = true;
				playerServe();
				break;
			case 'e':
				socket.close();
				break;
			}
			request = 0;
			getRequest = new TimerTask() {
				public void run() {
					try {
						request = Server.getRequestClient(socket);
					} catch (IOException e) {
					}
					getRequest.cancel();
				}
			};
			timerR.schedule(getRequest, 0);
		}
	}

	public void setDelay() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setBounce() {
		// ����� ����� � ��������� [-5, 5]
		Random r = new Random();
		int k = -5 + r.nextInt(10 + 1);
		if (k > 0) {
			verticalSlide = 1;
		}
		if (k < 0) {
			verticalSlide = -1;
		}
	}

	// ������������ ������ ���������� KeyListener
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		if (e.getKeyCode() == e.VK_UP) {
			if (!startTimerS) {
				startTimerS = true;
				goUpS = new TimerTask() {
					public void run() {
						if (serverRacket_Y > TABLE_TOP) {
							serverRacket_Y -= RACKET_INCREMENT;
						}
						table.setPlayerRacket_Y(serverRacket_Y);
					}
				};
				Server.serverUp(socket);
				timerS.schedule(goUpS, 0, timeMove);
			}
		} else if (e.getKeyCode() == e.VK_DOWN) {
			if (!startTimerS) {
				startTimerS = true;
				goDownS = new TimerTask() {
					public void run() {
						if (serverRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
							serverRacket_Y += RACKET_INCREMENT;
						}
						table.setPlayerRacket_Y(serverRacket_Y);
					}
				};
				Server.serverDown(socket);
				timerS.schedule(goDownS, 0, timeMove);
			}
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
			goUpS.cancel();
			Server.serverUp(socket);
			startTimerS = false;
		}
		if (e.getKeyCode() == e.VK_DOWN) {
			goDownS.cancel();
			Server.serverDown(socket);
			startTimerS = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	// ������ ����� ����
	private void startNewGame() {
		clientScore = 0;
		serverScore = 0;
		table.setMessageText("Score Player 2: 0 Player 1: 0");
		canServe = true;
		playerServe();
		Server.startNewGame(socket);
	}

	// ��������� ����
	public void endGame() {
		Server.serverExit(socket);
		System.exit(0);
	}

	// ��������� � ����
	public void backToMenu() {
		PingPongGreenTable.f.dispose();
		Server.serverExit(socket);
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
			canBounce = (ballY >= clientRacket_Y && ballY < (clientRacket_Y + RACKET_LENGTH));
			ballX -= BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
		}
		// ������
		if (ballX <= LEFT_RACKET_X + RACKET_WIDTH && canBounce) {
			movingLeft = false;
			setBounce();
		}
	}

	private void goRight() {
		if (!movingLeft && ballX <= BALL_MAX_X) {
			canBounce = (ballY >= serverRacket_Y
					&& ballY <= (serverRacket_Y + RACKET_LENGTH) && (ballX + BALL_RADIUS) <= PLAYER_RACKET_X);
			ballX += BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
			// ������
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

	TimerTask ballMove = new TimerTask() {
		public void run() {
			if (ballServed) {
				// ��� �������� �����?
				goLeft();
				// ��� �������� ������?
				goRight();
				// �������������
				setDelay();
				// �������� ����
				updateScore();
				// ������ ����
				ballBounce();
			}
		}
	};

	public void run() {
		timerBall.schedule(ballMove, 0, ballSpeed);
		timerR.schedule(getRequest, 0);
		while (isActive) {
			if (socket == null) {
				JOptionPane.showMessageDialog(null,
						"No clients connected. Back to menu");
				backToMenu();
			}
			try {
				if (socket.isClosed() && isActive) {
					JOptionPane.showMessageDialog(null,
							"Connection is lost. Back to menu");
					backToMenu();
				}
			} catch (Exception e) {
				// NULL Exception
			}
			// work to client
			try {
				workServer(socket);
			} catch (IOException e) {
			}
		}
	}

	// ������ ���
	private void playerServe() {
		if (canServe) {
			ballServed = true;
			if (!clientServe) {
				Server.sendServe(socket);
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
