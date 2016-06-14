package scott.emmett;

/**
 * Creates melee weapon items
 */
public class MWeapon extends Item {

	private boolean hw;
	private boolean twoHand;
	private int ap;
	private int reach;
	private String damage;
	private String type;
	private String typeSF;//type sci-fi: chain, power, energy, etc
	
	/**
	 * Constructs a blank MWeapon
	 */
	public MWeapon(){
		
	}
	/**
	 * Constructs a complete MWeapon from a provided formatted string
	 */
	public MWeapon(String weapstring){
		String[] bits = weapstring.split("/");
		name = bits[0];
		typeSF = name.substring(0, name.indexOf(' '));
		type = name.substring(name.indexOf(' ') + 1);
		hw = Boolean.parseBoolean(bits[2]);
		twoHand = Boolean.parseBoolean(bits[3]);
		ap = Integer.parseInt(bits[4]);
		reach = Integer.parseInt(bits[5]);
		damage = bits[6];
	}
	
	/**
	 * Returns true if this weapon is a heavy weapon
	 */
	public boolean isHW(){
		return hw;
	}
	/**
	 * Returns true if this is a two-handed weapon
	 */
	public boolean isTwoHand(){
		return twoHand;
	}
	/**
	 * Returns the armour-piercing value of this weapon
	 */
	public int getAP(){
		return ap;
	}
	/**
	 * Returns the reach distance of this weapon
	 */
	public int getReach(){
		return reach;
	}
	/**
	 * Returns the formatted damage string of this weapon
	 */
	public String getDamage(){
		return damage;
	}
	/**
	 * Returns the weapon type (eg. sword, flail, axe, etc.)
	 */
	public String getType(){
		return type;
	}
	/**
	 * Returns the sci-fi type of this weapon (eg. chain, power, molecular, etc.)
	 */
	public String getTypeSF(){
		return typeSF;
	}
}
