package engine;

import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

import Menu.StartMenu;

import screens.*;

public class PingPongGameEngine implements Runnable, MouseMotionListener,
		KeyListener, GameConstants {
	private PingPongGreenTable table;
	private int playerRacket_Y = PLAYER_RACKET_Y_START;
	private int computerRacket_Y = COMPUTER_RACKET_Y_START;
	private int playerScore;
	private int computerScore;
	private int ballX;
	private int ballY;
	private boolean movingLeft = true;
	private boolean ballServed = true;
	private boolean canServe = false;
	// �������� ������������� ������������ ����
	private int verticalSlide;

	// �����������
	public PingPongGameEngine(PingPongGreenTable greenTable) {
		table = greenTable;
		Thread worker = new Thread(this);
		worker.start();
	}

	// ������������ ������ �� ���������� MouseMotionListener
	public void mouseDragged(MouseEvent e) {
	};

	public void mouseMoved(MouseEvent e) {
		int mouse_Y = e.getY();
		// ����������� ������� ������
		if (mouse_Y < playerRacket_Y && playerRacket_Y > TABLE_TOP) {
			playerRacket_Y -= RACKET_INCREMENT;
		} else if (playerRacket_Y < TABLE_BOTTOM) {
			playerRacket_Y += RACKET_INCREMENT;
		}
		// ���������� ����� ��������� �������
		table.setPlayerRacket_Y(playerRacket_Y);
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
		if ('n' == key || 'N' == key) {
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
	}

	public void keyTyped(KeyEvent e) {
	}

	// ������ ����� ����
	public void startNewGame() {
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
		StartMenu Menu = new StartMenu();
	}

	public void run() {
		boolean canBounce = false;
		while (true) {
			if (ballServed) {
				// ��� �������� �����?
				if (movingLeft && ballX > BALL_MIN_X) {
					canBounce = (ballY >= computerRacket_Y
							&& ballY < (computerRacket_Y + RACKET_LENGTH) ? true
							: false);
					ballX -= BALL_INCREMENT;
					ballY -= verticalSlide;
					table.setBallPosition(ballX, ballY);
					// ������
					if (ballX <= COMPUTER_RACKET_X && canBounce) {
						movingLeft = false;
						setBounce();
					}
				}
				// ��� �������� ������?
				if (!movingLeft && ballX <= BALL_MAX_X) {
					canBounce = (ballY >= playerRacket_Y - 5
							&& ballY < (playerRacket_Y - 5 + RACKET_LENGTH) ? true
							: false);
					ballX += BALL_INCREMENT;
					ballY -= verticalSlide;
					table.setBallPosition(ballX, ballY);
					// ������
					if (ballX >= PLAYER_RACKET_X - 2 * RACKET_WIDTH
							&& canBounce) {
						movingLeft = true;
						setBounce();
					}

				}
				// ����������� ����������
				if (computerRacket_Y < ballY && computerRacket_Y < TABLE_BOTTOM) {
					computerRacket_Y += RACKET_INCREMENT;
				} else if (computerRacket_Y > TABLE_TOP) {
					computerRacket_Y -= RACKET_INCREMENT;
				}
				table.setComputerRacket_Y(computerRacket_Y);
				// �������������
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// �������� ����
				if (isBallOnTheTable()) {
					if (ballX > BALL_MAX_X && ballX < BALL_MAX_X + 100) {
						computerScore++;
						ballX += 100;
						table.setBallPosition(ballX, ballY);
						displayScore();
					} else if (ballX < BALL_MIN_X && ballX > BALL_MIN_X - 100) {
						playerScore++;
						ballX -= 100;
						table.setBallPosition(ballX, ballY);
						displayScore();
					}
				}
				// 10 � 200 ������� ����� �����
				if (ballY <= 10) {
					verticalSlide = -1;
				}
				if (ballY >= 200) {
					verticalSlide = 1;
				}
			}
		}
	}

	// ������ ���
	private void playerServe() {
		if (canServe) {
			ballServed = true;
			movingLeft = true;
			ballX = PLAYER_RACKET_X - 1;
			ballY = playerRacket_Y;
			setBounce();
			table.setBallPosition(ballX, ballY);
			table.setPlayerRacket_Y(playerRacket_Y);
			canServe = false;
		}
	}

	private void displayScore() {
		if (computerScore == WINNING_SCORE) {
			table.setMessageText("Computer won! " + computerScore + ":"
					+ playerScore);
		} else if (playerScore == WINNING_SCORE) {
			table.setMessageText("You won!" + playerScore + ":" + computerScore);
		} else {
			table.setMessageText("Computer: " + computerScore + "Player: "
					+ playerScore);
		}
		canServe = true;
	}

	private boolean isBallOnTheTable() {
		if (ballY >= BALL_MIN_Y && ballY <= BALL_MAX_Y) {
			return true;
		} else {
			return false;
		}
	}
}
