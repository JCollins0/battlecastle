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
	
	public State(int restTime)
	{
		this.paused=true;
		this.restTime=restTime;
	}
	
	public State(int sx,int sy)
	{
		this.continual=true;
		this.sx=sx;
		this.sy=sy;
	}
	
	public State()
	{
		continual=true;
		sx=0;
		sy=0;
	}
	
	public void reset()
	{
		if(paused)
			this.restTime=0;
		else if(!continual)
		{
			tx=0;
			ty=0;
		}
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
			t.shift(sx, sy);
			if(tx<dx)
				tx+=sx;
			if(ty<dy)
				ty+=sy;
			if(tx>=dx&&ty>=dy)
			{
				return true;
			}
		}
		if(paused)
		{
			if(--restTime==0)
				return true;
		}
		return true;
	}
	
}
