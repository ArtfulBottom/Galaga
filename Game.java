/*
 * File: Game.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class Game extends JFrame implements ActionListener
{
	private Controller controller;
	private Model model;
	private Player player;
	private Timer timer;
	private JMenuBar menuBar;
	public enum State {
		Starting, Loading, Running, Paused, Death, Victory
	};
	private static State state;
	private static String phase = "";
	private boolean isStartup = true;
	
    private class MyPanel extends JPanel
    {
    	Image bg;
        Controller controller;

        MyPanel(Controller c) 
        {
            controller = c;
            addMouseListener(controller);
            
            try 
            {
                bg = ImageIO.read(new File("background.png"));
            } 
            catch (IOException ioe) 
            {
                System.out.println("Unable to load background image file.");
            }
        }

        @Override
        public void paintComponent(Graphics g)
        {
        	// Starting game screen.
        	if (getGameState() == State.Starting)
        	{
        		g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        		Font font = new Font("arial", Font.BOLD, 100);
	        	g.setFont(font);
	        	g.setColor(Color.white);
	        	g.drawString("SPACE", 350, 300);
	        	g.drawString("ADVENTURE", 200, 400);
	        	font = new Font("arial", Font.PLAIN, 30);
	        	
	        	g.setFont(font);
	        	g.drawString("Created by Luke S. Snyder", 335, 500);
        	}
        	else 
        	{
	        	g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
		        model.update(g);
		        player.updateImage(g);
		        
		        Font font = new Font("arial", Font.PLAIN, 15);
		        g.setFont(font);
		        g.setColor(Color.white);
		        g.drawString("Press [Esc] to Pause/Resume", 20, 30);
		     
		        font = new Font("arial", Font.BOLD, 20);
	        	g.setFont(font);
	        	g.drawString(phase, 20, 60);
	        	
	        	// Display number of enemies killed.
		        if (SpawningThread.getBossState() == SpawningThread.BossState.None)
		        {
		        	int enemiesKilled = Enemy.getEnemiesKilled() > SpawningThread.getTotalEnemies() ? 
		        					    SpawningThread.getTotalEnemies() : Enemy.getEnemiesKilled();
		        	g.drawString(enemiesKilled + "/" + SpawningThread.getTotalEnemies(), 20, 90);
		        }
		        // Victory game screen.
	        	if (getGameState() == State.Victory)
	        	{
		        	font = new Font("arial", Font.BOLD, 100);
		        	g.setFont(font);
		        	g.drawString("VICTORY", 300, 400);
		        	menuBar.getMenu(0).getItem(0).setEnabled(false);
		        	menuBar.getMenu(0).getItem(3).setEnabled(false);
	        	}
	        	// Death game screen.
	        	else if (getGameState() == State.Death)
	        	{
	        		font = new Font("arial", Font.BOLD, 100);
	            	g.setFont(font);
	            	g.drawString("GAME OVER", 200, 400);
	            	menuBar.getMenu(0).getItem(0).setEnabled(false);
	            	menuBar.getMenu(0).getItem(3).setEnabled(false);
	        	}
        	}
        	revalidate();
        }
    }

    public Game() 
    {
    	// Start game and add menu bar.
    	setGameState(State.Starting);
    	setJMenuBar(menuBar());
    	for (int i = 0; i < 4; ++i)
			menuBar.getMenu(0).getItem(i).setEnabled(false);
  
    	player = new Player(150);
    	controller = new Controller(player, this);
    	model = new Model(player, this);
    	
        setSize(1100, 800);
        getContentPane().add(new MyPanel(controller));
        addKeyListener(controller);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        
        // Initialize sprite images and start timer.
        Sprite.initialize(getHeight());
        timer = new Timer(5, this);
        timer.start();
        model.start();
    }
    
    // Initialize menu bar.
    public JMenuBar menuBar()
	{
		menuBar = new JMenuBar();
		menuBar.add(getOptionMenu());
		menuBar.add(getInstructionMenu());
		return menuBar;
	}
	
	public JMenu getOptionMenu()
	{
		JMenu optionMenu = new JMenu("Options");
		optionMenu.setMnemonic(KeyEvent.VK_O);
	    
	    JMenuItem item = new JMenuItem("Pause/Resume");
		item.setMnemonic(KeyEvent.VK_P);
	    item.addActionListener(this);
	    optionMenu.add(item);
	    
	    item = new JMenuItem("Restart Game");
		item.setMnemonic(KeyEvent.VK_R);
	    item.addActionListener(this);
	    optionMenu.add(item);
	    
	    item = new JMenuItem("Load Game");
	    item.setMnemonic(KeyEvent.VK_L);
	    item.addActionListener(this);
	    optionMenu.add(item);
	    
	    item = new JMenuItem("Save Game");
	    item.setMnemonic(KeyEvent.VK_S);
	    item.addActionListener(this);
	    optionMenu.add(item);
	    
	    optionMenu.addSeparator();
	    item = new JMenuItem("Exit Game");
	    item.setMnemonic(KeyEvent.VK_E);
	    item.addActionListener(this);
	    optionMenu.add(item);
	    
		return optionMenu;
	}
	
	public JMenu getInstructionMenu()
	{
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
	    
	    JMenuItem item = new JMenuItem("Instructions");
		item.setMnemonic(KeyEvent.VK_I);
	    item.addActionListener(this);
	    helpMenu.add(item);
	    
		return helpMenu;
	}
    
    public void addBeam()
    {
    	if (SpawningThread.getBossState() != SpawningThread.BossState.Loading &&
    		getGameState() == State.Running)
    		model.addBeam(player.getX() + 30, player.getY() - 20, -12);
    }
    
    public void death()
    {
    	// To curb against both the boss and player dying
    	// simultaneously, if victory occurred first, don't
    	// claim defeat.
    	if (getGameState() != State.Victory)
    	{
	    	setGameState(State.Death);
	    	Sprite.setPlayerImage("explode.png");
	    	repaint();
    	}
    }
    
    public void win()
    {
    	// To curb against both the boss and player dying
    	// simultaneously, if death occurred first, don't
    	// claim victory.
    	if (getGameState() != State.Death)
    	{
	    	setGameState(State.Victory);
	    	Boss boss = model.getBoss();
	    	Sprite.setBossImage("explode.png");
	    	boss.setVelocityX(0);
	    	boss.setVelocityY(0);
	    	repaint();
    	}
    }

    public void actionPerformed(ActionEvent evt) 
    {
    	if (getGameState() == State.Running)
    	{
    		// Update player movement.
    		player.tick(this.getWidth() - 80);
        	repaint();
    	}
    	if (evt.getActionCommand() != null)
    	{
    		// Pause or resume game.
    		if ((getGameState() == State.Running || getGameState() == State.Paused) &&
    			evt.getActionCommand().equals("Pause/Resume"))
    		{
    			if (getGameState() == State.Running)
    				setGameState(State.Paused);
    			else 
    				setGameState(State.Running);
    		}
    		// Exit game.
    		else if (evt.getActionCommand().equals("Exit Game"))
    			System.exit(0);	
    		// Show instructions.
    		else if (evt.getActionCommand().equals("Instructions"))
    		{
    			if (getGameState() == State.Running)
    			{
    				setGameState(State.Paused);
    				displayInstructions();
    				setGameState(State.Running);
    			}
    			else
    				displayInstructions();
    		}
    		// Restart game.
    		else if (evt.getActionCommand().equals("Restart Game"))
    		{
    			setGameState(State.Starting);
    			for (int i = 0; i < 4; ++i)
    				menuBar.getMenu(0).getItem(i).setEnabled(false);
    			repaint();
    			model.end();
    			setIsStartup(true);
    			SpawningThread.setPhaseLocation("Phase One Beginning");
    			model.start();
    		}
    		else if (evt.getActionCommand().equals("Load Game"))
    			loadGame();
    		else if (evt.getActionCommand().equals("Save Game"))
    			saveGame();
    	}
    }
    
    public void loadGame()
    {
    	boolean didSucceed = false;
		State initialState = getGameState();
		if (getGameState() == State.Running)
			setGameState(State.Paused);
			
		// Get file name.
		String name = JOptionPane.showInputDialog("Please enter the name of the saved game "
				+ "you would\nlike to load (please do not include a file extension):");
		while (name != null && name.equals(""))
			name = JOptionPane.showInputDialog("Please enter a non-empty name:");
		if (name != null)
		{
			if (initialState == State.Victory || initialState == State.Death)
				setGameState(State.Paused);
			if (model.readGameFromFile(name))
			{
				// End current threads, copy loaded game sprites, and restart.
				didSucceed = true;
				setGameState(State.Loading);
				model.end();
				setGameState(State.Paused);
				model.copy();
				JOptionPane.showMessageDialog(null, "Game successfully loaded.");
				
				// Enable menu options in case they were disabled from a previous death.
    			menuBar.getMenu(0).getItem(0).setEnabled(true);
    			menuBar.getMenu(0).getItem(3).setEnabled(true);
				repaint();
				model.start();
			}
		}
		// Restore game state if game load was unsuccessful.
		if (initialState == State.Running && !didSucceed)
			setGameState(State.Running);
		else if (initialState == State.Death && !didSucceed)
			setGameState(State.Death);
		else if (initialState == State.Victory && !didSucceed)
			setGameState(State.Victory);
    }
    
    public void saveGame()
    {
    	State initialState = getGameState();
		if (getGameState() == State.Running)
			setGameState(State.Paused);
		
		// Get name of saved game.
		String name = JOptionPane.showInputDialog("What would you like to name the saved game"
				+ "\n(please do not include a file extension)?");
		while (name != null && name.equals(""))
			name = JOptionPane.showInputDialog("Please enter a non-empty name:");
		if (name != null)
			if (model.writeGameToFile(name))
				JOptionPane.showMessageDialog(null, "Game successfully saved.");
		
		if (initialState == State.Running)
			setGameState(State.Running);
    }
    
    public void displayInstructions()
    {
    	String instructions = "The goal of Space Adventure is to destroy a certain number of enemy vessels in\n"
    						+ "each of the first three phases. The required number is displayed in the top\n"
    						+ "left corner of the screen. In the final phase, the boss ship must be destroyed\n"
    						+ "to claim your victory.\n\nYour health, which is shown in the top right corner "
    						+ "of the screen, is reduced any time\nan enemy vessel or enemy projectile "
    						+ "comes in contact with your ship. On your ship's\ntenth collision, your health "
    						+ "will be reduced to zero, and your ship will explode.\n\nControls:\n1. Ship "
    						+ "movement: Keys A/D or left arrow/right arrow to move left or right, respectively.\n"
    						+ "2. Projectiles: Spacebar or left mouse click to fire a beam.\n\n"
    						+ "NOTE: When a game is loaded, it will automatically be in a paused state.\n"
    						+ "Thus, you must press the resume button to continue gameplay.";
    	JOptionPane.showMessageDialog(null, instructions);
    }
    
    // Getters and setters.
    public static void setPhase(String phase)
    {
    	if (getGameState() == State.Running || getGameState() == State.Paused)
    		Game.phase = phase;
    }
    
    public static String getPhase()
    {
    	return phase;
    }
    
    
    static synchronized void setGameState(State state)
    {
    	Game.state = state;
    }
    
    static synchronized State getGameState()
    {
    	return state;
    }

    public synchronized boolean getIsStartup()
    {
    	return isStartup;
    }
    
    public synchronized void setIsStartup(boolean isStartup)
    {
    	this.isStartup = isStartup;
    }
    
    public JMenuBar getJMenuBar()
    {
    	return menuBar;
    }
    
    // Main.
    public static void main(String[] args) throws Exception 
    {
    	new Game();
    }
}