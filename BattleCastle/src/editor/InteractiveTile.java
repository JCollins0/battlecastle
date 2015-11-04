package editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import game.player.Player;

public class InteractiveTile extends Tile
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2723258315202922739L;
	
	private boolean interacting;
	
	public InteractiveTile(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}
	
	public void interact(Player p)
	{
		interacting=p.getBounds().intersects(this);
	}
	
	public void draw(Graphics g)
	{
		g.drawImage(pics[animation], x, y, width, height, null);
		if(interacting)
		{
			g.setColor(Color.YELLOW);
			g.fillRect(x,y,width,height);
		}
	}

}
