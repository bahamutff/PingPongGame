package screens;

public interface GameConstants {
	// Размеры окна
	public final int WINDOW_WIDTH = 720;
	public final int WINDOW_HEIGHT = 480;
	// Размеры игрового поля
	public final int TABLE_LEFT = 10;
	public final int TABLE_RIGHT = WINDOW_WIDTH - 30;
	public final int TABLE_BOTTOM = WINDOW_HEIGHT - 20;
	public final int TABLE_TOP = 10;
	// Шаг перемещения мяча
	public final int BALL_INCREMENT = 1;
	// Радиус мяча
	public final int BALL_RADIUS = 10;
	// Макс и мин координаты мяча
	public final int BALL_MIN_X = 1 + BALL_INCREMENT;
	public final int BALL_MIN_Y = 1 + BALL_INCREMENT;
	public final int BALL_MAX_X = WINDOW_WIDTH - BALL_INCREMENT;
	public final int BALL_MAX_Y = WINDOW_HEIGHT - BALL_INCREMENT;
	// Начальные координаты мяча
	public final int BALL_START_X = WINDOW_WIDTH / 2;
	public final int BALL_START_Y = WINDOW_HEIGHT / 2;
	// Размеры, разположение и шаг ракеток
	public final int RACKET_INCREMENT = 5;
	public final int RACKET_LENGTH = 40;
	public final int RACKET_WIDTH = 5;
	// Player 1
	public final int PLAYER_RACKET_X = WINDOW_WIDTH - 30;
	public final int PLAYER_RACKET_Y_START = WINDOW_HEIGHT / 2;
	// Left Racket
	public final int LEFT_RACKET_X = 15;
	// Computer
	public final int COMPUTER_RACKET_Y_START = WINDOW_HEIGHT / 2;
	// Player 2
	public final int PLAYER2_RACKET_Y_START = WINDOW_HEIGHT / 2;
	// Кол-во очков для победы
	public final int WINNING_SCORE = 21;
	// Задержка для компьютера
	public final int SLEEP_TIME = 1; // время в милисикундах

	// Координаты
	class Coord {
		public int x;
		public int y;

		public Coord(int initX, int initY) {
			x = initX;
			y = initY;
		}
	}

	// Счет
	class Score {
		public int scoreClient;// счет текущего игрока
		public int scoreServer;// счет соперника

		public Score(int initScoreClient, int initScoreServer) {
			scoreClient = initScoreClient;
			scoreServer = initScoreServer;
		}

	}
}
