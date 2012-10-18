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
	};

	public void PlayerRacketMove(String s) {
		switch (s) {
		case "Up":
			if (playerRacket_Y > TABLE_TOP) {
				playerRacket_Y -= RACKET_INCREMENT;
			}
			break;
		case "Down":
			if (playerRacket_Y + RACKET_LENGTH < TABLE_BOTTOM + 10) {
				playerRacket_Y += RACKET_INCREMENT;
			}
			break;
		}
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
		if (e.getKeyCode() == e.VK_UP) {
			PlayerRacketMove("Up");
		} else if (e.getKeyCode() == e.VK_DOWN) {
			PlayerRacketMove("Down");
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

	// Вернуться в меню
	public void backToMenu() {
		PingPongGreenTable.f.dispose();
		StartMenu Menu = new StartMenu();
	}

	public void run() {
		boolean canBounce = false;
		while (true) {
			if (ballServed) {
				// Мяч движется влево?
				if (movingLeft && ballX > BALL_MIN_X) {
					canBounce = (ballY >= computerRacket_Y && ballY < (computerRacket_Y + RACKET_LENGTH));
					ballX -= BALL_INCREMENT;
					ballY -= verticalSlide;
					table.setBallPosition(ballX, ballY);
					// Отскок
					if (ballX <= COMPUTER_RACKET_X + RACKET_WIDTH && canBounce) {
						movingLeft = false;
						setBounce();
					}
				}
				// Мяч движется вправо?
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
				// Перемещение компьютера
				if (computerRacket_Y < ballY
						&& computerRacket_Y < TABLE_BOTTOM - RACKET_LENGTH / 2
								- 10) {
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
				if (ballY <= TABLE_TOP) {
					verticalSlide = -1;
				}
				if (ballY >= TABLE_BOTTOM) {
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
			ballServed = false;
		} else if (playerScore == WINNING_SCORE) {
			table.setMessageText("You won!" + playerScore + ":" + computerScore);
			ballServed = false;
		} else {
			table.setMessageText("Computer: " + computerScore + "Player: "
					+ playerScore);
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
