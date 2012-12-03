package gameEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import menu.Menu;
import screens.*;
import inetConnection.Client;

public class PingPongClientEngine implements Runnable, KeyListener,
		GameConstants {
	private PingPongGreenTable table;
	private int clientRacket_Y = PLAYER2_RACKET_Y_START; // client
	private int serverRacket_Y = PLAYER_RACKET_Y_START; // server
	private int serverScore;
	private int clientScore;
	private int ballX;
	private int ballY;
	// Значение вертикального передвижения мяча
	private int verticalSlide;
	private boolean movingLeft = false;
	private boolean ballServed = false;
	boolean canBounce = true;
	private boolean canServe = false;
	//
	private boolean isActive = true;
	//
	private boolean pressUp = false;
	private boolean pressDown = false;
	// client socket
	Socket socket = null;
	// Timer
	private boolean startTimer = false;
	private boolean startTimerS = false;
	// Request
	private char request = 0;

	// Конструктор
	public PingPongClientEngine(PingPongGreenTable greenTable)
			throws IOException {
		table = greenTable;
		socket = Client.createClient();
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
				request = Client.getRequestServer(socket);
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

	// Client works
	public void workClient(final Socket socket) throws IOException {
		if (request != 0) {
			switch (request) {
			case 'u':
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
					timerS.schedule(goUpS, 0, timeMove);
				} else {
					goUpS.cancel();
					startTimerS = false;
				}
				break;
			case 'd':
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
					timerS.schedule(goDownS, 0, timeMove);
				} else {
					goDownS.cancel();
					startTimerS = false;
				}
				break;
			case 'n':
				startNewGame();
				break;
			case '+':
				verticalSlide = 1;
				break;
			case '-':
				verticalSlide = -1;
				break;
			case 'e':
				socket.close();
				break;
			case 'g':
				movingLeft = true;
				ballX = PLAYER_RACKET_X - 1;
				ballY = serverRacket_Y;
				table.setPlayerRacket_Y(serverRacket_Y);
				table.setBallPosition(ballX, ballY);
				setServe();
				canServe = false;
				break;
			}
			request = 0;
			getRequest = new TimerTask() {
				public void run() {
					try {
						request = Client.getRequestServer(socket);
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

	// Обязательные методы интерфейса KeyListener
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		if ('e' == key || 'E' == key) {
			if (!startTimer) {
				startTimer = true;
				pressUp = true;
				goUp = new TimerTask() {
					public void run() {
						if (clientRacket_Y > TABLE_TOP) {
							clientRacket_Y -= RACKET_INCREMENT;
						}
						table.setPlayer2Racket_Y(clientRacket_Y);
					}
				};
				Client.clientUp(socket);
				timer.schedule(goUp, 0, timeMove);
			}
		} else if ('d' == key || 'D' == key) {
			if (!startTimer) {
				startTimer = true;
				pressDown = true;
				goDown = new TimerTask() {
					public void run() {
						if (clientRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
							clientRacket_Y += RACKET_INCREMENT;
						}
						table.setPlayer2Racket_Y(clientRacket_Y);
					}
				};
				Client.clientDown(socket);
				timer.schedule(goDown, 0, timeMove);
			}
		} else if ('q' == key || 'Q' == key) {
			endGame();
		} else if ('s' == key || 'S' == key) {
			if (canServe) {
				ballServed = true;
				movingLeft = false;
				ballX = LEFT_RACKET_X + 1;
				ballY = clientRacket_Y;
				table.setPlayer2Racket_Y(clientRacket_Y);
				try {
					Client.sendServe(socket);
				} catch (IOException e1) {
				} finally {
					canServe = false;
				}
				table.setBallPosition(ballX, ballY);
				canServe = false;
			}
		} else if ('b' == key || 'B' == key) {
			backToMenu();
		}
	}

	public void keyReleased(KeyEvent e) {
		char key = e.getKeyChar();
		if (('e' == key || 'E' == key) && pressUp) {
			goUp.cancel();
			Client.clientUp(socket);
			startTimer = false;
			pressUp = false;
		}
		if (('d' == key || 'D' == key) && pressDown) {
			goDown.cancel();
			Client.clientDown(socket);
			startTimer = false;
			pressDown = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	// Начать новую игру
	private void startNewGame() {
		clientScore = 0;
		serverScore = 0;
		table.setMessageText("Score Player 2: 0 Player 1: 0");
		movingLeft = true;
		ballX = PLAYER_RACKET_X - 1;
		ballY = serverRacket_Y;
		table.setPlayerRacket_Y(serverRacket_Y);
		table.setBallPosition(ballX, ballY);
		canServe = false;
		ballServed = true;
	}

	// Завершить игру
	public void endGame() {
		Client.clientExit(socket);
		System.exit(0);
	}

	// Вернуться в меню
	public void backToMenu() {
		PingPongGreenTable.f.dispose();
		Client.clientExit(socket);
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

	private void ballBounce(boolean canBounce) {
		if (ballY <= TABLE_TOP) {
			verticalSlide = -1;
		} else if (ballY >= TABLE_BOTTOM) {
			verticalSlide = 1;
		} else if (canBounce) {
			verticalSlide *= -1;
		}
	}

	private void goLeft() {
		if (movingLeft && ballX > BALL_MIN_X) {
			canBounce = (ballY >= clientRacket_Y && ballY < (clientRacket_Y + RACKET_LENGTH));
			ballX -= BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
		}
		// Отскок
		if (ballX <= LEFT_RACKET_X + RACKET_WIDTH && canBounce) {
			movingLeft = false;
			ballBounce(canBounce);
		}
	}

	private void goRight() {
		if (!movingLeft && ballX <= BALL_MAX_X) {
			canBounce = (ballY >= serverRacket_Y
					&& ballY <= (serverRacket_Y + RACKET_LENGTH) && (ballX + BALL_RADIUS) <= PLAYER_RACKET_X);
			ballX += BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
			// Отскок
			if (ballX + BALL_RADIUS >= PLAYER_RACKET_X && canBounce) {
				movingLeft = true;
				ballBounce(canBounce);
			}
		}
	}

	private void updateScore() {
		if (isBallOnTheTable()) {
			if (ballX > BALL_MAX_X && ballX < BALL_MAX_X + 100) {
				clientScore++;
				ballX += 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			} else if (ballX <= BALL_MIN_X && ballX > BALL_MIN_X - 100) {
				serverScore++;
				canServe = true;
				ballX -= 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			}
		}
	}

	TimerTask ballMove = new TimerTask() {
		public void run() {
			if (ballServed) {
				// Мяч движется влево?
				goLeft();
				// Мяч движется вправо?
				goRight();
				// Приостановить
				setDelay();
				// Обновить счет
				updateScore();
				// Отскок мяча
				ballBounce(false);
			}
		}
	};

	public void run() {
		timerBall.schedule(ballMove, 0, ballSpeed);
		timerR.schedule(getRequest, 0);
		while (isActive) {
			if (socket == null) {
				JOptionPane.showMessageDialog(null, "Server not found");
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
			try {
				workClient(socket);
			} catch (IOException e) {
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

	private boolean isBallOnTheTable() {
		if (ballY >= BALL_MIN_Y && ballY <= BALL_MAX_Y) {
			return true;
		} else {
			return false;
		}
	}

}
