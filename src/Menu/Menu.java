package menu;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;

public class Menu {
	public static String IP = null;
	// Стартовое меню
	JButton startGame = new JButton("Start game");
	JButton lanGame = new JButton("Lan game");
	JButton rulesGame = new JButton("Rules");
	JButton exitGame = new JButton("Exit");
	JFrame fStartMenu = new JFrame("Ping Pong Game");
	GridLayout glStart = new GridLayout(4, 1);
	// Mode Selection
	JButton training = new JButton("Training");
	JButton vsMode = new JButton("Player VS Player");
	JButton back = new JButton("Back");
	JFrame fModeSelection = new JFrame("Mode selection");
	GridLayout glModeSelection = new GridLayout(3, 1);
	// Lan option
	JButton server = new JButton("Server");
	JButton client = new JButton("Client");
	JButton backOutLan = new JButton("Back");
	JFrame fLanSelection = new JFrame("Lan selection");
	GridLayout glLanSelection = new GridLayout(3, 1);
	// Get Ip
	JButton backOutIp = new JButton("Back");
	JButton confirm = new JButton("Confirm");
	JFrame fGetIp = new JFrame("Address");
	static Label ipTextMessage = new Label("Enter IP", Label.CENTER);
	static JTextField getIp = new JTextField();
	GridLayout glGetIp = new GridLayout(2, 2);
	// Основная панель
	JPanel windowContent = new JPanel();

	public Menu(int codeMenu) {
		MenuEngine mE = new MenuEngine(this);
		if (codeMenu == 0) {
			windowContent.setLayout(glStart);
			windowContent.add(startGame);
			windowContent.add(lanGame);
			windowContent.add(rulesGame);
			windowContent.add(exitGame);
			startGame.addActionListener(mE);
			lanGame.addActionListener(mE);
			rulesGame.addActionListener(mE);
			exitGame.addActionListener(mE);
			fStartMenu.setContentPane(windowContent);
			fStartMenu.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			fStartMenu.setSize(250, 200);
			fStartMenu.setLocationRelativeTo(null);
			fStartMenu.setResizable(false);
			fStartMenu.setVisible(true);
		} else if (codeMenu == 1) {
			windowContent.setLayout(glModeSelection);
			windowContent.add(training);
			windowContent.add(vsMode);
			windowContent.add(back);
			training.addActionListener(mE);
			vsMode.addActionListener(mE);
			back.addActionListener(mE);
			fModeSelection.setContentPane(windowContent);
			fModeSelection
					.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			fModeSelection.setSize(250, 200);
			fModeSelection.setLocationRelativeTo(null);
			fModeSelection.setResizable(false);
			fModeSelection.setVisible(true);
		} else if (codeMenu == 2) {
			windowContent.setLayout(glLanSelection);
			windowContent.add(server);
			windowContent.add(client);
			windowContent.add(backOutLan);
			server.addActionListener(mE);
			client.addActionListener(mE);
			backOutLan.addActionListener(mE);
			fLanSelection.setContentPane(windowContent);
			fLanSelection
					.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			fLanSelection.setSize(250, 200);
			fLanSelection.setLocationRelativeTo(null);
			fLanSelection.setResizable(false);
			fLanSelection.setVisible(true);
		} else if (codeMenu == 3) {
			windowContent.setLayout(glGetIp);
			ipTextMessage.setFont(new Font("Serif", Font.PLAIN, 30));
			windowContent.add(ipTextMessage);
			windowContent.add(backOutIp);
			windowContent.add(getIp);
			windowContent.add(confirm);
			confirm.addActionListener(mE);
			backOutIp.addActionListener(mE);
			fGetIp.setContentPane(windowContent);
			fGetIp.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			fGetIp.setSize(350, 100);
			fGetIp.setLocationRelativeTo(null);
			fGetIp.setResizable(false);
			fGetIp.setVisible(true);
		}
	}

	public static void main(String[] args) {
		int codeMenu = 0;
		new Menu(codeMenu);
	}

}
