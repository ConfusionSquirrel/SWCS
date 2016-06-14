package scott.emmett;

import java.awt.Point;

/**
 * A square on a grid of the simulation's map, connected to a visual tile
 */
public class Block {

	private boolean occupied;
	private boolean open;
	private Point loc;
	private Robot occupant;
	
	/**
	 * Constructs a blank Block
	 */
	public Block(){
	}


	/**
	 * Returns true if this block is available for occupation
	 */
	public boolean isFree(){
		if(open && !occupied) return true;
		else return false;
	}
	/**
	 * Returns true if there is a bot in this space
	 */
	public boolean isOccupied(){
		return occupied;
	}
	/**
	 * Returns true if this space is not a wall
	 */
	public boolean isOpen(){
		return open;
	}
	/**
	 * Returns true if both Blocks share a location
	 */
	public boolean equals(Block b){
		if(b.getLoc() == loc) return true;
		else return false;
	}
	/**
	 * Returns the location of this Block
	 */
	public Point getLoc(){
		return loc;
	}
	/**
	 * Returns the Robot in this space, null if there is none
	 */
	public Robot getOccupant(){
		return occupant;
	}
	
	/**
	 * Sets the location of this Block to the given Point
	 */
	public void setLoc(Point p){
		loc = p;
	}
	/**
	 * Sets whether or not there is a Robot in this space
	 */
	public void setOccupied(boolean b){
		occupied = b;
	}
	/**
	 * Sets that there is a Robot in this space, and provides that Robot
	 */
	public void setOccupied(Robot r){
		occupied = true;
		occupant = r;
	}
	/**
	 * Sets the Robot in this space, null if none
	 */
	public void setOccupant(Robot r){
		occupant = r;
	}
	/**
	 * Sets whether or not this space is a wall (false) or not (true)
	 */
	public void setOpen(boolean b){
		open = b;
	}

	
	
}
