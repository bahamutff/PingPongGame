package engine;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import screens.*;

public class PingPongGameEngine implements MouseListener, MouseMotionListener,
		GameConstants {
	PingPongGreenTable table;
	public int playerRacket_Y = PLAYER_RACKET_Y_START;

	// Конструктор
	public PingPongGameEngine(PingPongGreenTable greenTable) {
		table = greenTable;
	}

	// Обязательные методы из интерфейса MouseListener
	public void mousePressed(MouseEvent e) {
		table.point.x = e.getX();
		table.point.y = e.getY();
		table.repaint();
	}

	public void mouseReleased(MouseEvent e) {
	};

	public void mouseEntered(MouseEvent e) {
	};

	public void mouseExited(MouseEvent e) {
	};

	public void mouseClicked(MouseEvent e) {
	};

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
		table.repaint();
	}

}
