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

import engine.PingPongGameEngine;

public class PingPongGreenTable extends JPanel implements GameConstants {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private int computerRacket_Y = COMPUTER_RACKET_Y_START;
	private int playerRacket_Y = PLAYER_RACKET_Y_START;
	private int ballX = BALL_START_X;
	private int ballY = BALL_START_Y;
	public static JFrame f;

	Dimension preferredSize = new Dimension(TABLE_WIDTH, TABLE_HEIGHT);

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	// �����������
	public PingPongGreenTable() {
		PingPongGameEngine gameEngine = new PingPongGameEngine(this);
		// ��������� �������� ����
		addMouseMotionListener(gameEngine);
		// ��������� ������� ����������
		addKeyListener(gameEngine);
		// �������� ����
		f = new JFrame("Ping Pong Game");
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.addPaneltoFrame(f.getContentPane());
		f.setBounds(0, 0, TABLE_WIDTH + 15, TABLE_HEIGHT + 55);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setVisible(true);
	}

	// ���������� ������ � JLabel � ����
	void addPaneltoFrame(Container container) {
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(this);
		label = new JLabel("Press N - for a new game, B - to back menu");
		container.add(label);
		label = new JLabel("S - to serve, Q - to quit");
		container.add(label);
	}

	// ����������� ����
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// ���������� ����
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);
		// ������ �������
		g.setColor(Color.yellow);
		g.fillRect(PLAYER_RACKET_X, playerRacket_Y, RACKET_WIDTH, RACKET_LENGTH);
		// ����� �������
		g.setColor(Color.blue);
		g.fillRect(COMPUTER_RACKET_X, computerRacket_Y, RACKET_WIDTH,
				RACKET_LENGTH);
		// ���
		g.setColor(Color.red);
		g.fillOval(ballX, ballY, 10, 10);
		// ����� �����
		g.setColor(Color.white);
		g.drawRect(10, 10, 300, 200);
		g.drawLine(160, 10, 160, 210);
		// ��������� ������ �� ���� ��� ������ ����������
		requestFocus();
	}

	// ���������� ��������� ������� ������
	public void setPlayerRacket_Y(int yCoordinate) {
		this.playerRacket_Y = yCoordinate;
		repaint();
	}

	// ������� ������� ��������� ������� ������
	public int getPlayer_Y() {
		return playerRacket_Y;
	}

	public void setComputerRacket_Y(int yCoordinate) {
		this.computerRacket_Y = yCoordinate;
		repaint();
	}

	// ���������� ������� ���������
	public void setMessageText(String text) {
		label.setText(text);
		repaint();
	}

	public void setBallPosition(int xPos, int yPos) {
		ballX = xPos;
		ballY = yPos;
		repaint();
	}

}
