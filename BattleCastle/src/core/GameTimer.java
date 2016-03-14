package core;

public class GameTimer
{
	private long startTime;
	
	/**
	 * Construct a new GameTmer
	 */
	public GameTimer() {
		restart();
	}
	
	/**
	 * Restart the timer
	 */
	public void restart() {
		startTime = System.nanoTime();
	}
	
	/**
	 * Returns elapsed time in milliseconds
	 * @return (System.nanoTime() - startTime) / 1000000;
	 */
	public long getElapsedTime() {
		return (System.nanoTime() - startTime) / 1000000;
	}
}