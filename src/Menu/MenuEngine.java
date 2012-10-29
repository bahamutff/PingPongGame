package Menu;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import screens.PingPongGreenTable;

public class MenuEngine implements ActionListener {
	Menu parent;

	MenuEngine(Menu parent) {
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e) {
		JButton clickedButton = (JButton) e.getSource();
		// ���������� ���������� � ����������� ��� ������ ������
		int codeEngine;
		// ���������� ���������� � ����������� ��� ������ ����
		int codeMenu;
		// Start Menu
		if (clickedButton == parent.startGame) {
			parent.fStartMenu.dispose();
			codeMenu = 1;
			new Menu(codeMenu);
		} else if (clickedButton == parent.lanGame) {
			parent.fStartMenu.dispose();
			codeMenu = 2;
			new Menu(codeMenu);
		} else if (clickedButton == parent.optionGame) {

		} else if (clickedButton == parent.exitGame) {
			System.exit(0);
		}
		// Mode Select
		if (clickedButton == parent.training) {
			parent.fModeSelection.dispose();
			codeEngine = 0;
			new PingPongGreenTable(codeEngine);
		} else if (clickedButton == parent.vsMode) {
			parent.fModeSelection.dispose();
			codeEngine = 1;
			new PingPongGreenTable(codeEngine);
		} else if (clickedButton == parent.back) {
			parent.fModeSelection.dispose();
			codeMenu = 0;
			new Menu(codeMenu);
		}
		// Lan option
		if (clickedButton == parent.server) {
			parent.fLanSelection.dispose();
			codeEngine = 2;
			new PingPongGreenTable(codeEngine);
		} else if (clickedButton == parent.client) {
			parent.fLanSelection.dispose();
			codeEngine = 3;
			new PingPongGreenTable(codeEngine);
		} else if (clickedButton == parent.backOutLan) {
			parent.fLanSelection.dispose();
			codeMenu = 0;
			new Menu(codeMenu);
		}
	}

}
