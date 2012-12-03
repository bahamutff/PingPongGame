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
	// Нажаты одновременно 2 клавиши
	private boolean startTimer1 = false;
	private boolean startTimer2 = false;
	// Значение вертикального передвижения мяча
	private int verticalSlide;

	// Конструктор
	public PingPongVSEngine(PingPongGreenTable greenTable) {
		table = greenTable;
		Thread worker = new Thread(this);
		worker.start();
	}

	java.util.Timer timer1 = new java.util.Timer(); // Player 1
	java.util.Timer timer2 = new java.util.Timer(); // Player 2
	// Player 1
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
			if (playerRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
				playerRacket_Y += RACKET_INCREMENT;
			}
			table.setPlayerRacket_Y(playerRacket_Y);
		}
	};

	// Player 2
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
			if (player2Racket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
				player2Racket_Y += RACKET_INCREMENT;
			}
			table.setPlayer2Racket_Y(player2Racket_Y);
		}
	};

	public void setDelay() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		if (!startTimer1 && e.getKeyCode() == e.VK_UP) {
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
		} else if (!startTimer1 && e.getKeyCode() == e.VK_DOWN) {
			goDown1 = new TimerTask() {
				public void run() {
					if (playerRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
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
					if (player2Racket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
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
		if (e.getKeyCode() == e.VK_UP) {
			goUp1.cancel();
			startTimer1 = false;
		}
		if (e.getKeyCode() == e.VK_DOWN) {
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

	// Начать новую игру
	private void startNewGame() {
		player2Score = 0;
		playerScore = 0;
		table.setMessageText("Score Player 2: 0 Player 1: 0");
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
			canBounce = (ballY >= player2Racket_Y && ballY < (player2Racket_Y + RACKET_LENGTH));
			ballX -= BALL_INCREMENT;
			ballY -= verticalSlide;
			table.setBallPosition(ballX, ballY);
		}
		// Отскок
		if (ballX <= LEFT_RACKET_X + RACKET_WIDTH && canBounce) {
			movingLeft = false;
			setBounce();
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
			if (!player1Serve) {
				movingLeft = true;
				ballX = PLAYER_RACKET_X - 1;
				ballY = playerRacket_Y;
				table.setPlayerRacket_Y(playerRacket_Y);
			} else {
				movingLeft = false;
				ballX = LEFT_RACKET_X + 1;
				ballY = player2Racket_Y;
				table.setPlayer2Racket_Y(player2Racket_Y);
			}
			table.setBallPosition(ballX, ballY);
			setBounce();
			canServe = false;
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

	private boolean isBallOnTheTable() {
		if (ballY >= BALL_MIN_Y && ballY <= BALL_MAX_Y) {
			return true;
		} else {
			return false;
		}
	}

}
