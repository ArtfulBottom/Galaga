/*
 * File: Model.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

public class Model
{
	private ReadFromFile rff;
	private ArrayList<Beam> beams;
	private ArrayList<Missle> missles;
	private ArrayList<Enemy> enemies;
	private Boss boss;
	private Player player;
	private Game game;
	private Thread spawningThread;
	private Thread projectilesThread;
	private boolean loadBoss;
	
	public Model(Player player, Game game)
	{
		this.player = player;
		this.game = game;
		beams = new ArrayList<Beam>();
		enemies = new ArrayList<Enemy>();
		missles = new ArrayList<Missle>();
	}
	
	// Load game from file.
	public boolean readGameFromFile(String fileName)
	{
		rff = new ReadFromFile(fileName);
		return rff.start();
	}
	
	// Copy file data that was read from file.
	public void copy()
	{
		boss = rff.copy(beams, missles, enemies, player);
		loadBoss = true;
	}
	
	// Save game to file.
	public boolean writeGameToFile(String fileName)
	{
		return new WriteToFile(fileName, beams, missles, enemies, boss, player).start();
	}
	
	// Start threads.
	public void start()
	{
		projectilesThread = new Thread(new ProjectilesThread(game, this));
		
		// If a game is loaded, then use the boss data from that file.
		if (loadBoss)
		{
			spawningThread = new Thread(new SpawningThread(game, this, missles, enemies, boss));
			loadBoss = false;
		}
		else
			spawningThread = new Thread(new SpawningThread(game, this, missles, enemies, null));
		
		projectilesThread.start();
		spawningThread.start();
	}
	
	// End threads and clear all sprite data.
	public void end()
	{
		try 
		{
			projectilesThread.join();
			spawningThread.join();
			
			Iterator<Beam> iterBeam = beams.iterator();
			while (iterBeam.hasNext())
			{
				iterBeam.next();
				iterBeam.remove();
			}
				
			Iterator<Enemy> iterEnemies = enemies.iterator();
			while (iterEnemies.hasNext())
			{
				iterEnemies.next(); 
				iterEnemies.remove();
			}
					
			Iterator<Missle> iterMissles = missles.iterator();
			while (iterMissles.hasNext())
			{
				iterMissles.next(); 
				iterMissles.remove();	
			}
			
			boss = null;
			Sprite.setBossImage("boss.png");
			player.reset();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	// Add beam.
	public void addBeam(int x, int y, int velY)
    {
    	synchronized(beams)
    	{
    		// Successive beams must have a minimum distance between them.
    		if (beams.isEmpty())
    			beams.add(new Beam(x, y, velY));
    		else if (y > beams.get(beams.size() - 1).getBottomSide() + 50)
    			beams.add(new Beam(x, y, velY));
    	}
    }
	
	// Add missle.
	public void addMissle(int x, int y, int velY)
	{
		synchronized(missles)
		{
			missles.add(new Missle(x, y, velY));
		}
	}
	
	// Add enemy.
	public void addEnemy(int x, int y, int velY)
	{
		synchronized(enemies)
		{
			enemies.add(new Enemy(x, y, velY));
		}
	}
	
	// Update sprites.
	public void update(Graphics g)
	{
		synchronized(beams)
		{
			for (Beam beam : beams)
				beam.updateImage(g);
		}
		
		synchronized(enemies)
		{
			for (Enemy enemy : enemies)
				enemy.updateImage(g);
		}
		
		synchronized(missles)
		{
			for (Missle missle : missles)
				missle.updateImage(g);
		}
		
		if (boss != null)
			boss.updateImage(g);
	}
	
	// Detect collisions.
	public void detectCollisions()
	{
		// Check if any missles collide with the player.
		synchronized(missles)
		{
			for (Missle missle : missles)
			{
				if (missle.overlaps(player))
				{
					missle.setHasCollided(true);
					player.setHealth(player.getHealth() - 15);
					if (player.getHealth() == 0)
						game.death();
				}
			}
		}
		
		// Check if any beams collide with either enemies or the boss.
		synchronized(beams)
		{
			synchronized(enemies)
			{
				for (Beam beam : beams)
				{
					Iterator<Enemy> iter = enemies.iterator();
					while (iter.hasNext())
					{
						if (beam.overlaps(iter.next()))
						{
							iter.remove();
							Enemy.setEnemiesKilled(Enemy.getEnemiesKilled() + 1);
							beam.setHasCollided(true);
						}
					}
					if (boss != null && beam.overlaps(boss) &&
						SpawningThread.getBossState() != SpawningThread.BossState.Loading)
					{
						beam.setHasCollided(true);
					
						if (SpawningThread.getBossState() == SpawningThread.BossState.Easy ||
							SpawningThread.getBossState() == SpawningThread.BossState.Medium)
							boss.setHealth(boss.getHealth() - 5);
						else if (SpawningThread.getBossState() == SpawningThread.BossState.Hard)
							boss.setHealth(boss.getHealth() - 10);
					}
				}
			}
		}
	}
	
	// Move missles and beams.
	public void moveProjectiles()
	{
		if (Game.getGameState() == Game.State.Running)
		{
			synchronized(beams)
			{
				Iterator<Beam> iter = beams.iterator();
				while (iter.hasNext())
				{
					Beam beam = iter.next();
					if (!beam.hasEscaped() && !beam.hasCollided())
						beam.tick();
					else
						iter.remove();
				}
			}
			
			synchronized(missles)
			{
				Iterator<Missle> iter = missles.iterator();
				while (iter.hasNext())
				{
					Missle missle = iter.next();
					if (!missle.hasEscaped() && !missle.hasCollided())
						missle.tick();
					else
						iter.remove();
				}
			}
			detectCollisions();
		}
	}
	
	// Move enemies.
	public void moveEnemies()
	{
		synchronized(enemies)
		{ 
			Iterator<Enemy> iter = enemies.iterator();
			while (iter.hasNext())
			{
				Enemy enemy = iter.next();
				if(enemy.overlaps(player) && !enemy.hasCollided())
				{
					player.setHealth(player.getHealth() - 15);
					enemy.setHasCollided(true);
					if (player.getHealth() == 0)
						game.death();
				}
				if (!enemy.hasEscaped())
					enemy.tick();
				else
					iter.remove();
			}
		}
	}
	
	// Move boss.
	public void moveBoss(int distance)
	{
		synchronized(boss)
		{
			if (boss.getX() < distance)
			{
				while (boss.getX() < distance)
				{
					if (Game.getGameState() == Game.State.Running)
					{
						boss.shift();
						sleep(10);
					}
					else if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
						return;
				}
			}
			else
			{
				while (boss.getX() > distance)
				{
					if (Game.getGameState() == Game.State.Running)
					{
						boss.shift();
						sleep(10);
					}
					else if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
						return;
				}
			}
		}
	}
	
	// Reset boss position to middle of top screen.
	public void resetBossPosition()
	{
		if (boss.getX() < 430)
			boss.setVelocityX(1);
		else 
			boss.setVelocityX(-1);
		while (boss.getX() != 430)
		{	
			if (Game.getGameState() == Game.State.Running)
			{
				boss.shift();
				sleep(10);
			}
			else if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
		}
	}

	// Clear enemies off screen to advance to next phase.
	public void waitForEnemiesToClear()
	{
		while(!isEmpty())
		{
			if (Game.getGameState() == Game.State.Running)
			{
				moveEnemies();
				sleep(10);
			}
			else if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
		}
	}
	
	public boolean isEmpty()
	{
		synchronized(enemies)
		{
			return enemies.isEmpty();
		}
	}
	
	public void setBoss(Boss boss) 
	{
		this.boss = boss;
	}
	
	public Boss getBoss()
	{
		return boss;
	}
	
	// Thread sleep function.
	public void sleep(int time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
