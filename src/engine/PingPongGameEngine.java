package engine;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import screens.*;

public class PingPongGameEngine implements MouseListener, MouseMotionListener,
		GameConstants {
	PingPongGreenTable table;
	public int playerRacket_Y = PLAYER_RACKET_Y_START;

	// �����������
	public PingPongGameEngine(PingPongGreenTable greenTable) {
		table = greenTable;
	}

	// ������������ ������ �� ���������� MouseListener
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

	// ������������ ������ �� ���������� MouseMotionListener
	public void mouseDragged(MouseEvent e) {
	};

	public void mouseMoved(MouseEvent e) {
		int mouse_Y = e.getY();
		// ����������� ������� ������
		if (mouse_Y < playerRacket_Y && playerRacket_Y > TABLE_TOP) {
			playerRacket_Y -= RACKET_INCREMENT;
		} else if (playerRacket_Y < TABLE_BOTTOM) {
			playerRacket_Y += RACKET_INCREMENT;
		}
		// ���������� ����� ��������� �������
		table.setPlayerRacket_Y(playerRacket_Y);
		table.repaint();
	}

}
