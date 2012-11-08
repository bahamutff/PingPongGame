package screens;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Color;
import java.io.IOException;

import engine.*;

public class PingPongGreenTable extends JPanel implements GameConstants {

	private static final long serialVersionUID = 1L;
	private JLabel label;
	private int player2Racket_Y = PLAYER2_RACKET_Y_START;
	private int playerRacket_Y = PLAYER_RACKET_Y_START;
	private int ballX = BALL_START_X;
	private int ballY = BALL_START_Y;
	public static JFrame f;

	Dimension preferredSize = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	// �����������
	public PingPongGreenTable(int codeEngine) throws IOException {
		if (codeEngine == 0) {
			PingPongTrainingEngine trainingEngine = new PingPongTrainingEngine(
					this);
			// ��������� ������� ����������
			addKeyListener(trainingEngine);
			f = new JFrame("Ping Pong Training");
		} else if (codeEngine == 1) {
			PingPongVSEngine gameEngine = new PingPongVSEngine(this);
			// ��������� ������� ����������
			addKeyListener(gameEngine);
			f = new JFrame("Ping Pong VS Game");
		} else if (codeEngine == 2) {
			f = new JFrame("Ping Pong Server");
			PingPongServerEngine serverGameEngine = new PingPongServerEngine(
					this);
			// ��������� ������� ����������
			addKeyListener(serverGameEngine);
		} else if (codeEngine == 3) {
			f = new JFrame("Ping Pong Client");
			PingPongClientEngine clientGameEngine = new PingPongClientEngine(
					this);
			// ��������� ������� ����������
			addKeyListener(clientGameEngine);
		}
		// �������� ����
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.addPaneltoFrame(f.getContentPane());
		f.setBounds(0, 0, WINDOW_WIDTH - 2, WINDOW_HEIGHT + 55);// !!!!!!!!!!!!!!!!
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setVisible(true);
	}

	// ���������� ������ � JLabel � ����
	void addPaneltoFrame(Container container) {
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(this);
		label = new JLabel(
				"Press N - new game, B - back menu, S - serve, Q - quit");
		container.add(label);
	}

	// ����������� ����
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// ���������� ����
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		// ������ �������
		g.setColor(Color.yellow);
		g.fillRect(PLAYER_RACKET_X, playerRacket_Y, RACKET_WIDTH, RACKET_LENGTH);
		// ����� �������
		g.setColor(Color.blue);
		g.fillRect(LEFT_RACKET_X, player2Racket_Y, RACKET_WIDTH, RACKET_LENGTH);
		// ����� �����
		g.setColor(Color.white);
		g.drawRect(TABLE_LEFT, TABLE_TOP, TABLE_RIGHT, TABLE_BOTTOM);
		g.drawLine(WINDOW_WIDTH / 2, TABLE_TOP, WINDOW_WIDTH / 2,
				TABLE_BOTTOM + 10);
		// ���
		g.setColor(Color.red);
		g.fillOval(ballX, ballY, BALL_RADIUS, BALL_RADIUS);
		// ��������� ������ �� ���� ��� ������ ����������
		requestFocus();
	}

	// ���������� ��������� ������� ������
	public void setPlayerRacket_Y(int yCoordinate) {
		this.playerRacket_Y = yCoordinate;
		repaint();
	}

	// ���������� ��������� ������� ������ 2
	public void setPlayer2Racket_Y(int yCoordinate) {
		this.player2Racket_Y = yCoordinate;
		repaint();
	}

	// ������� ������� ��������� ������� ������
	public int getPlayer_Y() {
		return playerRacket_Y;
	}

	public void setComputerRacket_Y(int yCoordinate) {
		this.player2Racket_Y = yCoordinate;
		repaint();
	}

	// ���������� ������� ���������
	public void setMessageText(String text) {
		label.setText(text);
	}

	public void setBallPosition(int xPos, int yPos) {
		ballX = xPos;
		ballY = yPos;
		repaint();
	}

}
