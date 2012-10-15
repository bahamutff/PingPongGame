package Menu;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class StartMenu {

	JButton startGame = new JButton("Start game");
	JButton lanGame = new JButton("Lan game");
	JButton optionGame = new JButton("Option");
	JButton exitGame = new JButton("Exit");
	JFrame frame = new JFrame("Ping Pong Game");
	GridLayout gl = new GridLayout(4, 1);
	JPanel windowContent = new JPanel();

	public StartMenu() {
		windowContent.setLayout(gl);
		windowContent.add(startGame);
		windowContent.add(lanGame);
		windowContent.add(optionGame);
		windowContent.add(exitGame);
		MenuEngine mE = new MenuEngine(this);
		startGame.addActionListener(mE);
		lanGame.addActionListener(mE);
		optionGame.addActionListener(mE);
		exitGame.addActionListener(mE);
		frame.setContentPane(windowContent);
		frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
		frame.setSize(250, 200);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		StartMenu Menu = new StartMenu();
	}

}
