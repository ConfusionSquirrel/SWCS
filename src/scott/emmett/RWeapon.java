package scott.emmett;

import java.util.ArrayList;

/**
 * Creates ranged weapon items
 */
public class RWeapon extends Item {//ranged weapon

	private boolean burst; //3RB
	private boolean hw; //heavy weapon
	private boolean sa; //semi automatic
	private boolean template;
	private int ap; //armour piercing
	private int minStr; //min strength to use
	private int range; //fixed for template, first increment for incremental
	private int rof; //rate of fire
	private String damage;
	private String templateShape;
	private String type;
	
	/**
	 * Constructs a blank RWeapon
	 */
	public RWeapon(){
		
	}
	/**
	 * Constructs a complete RWeapon from a provided formatted string
	 */
	public RWeapon(String weapstring){
		String[] bits = weapstring.split("/");
		name = bits[0];
		type = bits[2];
		burst = Boolean.parseBoolean(bits[3]);
		hw = Boolean.parseBoolean(bits[4]);
		sa = Boolean.parseBoolean(bits[5]);
		template = Boolean.parseBoolean(bits[6].substring(0, bits[6].indexOf(':')));
		templateShape = bits[6].substring(bits[6].indexOf(':') + 1);
		ap = Integer.parseInt(bits[7]);
		minStr = Integer.parseInt(bits[8]);
		range = Integer.parseInt(bits[9]);
		rof = Integer.parseInt(bits[10]);
		damage = bits[11];
	}
	
	/**
	 * Returns true if this weapon is capable of three-round bursts
	 */
	public boolean isBurst(){
		return burst;
	}
	/**
	 * Returns true if this weapon is a heavy weapon
	 */
	public boolean isHW(){
		return hw;
	}
	/**
	 * Returns true if this weapon is semi-automatic
	 */
	public boolean isSA(){
		return sa;
	}
	/**
	 * Returns true if this weapon inflicts damage in a template rather than to an individual
	 */
	public boolean isTemplate(){
		return template;
	}
	/**
	 * Returns the approximately average damage that could be expected of this weapon
	 */
	public double getAvgDamage(){
		double total = 0;
		ArrayList<Integer> dice = new ArrayList<>();
		int mod = 0;
		String damagestring = damage;
		if(damagestring.equals("null")) return 0;
		else{
			String temp;
			while(damagestring.contains("+")){
				temp = damagestring.substring(0,damagestring.indexOf("+"));
				damagestring = damagestring.substring(damagestring.indexOf("+") + 1);
				if(temp.indexOf("d") > 0){
					for(int i = 0; i < Integer.parseInt(temp.substring(0, temp.indexOf("d"))); i++){
						dice.add(Integer.parseInt(temp.substring(temp.indexOf("d") + 1)));
					}
				}
				else{
					dice.add(Integer.parseInt(temp.substring(1)));
				}
			}
			if(damagestring.indexOf("d") != -1){
				if(damagestring.indexOf("d") > 0){
					for(int i = 0; i < Integer.parseInt(damagestring.substring(0, damagestring.indexOf("d"))); i++){
						dice.add(Integer.parseInt(damagestring.substring(damagestring.indexOf("d") + 1)));
					}
				}
				else{
					dice.add(Integer.parseInt(damagestring.substring(1)));
				}
			}
			else{
				mod += Integer.parseInt(damagestring);
			}
			
			for(int i : dice){
				total += i/2 + 1;
			}
			total += mod;
			total *= rof;
		
			return total;
		}
	}
	/**
	 * Returns the armour-piercing value of this weapon
	 */
	public int getAP(){
		return ap;
	}
	/**
	 * Returns the minimum strength value required to wield this weapon
	 */
	public int getMinStr(){
		return minStr;
	}
	/**
	 * Returns the shortest range increment of this weapon
	 */
	public int getRange(){
		return range;
	}
	/**
	 * Returns this weapon's rate of fire
	 */
	public int getROF(){
		return rof;
	}
	/**
	 * Returns this weapon's formatted damage string
	 */
	public String getDamage(){
		return damage;
	}
	/**
	 * Returns the shape of the this weapon's damage template, null if not applicable
	 */
	public String getTemplateShape(){
		return templateShape;
	}
	/**
	 * Returns the type of this weapon (eg. laser, blaster, slugthrower, plasma, etc.)
	 */
	public String getType(){
		return type;
	}

}
