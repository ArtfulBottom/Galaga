/*
 * File: Beam.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import java.awt.Graphics;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Beam extends Sprite implements Serializable
{
	public Beam(int x, int y, int velY)
	{
		setX(x);
		setY(y);
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
		g.drawImage(Sprite.getBeamImage(), getX(), getY(), 20, 20, null);
	}
	
	@Override
	public boolean hasEscaped()
	{
		if (getY() + getSpan() < 0)
			return true;
		
		return false;
	}
}