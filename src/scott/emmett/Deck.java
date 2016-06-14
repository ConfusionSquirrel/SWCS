package scott.emmett;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Stack;

/**
 * Represents a deck of cards, complete with two jokers
 */
public class Deck {

	private Stack<Card> activeDeck;
	private ArrayList<Card> fullDeck;
	private Random rand;
	
	/**
	 * Constructs a new Deck, initiates the Random, and prepares the Deck for use
	 */
	public Deck(){
		activeDeck = new Stack<>();
		fullDeck = new ArrayList<>();
		rand = new Random();
		loadFullDeck();
		initiateDeck();
	}
	
	/**
	 * Gives the Deck a copy of every card, and shuffles
	 */
	private void fill(){
		initiateDeck();
	}
	/**
	 * Gives the Deck a copy of every card, and shuffles (called by Deck() and fill())
	 */
	private void initiateDeck(){
		for(Card c : fullDeck){
			activeDeck.push(c);
			shuffle();
		}
	}
	/**
	 * Fills the reference deck with one of every card
	 */
	private void loadFullDeck(){
		String[] suits = {"Spades","Hearts","Diamonds","Clubs"};
		for(String s : suits){
			fullDeck.add(new Card("Ace" + " of " + s));
			fullDeck.add(new Card("Two" + " of " + s));
			fullDeck.add(new Card("Three" + " of " + s));
			fullDeck.add(new Card("Four" + " of " + s));
			fullDeck.add(new Card("Five" + " of " + s));
			fullDeck.add(new Card("Six" + " of " + s));
			fullDeck.add(new Card("Seven" + " of " + s));
			fullDeck.add(new Card("Eight" + " of " + s));
			fullDeck.add(new Card("Nine" + " of " + s));
			fullDeck.add(new Card("Ten" + " of " + s));
			fullDeck.add(new Card("Jack" + " of " + s));
			fullDeck.add(new Card("Queen" + " of " + s));
			fullDeck.add(new Card("King" + " of " + s));
		}
		fullDeck.add(new Card("Red Joker"));
		fullDeck.add(new Card("Black Joker"));
	}
	
	/**
	 * Returns the top card, if there is none the Deck is refilled and shuffled first
	 */
	public Card draw(){
		if(!activeDeck.isEmpty()){
			return activeDeck.pop();
		}
		else{
			fill();
			return activeDeck.pop();
		}
	}
	/**
	 * Puts the Deck's cards in a random order
	 */
	public void shuffle(){
		ArrayList<Card> locker = new ArrayList<>();
		for(Card c : activeDeck){
			locker.add(c);
		}
		int num = locker.size();
		activeDeck.clear();
		int pos = 0;
		for(int i = 0; i < num; i++){
			pos = rand.nextInt(locker.size());
			activeDeck.push(locker.get(pos));
			locker.remove(pos);
		}
	}
	
	/**
	 * Returns a unique (per deck) numerical value based on the card's type and suit
	 */
	public static double cardToValue(String card){
		Hashtable<String,Integer> values = new Hashtable<>();
		values.put("Two", 2);
		values.put("Three", 3);
		values.put("Four", 4);
		values.put("Five", 5);
		values.put("Six", 6);
		values.put("Seven", 7);
		values.put("Eight", 8);
		values.put("Nine",9);
		values.put("Ten", 10);
		values.put("Jack", 11);
		values.put("Queen", 12);
		values.put("King", 13);
		values.put("Ace", 14);
		values.put("Black Joker", 15);
		values.put("Red Joker", 16);
		Hashtable<String,Double> suits = new Hashtable<>();
		suits.put("Clubs", 0.2);
		suits.put("Diamonds", 0.4);
		suits.put("Hearts", 0.6);
		suits.put("Spades", 0.8);
		
		double total = 0;
		for(String s : values.keySet()){
			if(card.contains(s)){
				total += values.get(s);
			}
		}
		for(String s : suits.keySet()){
			if(card.contains(s)){
				total += suits.get(s);
			}
		}
		return total;
	}


}
