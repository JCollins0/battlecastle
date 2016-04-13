package editor;

import game.physics.Vector;

public class State
{
	
	private boolean paused, continual;
	private int restTime, dx, dy, sx, sy, tx, ty;
	private Vector v;
	
	public State(int dx,int sx,int dy,int sy)//moves an object over a set distance (denoted by d) with a set speed (denoted by s)
	{
		this.dx=dx;
		this.dy=dy;
		this.sx=sx;
		this.sy=sy;
		tx=0;
		ty=0;
		v=new Vector(sx,sy);
		continual=false;
	}
	
	public State(int restTime)//an in between state to pause an object in place for a set time
	{
		this.paused=true;
		this.restTime=restTime;
	}
	
	public State(int sx,int sy)//a continual movement with a set speed (platforms or objects that screen wrap)
	{
		this.continual=true;
		this.sx=sx;
		this.sy=sy;
		v=new Vector(sx,sy);
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
			t.move(v);
			return false;
		}
		if(!paused)
		{
			t.move(v);
			if(tx<dx)
				tx+=Math.abs(sx);
			if(ty<dy)
				ty+=Math.abs(sy);
			//System.out.println(tx+" "+ty);
			if(tx>=dx&&ty>=dy)
			{
				tx=ty=0;
				return true;
			}
		}
		if(paused)
		{
			if(--restTime==0)
				return true;
		}
		return false;
	}
	
	public void moveToEndPos(Tile t)
	{
		if(!(continual||paused))
		{
			t.move(new Vector(dx-tx,dy-ty));
			ty=tx=0;
		}
	}
	
	public String stringify()
	{
		if(paused)
			return restTime+"";
		if(continual)
			return sx+"+"+sy;
		return dx+"+"+sx+"+"+dy+"+"+sy;
	}

	public Vector getV() {
		return v;
	}

	public void setV(Vector v) {
		this.v = v;
	}
	
}
