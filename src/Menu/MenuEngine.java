package menu;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import screens.PingPongGreenTable;

public class MenuEngine implements ActionListener {
	Menu parent;

	MenuEngine(Menu parent) {
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e) {
		JButton clickedButton = (JButton) e.getSource();
		// Переменная передается в конструктор для выбора движка
		int codeEngine;
		// Переменная передается в конструктор для выбора меню
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
		} else if (clickedButton == parent.exitGame) {
			System.exit(0);
		}
		// Mode Select
		if (clickedButton == parent.training) {
			parent.fModeSelection.dispose();
			codeEngine = 0;
			try {
				new PingPongGreenTable(codeEngine);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (clickedButton == parent.vsMode) {
			parent.fModeSelection.dispose();
			codeEngine = 1;
			try {
				new PingPongGreenTable(codeEngine);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (clickedButton == parent.back) {
			parent.fModeSelection.dispose();
			codeMenu = 0;
			new Menu(codeMenu);
		}
		// Lan option
		if (clickedButton == parent.server) {
			parent.fLanSelection.dispose();
			codeEngine = 2;
			try {
				new PingPongGreenTable(codeEngine);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (clickedButton == parent.client) {
			parent.fLanSelection.dispose();
			codeMenu = 3;
			new Menu(codeMenu);
		} else if (clickedButton == parent.backOutLan) {
			parent.fLanSelection.dispose();
			codeMenu = 0;
			new Menu(codeMenu);
		}
		// Ip option
		if (clickedButton == parent.confirm) {
			if (isIP(Menu.getIp.getText())) {
				Menu.IP = Menu.getIp.getText();
				parent.fGetIp.dispose();
				codeEngine = 3;
				try {
					new PingPongGreenTable(codeEngine);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Error IP address. Please try again");
			}
		} else if (clickedButton == parent.backOutIp) {
			parent.fGetIp.dispose();
			codeMenu = 0;
			new Menu(codeMenu);
		}
		// Правила
		if (clickedButton == parent.rulesGame) {
			JOptionPane.showMessageDialog(null, "Control:\n "
					+ "s - serve the ball   " + "n - start new game\n"
					+ "b - back to main menu   " + "q - quit\n" + "\n"
					+ "Move button:\n" + "\u2191 - up right player   "
					+ "\u2193 - down right player\n" + "e - up left player   "
					+ "d - down left player\n" + "\n" + "Rules of the game\n"
					+ "task: getting the ball to the enemy\n"
					+ "Game goes to 21 points\n", "Rules and control", 3);
		}
	}

	boolean isIP(String IP) {
		String testDigit = "";
		int numbPoints = 0;
		for (int i = 0; i < IP.length(); i++) {
			if (i > 15) {
				return false;
			}
			if (IP.charAt(i) >= '0' && IP.charAt(i) <= '9') {
				testDigit += IP.charAt(i);
			} else if (IP.charAt(i) == '.') {
				numbPoints++;
				if (numbPoints > 3) {
					return false;
				}
				if (Integer.parseInt(testDigit) >= 0
						&& Integer.parseInt(testDigit) <= 255) {
					testDigit = "";
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
