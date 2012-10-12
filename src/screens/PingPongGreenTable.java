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
	private JLabel label;
	private int computerRacket_Y = COMPUTER_RACKET_Y_START;
	private int playerRacket_Y = PLAYER_RACKET_Y_START;
	private int ballX = BALL_START_X;
	private int ballY = BALL_START_Y;

	Dimension preferredSize = new Dimension(TABLE_WIDTH, TABLE_HEIGHT);

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	// Конструктор
	PingPongGreenTable() {
		PingPongGameEngine gameEngine = new PingPongGameEngine(this);
		// Обработка движений мыши
		addMouseMotionListener(gameEngine);
		// Обработка событий клавиатуры
		addKeyListener(gameEngine);
	}

	// Добавление панели с JLabel в окно
	void addPaneltoFrame(Container container) {
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(this);
		label = new JLabel("Press N for a new game, S to serve or Q to quit");
		container.add(label);
	}

	// Перерисовка окна
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Нарисовать стол
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);
		// Правая ракетка
		g.setColor(Color.yellow);
		g.fillRect(PLAYER_RACKET_X, playerRacket_Y, RACKET_WIDTH, RACKET_LENGTH);
		// Левая ракетка
		g.setColor(Color.blue);
		g.fillRect(COMPUTER_RACKET_X, computerRacket_Y, RACKET_WIDTH,
				RACKET_LENGTH);
		// Мяч
		g.setColor(Color.red);
		g.fillOval(ballX, ballY, 10, 10);
		// Белые линии
		g.setColor(Color.white);
		g.drawRect(10, 10, 300, 200);
		g.drawLine(160, 10, 160, 210);
		// Установка фокуса на стол для команд клавиатуры
		requestFocus();
	}

	// Установить положение ракетки игрока
	public void setPlayerRacket_Y(int yCoordinate) {
		this.playerRacket_Y = yCoordinate;
		repaint();
	}

	// Вернуть текущее положение ракетки игрока
	public int getPlayer_Y() {
		return playerRacket_Y;
	}

	public void setComputerRacket_Y(int yCoordinate) {
		this.computerRacket_Y = yCoordinate;
		repaint();
	}

	// Установить игровое сообщение
	public void setMessageText(String text) {
		label.setText(text);
		repaint();
	}

	public void setBallPosition(int xPos, int yPos) {
		ballX = xPos;
		ballY = yPos;
		repaint();
	}

	public static void main(String[] args) {
		// Создание окна
		JFrame f = new JFrame("Ping Pong Green Table");
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		PingPongGreenTable table = new PingPongGreenTable();
		table.addPaneltoFrame(f.getContentPane());
		f.setBounds(0, 0, TABLE_WIDTH + 15, TABLE_HEIGHT + 55);
		f.setResizable(false);
		f.setVisible(true);
	}

}
