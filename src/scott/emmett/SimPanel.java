package scott.emmett;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.*;

/**
* Constructs and displays the simulation
*/
public class SimPanel extends JPanel{
	
	private ArrayList<Robot> team1Members = new ArrayList<>();
	private ArrayList<Robot> team2Members = new ArrayList<>();
	private ArrayList<JButton[]> tiles = new ArrayList<>();
	private boolean adding1;
	private boolean adding2;
	private boolean mapChoosing;
	private boolean mapPreping;
	private boolean mapRunning;
	private boolean removing1;
	private boolean removing2;
	private boolean simOver;
	private Hashtable<JButton,Block> tileBlocks = new Hashtable<>();
	private GridBagConstraints c;
	private int selectedMap;
	
	private JButton btnAdd1;
	private JButton btnAdd2;
	private JButton btnBack;
	private JButton btnNextMap;
	private JButton btnPreviousMap;
	private JButton btnRemove1;
	private JButton btnRemove2;
	private JButton btnRunMap;
	private JLabel team1label;
	private JLabel team2label;
	private JScrollPane descScroll;//description scroll
	private JScrollPane outputScroll;
	private JScrollPane team1scroll;
	private JScrollPane team2scroll;
	private JTextArea descDisplay;
	private JTextArea outputDisplay;
	private JTextArea team1display;
	private JTextArea team2display;
	
	//round variables
	private ArrayList<Robot> allMembers = new ArrayList<>();
	private Deck deck;
	private int roundTracker;
	private int turn;
	
	
	/**
 	* Constructs a new simulation and initializes the status booleans
 	*/
	public SimPanel(){
		adding1 = false;
		adding2 = false;
		mapChoosing = false;
		mapPreping = false;
		mapRunning = false;
		removing1 = false;
		removing2 = false;
		
		Data.wipeTracker();
		
		setPreferredSize(new Dimension(1200,640));
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		mapChooser();
	}
	
	
	/**
 	* Sets the tiles' icons to correspond to the selected map
 	*/
	private void buildMap(){
		ArrayList<char[]> reference = Data.getLayout(selectedMap);
		for(int i = 0; i < reference.size(); i++){//rows
			for(int j = 0; j < reference.get(0).length; j++){//columns
				if(reference.get(i)[j] == '-'){
					tiles.get(i)[j].setIcon(Data.pic("Open"));
					tileBlocks.get(tiles.get(i)[j]).setOpen(true);
				}
				else{
					tiles.get(i)[j].setIcon(Data.pic("Closed"));
					tileBlocks.get(tiles.get(i)[j]).setOpen(false);
				}
			}
		}
	}
	/**
 	* Sorts the Robots in this simulation by the numerical value of their card
 	*/
	private void initiativeSort(){
		for(int i = 0; i < allMembers.size() - 1; i++){
			for(int j = i; j < allMembers.size() - 1; j++){
				if(allMembers.get(j).getCard().getValue() < allMembers.get(j+1).getCard().getValue()){
					Robot store = allMembers.get(j+1);
					allMembers.set(j+1, allMembers.get(j));
					allMembers.set(j, store);
				}
			}
		}
	}
	/**
 	* Creates the components for the map choosing screen
 	*/
	private void mapChooser(){
		//System.out.println("here");
		selectedMap = 1;
		mapChoosing = true;
		final int offset = 10;
		
		for(int i = 0; i < Data.getLayout(1).size(); i++){//rows
			JButton[] row = new JButton[Data.getLayout(1).get(0).length];
			for(int j = 0; j < Data.getLayout(1).get(0).length; j++){//columns
				JButton tile = new JButton();
				tile.addActionListener(new ClickListener_Tile());
				tile.addMouseListener(new MListener_Tile());
				tile.setIcon(Data.pic("Blank"));
				tile.setBorder(BorderFactory.createEmptyBorder());
				tile.setContentAreaFilled(false);
				row[j] = tile;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.gridx = j + offset;
				c.gridy = i;
				add(tile,c);
				Block b = new Block();
				b.setLoc(new Point(j,i));
				b.setOccupied(false);
				tileBlocks.put(tile, b);
			}
			tiles.add(row);
			//System.out.println("Adding a row " + row.length + " long");
		}
		
		buildMap();
		
		btnPreviousMap = new JButton("{|Previous Map|}");
		btnPreviousMap.addActionListener(new ClickListener_PreviousMap());
		c.gridwidth = 3;
		c.gridheight = 3;
		c.gridx = 0;
		c.gridy = 15;
		add(btnPreviousMap,c);
		btnNextMap = new JButton("{|  Next Map  |}");
		btnNextMap.addActionListener(new ClickListener_NextMap());
		c.gridx = 53;
		add(btnNextMap,c);
		
		btnBack = new JButton("{|   Cancel   |}");
		btnBack.addActionListener(new ClickListener_Back());
		c.gridx = 0;
		c.gridy = 38;
		add(btnBack,c);
		btnRunMap = new JButton("{|Use This Map|}");
		btnRunMap.addActionListener(new ClickListener_RunMap());
		c.gridx = 53;
		add(btnRunMap,c);
		
	}
	/**
 	* Creates the components for the map preparing screen
 	*/
	private void mapPreper(){
		mapChoosing = false;
		mapPreping = true;
		
		remove(btnBack);
		btnNextMap.setVisible(false);
		btnPreviousMap.setVisible(false);
		remove(btnRunMap);
		
		//btns
		c.gridwidth = 1;
		c.gridheight = 3;
		c.gridx = 9;
		c.gridy = 38;
		add(btnBack,c);
		btnRunMap.setText("{|    Run     |}");
		c.gridwidth = 4;
		c.gridx = 50;
		add(btnRunMap,c);
		
		btnAdd1 = new JButton("Add bot");
		btnAdd1.addActionListener(new ClickListener_Add());
		c.gridwidth = 1;
		c.gridheight = 2;
		c.gridx = 5;
		c.gridy = 15;
		add(btnAdd1,c);
		btnAdd2 = new JButton("Add bot");
		btnAdd2.addActionListener(new ClickListener_Add());
		c.gridy = 30;
		add(btnAdd2,c);
		
		btnRemove1 = new JButton("Remove bot");
		btnRemove1.addActionListener(new ClickListener_Remove());
		c.gridwidth = 2;
		c.gridheight = 2;
		c.gridx = 8;
		c.gridy = 15;
		add(btnRemove1,c);
		btnRemove2 = new JButton("Remove bot");
		btnRemove2.addActionListener(new ClickListener_Remove());
		c.gridy = 30;
		add(btnRemove2,c);
		
		
		//scrollpanes
		team1display = new JTextArea(8,20);
		team1scroll = new JScrollPane(team1display);
		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridx = 0;
		c.gridy = 5;
		add(team1scroll,c);
		team2display = new JTextArea(8,20);
		team2scroll = new JScrollPane(team2display);
		c.gridy = 20;
		add(team2scroll,c);
		outputDisplay = new JTextArea(16,20);
		outputDisplay.setText("{|Add robots to the simulation.|}");
		outputScroll = new JScrollPane(outputDisplay);
		c.gridheight = 20;
		c.gridx = 50;
		c.gridy = 5;
		add(outputScroll,c);
		descDisplay = new JTextArea(8,20);
		descScroll = new JScrollPane(descDisplay);
		c.gridheight = 10;
		c.gridy = 27;
		add(descScroll,c);
		
		//labels
		team1label = new JLabel("Team 1");
		c.gridwidth = 2;
		c.gridheight = 2;
		c.gridx = 5;
		c.gridy = 4;
		add(team1label,c);
		team2label = new JLabel("Team 2");
		c.gridy = 19;
		add(team2label,c);
		
		
		
		buildMap();
		setTilesHighlight(true);
		
		
	}
	/**
 	* Creates the components for the map running screen
 	*/
	private void mapRunner(){	
		mapPreping = false;
		mapRunning = true;

		roundTracker = 0;
	
		remove(btnAdd1);
		remove(btnAdd2);
		remove(btnRemove1);
		remove(btnRemove2);
		updateOutput("\n{|Simulation beginning.|}");
		setTilesHighlight(false);	
		
		allMembers.addAll(team1Members);
		allMembers.addAll(team2Members);
		for(Robot r : allMembers){
			r.setSim(this);
		}
		simOver = false;
		deck = new Deck();
		turn = 0;
		
	}
	/**
 	* Determine which Robot is next in the initiative order and activates its turn
 	*/
	private void nextTurn(){
		
		if(turn == 0){
			roundTracker++;
			updateOutput("\n{|ROUND " + roundTracker + "|}");
			for(Robot r : allMembers){
				r.setCard(deck.draw());
			}
			boolean jokerDealt = false;
			for(Robot r : allMembers){
				if(r.getCard().getName().contains("Joker")){
					jokerDealt = true;
				}
			}
			if(jokerDealt) deck.shuffle();
			initiativeSort();
		}
		allMembers.get(turn).takeTurn();
		if(turn + 1 >= allMembers.size()){
			turn = 0;
		}
		else{
			turn++;
		}		
	}
	/**
 	* Toggles the highlight square that follows the cursor over the map
 	*/
	private void setTilesHighlight(boolean b){
		for(int i = 0; i < tiles.size(); i++){
			for(int j = 0; j < tiles.get(0).length; j++){
				if(b){
					if(tiles.get(i)[j].getIcon().equals(Data.pic("Open"))){
						tiles.get(i)[j].setRolloverIcon(Data.pic("OpenHover"));
					}
					else if(tiles.get(i)[j].getIcon().equals(Data.pic("Closed"))){
						tiles.get(i)[j].setRolloverIcon(Data.pic("ClosedHover"));
					}
				}
				else{
					tiles.get(i)[j].setRolloverIcon(null);
				}
			}
		}
	}
	/**
 	* Updates the description text to display the stats of the currently highlighted Robot
 	*/
	private void updateDesc(String s){
		descDisplay.setText(s);
	}
	/**
 	* Updates the text of the scrollpanes displaying the team lists
 	*/
	private void updateMembers(){
		String text1 = "";
		for(int i = 0; i < team1Members.size(); i++){
			text1 = text1 + team1Members.get(i).getID() + "\n";
		}
		team1display.setText(text1);
		String text2 = "";
		for(int i = 0; i < team2Members.size(); i++){
			text2 = text2 + team2Members.get(i).getID() + "\n";
		}
		team2display.setText(text2);
	}
	
	/**
 	* Returns a list of all Blocks that the provided Robot can see
 	*/
	public ArrayList<Block> see(Robot r){
		Point loc = r.getLoc();
		int height = tiles.size() - 1;
		int width = tiles.get(0).length - 1;
		ArrayList<Block> seen = new ArrayList<>();
		
		int[] xs = {loc.x,loc.x,loc.x +1,loc.x +1};
		int[] ys = {loc.y,loc.y +1,loc.y,loc.y +1};
		
		for(int i = 0; i < 4; i++){//once from each corner, to ensure proper sightlines
			for(int j = 0; j < 360; j += 1){//j = degrees from positive x axis
				int raa;//here, not actually related acute angle, more required acute angle
				
				if(j == 0){
					double xmod = 1;
					double x = xs[i];
					double y = ys[i];
					while(x + xmod < width){
						x += xmod;
						Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
						if(!seen.contains(tile)){
							seen.add(tile);
						}
						if(!tile.isOpen()){
							break;
						}
					}
				}
				else if(0 < j && j <= 45){
					raa = j;
					double xmod = 1;
					double ymod = Math.tan(Math.toRadians(raa));
					double x = xs[i];
					double y = ys[i];
					double ytracker = 0;
					while(y + ymod < height){
						y += ymod;
						ytracker += ymod;
						if(ytracker - 1 > 0){
							ytracker -= 1;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
						if(x + xmod > width) break;
						else{
							x += xmod;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
					}
				}
				else if(45 < j && j < 90){
					raa = 90 - j;
					double xmod = Math.tan(Math.toRadians(raa));
					double ymod = 1;
					double x = xs[i];
					double y = ys[i];
					double xtracker = 0;
					while(x + xmod < width){
						x += xmod;
						xtracker += xmod;
						if(xtracker - 1 > 0){
							xtracker -= 1;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
						if(y + ymod > height) break;
						else{
							y += ymod;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
					}
				}
				else if(j == 90){
					double ymod = 1;
					double x = xs[i];
					double y = ys[i];
					while(y + ymod < height){
						y += ymod;
						Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
						if(!seen.contains(tile)){
							seen.add(tile);
						}
						if(!tile.isOpen()){
							break;
						}
					}
				}
				else if(90 < j && j < 135){
					raa = j - 90;
					double xmod = 0 - Math.tan(Math.toRadians(raa));
					double ymod = 1;
					double x = xs[i];
					double y = ys[i];
					double xtracker = 0;
					while(x + xmod >= 0){
						x += xmod;
						xtracker -= xmod;
						if(xtracker - 1 > 0){
							xtracker -= 1;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
						if(y + ymod > height) break;
						else{
							y += ymod;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
					}
				}
				else if(135 <= j && j < 180){
					raa = 180 - j;
					double xmod = -1;
					double ymod = Math.tan(Math.toRadians(raa));
					double x = xs[i];
					double y = ys[i];
					double ytracker = 0;
					while(y + ymod < height){
						y += ymod;
						ytracker += ymod;
						if(ytracker - 1 > 0){
							ytracker -= 1;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
						if(x + xmod < 0) break;
						else{
							x += xmod;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
					}
				}
				else if(j == 180){
					double xmod = -1;
					double x = xs[i];
					double y = ys[i];
					while(x + xmod >= 0){
						x += xmod;
						Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
						if(!seen.contains(tile)){
							seen.add(tile);
						}
						if(!tile.isOpen()){
							break;
						}
					}
				}
				else if(180 < j && j <= 225){
					raa = j % 180;
					double xmod = -1;
					double ymod = 0 - Math.tan(Math.toRadians(raa));
					double x = xs[i];
					double y = ys[i];
					double ytracker = 0;
					while(y + ymod >= 0){
						y += ymod;
						ytracker -= ymod;
						if(ytracker - 1 > 0){
							ytracker -= 1;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
						if(x + xmod < 0) break;
						else{
							x += xmod;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
					}
				}
				else if(225 < j && j < 270){
					raa = 90 - (j % 180);
					double xmod = 0 - Math.tan(Math.toRadians(raa));
					double ymod = -1;
					double x = xs[i];
					double y = ys[i];
					double xtracker = 0;
					while(x + xmod >= 0){
						x += xmod;
						xtracker -= xmod;
						if(xtracker - 1 > 0){
							xtracker -= 1;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
						if(y + ymod < 0) break;
						else{
							y += ymod;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
					}
				}
				else if(j == 270){
					double ymod = -1;
					double x = xs[i];
					double y = ys[i];
					while(y + ymod >= 0){
						y += ymod;
						Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
						if(!seen.contains(tile)){
							seen.add(tile);
						}
						if(!tile.isOpen()){
							break;
						}
					}
				}
				else if(270 < j && j < 315){
					raa = (j % 180) - 90;
					double xmod = Math.tan(Math.toRadians(raa));
					double ymod = -1;
					double x = xs[i];
					double y = ys[i];
					double xtracker = 0;
					while(x + xmod < width){
						x += xmod;
						xtracker += xmod;
						if(xtracker - 1 > 0){
							xtracker -= 1;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
						if(y + ymod < 0) break;
						else{
							y += ymod;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
					}
				}
				else if(315 <= j && j < 360){
					raa = 180 - (j % 180);
					double xmod = 1;
					double ymod = 0 - Math.tan(Math.toRadians(raa));
					double x = xs[i];
					double y = ys[i];
					double ytracker = 0;
					while(y + ymod >= 0){
						y += ymod;
						ytracker -= ymod;
						if(ytracker - 1 > 0){
							ytracker -= 1;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
						if(x + xmod > width) break;
						else{
							x += xmod;
							Block tile = tileBlocks.get(tiles.get((int) y)[(int) x]);
							if(!seen.contains(tile)){
								seen.add(tile);
							}
							if(!tile.isOpen()){
								break;
							}
						}
					}
				}
				
			}
		}
		
		return seen;
	}
	/**
 	* Returns the Block found at the provided coordinates
 	*/
	public Block getBlock(int x, int y){
		return tileBlocks.get(tiles.get(y)[x]);
	}
	/**
 	* Returns true if the Block at the provided coordinates is occupied
 	*/
	public boolean tileOccupied(int x, int y){
		if(tileBlocks.get(tiles.get(y)[x]).isOccupied()) return true;
		else return false;
	}
	/**
 	* Returns the height of the map
 	*/
	public int height(){
		return tiles.size();
	}
	/**
 	* Returns the width of the map
 	*/
	public int width(){
		return tiles.get(0).length;
	}
	/**
 	* Ends the simulation and makes the "next turn" button unresponsive
 	*/
	public void endSim(){
		simOver = true;
		mapRunning = false;
	}
	/**
 	* Graphically moves the specified Robot from its old location to its new one
 	*/
	public void moved(Robot r, Point oldP, Point newP){
		JButton oldJ = tiles.get(oldP.y)[oldP.x];
		oldJ.setIcon(Data.pic("Open"));
		Block oldB = tileBlocks.get(oldJ);
		oldB.setOccupied(false);
		oldB.setOccupant(null);
		JButton newJ = tiles.get(newP.y)[newP.x];
		if(r.getTeam() == 1) newJ.setIcon(Data.pic("Bot1"));
		else newJ.setIcon(Data.pic("Bot2"));
		Block newB = tileBlocks.get(newJ);
		newB.setOccupied(r);
	}
	/**
 	* Removes the specified Robot from the map and from its team list, and ends the simulation if that team list is now empty
 	*/
	public void kill(Robot r){		
		JButton j = tiles.get(r.getLoc().y)[r.getLoc().x];
		j.setIcon(Data.pic("Open"));
		Block b = tileBlocks.get(j);
		b.setOccupied(false);
		b.setOccupant(null);

		if(r.getTeam() == 1){
			team1Members.remove(r);
			if(team1Members.isEmpty()){
				endSim();
				updateOutput("{|TEAM 2 WINS|}");
			}
		}
		else{
			team2Members.remove(r);
			if(team2Members.isEmpty()){
				endSim();
				updateOutput("{|TEAM 1 WINS|}");
			}
		}
		allMembers.remove(r);
		updateMembers();
	}
	/**
 	* Adds text to the log
 	*/
	public void updateOutput(String line){
		String text = outputDisplay.getText();
		outputDisplay.setText(text + "\n" + line);
	}
	
	//CLASSES
	/**
 	* Processes click events for the "add" buttons
 	*/
	private class ClickListener_Add implements ActionListener{
		/**
		 * Toggles the adding and removing status booleans
		 */
		public void actionPerformed(ActionEvent e){
			if(e.getSource().equals(btnAdd1)){
				adding1 = true;
				adding2 = false;
				removing1 = false;
				removing2 = false;
			}
			else{
				adding1 = false;
				adding2 = true;
				removing1 = false;
				removing2 = false;
			}
		}
	}
	/**
 	* Processes click events for the "back" button
 	*/
	private class ClickListener_Back implements ActionListener{
		/**
		 * Returns the user to the main menu
 		 */
		public void actionPerformed(ActionEvent e){
			Driver.newSim();
			Driver.play("Menu");
		}
	}
	/**
 	* Processes click events for the "next map" button
 	*/
	private class ClickListener_NextMap implements ActionListener{
		/**
		 * cycles forwards through the available maps and displays the next map
		 */
		public void actionPerformed(ActionEvent arg0) {
			if(selectedMap != Data.numOfMaps()){
				selectedMap++;
			}
			else{
				selectedMap = 1;
			}
			buildMap();
		}
		
	}
	/**
 	* Processes click events for the "previous map" button
 	*/
	private class ClickListener_PreviousMap implements ActionListener{
		/**
	 	* Cycles through the available maps and displays the previous map
	 	*/
		public void actionPerformed(ActionEvent e){
			if(selectedMap != 1){
				selectedMap--;
			}
			else{
				selectedMap = Data.numOfMaps();
			}
			buildMap();
		}
	}
	/**
 	* Processes click events for the "remove" buttons
 	*/
	private class ClickListener_Remove implements ActionListener{
		/**
		 * Toggles the adding and removing status booleans
		 */
		public void actionPerformed(ActionEvent e){
			if(e.getSource().equals(btnRemove1)){
				adding1 = false;
				adding2 = false;
				removing1 = true;
				removing2 = false;
			}
			else{
				adding1 = false;
				adding2 = false;
				removing1 = false;
				removing2 = true;
			}
		}
	}
	/**
 	* Processes click events for the "run" button
 	*/
	private class ClickListener_RunMap implements ActionListener{
	   /**
	 	* Moves the simulation to the next stage when conditions are met
	 	*/
		public void actionPerformed(ActionEvent e){
			if(mapChoosing && selectedMap == 1){
				mapPreper();
			}
			else if(mapPreping){
				if(!(team1Members.isEmpty() || team2Members.isEmpty())){
					mapRunner();
					try{
						JButton j = (JButton) e.getSource();
						j.setText("{| Next  Turn |}");
					}catch(Exception x){x.printStackTrace();}
				}
				else{
					Driver.getSim().updateOutput("{|No empty teams.|}");
				}
			}
			else if(mapRunning){
				nextTurn();
			}
		}
	}
	/**
 	* Processes click events for the map buttons
 	*/
	private class ClickListener_Tile implements ActionListener{
		/**
		* Adds a Robot to team 1 
 		*/
		private void add1(ActionEvent e){
			if(tileBlocks.get(e.getSource()).isFree()){
				try{
					JButton b = (JButton) e.getSource();
					b.setIcon(Data.pic("Bot1"));
					b.setRolloverIcon(Data.pic("Bot1Hover"));
					
					String[] models = Data.modelList();
					String choice = (String)JOptionPane.showInputDialog(Driver.getFrame(),
							"Choose the type of robot:","Robot Selection",
							JOptionPane.PLAIN_MESSAGE,null,models,models[0]);
					
					Data.madeBot(choice);
					Robot r = new Robot(Data.botstring(choice));
					r.setLoc(tileBlocks.get(b).getLoc());
					r.setTeam(1);
					tileBlocks.get(b).setOccupied(r);
					team1Members.add(r);
				}catch(Exception x){}
			}
		}
		/**
		* Adds a Robot to team 2
 		*/
		private void add2(ActionEvent e){
			if(tileBlocks.get(e.getSource()).isFree()){
				try{
					JButton b = (JButton) e.getSource();
					b.setIcon(Data.pic("Bot2"));
					b.setRolloverIcon(Data.pic("Bot2Hover"));
					
					String[] models = Data.modelList();
					String choice = (String)JOptionPane.showInputDialog(Driver.getFrame(),
							"Choose the type of robot:","Robot Selection",
							JOptionPane.PLAIN_MESSAGE,null,models,models[0]);
					
					Data.madeBot(choice);
					Robot r = new Robot(Data.botstring(choice));
					r.setLoc(tileBlocks.get(b).getLoc());
					r.setTeam(2);
					tileBlocks.get(b).setOccupied(r);
					team2Members.add(r);
				}catch(Exception x){}
			}
		}
		/**
		* Removes a Robot from team 1
		*/
		private void remove1(ActionEvent e){
			if(team1Members.contains(tileBlocks.get(e.getSource()).getOccupant())){
				try{
					JButton b = (JButton) e.getSource();
					b.setIcon(Data.pic("Open"));
					b.setRolloverIcon(Data.pic("OpenHover"));
					team1Members.remove(tileBlocks.get(b).getOccupant());
					tileBlocks.get(b).setOccupant(null);
					tileBlocks.get(b).setOccupied(false);
				}catch(Exception x){}
			}
		}
		/**
		* Removes a Robot from team 2
		*/
		private void remove2(ActionEvent e){
			if(team2Members.contains(tileBlocks.get(e.getSource()).getOccupant())){
				try{
					JButton b = (JButton) e.getSource();
					b.setIcon(Data.pic("Open"));
					b.setRolloverIcon(Data.pic("OpenHover"));
					team2Members.remove(tileBlocks.get(b).getOccupant());
					tileBlocks.get(b).setOccupant(null);
					tileBlocks.get(b).setOccupied(false);
				}catch(Exception x){}
			}
		}
		
		/**
	 	* Determines what action is being requested and performs it, and updates the team lists
	 	*/
		public void actionPerformed(ActionEvent e){
			if(mapPreping){
				if(adding1){
					add1(e);
				}
				else if(adding2){
					add2(e);
				}
				else if(removing1){
					remove1(e);
				}
				else if(removing2){
					remove2(e);
				}
				updateMembers();
			}
		}
	}

	/**
 	* Enables the stats appearing whenever the mouse enters the space of a map button that is occupied
 	*/
	private class MListener_Tile implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if(tileBlocks.get(e.getSource()).isOccupied() && (mapPreping || mapRunning)){
				Robot r = tileBlocks.get(e.getSource()).getOccupant();
				String text = "";
				text = "{|  TEAM  " + r.getTeam() + "  |}\n";
				text = text + r.getID() + "\n";
				text = text + "Shaken: " + r.isShaken() + "\n";
				text = text + "Wounds: " + r.getWounds() + "\n";
				updateDesc(text);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if(mapPreping || mapRunning){
				updateDesc("");
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}



}
