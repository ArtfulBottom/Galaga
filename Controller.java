/*
 * File: Controller.java
 * Authors: Luke S. Snyder
 * UA ID: 010691128
 */

import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

class Controller implements MouseListener, KeyListener
{
	private Player player;
	private Game game;

	public Controller(Player player, Game game)
	{
		this.player = player;
		this.game = game;
	}
	
	public void mousePressed(MouseEvent e) 
	{ 
		if (SwingUtilities.isLeftMouseButton(e))
			game.addBeam();
	}
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    
    // Handle certain typed keys.
	public void keyTyped(KeyEvent e) { }
	public void keyPressed(KeyEvent e) 
	{ 
		// Right arrow or 'D'.
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyChar() == 'd') 
			player.setVelocityX(5);
		// Left arrow or 'A.
		else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyChar() == 'a')
			player.setVelocityX(-5);
		
		// Add beam with space bar.
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			game.addBeam();
		
		// Pause if escape key is pressed.
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			game.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Pause/Resume"));
	}
	
	public void keyReleased(KeyEvent e) 
	{ 
		// Right arrow or 'D'.
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyChar() == 'd' ||
			e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyChar() == 'a') 
			player.setVelocityX(0);
	}	
}