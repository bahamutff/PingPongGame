package gameEngine;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.TimerTask;

import menu.*;
import screens.*;

public class PingPongTrainingEngine implements Runnable, KeyListener,
		GameConstants {
	private PingPongGreenTable table;
	private int playerRacket_Y = PLAYER_RACKET_Y_START;
	private int computerRacket_Y = COMPUTER_RACKET_Y_START;
	private int computerDir = 1;
	private int playerScore;
	private int computerScore;
	private int ballX;
	private int ballY;
	private boolean movingLeft = true;
	private boolean ballServed = true;
	private boolean canServe = false;
	private boolean startTimer = false;
	// �������� ������������� ������������ ����
	private int verticalSlide;

	// �����������
	public PingPongTrainingEngine(PingPongGreenTable greenTable) {
		table = greenTable;
		Thread worker = new Thread(this);
		worker.start();
	}

	// Timers
	java.util.Timer timer = new java.util.Timer();
	TimerTask goUp = new TimerTask() {
		public void run() {
			if (playerRacket_Y > TABLE_TOP) {
				playerRacket_Y -= RACKET_INCREMENT;
			}
			table.setPlayerRacket_Y(playerRacket_Y);
		}
	};

	TimerTask goDown = new TimerTask() {
		public void run() {
			if (playerRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + TABLE_TOP) {
				playerRacket_Y += RACKET_INCREMENT;
			}
			table.setPlayerRacket_Y(playerRacket_Y);
		}
	};

	// ������������ ������ ���������� KeyListener
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		if (!startTimer && e.getKeyCode() == e.VK_UP) {
			goUp = new TimerTask() {
				public void run() {
					if (playerRacket_Y > TABLE_TOP) {
						playerRacket_Y -= RACKET_INCREMENT;
					}
					table.setPlayerRacket_Y(playerRacket_Y);
				}
			};
			timer.schedule(goUp, 0, timeMove);
			startTimer = true;
		} else if (!startTimer && e.getKeyCode() == e.VK_DOWN) {
			goDown = new TimerTask() {
				public void run() {
					if (playerRacket_Y + RACKET_LENGTH < TABLE_BOTTOM
							+ TABLE_TOP) {
						playerRacket_Y += RACKET_INCREMENT;
					}
					table.setPlayerRacket_Y(playerRacket_Y);
				}
			};
			timer.schedule(goDown, 0, timeMove);
			startTimer = true;
		} else if ('n' == key || 'N' == key) {
			startNewGame();
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
			goUp.cancel();
			startTimer = false;
		}
		if (e.getKeyCode() == e.VK_DOWN) {
			goDown.cancel();
			startTimer = false;
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
			movingLeft = true;
			ballX = PLAYER_RACKET_X - BALL_RADIUS;
			ballY = playerRacket_Y + RACKET_LENGTH / 2 - BALL_RADIUS / 2;
			setServe();
			table.setBallPosition(ballX, ballY);
			table.setPlayerRacket_Y(playerRacket_Y);
			canServe = false;
		}
	}

	// ������ ����� ����
	private void startNewGame() {
		computerScore = 0;
		playerScore = 0;
		table.setMessageText("Score Computer: 0 Player: 0");
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
			canBounce = (ballY + BALL_RADIUS >= computerRacket_Y && ballY <= (computerRacket_Y + RACKET_LENGTH));
			ballX -= BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
			// ������
			if (ballX <= LEFT_RACKET_X + RACKET_WIDTH && canBounce) {
				movingLeft = false;
			}
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

	private void computerMove() {
		computerRacket_Y += 3 * computerDir;
		// computerRacket_Y += RACKET_INCREMENT * computerDir;
		if (computerRacket_Y <= TABLE_TOP
				|| computerRacket_Y + RACKET_LENGTH >= TABLE_BOTTOM + TABLE_TOP) {
			computerDir *= -1;
		}
		table.setComputerRacket_Y(computerRacket_Y);
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
				computerScore++;
				ballX += 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			} else if (ballX <= BALL_MIN_X && ballX > BALL_MIN_X - 100) {
				playerScore++;
				ballX -= 100;
				table.setBallPosition(ballX, ballY);
				displayScore();
			}
		}
	}

	private void displayScore() {
		if (computerScore == WINNING_SCORE) {
			table.setMessageText("Computer won! " + computerScore + ":"
					+ playerScore);
			ballServed = false;
		} else if (playerScore == WINNING_SCORE) {
			table.setMessageText("You won! " + playerScore + ":"
					+ computerScore);
			ballServed = false;
		} else {
			table.setMessageText("Computer: " + computerScore + "   "
					+ "Player: " + playerScore);
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
				// ����������� ����������
				computerMove();
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
