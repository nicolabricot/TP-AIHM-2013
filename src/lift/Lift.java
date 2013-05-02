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
	
	private enum Sens {UP, DOWN, STOP, PAUSE};
	private Sens sens = Sens.STOP;
	private enum Door {CLOSE, OPEN, OPENING, CLOSING};
	private Door state = Door.CLOSE;
	
	private int position = 0;
	private int door = 0;
	private TreeSet<Integer> destinations = new TreeSet<Integer>();
	private final int floorHeight = CAGE.height;
	
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
				this.sens = Sens.PAUSE;
				this.state = Door.OPENING;
			}
			this.appli.updateFloorText(floor);
		}
		
		// what should we do?
		if (this.sens == Sens.PAUSE) {
			this.doorMotion();
		}
		else {
			// we can move
			if (! this.destinations.isEmpty())
				this.choseSens();
			else
				this.sens = Sens.STOP;
		}
	}
	
	/**
	 * Manage the opening/closing motion for the doors
	 */
	@SuppressWarnings("incomplete-switch")
	private void doorMotion() {
		switch (this.state) {
        case OPENING:
            if (this.door < CAGE.width / 2)
                this.view.changeDoor(++this.door);
            else {
                this.state = Door.CLOSING;
                this.view.changeDoor(--this.door);
            }
            break;
        case CLOSING:
            if (this.door > 0)
                this.view.changeDoor(--this.door);
            else {
                this.state = Door.CLOSE;
                this.sens = Sens.STOP;
            }
            break;
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
