package screens;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Color;

import engine.PingPongGameEngine;

public class PingPongGreenTable extends JPanel implements GameConstants {
	JLabel label;
	public Point point = new Point(0, 0);
	public int ComputerRacket_X = 15;
	private int playerRacket_Y = PLAYER_RACKET_Y_START;

	Dimension preferredSize = new Dimension(TABLE_WIDTH, TABLE_HEIGHT);

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	// �����������
	PingPongGreenTable() {
		PingPongGameEngine gameEngine = new PingPongGameEngine(this);
		addMouseListener(gameEngine);
		addMouseMotionListener(gameEngine);
	}

	// ���������� ������ � JLabel � ����
	void addPaneltoFrame(Container container) {
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(this);
		label = new JLabel("Click to see coordinates");
		container.add(label);
	}

	// ����������� ����
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.CYAN);
		// ���������� ����
		g.fillRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);
		g.setColor(Color.yellow);
		// ������ �������
		g.fillRect(PLAYER_RACKET_X_START, playerRacket_Y, 5, 30);
		g.setColor(Color.blue);
		// ����� �������
		g.fillRect(ComputerRacket_X, 100, 5, 30);
		g.setColor(Color.red);
		// ���
		g.fillOval(25, 110, 10, 10);
		g.setColor(Color.white);

		g.drawRect(10, 10, 300, 200);
		g.drawLine(160, 10, 160, 210);
		// ����������� ����� ��� ������� 2�2
		if (point != null) {
			label.setText("Coordinates (x,y): " + point.x + ", " + point.y);
			g.fillRect(point.x, point.y, 2, 2);

		}
	}

	// ���������� ��������� ������� ������
	public void setPlayerRacket_Y(int xCoordinate) {
		this.playerRacket_Y = xCoordinate;
	}

	// ������� ������� ��������� ������� ������
	public int getPlayer_Y(int xCoordinate) {
		return playerRacket_Y;
	}

	public static void main(String[] args) {
		// �������� ����
		JFrame f = new JFrame("Ping Pong Green Table");
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		PingPongGreenTable table = new PingPongGreenTable();
		table.addPaneltoFrame(f.getContentPane());
		f.pack();
		f.setVisible(true);
	}

}
