package editor;

public class State
{
	
	private boolean paused, continual;
	private int restTime, dx, dy, sx, sy, tx, ty;
	
	public State(int dx,int sx,int dy,int sy)
	{
		this.dx=dx;
		this.dy=dy;
		this.sx=sx;
		this.sy=sy;
		tx=0;
		ty=0;
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
	
	public void reset()
	{
		tx=0;
		ty=0;
	}
	
	public boolean increment(Tile t)//boolean true indicates the change of state
	{
		if(continual)
		{
			t.shift(sx, sy);
			return false;
		}
		if(!paused)
		{
			
		}
		return true;
	}
	
}
