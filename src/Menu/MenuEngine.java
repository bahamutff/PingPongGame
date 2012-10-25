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
		// Start Menu
		if (clickedButton == parent.startGame) {
			parent.fStartMenu.dispose();
			boolean isStartMenu = false;
			new Menu(isStartMenu);
		} else if (clickedButton == parent.lanGame) {

		} else if (clickedButton == parent.optionGame) {

		} else if (clickedButton == parent.exitGame) {
			System.exit(0);
		}
		// Mode Select
		boolean isTraining;
		if (clickedButton == parent.training) {
			parent.fModeSelection.dispose();
			isTraining = true;
			new PingPongGreenTable(isTraining);
		} else if (clickedButton == parent.vsMode) {
			parent.fModeSelection.dispose();
			isTraining = false;
			new PingPongGreenTable(isTraining);
		} else if (clickedButton == parent.back) {
			parent.fModeSelection.dispose();
			boolean isStartMenu = true;
			new Menu(isStartMenu);
		}
	}

}
