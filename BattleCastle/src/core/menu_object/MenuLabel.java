package core.menu_object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GameState;

public class MenuLabel {

	private Rectangle bounds;
	private ArrayList<GameState> visibleStates;
	private BufferedImage image;
	
	private String text;
	private static final Font TEXT_FONT = new Font("Courier New",Font.BOLD,17);
	
	public MenuLabel(int x, int y, int width, int height,
			BufferedImage base_image, GameState visibleState)
	{
		this(x,y,width,height,base_image, null, new GameState[]{visibleState});
	}
	
	public MenuLabel(int x, int y, int width, int height,
			BufferedImage base_image,
			GameState... visibleStatesList)
	{
		this(x,y,width,height,base_image, null, visibleStatesList);
	}
	
	public MenuLabel(int x, int y, int width, int height,
			BufferedImage base_image, String text, GameState... visibleStatesList)
	{
		bounds = new Rectangle(x,y,width,height);
		this.image = base_image;
		visibleStates = new ArrayList<GameState>();
		for(int i = 0; i < visibleStatesList.length; i++)
			visibleStates.add(visibleStatesList[i]);
		this.text = text;
	}
	
	public MenuLabel(int x, int y, int width, int height,
			BufferedImage base_image, String text, GameState visibleState)
	{
		this(x, y, width, height, base_image, text, new GameState[]{visibleState});
	}
	
	public void render(Graphics g)
	{
		if(image != null)
		{
			g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
			
			if(text != null)
			{
				g.setFont(TEXT_FONT);
				g.setColor(Color.black);
				int dx = bounds.x + bounds.width/2 - g.getFontMetrics(TEXT_FONT).charWidth('a') * text.length()/2;
				int dy = bounds.y + bounds.height + g.getFontMetrics(TEXT_FONT).getHeight()/2+5;

				g.drawString(text, dx, dy);
			}
			
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
