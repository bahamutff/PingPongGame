package gameEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.TimerTask;

import menu.Menu;

import screens.*;

public class PingPongVSEngine implements Runnable, KeyListener, GameConstants {
	private PingPongGreenTable table;
	private int playerRacket_Y = PLAYER_RACKET_Y_START;
	private int player2Racket_Y = PLAYER2_RACKET_Y_START;
	private int playerScore;
	private int player2Score;
	private int ballX;
	private int ballY;
	private boolean movingLeft = true;
	private boolean ballServed = true;
	private boolean canServe = false;
	private boolean player1Serve = true;
	// ������ ������������ 2 �������
	private boolean startTimer1 = false;
	private boolean startTimer2 = false;
	// �������� ������������� ������������ ����
	private int verticalSlide;

	// �����������
	public PingPongVSEngine(PingPongGreenTable greenTable) {
		table = greenTable;
		Thread worker = new Thread(this);
		worker.start();
	}

	// ������� ��� ������� �������
	java.util.Timer timer1 = new java.util.Timer(); // ����� 1
	java.util.Timer timer2 = new java.util.Timer(); // ����� 2
	// ����� 1
	TimerTask goUp1 = new TimerTask() {
		public void run() {
			if (playerRacket_Y > TABLE_TOP) {
				playerRacket_Y -= RACKET_INCREMENT;
			}
			table.setPlayerRacket_Y(playerRacket_Y);
		}
	};

	TimerTask goDown1 = new TimerTask() {
		public void run() {
			if (playerRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + TABLE_TOP) {
				playerRacket_Y += RACKET_INCREMENT;
			}
			table.setPlayerRacket_Y(playerRacket_Y);
		}
	};

	// ����� 2
	TimerTask goUp2 = new TimerTask() {
		public void run() {
			if (player2Racket_Y > TABLE_TOP) {
				player2Racket_Y -= RACKET_INCREMENT;
			}
			table.setPlayer2Racket_Y(player2Racket_Y);
		}
	};

	TimerTask goDown2 = new TimerTask() {
		public void run() {
			if (player2Racket_Y + RACKET_LENGTH < TABLE_BOTTOM + TABLE_TOP) {
				player2Racket_Y += RACKET_INCREMENT;
			}
			table.setPlayer2Racket_Y(player2Racket_Y);
		}
	};

	// ������������ ������ ���������� KeyListener
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		if (!startTimer1 && e.getKeyCode() == KeyEvent.VK_UP) {
			goUp1 = new TimerTask() {
				public void run() {
					if (playerRacket_Y > TABLE_TOP) {
						playerRacket_Y -= RACKET_INCREMENT;
					}
					table.setPlayerRacket_Y(playerRacket_Y);
				}
			};
			timer1.schedule(goUp1, 0, timeMove);
			startTimer1 = true;
		} else if (!startTimer1 && e.getKeyCode() == KeyEvent.VK_DOWN) {
			goDown1 = new TimerTask() {
				public void run() {
					if (playerRacket_Y + RACKET_LENGTH < TABLE_BOTTOM
							+ TABLE_TOP) {
						playerRacket_Y += RACKET_INCREMENT;
					}
					table.setPlayerRacket_Y(playerRacket_Y);
				}
			};
			timer1.schedule(goDown1, 0, timeMove);
			startTimer1 = true;
		} else if ('n' == key || 'N' == key) {
			startNewGame();
		} else if (!startTimer2 && ('e' == key || 'E' == key)) {
			goUp2 = new TimerTask() {
				public void run() {
					if (player2Racket_Y > TABLE_TOP) {
						player2Racket_Y -= RACKET_INCREMENT;
					}
					table.setPlayer2Racket_Y(player2Racket_Y);
				}
			};
			timer2.schedule(goUp2, 0, timeMove);
			startTimer2 = true;
		} else if (!startTimer2 && ('d' == key || 'D' == key)) {
			goDown2 = new TimerTask() {
				public void run() {
					if (player2Racket_Y + RACKET_LENGTH < TABLE_BOTTOM
							+ TABLE_TOP) {
						player2Racket_Y += RACKET_INCREMENT;
					}
					table.setPlayer2Racket_Y(player2Racket_Y);
				}
			};
			timer2.schedule(goDown2, 0, timeMove);
			startTimer2 = true;
		} else if ('q' == key || 'Q' == key) {
			endGame();
		} else if ('s' == key || 'S' == key) {
			playerServe();
		} else if ('b' == key || 'B' == key) {
			backToMenu();
		}
	}

	public void keyReleased(KeyEvent e) {
		char key = e.getKeyChar();
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			goUp1.cancel();
			startTimer1 = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			goDown1.cancel();
			startTimer1 = false;
		}
		if ('e' == key || 'E' == key) {
			goUp2.cancel();
			startTimer2 = false;
		}
		if ('d' == key || 'D' == key) {
			goDown2.cancel();
			startTimer2 = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	private void setServe() {
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

	// ������ ���
	private void playerServe() {
		if (canServe) {
			ballServed = true;
			if (!player1Serve) {
				movingLeft = true;
				ballX = PLAYER_RACKET_X - BALL_RADIUS;
				ballY = playerRacket_Y + RACKET_LENGTH / 2 - BALL_RADIUS / 2;
				table.setPlayerRacket_Y(playerRacket_Y);
			} else {
				movingLeft = false;
				ballX = LEFT_RACKET_X + RACKET_WIDTH;
				ballY = player2Racket_Y + RACKET_LENGTH / 2 - BALL_RADIUS / 2;
				table.setPlayer2Racket_Y(player2Racket_Y);
			}
			table.setBallPosition(ballX, ballY);
			setServe();
			canServe = false;
		}
	}

	// ������ ����� ����
	private void startNewGame() {
		player2Score = 0;
		playerScore = 0;
		table.setMessageText("Score Player 2: 0 Player 1: 0");
		canServe = true;
		playerServe();
	}

	// ��������� ����
	public void endGame() {
		System.exit(0);
	}

	// ��������� � ����
	public void backToMenu() {
		PingPongGreenTable.f.dispose();
		int codeMenu = 0;
		new Menu(codeMenu);
	}

	private void goLeft(boolean canBounce) {
		if (movingLeft && ballX > BALL_MIN_X) {
			canBounce = (ballY + BALL_RADIUS >= player2Racket_Y && ballY <= player2Racket_Y
					+ RACKET_LENGTH);
			ballX -= BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
		}
		// ������
		if (ballX <= LEFT_RACKET_X + RACKET_WIDTH && canBounce) {
			movingLeft = false;
		}
	}

	private void goRight(boolean canBounce) {
		if (!movingLeft && ballX <= BALL_MAX_X) {
			canBounce = (ballY + BALL_RADIUS >= playerRacket_Y && ballY <= playerRacket_Y
					+ RACKET_LENGTH);
			ballX += BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
			// ������
			if (ballX + BALL_RADIUS >= PLAYER_RACKET_X && canBounce
					&& ballX < PLAYER_RACKET_X) {
				movingLeft = true;
			}
		}
	}

	// �������� ��� ������� �����������
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
				player2Score++;
				player1Serve = false;
				ballX += 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			} else if (ballX <= BALL_MIN_X && ballX > BALL_MIN_X - 100) {
				playerScore++;
				player1Serve = true;
				ballX -= 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			}
		}
	}

	private void displayScore() {
		if (player2Score == WINNING_SCORE) {
			table.setMessageText("Player 2 won! " + player2Score + ":"
					+ playerScore);
			ballServed = false;
		} else if (playerScore == WINNING_SCORE) {
			table.setMessageText("Player 1 won! " + playerScore + ":"
					+ player2Score);
			ballServed = false;
		} else {
			table.setMessageText("Player 2: " + player2Score + "   "
					+ "Player 1: " + playerScore);
			canServe = true;
		}
	}

	private void ballBounce() {
		if (ballY <= TABLE_TOP
				|| ballY + BALL_RADIUS >= TABLE_BOTTOM + TABLE_TOP) {
			verticalSlide *= -1;
		}
	}

	public void run() {
		boolean canBounce = false;
		while (true) {
			if (ballServed) {
				// ��� �������� �����?
				goLeft(canBounce);
				// ��� �������� ������?
				goRight(canBounce);
				// �������������
				setDelay();
				// �������� ����
				updateScore();
				// ������ ����
				ballBounce();
			}
		}
	}
}
