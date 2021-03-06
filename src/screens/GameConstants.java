package screens;

public interface GameConstants {
	// ������� ����
	public final int WINDOW_WIDTH = 720;
	public final int WINDOW_HEIGHT = 480;
	// ������� �������� ����
	public final int TABLE_LEFT = 10;
	public final int TABLE_RIGHT = WINDOW_WIDTH - 30;
	public final int TABLE_BOTTOM = WINDOW_HEIGHT - 20;
	public final int TABLE_TOP = 10;
	// ��� ����������� ����
	public final int BALL_INCREMENT = 2;
	// ������ ����
	public final int BALL_RADIUS = 10;
	// ���� � ��� ���������� ����
	public final int BALL_MIN_X = 1 + BALL_INCREMENT;
	public final int BALL_MIN_Y = 1 + BALL_INCREMENT;
	public final int BALL_MAX_X = WINDOW_WIDTH - BALL_INCREMENT;
	public final int BALL_MAX_Y = WINDOW_HEIGHT - BALL_INCREMENT;
	// ��������� ���������� ����
	public final int BALL_START_X = WINDOW_WIDTH / 2;
	public final int BALL_START_Y = WINDOW_HEIGHT / 2;
	// �������, ������������ � ��� �������
	public final int RACKET_INCREMENT = 1;
	public final int RACKET_LENGTH = 40;
	public final int RACKET_WIDTH = 5;
	// ���� ��� �������
	public final int RACKET_ZONE_START = RACKET_LENGTH / 2 - RACKET_LENGTH / 4;
	public final int RACKET_ZONE_END = RACKET_LENGTH / 2 + RACKET_LENGTH / 4;
	// ����� 1
	public final int PLAYER_RACKET_X = WINDOW_WIDTH - 30;
	public final int PLAYER_RACKET_Y_START = WINDOW_HEIGHT / 2;
	// ����� �������
	public final int LEFT_RACKET_X = 15;
	// ���������
	public final int COMPUTER_RACKET_Y_START = WINDOW_HEIGHT / 2;
	// ����� 2
	public final int PLAYER2_RACKET_Y_START = WINDOW_HEIGHT / 2;
	// ���-�� ����� ��� ������
	public final int WINNING_SCORE = 21;
	// �������� ��� ����������
	public final int SLEEP_TIME = 3; // ����� � ������������
	// �������� �������� ������� � ������������
	public final int timeMove = 3; // 0.02 sec
	// �������� �������� ���� � ������������
	public final int ballSpeed = 2; // 0.001 sec
}
