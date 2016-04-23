package utility;

import game.physics.Vector;

public class BattleMath {

	private BattleMath(){}
	
	public static int Sign(double d)
	{
		if(d < 0)
			return -1;
		else
			return 1;
	}
	
	public static int Sign(Vector v)
	{
		if(v.XPoint() < 0 )
			return -1;
		else if(v.XPoint()> 0)
			return 1;
		else 
			return Sign(v.YPoint());
			
	}
}
