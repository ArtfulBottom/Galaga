/*
 * File: ProjectilesThread.java
 * Author: Luke S. Snyder
 * UA ID: 010691128
 */

public class ProjectilesThread implements Runnable
{
	private Game game;
	private Model model;
	
	public ProjectilesThread(Game game, Model model)
	{
		this.game = game;
		this.model = model;
	}
	
	public void run()
	{
		// Continually update any projectiles.
		while (true)
		{		
			// If the game is needing to restart (i.e., in starting state but not actually
			// starting up yet), or loading another game, end thread.
			if ((Game.getGameState() == Game.State.Starting && !game.getIsStartup()) ||
				 Game.getGameState() == Game.State.Loading)
				return;
			
			model.moveProjectiles();
			try 
			{
				Thread.sleep(10);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}