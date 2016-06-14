package scott.emmett;

import java.awt.*;
import java.util.Scanner;
import javax.swing.*;

/**
 * Centralizes active graphics and runs the program
 */
public class Driver {

	private static JFrame frame;
	private static JPanel cards;
	private static MenuPanel menu;
	private static SimPanel sim;
	
	/**
	 * Initializes the Frame and Panels, stores them, and activates the MenuPanel
	 */
	public static void main(String[] args) {
		Data.loadFiles();

		frame = new JFrame();
		cards = new JPanel(new CardLayout());
		menu = new MenuPanel();
		sim = new SimPanel();
		cards.add(menu, "Menu");
		cards.add(sim, "Sim");
		frame.add(cards);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		play("Menu");
	}

	/**
	 * Returns the program's JFrame 
	 */
	public static JFrame getFrame(){
		return frame;
	}
	/**
	 * Returns the active SimPanel
	 */
	public static SimPanel getSim(){
		return sim;
	}
	
	/**
	 * Replaces the existing MenuPanel with a new one
	 */
	public static void newMenu(){
		cards.remove(menu);
		menu = new MenuPanel();
		cards.add(menu,"Menu");
	}
	/**
	 * Replaces the existing SimPanel with a new one
	 */
	public static void newSim(){
		cards.remove(sim);
		sim = new SimPanel();
		cards.add(sim,"Sim");
	}
	/**
	 * Activates the Panel that corresponds with the name provided
	 */
	public static void play(String s){
		CardLayout cl = (CardLayout)(cards.getLayout());
		cl.show(cards, s);
	}
	
}
