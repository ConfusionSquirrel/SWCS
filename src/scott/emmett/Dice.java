package scott.emmett;

import java.util.ArrayList;
import java.util.Random;

/**
 * Controls dice rolling
 */
public class Dice {

	private static Random rand = new Random();
	
	/**
	 * Returns the total sum of a die with "type" sides rolled "num" times
	 */
	public static int roll(int type, int num){
		int total = 0;
		int rolled = 0;
		for(int i = 0; i < num; i++){
			do{
				rolled = rand.nextInt(type) + 1;
				total += rolled;
			}while(rolled == type);
		}
		return total;
	}
	/**
	 * Returns the total sum of a dice roll, derived from a formatted string
	 */
	public static int rollInterpreted(String s){
		int total = 0;
		ArrayList<Integer> dice = new ArrayList<>();
		int mod = 0;
		String damagestring = s;
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
				total += roll(i,1);
			}
			total += mod;
		
			return total;
		}
	}
	
}
