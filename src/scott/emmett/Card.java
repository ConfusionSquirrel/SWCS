package scott.emmett;
/**
 * 
 * Represents a playing card drawn
 *
 */
public class Card {

	private double value;
	private String name;
	
	/**
	 * Constructs card, assigns name and derived numerical value
	 */
	public Card(String s){
		name = s;
		value = Deck.cardToValue(name);
	}

	/**
	 * Returns numerical value of card (accounts for reverse-alpha suit priority)
	 */
	public double getValue(){
		return value;
	}
	/**
	 * Returns name (eg. Ace of Spades)
	 */
	public String getName(){
		return name;
	}
}
