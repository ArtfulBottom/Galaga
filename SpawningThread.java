/*
 * File: SpawningThread.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

import java.util.ArrayList;
import java.util.Random;

public class SpawningThread implements Runnable
{
	private Game game;
	private Model model;
	private ArrayList<Missle> missles;
	private ArrayList<Enemy> enemies;
	private Random random;
	private Boss boss;
	public enum BossState {
		None, Loading, Easy, Medium, Hard
	};
	private static BossState bossState = BossState.None;
	private static int totalEnemies;
	private static String phaseLocation = "Phase One Beginning";
	
	public SpawningThread(Game game, Model model, ArrayList<Missle> missles, ArrayList<Enemy> enemies, Boss boss)
	{
		this.game = game;
		this.model = model;
		this.missles = missles;
		this.enemies = enemies;
		this.boss = boss;
		random = new Random();
	}
	
	public void run()
	{	
		phase1();	
		phase2();
		phase3();
		finalPhase();
	}
	
	public void phase1()
	{
		if (getPhaseLocation().equals("Phase One Beginning"))
		{
			// Initialize starting game data.
			sleep(4000);
			for (int i = 0; i < 4; ++i)
				game.getJMenuBar().getMenu(0).getItem(i).setEnabled(true);
			Game.setGameState(Game.State.Running);
			game.setIsStartup(false);
			Game.setPhase("Phase One");
			Enemy.setEnemiesKilled(0);
			setBossState(BossState.None);
			setTotalEnemies(30);
			sleep(2000);
			setPhaseLocation("Phase One");
		}
		if (getPhaseLocation().equals("Phase One"))
		{
			// Continually spawn and move enemies until a certain amount have been killed.
			while (Enemy.getEnemiesKilled() < getTotalEnemies())
			{
				if (Game.getGameState() == Game.State.Running)
				{
					spawnEnemies(6, 2, 4, 2, totalEnemies);
					model.moveEnemies();
					sleep(10);
				}
				else if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
					return;
			}
			setPhaseLocation("Phase Two Waiting");
		}
	}
	
	public void phase2()
	{
		if (getPhaseLocation().equals("Phase Two Waiting"))
		{
			model.waitForEnemiesToClear();
			setPhaseLocation("Phase Two Beginning");
		}
		
		if (getPhaseLocation().equals("Phase Two Beginning"))
		{
			// Initialize phase two data.
			if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
			Game.setPhase("Phase Two");
			Enemy.setEnemiesKilled(0);
			setTotalEnemies(40);
			sleep(2000);
			setPhaseLocation("Phase Two");
		}
		
		if (getPhaseLocation().equals("Phase Two"))
		{
			// Spawn enemies until a certain amount have been killed.
			while (Enemy.getEnemiesKilled() < getTotalEnemies())
			{	
				if (Game.getGameState() == Game.State.Running)
				{
					spawnEnemies(8, 3, 7, 1, getTotalEnemies());
					model.moveEnemies();
					sleep(10);
				}
				else if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
					return;
			}
			setPhaseLocation("Phase Three Waiting");
		}
	}
	
	public void phase3()
	{
		if (getPhaseLocation().equals("Phase Three Waiting"))
		{
			model.waitForEnemiesToClear();
			setPhaseLocation("Phase Three Beginning");
		}

		if (getPhaseLocation().equals("Phase Three Beginning"))
		{
			// Initialize phase three data.
			if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
			Game.setPhase("Phase Three");
			Enemy.setEnemiesKilled(0);
			setTotalEnemies(25);
			sleep(2000);
			setPhaseLocation("Phase Three");
		}
		
		if (getPhaseLocation().equals("Phase Three"))
		{
			// Spawn enemies until a certain amount have been killed.
			while (Enemy.getEnemiesKilled() < getTotalEnemies())
			{	
				if (Game.getGameState() == Game.State.Running)
				{
					spawnEnemies(10, 4, 10, 0, getTotalEnemies());
					model.moveEnemies();
					sleep(10);
				}
				else if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
					return;
			}
			setPhaseLocation("Final Phase Waiting");
		}
	}
	
	public void finalPhase()
	{
		if (getPhaseLocation().equals("Final Phase Waiting"))
		{
			model.waitForEnemiesToClear();
			setPhaseLocation("Final Phase Beginning");
		}
		
		if (getPhaseLocation().equals("Final Phase Beginning"))
		{
			// Initialize boss phase data.
			if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
			Game.setPhase("Boss Phase");
			setBossState(BossState.Loading);
			sleep(2000);
		
			boss = new Boss(430, -300, 1, 150);
			model.setBoss(boss);
			setPhaseLocation("Final Phase Move Boss");
		}
		
		if (getPhaseLocation().equals("Final Phase Move Boss"))
		{
			// Move boss to initial starting point.
			while (boss.getY() != 30)
			{	
				if (Game.getGameState() == Game.State.Running)
				{
					boss.tick();
					sleep(10);
				}
				else if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
					return;
			}
			sleep(1000);
			setBossState(BossState.Easy);
			setPhaseLocation("Final Phase Easy");
		}
		
		if (getPhaseLocation().equals("Final Phase Easy"))
		{
			// Execute easy stage.
			easyStage();
			if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
			setPhaseLocation("Final Phase Medium Loading");
		}
		
		if (getPhaseLocation().equals("Final Phase Medium Loading"))
		{
			// Load medium boss phase data. 
			setBossState(BossState.Loading);
			model.resetBossPosition();
			boss.setHealth(150);
			sleep(1000);
			setBossState(BossState.Medium);
			setPhaseLocation("Final Phase Medium");
		}
		
		if (getPhaseLocation().equals("Final Phase Medium"))
		{
			// Execute medium boss phase.
			mediumStage();
			if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
			setPhaseLocation("Final Phase Hard Loading");
		}
	
		if (getPhaseLocation().equals("Final Phase Hard Loading"))
		{
			// Load hard boss phase data.
			setBossState(BossState.Loading);
			model.resetBossPosition();
			boss.setHealth(150);
			sleep(1000);
			setBossState(BossState.Hard);
			setPhaseLocation("Final Phase Hard");
		}
		
		if (getPhaseLocation().equals("Final Phase Hard"))
		{
			// Execute hard boss phase.
			hardStage();
		}
	}
	
	// Easy boss phase.
	public void easyStage()
	{
		// Continually move boss and project missles until its health is zero.
		while (boss.getHealth() != 0)
		{
			int randX = generateBossPosition(8);
			model.addMissle(boss.getX() + 80, boss.getY() + 200, 4);
			model.moveBoss(randX);
			if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
		}
	}
	
	// Medium boss phase.
	public void mediumStage()
	{
		// Continually move boss and project missles until its health is zero.
		while (boss.getHealth() != 0)
		{
			int randX = generateBossPosition(10);
			model.addMissle(boss.getX() + 80, boss.getY() + 200, 8);
			model.moveBoss(randX);
			if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
		}
	}
	
	// Hard boss phase.
	public void hardStage()
	{
		// Continually move boss and project missles until its health is zero.
		while (boss.getHealth() != 0)
		{
			int randX = generateBossPosition(12);
			model.addMissle(boss.getX() + 80, boss.getY() + 200, 12);
			generateRandomMissles();
			model.moveBoss(randX);
			if (Game.getGameState() == Game.State.Starting || Game.getGameState() == Game.State.Loading)
				return;
		}
		game.win();
	}
	
	// Spawn enemies.
	public void spawnEnemies(int maxSize, int maxEnemies, int maxVelocity, int threshold, int killsNeeded)
	{
		if (getEnemiesSize() < maxSize && Enemy.getEnemiesKilled() < killsNeeded)
		{	
			int numEnemies = random.nextInt(maxEnemies) + 1;
			for (int i = 0; i < numEnemies; ++i)
			{
				int randX = random.nextInt(game.getWidth() - 89);
				int randY = random.nextInt(61) - 120;
				int randVelY = random.nextInt(maxVelocity) + 1;
				model.addEnemy(randX, randY, randVelY);
			}
			attack(threshold);
		}
	}
	
	// Generate numerous missles.
	public void generateRandomMissles()
	{
		if (getMisslesSize() < 10)
		{
			int numEnemies = random.nextInt(8) + 1;
			for (int i = 0; i < numEnemies; ++i)
			{
				int randX = random.nextInt(game.getWidth() - 89);
				int randY = random.nextInt(61) - 120;
				int randVelY = random.nextInt(8) + 3;
				model.addMissle(randX, randY, randVelY);
			}
		}
	}
	
	// Simulate enemy attack by having a random number of the enemies fire missles.
	public void attack(int threshold)
	{
		synchronized(enemies)
		{
			for (int i = random.nextInt(enemies.size()) + threshold; i < enemies.size(); ++i)
			{
				Enemy enemy = enemies.get(random.nextInt(enemies.size()));
				model.addMissle(enemy.getX() + 20, enemy.getY() + 80, enemy.getVelocityY() + 6);
			}
		}
	}
	
	// Generate random boss position.
	public int generateBossPosition(int vel)
	{
		int randX = random.nextInt(game.getWidth() - 199);
		if (randX < boss.getX())
			boss.setVelocityX(-vel);
		else
			boss.setVelocityX(vel);
		
		return randX;
	}
	
	// Getters and setters.
	public int getEnemiesSize()
	{
		synchronized(enemies)
		{
			return enemies.size();
		}
	}
	
	public int getMisslesSize()
	{
		synchronized(missles)
		{
			return missles.size();
		}
	}

	public static BossState getBossState() 
	{
		synchronized (bossState)
		{
			return bossState;
		}
	}

	public static void setBossState(BossState bossState) 
	{
		synchronized (SpawningThread.bossState)
		{
			SpawningThread.bossState = bossState;
		}
	}
	
	static synchronized int getTotalEnemies() 
	{
		return totalEnemies;
	}

	static synchronized void setTotalEnemies(int totalEnemies) 
	{
		SpawningThread.totalEnemies = totalEnemies;
	}
	
	public static String getPhaseLocation() 
	{
		return phaseLocation;
	}

	public static void setPhaseLocation(String phaseLocation) 
	{
		SpawningThread.phaseLocation = phaseLocation;
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