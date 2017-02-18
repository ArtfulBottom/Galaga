/*
 * File: Sprite.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

@SuppressWarnings("serial")
class Sprite implements Serializable
{
	private int locationX;
	private int locationY;
	private int velocityX;
	private int velocityY;
	private int span;
	private boolean hasCollided;
	private static int height;
	private static Image beamImage;
	private static Image missleImage;
	private static Image enemyImage;
	private static Image bossImage;
	private static Image playerImage;
	
	// Read in all images.
	public static void initialize(int height)
	{
		Sprite.height = height;
		try
		{
			beamImage   = ImageIO.read(new File("beam.png"));
			missleImage = ImageIO.read(new File("missle.png"));
			enemyImage  = ImageIO.read(new File("hamster.png"));
			bossImage   = ImageIO.read(new File("evilHamster.png"));
			playerImage = ImageIO.read(new File("spaceship.png"));
		}
		catch(IOException e)
		{
			System.out.println("Unable to load image file.");
		}
	}
	
	// Image getters and setters.
	public static Image getBeamImage() { return beamImage; }
	public static Image getMissleImage() { return missleImage; }
	public static Image getEnemyImage() { return enemyImage; }
	public static Image getBossImage() { return bossImage; }
	public static Image getPlayerImage() { return playerImage; }
	
	public static void setBossImage(String filePath) 
	{
		try
		{
			bossImage   = ImageIO.read(new File(filePath));
		}
		catch(IOException e)
		{
			System.out.println("Unable to load image file.");
		}
	}
	public static void setPlayerImage(String filePath)
	{
		try
		{
			playerImage   = ImageIO.read(new File(filePath));
		}
		catch(IOException e)
		{
			System.out.println("Unable to load image file.");
		}
	}
	
	public int getX() {	return locationX; }
	public int getY() {	return locationY; }
	public void setX(int x) { locationX = x; }
	public void setY(int y) { locationY = y; }
	
	public void updateImage(Graphics g) { }
	public void tick() { }
	
	// Determine if invoking sprite overlaps another sprite.
	public boolean overlaps(Sprite s)
	{
		if (getRightSide() < s.getLeftSide())
			return false;
		if (getLeftSide() > s.getRightSide())
			return false;
		if (getTopSide() > s.getBottomSide())
			return false;
		if (getBottomSide() < s.getTopSide())
			return false;
		return true;
	}
	
	// Determine if sprite has escaped.
	public boolean hasEscaped() 
	{ 
		if (getY() > height)
			return true;
		return false; 
	}
	
	// Getters and setters.
	public void setHasCollided(boolean hasCollided)
	{
		this.hasCollided = hasCollided;
	}
	
	public boolean hasCollided()
	{
		return hasCollided;
	}
	
	public void setSpan(int span)
	{
		this.span = span;
	}
	
	public int getSpan()
	{
		return span;
	}
	
	public int getTopSide()
	{
		return getY();
	}
	
	public int getBottomSide()
	{
		return getY() + getSpan();
	}
	
	public int getLeftSide()
	{
		return getX();
	}
	
	public int getRightSide()
	{
		return getX() + getSpan();
	}

	public int getVelocityX() 
	{
		return velocityX;
	}

	public void setVelocityX(int velocityX) 
	{
		this.velocityX = velocityX;
	}

	public int getVelocityY() 
	{
		return velocityY;
	}

	public void setVelocityY(int velocityY) 
	{
		this.velocityY = velocityY;
	}
}