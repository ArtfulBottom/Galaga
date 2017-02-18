/*
 * File: ReadFromFile.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class ReadFromFile 
{
	String fileName;
	private ArrayList<Beam> beams;
	private ArrayList<Missle> missles;
	private ArrayList<Enemy> enemies;
	private Boss boss;
	private Player player;
	private int enemiesKilled;
	private int totalEnemies;
	private Game.State gameState;
	private SpawningThread.BossState bossState;
	private String phaseLocation;
	private String phase;
	FileInputStream fis;
	ObjectInputStream ois;
	
	public ReadFromFile(String fileName)
	{
		this.fileName = fileName;
		beams = new ArrayList<Beam>();
		missles = new ArrayList<Missle>();
		enemies = new ArrayList<Enemy>();
		createFileStreams();
	}
	
	// Try making new file streams.
	public void createFileStreams()
	{
		try 
		{
			fis = new FileInputStream(fileName + ".ser");
			ois = new ObjectInputStream(fis);
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "ERROR: Could not open save file.");
		}
	}
	
	// Start file reading.
	public boolean start()
	{
		if (fis == null || ois == null)
			return false;
		
		try 
		{
			enemiesKilled = ois.readInt();
			totalEnemies = ois.readInt();
			gameState = (Game.State) ois.readObject();
			bossState = (SpawningThread.BossState) ois.readObject();
			phaseLocation = (String) ois.readObject();
			phase = (String) ois.readObject();
			
			// While bytes remain in the file, read in sprites.
			while (fis.available() > 0)
			{
				// Read in a sprite and determine its type.
				Sprite sprite = (Sprite) ois.readObject();
				if (sprite instanceof Beam)
					beams.add((Beam) sprite);
				else if (sprite instanceof Missle)
					missles.add((Missle) sprite);
				else if (sprite instanceof Enemy)
					enemies.add((Enemy) sprite);
				else if (sprite instanceof Boss)
					boss = (Boss) sprite;
				else if (sprite instanceof Player)
					player = (Player) sprite;
			}
		}
		catch (IOException | ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, "ERROR: Exception thrown while reading from file.");
			close();
			return false;
		}
		close();
		return true;
	}
	
	// Close file.
	public void close()
	{
		try 
		{
			fis.close();
			ois.close();
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "ERROR: Could not close file stream.");
		}
	}
	
	// Copy loaded data over to variables.
	public Boss copy(ArrayList<Beam> beams, ArrayList<Missle> missles, ArrayList<Enemy> enemies, Player player)
	{
		Enemy.setEnemiesKilled(enemiesKilled);
		SpawningThread.setTotalEnemies(totalEnemies);
		Game.setGameState(gameState);
		SpawningThread.setBossState(bossState);
		SpawningThread.setPhaseLocation(phaseLocation);
		Game.setPhase(phase);
		
		for (Beam beam : this.beams)
			beams.add(beam);
		
		for (Missle missle : this.missles)
			missles.add(missle);
		
		for (Enemy enemy : this.enemies)
			enemies.add(enemy);
		
		player.setX(this.player.getX());
		player.setY(this.player.getY());
		player.setHealth(this.player.getHealth());
		
		if (this.boss != null)
		{
			int x = this.boss.getX();
			int y = this.boss.getY();
			int velY = this.boss.getVelocityY();
			int health = this.boss.getHealth();
			return new Boss(x, y, velY, health);
		}
		return null;
	}
}