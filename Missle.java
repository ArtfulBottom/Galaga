/*
 * File: Missle.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import java.awt.Graphics;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Missle extends Sprite implements Serializable
{	
	public Missle(int x, int y, int velY)
	{
		setX(x);
		setY(y);
		setSpan(25);
		setVelocityY(velY);
	}
	
	@Override
	public void tick()
	{
		setY(getY() + getVelocityY());
	}
	
	@Override
	public void updateImage(Graphics g) 
	{
		g.drawImage(Sprite.getMissleImage(), getX(), getY(), 40, 40, null);
	}
}
