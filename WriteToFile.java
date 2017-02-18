/*
 * File: WriteToFile.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class WriteToFile 
{
	String fileName;
	private ArrayList<Beam> beams;
	private ArrayList<Missle> missles;
	private ArrayList<Enemy> enemies;
	private Boss boss;
	private Player player;
	FileOutputStream fos;
	ObjectOutputStream oos;
	
	public WriteToFile(String fileName, ArrayList<Beam> beams, ArrayList<Missle> missles,  
					   ArrayList<Enemy> enemies, Boss boss, Player player)
	{
		this.fileName = fileName;
		this.beams = beams;
		this.missles = missles;
		this.enemies = enemies;
		this.boss = boss;
		this.player = player;
		createFileStreams();
	}
	
	// Try making new file streams.
	public void createFileStreams()
	{
		try 
		{
			fos = new FileOutputStream(fileName + ".ser");
			oos = new ObjectOutputStream(fos);
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "ERROR: Could not create save file.");
		}
	}
	
	// Save game to file.
	public boolean start()
	{
		if (fos == null || oos == null)
			return false;
		
		try 
		{
			// Write all relevant data to file.
			oos.writeInt(Enemy.getEnemiesKilled());
			oos.writeInt(SpawningThread.getTotalEnemies());
			oos.writeObject(Game.getGameState());
			oos.writeObject(SpawningThread.getBossState());
			oos.writeObject(SpawningThread.getPhaseLocation());
			oos.writeObject(Game.getPhase());
			
			for (Beam beam : beams)
				oos.writeObject((Sprite) beam);
			for (Missle missle : missles)
				oos.writeObject((Sprite) missle);
			for (Enemy enemy : enemies)
				oos.writeObject((Sprite) enemy);
			
			oos.writeObject((Sprite) boss);
			oos.writeObject((Sprite) player);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "ERROR: Exception thrown while writing to file.");
			close();
			return false;
		}
		close();
		return true;
	}
	
	public void close()
	{
		try 
		{
			fos.close();
			oos.close();
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "ERROR: Could not close file stream.");
		}
	}
}