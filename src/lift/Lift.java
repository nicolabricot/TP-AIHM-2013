/**
 * @author Nicolas
 */

package lift;

import gui.Application;

import java.awt.Dimension;
import java.util.TreeSet;

public class Lift {
	
	private Application appli;
	private LiftView view;
	protected static Dimension CAGE = new Dimension(80, FLOOR() > 4 ? 70 : 100);
	
	private enum Sens {UP, DOWN, STOPPED};
	private Sens sens = Sens.STOPPED;
	
	private int position = 0;
	private TreeSet<Integer> destinations = new TreeSet<Integer>();
	private final int floorHeight = CAGE.height;
	private final int MAX = FLOOR() * CAGE.height - CAGE.height;
	
	private int pause = 0;
	
	/**
	 * Constructor
	 * @param appli
	 */
	public Lift(Application appli) {
		this.appli = appli;
		this.view = new LiftView(this.appli.getLiftPanelButton());
	}
	
	/**
	 * Get the lift panel from the view
	 * @return JPanel lift
	 */
	public LiftView getLiftPanel() {
		return this.view;
	}
	
	/**
	 * Register a new destination
	 * @param floor
	 */
	public void goFloor(int floor) {
		this.destinations.add(floor);
	}
	
	/**
	 * Control to the lift view
	 */
	public void update() {
		// we are in a floor
		if (this.position % this.floorHeight == 0) {
			int floor = this.position / this.floorHeight;
			// it's a destination
			if (this.destinations.contains(floor)) {
				this.destinations.remove(floor);
				this.appli.updateButton(floor);
				this.pause = 30;
			}
			this.appli.updateFloorText(floor);
		}
		
		// to avoid any out of range (normally not necessary, but i prefer to be careful)
		if (this.position >= this.MAX) {
			this.sens = Sens.STOPPED;
			this.destinations.remove(FLOOR()-1);
			this.appli.updateButton(FLOOR()-1);
		}
		else if (this.position <= 0) {
			this.sens = Sens.STOPPED;
			this.destinations.remove(0);
			this.appli.updateButton(0);
		}
		
		// what should we do?
		if (this.pause > 0) {
			// fake open/close door
			this.pause--;
		}
		else {
			// we can move
			if (! this.destinations.isEmpty())
				this.choseSens();
			else
				this.sens = Sens.STOPPED;
		}
	}
	
	/**
	 * Moving up please
	 */
	private void up() {
		this.sens = Sens.UP;
		this.view.changePosition(++this.position);
	}
	
	/**
	 * Moving down please
	 */
	private void down() {
		this.sens = Sens.DOWN;
		this.view.changePosition(--this.position);
	}
	
	/**
	 * Set the sens to move the lift
	 */
	private void choseSens() {
		int floor = this.position / this.floorHeight;
		switch (this.sens) {
		case UP:
			if (floor > this.destinations.last())
				this.down();
			else
				this.up();
			break;
		case DOWN:
			if (floor < this.destinations.first())
				this.up();
			else
				this.down();
			break;
		default:
			if (floor > this.destinations.last())
				this.down();
			else
				this.up();
		}

	}
	
	/**
	 * Return the width from the application
	 * @return WIDTH
	 */
	public static int WIDTH() {
		return Application.WIDTH/2 - 60;
	}
	/**
	 * Return the height from the application
	 * @return HEIGHT
	 */
	public static int HEIGHT() {
		return Application.HEIGHT - 40; 
	}
	/**
	 * Return the number of floor from the application
	 * @return FLOOR
	 */
	public static int FLOOR() {
		return Application.FLOOR;
	}

}
