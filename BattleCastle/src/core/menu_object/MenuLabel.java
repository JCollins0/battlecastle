package core.menu_object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GameState;

public class MenuLabel {

	private Rectangle bounds;
	private ArrayList<GameState> visibleStates;
	private BufferedImage image;
	
	public MenuLabel(int x, int y, int width, int height,
			BufferedImage base_image, GameState visibleState)
	{
		this(x,y,width,height,base_image,new GameState[]{visibleState});
	}
	
	public MenuLabel(int x, int y, int width, int height,
			BufferedImage base_image,
			GameState... visibleStatesList)
	{
		bounds = new Rectangle(x,y,width,height);
		this.image = base_image;
		visibleStates = new ArrayList<GameState>();
		for(int i = 0; i < visibleStatesList.length; i++)
			visibleStates.add(visibleStatesList[i]);
	}
	
	public void render(Graphics g)
	{
		if(image != null)
		{
			g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
		}else
		{
			g.setColor(Color.PINK);	
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}
	
	public void tick()
	{
		
	}
	
	public boolean isVisibleAtState(GameState state)
	{
		for(GameState s : visibleStates)
		{
			if (s.equals(state))
				return true;
		}
		return false;
	}
}
