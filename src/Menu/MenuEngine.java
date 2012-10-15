package Menu;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

import screens.PingPongGreenTable;

public class MenuEngine implements ActionListener {
	StartMenu parent;

	MenuEngine(StartMenu parent) {
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e) {
		JButton clickedButton = (JButton) e.getSource();
		if (clickedButton == parent.startGame) {
			parent.frame.setVisible(false);
			PingPongGreenTable Game = new PingPongGreenTable();
		} else if (clickedButton == parent.lanGame) {

		} else if (clickedButton == parent.optionGame) {

		} else if (clickedButton == parent.exitGame) {
			System.exit(0);
		}
	}

}
