package scott.emmett;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Controls and displays the main menu screen
 */
public class MenuPanel extends JPanel{

	private JButton simClicker;
	private JLabel title;
	
	/**
	 * Constructs the screen, creating all components
	 */
	public MenuPanel(){
		setPreferredSize(new Dimension(1200,640));
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		add(Box.createVerticalGlue());
		title = new JLabel("{|Savage Worlds Combat Simulator|}");
		title.setAlignmentX(CENTER_ALIGNMENT);
		add(title);
		add(Box.createVerticalGlue());
		simClicker = new JButton("Start Simulation");
		simClicker.addActionListener(new CL_sim());
		simClicker.setAlignmentX(CENTER_ALIGNMENT);
		add(simClicker);
		add(Box.createVerticalGlue());
		add(Box.createVerticalGlue());
	}
	
	/**
	 * Takes click events for the button to begin a simulation
	 */
	private class CL_sim implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Driver.play("Sim");
		}
		
	}
	
}
