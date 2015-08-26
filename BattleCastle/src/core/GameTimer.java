package core;

public class GameTimer
{
	private long startTime;
	
	public GameTimer() {
		restart();
	}
	
	public void restart() {
		startTime = System.nanoTime();
	}
	
	// Returns elapsed time in milliseconds
	public long getElapsedTime() {
		return (System.nanoTime() - startTime) / 1000000;
	}
}