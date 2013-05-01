/**
 * @author Nicolas
 */

package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import lift.Lift;

@SuppressWarnings("serial")
public class Application extends JFrame {
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final int FLOOR = 6; // authorized range : 1 to 6 
	private JButton button[][] = new JButton[3][FLOOR]; // 0: tool bar; 1: right panel; 2: lift panel
	private JTextField floorText;
	public static Lift LIFT;

	public Application() {
		this.setTitle("Simulateur d'ascenseur");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.createMenu();
		this.createButtons();
		LIFT = new Lift(this);
		this.createContent();
		
		Timer timer = new Timer(20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LIFT.update();
			}
		});
		timer.start();
	}
	
	/**
	 * Create the content components
	 */
	private void createContent() {
		// Toolbar
		JToolBar toolbar = new JToolBar();
		for (int i=0; i<FLOOR; i++)
			toolbar.add(this.button[0][i]);
		this.getContentPane().add(toolbar, BorderLayout.NORTH);
		// Spliter
		JSplitPane spliter = new JSplitPane();
		spliter.setDividerLocation(WIDTH/2);
			// Left side
			spliter.setLeftComponent(LIFT.getLiftPanel());
			// Right side
			this.createRightPanel(spliter);
		this.getContentPane().add(spliter, BorderLayout.CENTER);
	}
	
	/**
	 * Create the right panel
	 * @param spliter
	 */
	private void createRightPanel(JSplitPane spliter) {
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		
		JPanel rButtons = new JPanel();
		rButtons.setLayout(new BoxLayout(rButtons, BoxLayout.Y_AXIS));
		for (int i=FLOOR-1; i>=0; i--)
			rButtons.add(this.button[1][i]);
		
		JLabel label = new JLabel("Cabine");
		this.floorText = new JTextField();
		this.floorText.setEditable(false);
		this.floorText.setText("0");
		this.floorText.setColumns(3);
		this.floorText.setHorizontalAlignment(JTextField.CENTER);
		JPanel texts = new JPanel();
		texts.add(label);
		texts.add(Box.createGlue());
		texts.add(this.floorText);
		
		right.add(texts);
		right.add(Box.createVerticalGlue());
		right.add(rButtons);
		right.add(Box.createVerticalStrut(HEIGHT/6));
		right.add(Box.createVerticalGlue());
		spliter.setRightComponent(right);
	}

	/**
	 * Create buttons and models
	 */
	private void createButtons() {
		for (int i=0; i<FLOOR; i++) {
			ButtonModel m = new ButtonModel(i);
			JButton bar = createNumberButton(i, m);
			JButton panel = createNumberButton(i, m);
			JButton lift = createCallButton(i, m);
			this.button[0][i] = bar;
			this.button[1][i] = panel;
			this.button[2][i] = lift;
		}
	}
	
	/**
	 * Create a button with the floor number
	 * @param i
	 * @param m
	 * @return
	 */
	private JButton createNumberButton(int i, ButtonModel m) {
		JButton b = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("res/" + Integer.toString(i) + ".png")));
		b.setSelectedIcon(new ImageIcon(this.getClass().getClassLoader().getResource("res/" + Integer.toString(i) + ".selected.png")));
		b.setModel(m);
		b.addActionListener(new ButtonActionListener(i));
		return b;
	}
	
	/**
	 * Create a button for each floor (without number)
	 * @param i
	 * @param m
	 * @return
	 */
	private JButton createCallButton(int i, ButtonModel m) {
		JButton b = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("res/call.png")));
		b.setSelectedIcon(new ImageIcon(this.getClass().getClassLoader().getResource("res/call.selected.png")));
		b.setModel(m);
		b.addActionListener(new ButtonActionListener(i));
		return b;
	}
	
	/**
	 * Button action listener class
	 * @author Nicolas
	 */
	private class ButtonActionListener implements ActionListener {
		private final int floor;
		
		public ButtonActionListener(int floor) {
			this.floor = floor;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			LIFT.goFloor(this.floor);
		}
	}
	
	/**
	 * Button model implementation
	 * @author Nicolas
	 */
	private class ButtonModel extends DefaultButtonModel implements ActionListener {
		
		public ButtonModel(int number) {
			addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setSelected(!isSelected());
		}
	}
	
	/**
	 * Create the menu bar
	 */
	private void createMenu() {
		JMenuBar menu = new JMenuBar();
		
		JMenu file = new JMenu("Fichier");
		file.setMnemonic(KeyEvent.VK_F);
		menu.add(file);
		
		JMenuItem quit;
		quit = new JMenuItem("Quitter");
		quit.setMnemonic(KeyEvent.VK_Q);
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		file.add(quit);
		
		this.setJMenuBar(menu);
	}
	
	public void updateButton(int floor) {
		this.button[0][floor].setSelected(false);
	}
	
	public void updateFloorText(int floor) {
		this.floorText.setText(Integer.toString(floor));
	}
	
	public JButton[] getLiftPanelButton() {
		return this.button[2];
	}

	/**
	 * Lunch the application
	 * @param args
	 */
	public static void main(String[] args) {
			SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				Application window = new Application();
				window.pack();
				window.setResizable(false);
				window.setSize(Application.WIDTH, Application.HEIGHT);
				window.setVisible(true);
			}
			
		});
	}

}
