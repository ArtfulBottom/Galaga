/*
 * File: Player.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Player extends Sprite implements Serializable
{	
	private int health;
	
	// Constructor.
	public Player(int health)
	{
		// Initialize superclass.
		setX(500);
		setY(675);
		setSpan(65);
		this.health = health;
	}
	
	public void tick(int width)
	{
		if ((getX() > 0 && getX() < width) ||
			(getX() <= 0 && getVelocityX() > 0) ||
			(getX() >= width && getVelocityX() < 0))
			setX(getX() + getVelocityX());
	}
	
	@Override
	public void updateImage(Graphics g) 
	{
		g.drawImage(Enemy.getPlayerImage(), getX(), getY(), 80, 80, null);
		Font font = new Font("arial", Font.BOLD, 15);
    	g.setFont(font);
    	g.setColor(Color.white);
    	g.drawString("Health:", 900, 30);
    	g.drawRect(900, 40, 150, 25);
    	g.setColor(Color.green);
    	g.fillRect(900, 40, getHealth(), 25);
	}
	
	public void setHealth(int health)
	{
		this.health = health < 0 ? 0 : health;
	}
	
	public int getHealth()
	{
		return health;
	}
	
	// Reset player state.
	public void reset()
	{
		Sprite.setPlayerImage("spaceship.png");
		health = 150;
		setX(500);
		setY(675);
	}
}