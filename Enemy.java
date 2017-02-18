/*
 * File: Enemy.java
 * Authors: Luke S. Snyder
 * UA ID: 010691128
 */

import java.awt.Graphics;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Enemy extends Sprite implements Serializable
{
	private static int enemiesKilled;
	
	public Enemy(int x, int y, int velY)
	{
		setX(x);
		setY(y);
		setSpan(65);
		setVelocityY(velY);
	}
	
	@Override
	public void updateImage(Graphics g) 
	{
		g.drawImage(Sprite.getEnemyImage(), getX(), getY(), 80, 80, null);
	}
	
	@Override
	public void tick()
	{
		setY(getY() + getVelocityY());
	}
	
	static synchronized void setEnemiesKilled(int enemiesKilled)
	{
		Enemy.enemiesKilled = enemiesKilled;
	}
	
	static synchronized int getEnemiesKilled()
	{
		return enemiesKilled;
	}
}