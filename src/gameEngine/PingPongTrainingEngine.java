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
	private int playerScore;
	private int computerScore;
	private int ballX;
	private int ballY;
	private boolean movingLeft = true;
	private boolean ballServed = true;
	private boolean canServe = false;
	private boolean startTimer = false;
	// Значение вертикального передвижения мяча
	private int verticalSlide;

	// Конструктор
	public PingPongTrainingEngine(PingPongGreenTable greenTable) {
		table = greenTable;
		Thread worker = new Thread(this);
		worker.start();
	}

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
			if (playerRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
				playerRacket_Y += RACKET_INCREMENT;
			}
			table.setPlayerRacket_Y(playerRacket_Y);
		}
	};

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
					if (playerRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
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

	// Начать новую игру
	private void startNewGame() {
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

	// Вернуться в меню
	public void backToMenu() {
		PingPongGreenTable.f.dispose();
		int codeMenu = 0;
		new Menu(codeMenu);
	}

	private void goLeft(boolean canBounce) {
		if (movingLeft && ballX > BALL_MIN_X) {
			canBounce = (ballY >= computerRacket_Y && ballY < (computerRacket_Y + RACKET_LENGTH));
			ballX -= BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
			// Отскок
			if (ballX <= LEFT_RACKET_X + RACKET_WIDTH && canBounce) {
				movingLeft = false;
				setBounce();
			}
		}
	}

	private void goRight(boolean canBounce) {
		if (!movingLeft && ballX <= BALL_MAX_X) {
			canBounce = (ballY >= playerRacket_Y
					&& ballY <= (playerRacket_Y + RACKET_LENGTH) && (ballX + BALL_RADIUS) <= PLAYER_RACKET_X);
			ballX += BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
			// Отскок
			if (ballX + BALL_RADIUS >= PLAYER_RACKET_X && canBounce) {
				movingLeft = true;
				setBounce();
			}
		}
	}

	private void computerMove() {
		if (computerRacket_Y < ballY
				&& computerRacket_Y < TABLE_BOTTOM - RACKET_LENGTH / 2 - 10) {
			computerRacket_Y += RACKET_INCREMENT;
		} else if (computerRacket_Y > TABLE_TOP) {
			computerRacket_Y -= RACKET_INCREMENT;
		}
		table.setComputerRacket_Y(computerRacket_Y);
	}

	public void setDelay() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
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

	private void ballBounce() {
		if (ballY <= TABLE_TOP) {
			verticalSlide = -1;
		}
		if (ballY >= TABLE_BOTTOM) {
			verticalSlide = 1;
		}
	}

	public void run() {
		boolean canBounce = false;
		while (true) {
			if (ballServed) {
				// Мяч движется влево?
				goLeft(canBounce);
				// Мяч движется вправо?
				goRight(canBounce);
				// Перемещение компьютера
				computerMove();
				// Приостановить
				setDelay();
				// Обновить счет
				updateScore();
				// Отскок мяча
				ballBounce();
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

	private boolean isBallOnTheTable() {
		if (ballY >= BALL_MIN_Y && ballY <= BALL_MAX_Y) {
			return true;
		} else {
			return false;
		}
	}
}
