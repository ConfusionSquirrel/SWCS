package scott.emmett;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Creates and controls Robots
 */
public class Robot {
	//botstring data
	private Card card;
	private Hashtable<String,Integer> atts = new Hashtable<>();//attributes
	private Hashtable<String,Integer> gear = new Hashtable<>();//name,quantity
	private Hashtable<String,Integer> mods = new Hashtable<>();//name,quantity
	private Hashtable<String,Integer> skills = new Hashtable<>();
	private int pace;
	private int parry;
	private int size;
	private int team;
	private int toughness;
	private Point loc;
	private String id;
	private String model;
	//sim data
	private ArrayList<Block> closedTiles = new ArrayList<>();
	private ArrayList<Block> knownTiles = new ArrayList<>();
	private ArrayList<Block> seenTiles = new ArrayList<>();
	private ArrayList<Block> unknownTiles = new ArrayList<>();
	private ArrayList<Robot> friendlies = new ArrayList<>();
	private ArrayList<Robot> threats = new ArrayList<>();
	private ArrayList<Item> equipped = new ArrayList<>();//equipped gear (ie. reflective vest might only be worn sometimes)
	private ArrayList<Item> weapons = new ArrayList<>();
	private boolean hasRun;
	private boolean incapacitated;
	private boolean shaken;
	private double paceLeft;
	private int wounds;
	private SimPanel sim;
	private String priority;
	
	/**
	 * Constructs a complete Robot from a provided formatted string
	 */
	public Robot(String botstring){
		String[] b = botstring.split("/");
		ArrayList<String> bits = new ArrayList<>();
		for(String s : b){
			bits.add(s);
		}
		//process static positions
		model = bits.get(0);
		size = Integer.parseInt(bits.get(1));
		pace = Integer.parseInt(bits.get(2));
		atts.put("agility", Integer.parseInt(bits.get(3)));
		atts.put("smarts", Integer.parseInt(bits.get(4)));
		atts.put("spirit", Integer.parseInt(bits.get(5)));
		atts.put("strength", Integer.parseInt(bits.get(6)));
		atts.put("vigor", Integer.parseInt(bits.get(7)));
		//process non-static positions
		//ignore bits.get(8) as skill label
		int track = 9;
		while(bits.get(track).startsWith(">")){
			String skill = bits.get(track).substring(1, bits.get(track).indexOf(':'));
			int die = Integer.parseInt(bits.get(track).substring(bits.get(track).indexOf(':') + 1));
			skills.put(skill,die);
			track++;
		}
		track++;//skipping mod label
		while(bits.get(track).startsWith(">")){
			String mod = bits.get(track).substring(1, bits.get(track).indexOf(':'));
			int num = Integer.parseInt(bits.get(track).substring(bits.get(track).indexOf(':') + 1));
			mods.put(mod,num);
			track++;
		}
		track++;//skipping gear label
		while(bits.get(track).startsWith(">")){
			String item = bits.get(track).substring(1, bits.get(track).indexOf(':'));
			int num = Integer.parseInt(bits.get(track).substring(bits.get(track).indexOf(':') + 1));
			gear.put(item,num);
			track++;
		}
		//Derived stats
		deriveParry();
		deriveToughness();
		deriveID();
		//
		processGear();
		//
		shaken = false;
		incapacitated = false;
		wounds = 0;
	}
	
	/**
	 * Sorts the provided RWeapons in order of their estimated damage-dealing capability
	 */
	private ArrayList<RWeapon> efficacySort(ArrayList<RWeapon> a){
		ArrayList<RWeapon> sorted = new ArrayList<>();
		Hashtable<Integer,Double> damageForIndex = new Hashtable<>();
		for(int i = 0; i < a.size(); i++){
			if(!a.get(i).getType().equals("Launcher")){
				damageForIndex.put(i, a.get(i).getAvgDamage());
			}
			else{
				String searchType = a.get(i).getName().substring(0, a.get(i).getName().indexOf(" "));
				ArrayList<RWeapon> ammo = new ArrayList<>();
				for(Item it : weapons){
					try{
						RWeapon r = (RWeapon) it;
						if(r.getType().equals(searchType)) ammo.add(r);
					}catch(Exception x){}
				}
				double highestDamage = 0;
				for(RWeapon r : ammo){
					if(!r.getDamage().equals(null)){
						if(r.getAvgDamage() > highestDamage) highestDamage = r.getAvgDamage();
					}
				}
				damageForIndex.put(i, highestDamage);
			}
		}
		int track;
		double num;
		for(int i = 0; i < a.size(); i++){
			track = 0;
			num = 0;
			for(int j = 0; j < a.size(); j++){
				try{
					if(damageForIndex.get(j) > num){
						num = damageForIndex.get(j);
						track = j;
					}
				}catch(Exception x){}
			}
			sorted.add(a.get(track));
			damageForIndex.remove(track);
		}
		
		return sorted;
	}
	/**
	 * Determines the amount of movement it would require to reach the provided Point
	 */
	private double distanceTo(Point p){
		int myx = loc.x;
		int myy = loc.y;
		int theirx = p.x;
		int theiry = p.y;
		double total = 0;
		int xDif = Math.abs(myx - theirx);
		int yDif = Math.abs(myy - theiry);
		if(xDif > yDif){
			total += 1.5 * yDif;
			total += (xDif - yDif);
		}
		else{
			total += 1.5 * xDif;
			total += (yDif - xDif);
		}
		return total;
	}
	private int attCheck(String att){
		int total = Dice.roll(atts.get(att), 1);
		total -= wounds;
		return total;
	}
	/**
	 * Returns the result of a skill trait roll, performed the number of times specified for the provided skill
	 */
	private int[] skillCheck(String skill,int mod,int num){
		int[] results = new int[num];
		for(int i = 0; i < results.length; i++){
			results[i] = Dice.roll(skills.get(skill), 1);
			results[i] = results[i] - wounds;
		}
		int rollWild = Dice.roll(6, 1);
		int lowest = 0;
		int lowestValue = results[0];
		for(int i = 1; i < results.length; i++){
			if(results[i] < lowestValue){
				lowestValue = results[i];
				lowest = i;
			}
		}
		if(rollWild > lowestValue){
			results[lowest] = rollWild;
		}
		
		return results;
	}
	/**
	 * Returns the Point that is closer to this Robot
	 */
	private Point closer(Point p1, Point p2){
		if(p1.distance(loc) > p2.distance(loc)) return p2;
		else return p1;
	}
	
	/**
	 * Takes an action with the offense priority
	 */
	private void actOffense(){
		if(!threats.isEmpty()){
			//find closest threat
			Robot closest = threats.get(0);
			for(Robot r : threats){
				if(closer(closest.getLoc(),r.getLoc()).equals(r.getLoc())){
					closest = r;
				}
			}
			sim.updateOutput("> Targeted " + closest.getLoc().x + "," + closest.getLoc().y);
			sim.updateOutput("  > Distance is " + (distanceTo(closest.getLoc()) - 1));
			int shortestIncrement = 100;
			for(Item i : weapons){
				try{
					if(((RWeapon)i).getRange() > 5){
						if(shortestIncrement > ((RWeapon) i).getRange()) shortestIncrement = ((RWeapon) i).getRange();
					}
					else if(((RWeapon)i).getRange() == 5){
						boolean hasLauncher = false;
						for(Item j : weapons){
							if(j.name.equals("Grenade Launcher")){
								hasLauncher = true;
							}
						}
						if(!hasLauncher){
							if(shortestIncrement > 5) shortestIncrement = 5;
						}
					}
				}catch(Exception x){}
			}
			if(distanceTo(closest.getLoc()) > shortestIncrement){
				moveTowardBuffered(closest.getLoc(),shortestIncrement - 1);
			}
			sim.updateOutput("> Attacking " + closest.id);
			rangedAttack(closest);
			
		}
	}
	/**
 	* Performs a ranged attack on the specified target
 	*/
	private void rangedAttack(Robot target){
		ArrayList<RWeapon> usefulWeaps = new ArrayList<>();
		for(Item i : weapons){
			try{
				RWeapon r = (RWeapon) i;
				if(distanceTo(target.getLoc()) <=  r.getRange() * 4 && !(r.getType().equals("Grenade") || r.getType().equals("Gyrojet"))){
					usefulWeaps.add(r);
				}
			}catch(Exception x){}
		}
		
		int hands = 2 + mods.get("Weapon Mount");
		if(usefulWeaps.size() > hands){
			usefulWeaps = efficacySort(usefulWeaps);
			usefulWeaps = (ArrayList<RWeapon>) usefulWeaps.subList(0, hands - 1);
		}
		
		for(RWeapon r : usefulWeaps){
			if(!target.isIncapacitated()){
				int hitMod = 0;
				if(distanceTo(target.getLoc()) >= r.getRange() * 4){
					hitMod -= 4;
				}
				else if(distanceTo(target.getLoc()) >= r.getRange() * 2){
					hitMod -= 2;
				}
				
				if(mods.containsKey("Targeting System") && hitMod < 0){
					if(hitMod >= -2){
						hitMod = 0;
					}
					else{
						hitMod += 2;
					}
				}
				
				int[] toHit = skillCheck("shooting",hitMod,r.getROF());
				
				for(int i = 0; i < toHit.length; i++){
					if(!target.isIncapacitated()){
						sim.updateOutput("  > Rolled " + toHit[i] + " to hit with " + r.name);
						if(toHit[i] < 4){
							sim.updateOutput("    > Missed");
						}
						else{
							int damage = 0;
							
							if(toHit[i] < 8){
								sim.updateOutput("    > Hit");
							}
							else{
								sim.updateOutput("    > Hit with a raise");
								damage += Dice.roll(6, 1);
							}
							
							if(!r.getType().equals("Launcher")){
								damage += Dice.rollInterpreted(r.getDamage());
							}
							else{
								String searchType = r.getName().substring(0, r.getName().indexOf(" "));
								ArrayList<RWeapon> ammo = new ArrayList<>();
								for(Item it : weapons){
									try{
										RWeapon rw= (RWeapon) it;
										if(rw.getType().equals(searchType)) ammo.add(rw);
									}catch(Exception x){}
								}
								int highestDamage = 0;
								double highestDamageValue = ammo.get(0).getAvgDamage();
								for(int j = 0; j < ammo.size(); j++){
									if(!ammo.get(j).getDamage().equals(null)){
										if(ammo.get(j).getAvgDamage() > highestDamageValue){
											highestDamageValue = ammo.get(j).getAvgDamage();
											highestDamage = j;
										}
										
									}
								}
								damage += Dice.rollInterpreted(ammo.get(highestDamage).getDamage());
							}
							sim.updateOutput("    > Rolled " + damage + " damage");
							
							target.hit(damage,r.getAP(),r.getType());
							
						}
					}
				}
			}
			
		}
		
		
	}
	/**
 	* Derives this Robot's id from its model and the number of Robots of that model that already exist
 	*/
	private void deriveID(){
		id = model + "-" + Data.getNumInSim(model);
	}
	/**
 	* Derives the parry stat from the fighting skill
 	*/
	private void deriveParry(){
		parry = 2;
		if(skills.containsKey("fighting")){
			parry += skills.get("fighting")/2;
		}
	}
	/**
 	* Derives the toughness stat from the vigor attribute
 	*/
	private void deriveToughness(){
		toughness = 2 + atts.get("vigor")/2;
	}
	/**
 	* Determines the tactical priority for this round
 	* [Currently only has offense, but creates an infrastructure to support further expansion]
 	*/
	private void determinePriority(){
		priority = "offense"; //later add "defense" option
	}
	/**
 	* Attampts to move the Robot one tile closer to the specified Point
 	*/
	private void moveToward(Point target){
		int xDif = target.x - loc.x;
		int yDif = target.y - loc.y;
		int oldX = loc.x;
		int oldY = loc.y;
		int newX = loc.x;
		int newY = loc.y;
		if(Math.abs(xDif) > 0 && Math.abs(yDif) > 0 && paceLeft > 1){
			if(xDif > 0){
				newX++;
			}
			else if(xDif < 0){
				newX--;
			}
			if(yDif > 0){
				newY++;
			}
			else if(yDif < 0){
				newY--;
			}
			if(!sim.tileOccupied(newX, newY))loc.setLocation(newX, newY);
			paceLeft -= 1.5;
		}
		else if(Math.abs(xDif) > Math.abs(yDif) && paceLeft > 0.5){
			if(xDif > 0){
				newX++;
			}
			else if(xDif < 0){
				newX--;
			}
			if(!sim.tileOccupied(newX, newY))loc.setLocation(newX, newY);
			paceLeft -= 1;
		}
		else if(Math.abs(yDif) > Math.abs(xDif) && paceLeft > 0.5){
			if(yDif > 0){
				newY++;
			}
			else if(yDif < 0){
				newY--;
			}
			if(!sim.tileOccupied(newX, newY))loc.setLocation(newX, newY);
			paceLeft -= 1;
		}
		if(!sim.tileOccupied(newX, newY)){
			sim.updateOutput(">Moved from " + oldX + "," + oldY + " to " + newX + "," + newY);
			sim.moved(this,new Point(oldX,oldY),loc);
		}
	}
	/**
 	* Attempts to move as close as possible to the specified Point while leaving the minimum distance given
 	*/
	private void moveTowardBuffered(Point target,int buffer){
		while(paceLeft > 0.5 && distanceTo(target) > buffer){
			moveToward(target);
		}
	}
	/**
 	* Reads through the gear list and creates associated items
 	*/
	private void processGear(){
		for(String s : gear.keySet()){
			if(Data.isWeap(s)){
				for(int i = 0; i < gear.get(s); i++){
					weapons.add(Data.getWeap(s));
				}
			}
		}
	}
	/**
 	* Reads through the visual data and determines the location of friendly and hostile Robots
 	*/
	private void processSeen(){
		friendlies.clear();
		threats.clear();
		for(Block b : seenTiles){
			if(b.isOccupied()){
				if(b.getLoc().equals(loc)){}
				else if(b.getOccupant().getTeam() == team){
					friendlies.add(b.getOccupant());
				}
				else if(b.getOccupant().getTeam() != team){
					threats.add(b.getOccupant());
				}
			}
		}
	}
	/**
 	* Assigns wounds to the Robot and determines the effects
 	*/
	private void wound(int num){
		if(wounds + num < 3){
			wounds += num;
			shaken = true;
		}
		else{
			wounds = 3;
			incapacitated = true;
			sim.updateOutput("    > Killed " + id);
			sim.kill(this);
		}
	}
	
	/**
	 * Returns true if the Robot is incapacitated
	 */
	public boolean isIncapacitated(){
		return incapacitated;
	}
	/**
	 * Returns true if the Robot is shaken
	 */
	public boolean isShaken(){
		return shaken;
	}
	/**
 	* Returns the Card that this Robot is holding
 	*/
	public Card getCard(){
		return card;
	}
	/**
 	* Returns the team number of this Robot
 	*/
	public int getTeam(){
		return team;
	}
	/**
 	* Returns the number of wounds this Robot has suffered
 	*/
	public int getWounds(){
		return wounds;
	}
	/**
 	* Returns the location of this Robot
 	*/
	public Point getLoc(){
		return loc;
	}
	/**
	 * Returns the unique (per simulation) id of the Robot
	 */
	public String getID(){
		return id;
	}
	/**
	 * Returns this Robot's non-simulation-dependent data in a string
	 */
	public String toString(){
		String output = "";
		output = output + model + "/";
		output = output + size + "/";
		output = output + pace + "/";
		String[] a = {"agility","smarts","spirit","strength","vigor"};
		for(String s : a){
			output = output + atts.get(s) + "/";
		}
		output = output + "skills" + "/";
		for(String s : skills.keySet()){
			output = output + ">" + s + ":" + skills.get(s) + "/";
		}
		output = output + "mods" + "/";
		for(String s : mods.keySet()){
			output = output + ">" + s + ":" + mods.get(s) + "/";
		}
		output = output + "gear" + "/";
		for(String s : gear.keySet()){
			output = output + ">" + s + ":" + gear.get(s) + "/";
		}
				
		output += id;
		return output;
	}

	
	/**
	 * Hits this Robot with the given damage and determines if wounds are suffered
	 */
	public void hit(int damage, int ap, String type){
		int armour = 0;
		int tough = toughness;
		if(gear.keySet().contains("Combat Armour")){
			armour = 6;
		}
		if(mods.keySet().contains("Armour")){
			armour += 2 * mods.get("Armour");
		}
		if(mods.keySet().contains("Heavy Armour")){
			armour += 2 * mods.get("Heavy Armour");
		}
		
		if(type.equals("Blaster") || type.equals("Slugthrower") || type.equals("Flak")){
			ap -= 4;
		}
		if(ap > 0){
			armour -= ap;
		}
		if(armour > 0){
			tough += armour;
		}
		
		int excess = damage - tough;
		if(excess >= 0){
			if(excess > 3){
				int num = (int) (excess / 4);
				sim.updateOutput("    > " + id + " suffers " + num + " wound(s)");
				wound(num);
			}
			else{
				if(shaken){
					sim.updateOutput("    > " + id + " suffers 1 wound");
					wound(1);
				}
				else{
					shaken = true;
					sim.updateOutput("    > " + id + " is shaken");
				}
			}
		}
		else{
			sim.updateOutput("    > Hit resisted");
		}
	}
	/**
 	* Deals the provided card to this Robot
 	*/
	public void setCard(Card c){
		card = c;
	}
	/**
 	* Sets the location of this Robot
 	*/
	public void setLoc(Point p){
		loc = p;
	}
	/**
 	* Sets the active simulation with which this Robot is associated
 	*/
	public void setSim(SimPanel sp){
		sim = sp;
	}
	/**
 	* Sets the team number of this Robot
 	*/
	public void setTeam(int i){
		team = i;
	}
	/**
 	* Undergoes all the necessary steps of a turn of combat
 	*/
	public void takeTurn(){
		sim.updateOutput("{|" + id + " is acting.|}");
		sim.updateOutput("> " + card.getName());
		//take initial visual data
		seenTiles = sim.see(this);
		processSeen();
		paceLeft = pace - wounds;
		hasRun = false;
		if(shaken){
			sim.updateOutput("> " + id + " is shaken");
			int roll = attCheck("vigor");
			if(roll > 3){
				shaken = false;
				if(roll > 7){
					sim.updateOutput("  > Recovered fully");
					determinePriority();
					if(priority.equals("offense")){
						actOffense();
					}
				}
				else{
					sim.updateOutput("  > Recovered");
				}
			}
			else{
				sim.updateOutput("  > Failed to recover");
			}
		}
		else{
			determinePriority();
			if(priority.equals("offense")){
				actOffense();
			}
		}
	}
	
}
