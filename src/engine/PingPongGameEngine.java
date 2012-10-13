package engine;

import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

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
	// Значение вертикального передвижения мяча
	private int verticalSlide;

	// Конструктор
	public PingPongGameEngine(PingPongGreenTable greenTable) {
		table = greenTable;
		Thread worker = new Thread(this);
		worker.start();
	}

	// Обязательные методы из интерфейса MouseMotionListener
	public void mouseDragged(MouseEvent e) {
	};

	public void mouseMoved(MouseEvent e) {
		int mouse_Y = e.getY();
		// Передвинуть ракетку игрока
		if (mouse_Y < playerRacket_Y && playerRacket_Y > TABLE_TOP) {
			playerRacket_Y -= RACKET_INCREMENT;
		} else if (playerRacket_Y < TABLE_BOTTOM) {
			playerRacket_Y += RACKET_INCREMENT;
		}
		// Установить новое положение ракетки
		table.setPlayerRacket_Y(playerRacket_Y);
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
		if ('n' == key || 'N' == key) {
			startNewGame();
		} else if ('q' == key || 'Q' == key) {
			endGame();
		} else if ('s' == key || 'S' == key) {
			playerServe();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	// Начать новую игру
	public void startNewGame() {
		computerScore = 0;
		playerScore = 0;
		table.setMessageText("Score Computer: 0 Player: 0");
		canServe = true;
		playerServe();
	}

	// Завершить игру
	public void endGame() {
		System.exit(0);
	}

	public void run() {
		boolean canBounce = false;
		while (true) {
			if (ballServed) {
				// Мяч движется влево?
				if (movingLeft && ballX > BALL_MIN_X) {
					canBounce = (ballY >= computerRacket_Y
							&& ballY < (computerRacket_Y + RACKET_LENGTH) ? true
							: false);
					ballX -= BALL_INCREMENT;
					ballY -= verticalSlide;
					table.setBallPosition(ballX, ballY);
					// Отскок
					if (ballX <= COMPUTER_RACKET_X && canBounce) {
						movingLeft = false;
						setBounce();
					}
				}
				// Мяч движется вправо?
				if (!movingLeft && ballX <= BALL_MAX_X) {
					canBounce = (ballY >= playerRacket_Y - 5
							&& ballY < (playerRacket_Y - 5 + RACKET_LENGTH) ? true
							: false);
					ballX += BALL_INCREMENT;
					ballY -= verticalSlide;
					table.setBallPosition(ballX, ballY);
					// Отскок
					if (ballX >= PLAYER_RACKET_X - 2 * RACKET_WIDTH
							&& canBounce) {
						movingLeft = true;
						setBounce();
					}

				}
				// Перемещение компьютера
				if (computerRacket_Y < ballY && computerRacket_Y < TABLE_BOTTOM) {
					computerRacket_Y += RACKET_INCREMENT;
				} else if (computerRacket_Y > TABLE_TOP) {
					computerRacket_Y -= RACKET_INCREMENT;
				}
				table.setComputerRacket_Y(computerRacket_Y);
				// Приостановить
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// Обновить счет
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
				// 10 и 200 границы белых линий
				if (ballY <= 10) {
					verticalSlide = -1;
				}
				if (ballY >= 200) {
					verticalSlide = 1;
				}
			}
		}
	}

	// Подать мяч
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
