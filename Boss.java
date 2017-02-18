/*
 * File: Boss.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Boss extends Sprite implements Serializable
{
	private int health;
	
	public Boss(int x, int y, int velY, int health)
	{
		setX(x);
		setY(y);
		setSpan(185);
		setVelocityY(velY);
		this.health = health;
	}
	
	@Override
	public void updateImage(Graphics g) 
	{
		g.drawImage(Enemy.getBossImage(), getX(), getY(), 200, 200, null);
		Font font = new Font("arial", Font.BOLD, 15);
    	g.setFont(font);
    	g.setColor(Color.white);
    	g.drawString("Boss Health:", 900, 90);
    	g.drawRect(900, 100, 150, 25);
    	g.setColor(Color.red);
    	g.fillRect(900, 100, getHealth(), 25);
	}
	
	@Override
	public void tick()
	{
		setY(getY() + getVelocityY());
	}
	
	public void shift()
	{
		setX(getX() + getVelocityX());
	}
	
	public void setHealth(int health)
	{
		this.health = health < 0 ? 0 : health;
	}
	
	public int getHealth()
	{
		return health;
	}
}
