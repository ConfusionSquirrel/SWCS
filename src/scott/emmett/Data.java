package scott.emmett;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.ImageIcon;

/**
 * Retrieves, stores, and manages data from non-class files
 */
public class Data {
	//BOT MODELS
	private static Hashtable<String,Integer> numOfBots = new Hashtable<>();//model,number currently in simulation
	private static Hashtable<String,String> botstrings = new Hashtable<>();//model name, botstring
	/**
	 * Retrieves and stores botstrings off all available Robot models
	 */
	private static void loadBotModels(){
		String filename = "botModels.txt";
		File f = new File(filename);
		try{
			Scanner in = new Scanner(f);
			String line;
			String[] bits;
			while(in.hasNextLine()){
				line = in.nextLine();
				bits = line.split("/");
				botstrings.put(bits[0], line);
			}
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the number of Robots of the model provided that have been created in this simulation
	 */
	public static int getNumInSim(String model){
		return numOfBots.get(model);
	}
	/**
	 * Returns the botstring associated with that model
	 */
	public static String botstring(String model){
		return botstrings.get(model);
	}
	/**
	 * Returns an alphabetically sorted list of available Robot models
	 */
	public static String[] modelList(){
		String[] models = new String[botstrings.keySet().size()];
		Enumeration<String> e = botstrings.keys();
		for(int i = 0; i < botstrings.keySet().size(); i++){
			models[i] = e.nextElement();
		}
		models = alphaSort(models);
		return models;
	}
	
	/**
	 * Updates numOfBots, adding one to the number of Robots of the model provided
	 */
	public static void madeBot(String model){
		if(numOfBots.containsKey(model)){
			numOfBots.replace(model, numOfBots.get(model) + 1);
		}
		else{
			numOfBots.put(model, 1);
		}
	}
	/**
	 * Resets the number of Robots of each model to zero when a new simulation is started
	 */
	public static void wipeTracker(){
		numOfBots.clear();
	}
	
	//BOT MODS
	private static Hashtable<String,Double> slotCosts = new Hashtable<>();
	private static Hashtable<String,Integer> maxTimesTaken = new Hashtable<>();
	/**
	 * Retrieves and stores information on all possible Robot modifications
	 * [This data isn't used yet, but provides infrastructure for possible later expansion]
	 */
	private static void loadBotMods(){
			String filename = "botMods.txt";
			File f = new File(filename);
			try{
				Scanner in = new Scanner(f);
				String line = "";
				String[] bits = new String[3];
				while(in.hasNextLine()){
					line = in.nextLine();
					bits = line.split(",");
					if(!line.startsWith("//")){
						slotCosts.put(bits[0], Double.parseDouble(bits[2]));
						maxTimesTaken.put(bits[0], Integer.parseInt(bits[1]));
					}
				}
				
				in.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	//MAPS
	private static ArrayList<ArrayList<char[]>> mapLayouts = new ArrayList<>();//0 will be blank, 1 refers to file map1 and so on
	/**
	 * Retrieves and stores character-grid layouts of all available maps
	 */
	private static void loadMaps(){
		mapLayouts.add(new ArrayList<char[]>());
		for(int i = 1; i < 4; i++){
			String filename = "map" + i + ".txt";
			File f = new File(filename);
			try {
				Scanner in = new Scanner(f);
				ArrayList<char[]> map = new ArrayList<>();
				String line;
				while(in.hasNextLine()){
					line = in.nextLine();
					map.add(line.toCharArray());
				}
				mapLayouts.add(i,map);
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the grid of specified name (1 corresponds to Map1, etc)
	 */
	public static ArrayList<char[]> getLayout(int i){
		return mapLayouts.get(i);
	}
	/**
	 * Returns the number of available map grids
	 */
	public static int numOfMaps(){
		return mapLayouts.size() - 1;
	}
	
	//IMAGES
	private static Hashtable<String,ImageIcon> pics = new Hashtable<>();
	/**
	 * Retrieves and stores all associated image files as ImageIcons
	 */
	private static void loadPics(){
		pics.put("Blank", new ImageIcon("TileBlank.png"));
		pics.put("Bot1", new ImageIcon("TileBot1.png"));
		pics.put("Bot1Hover", new ImageIcon("TileBot1Hover.png"));
		pics.put("Bot2", new ImageIcon("TileBot2.png"));
		pics.put("Bot2Hover", new ImageIcon("TileBot2Hover.png"));
		pics.put("BotFriendly", new ImageIcon("TileBotFriendly.png"));
		pics.put("BotThreat", new ImageIcon("TileBotThreat.png"));
		pics.put("BotThreatSelected", new ImageIcon("TileBotThreatSelected.png"));
		pics.put("BotTurn", new ImageIcon("TileBotTurn.png"));
		pics.put("Closed", new ImageIcon("TileClosed.png"));
		pics.put("ClosedHover", new ImageIcon("TileClosedHover.png"));
		pics.put("ClosedSeen", new ImageIcon("TileClosedSeen.png"));
		pics.put("Open", new ImageIcon("TileOpen.png"));
		pics.put("OpenHover", new ImageIcon("TileOpenHover.png"));
		pics.put("OpenSeen", new ImageIcon("TileOpenSeen.png"));
	}
	
	/**
	 * Returns the ImageIcon associated with the provided name
	 */
	public static ImageIcon pic(String s){
		return pics.get(s);
	}
	
	//WEAPONS
	private static Hashtable<String,String> weapstrings = new Hashtable<>();
	/**
	 * Retrieves and stores weapstring data for all weapons
	 */
	private static void loadWeapons(){
		String filename = "weapons.txt";
		File f = new File(filename);
		try {
			Scanner in = new Scanner(f);
			String line;
			String[] bits;
			while(in.hasNextLine()){
				line = in.nextLine();
				if(!line.startsWith("//")){
					bits = line.split("/");
					weapstrings.put(bits[0], line);
				}
			}
			in.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns true if the name provided corresponds to a weapon
	 */
	public static boolean isWeap(String s){
		if(weapstrings.keySet().contains(s))return true;
		else return false;
	}
	/**
	 * Returns the weapon corresponding to the name provided as an Item
	 */
	public static Item getWeap(String s){
		if(weapstrings.get(s).contains("rweapon")){
			return new RWeapon(weapstrings.get(s));
		}
		else {
			return new MWeapon(weapstrings.get(s));
		}
	}
	
	//OTHER METHODS
	/**
	 * Calls all load methods
	 */
	public static void loadFiles(){
		loadBotModels();
		loadBotMods();
		loadMaps();
		loadPics();
		loadWeapons();
	}
	
	/**
	 * Returns an alphabetically sorted copy of the list provided
	 */
	private static String[] alphaSort(String[] s){
		String[] out = new String[s.length];
		String check;
		int checknum;
		ArrayList<String> a = new ArrayList<>();
		for(int i = 0; i < s.length; i++){
			a.add(s[i]);
		}
		for(int i = 0; i < s.length; i++){
			check = a.get(0);
			checknum = 0;
			if(a.size() > 1){	
				for(int j = 1; j < a.size(); j++){
					if(check.compareTo(a.get(j)) == 1){
						check = a.get(j);
						checknum = j;
					}
				}
			}
			out[i] = check;
			a.remove(checknum);
		}
		return out;
	}
	
}
