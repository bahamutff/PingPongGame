package engine;

import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;

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
					}
					// 10 и 200 границы белых линий
					if (ballY <= 10) {
						verticalSlide = -1;
					}
					if (ballY >= 200) {
						verticalSlide = 1;
					}
				}
				// Мяч движется вправо?
				if (!movingLeft && ballX <= BALL_MAX_X) {
					canBounce = (ballY >= playerRacket_Y
							&& ballY < (playerRacket_Y + RACKET_LENGTH) ? true
							: false);
					ballX += BALL_INCREMENT;
					table.setBallPosition(ballX, ballY);
					// Отскок
					if (ballX >= PLAYER_RACKET_X - 2*RACKET_WIDTH
							&& canBounce) {
						movingLeft = true;
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
					if (ballX > BALL_MAX_X) {
						computerScore++;
						displayScore();
					} else if (ballX < BALL_MIN_X) {
						playerScore++;
						displayScore();
					}
				}
			}
		}
	}

	// Подать мяч
	private void playerServe() {
		ballServed = true;
		movingLeft = true;
		ballX = PLAYER_RACKET_X - 1;
		ballY = playerRacket_Y;
		if (ballY > TABLE_HEIGHT / 2) {
			verticalSlide = -1;
		} else {
			verticalSlide = 1;
		}
		table.setBallPosition(ballX, ballY);
		table.setPlayerRacket_Y(playerRacket_Y);
	}

	private void displayScore() {
		if (computerScore == WINNING_SCORE) {
			table.setMessageText("Computer won! " + computerScore + ":"
					+ playerScore);
			ballServed = false;
		} else if (playerScore == WINNING_SCORE) {
			table.setMessageText("You won!" + playerScore + ":" + computerScore);
			ballServed = false;
		} else {
			table.setMessageText("Computer: " + computerScore + "Player: "
					+ playerScore);
		}
		playerServe();
	}

	private boolean isBallOnTheTable() {
		if (ballY >= BALL_MIN_Y && ballY <= BALL_MAX_Y) {
			return true;
		} else {
			return false;
		}
	}
}
