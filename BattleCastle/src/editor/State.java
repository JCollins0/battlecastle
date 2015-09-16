package editor;

public class State
{
	
	private boolean paused, continual;
	private int restTime, dx, dy, sx, sy;
	
	public State(int dx,int sx,int dy,int sy)
	{
		this.dx=dx;
		this.dy=dy;
		this.sx=sx;
		this.sy=sy;
	}
	
	public State(boolean paused,int restTime)
	{
		this.paused=paused;
		this.restTime=restTime;
	}
	
	public State(boolean continual,int sx,int sy)
	{
		this.continual=continual;
		this.sx=sx;
		this.sy=sy;
	}
	
}
