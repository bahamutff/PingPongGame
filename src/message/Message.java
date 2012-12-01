package message;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Message {
	// Search server
	static JFrame searchMessage = new JFrame("Please wait");
	private static boolean searchWasCreate = false;
	// Wait client
	static JFrame waitMessage = new JFrame("Please wait");
	private static boolean waitWasCreate = false;
	//
	static Label searchingMessage;
	static Label waitingMessage;

	public static void searchMessage() {
		if (searchWasCreate) {
			searchMessage.setVisible(true);
		} else {
			JPanel messageContent = new JPanel();
			GridLayout gr = new GridLayout(1, 1);
			messageContent.setLayout(gr);
			searchingMessage = new Label("Try connect to server...",
					Label.CENTER);
			searchingMessage.setFont(new Font("Serif", Font.PLAIN, 25));
			messageContent.add(searchingMessage);
			searchMessage.setContentPane(messageContent);
			searchMessage.setSize(300, 150);
			searchMessage.setLocationRelativeTo(null);
			searchMessage.setResizable(false);
			searchMessage.setVisible(true);
			searchWasCreate = true;
		}
	}

	public static void updateSearchMessage(int time) {
		searchingMessage.setText("Try connect to server..."
				+ Integer.toString(time));
	}

	public static void closeSearchMessage() {
		searchMessage.setVisible(false);
	}

	public static void waitMessage() {
		if (waitWasCreate) {
			waitMessage.setVisible(true);
		} else {
			JPanel messageContent = new JPanel();
			GridLayout gr = new GridLayout(2, 1);
			messageContent.setLayout(gr);
			waitingMessage = new Label("Wait client...", Label.CENTER);
			waitingMessage.setFont(new Font("Serif", Font.PLAIN, 40));
			messageContent.add(waitingMessage);
			waitingMessage = new Label("Time for wait 10 sec", Label.CENTER);
			waitingMessage.setFont(new Font("Serif", Font.PLAIN, 25));
			messageContent.add(waitingMessage);
			waitMessage.setContentPane(messageContent);
			waitMessage.setSize(300, 150);
			waitMessage.setLocationRelativeTo(null);
			waitMessage.setResizable(false);
			waitMessage.setVisible(true);
			waitWasCreate = true;
		}
	}

	public static void closeWaitMessage() {
		waitMessage.setVisible(false);
	}
}
