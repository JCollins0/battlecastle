package core.menu_object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GameState;

public class MenuButton{

	protected Rectangle bounds;
	private MenuButtonType buttonType;
	private BufferedImage image, hover_image;
	private ArrayList<GameState> visibleStates;
	protected boolean hoverOver;
	
	public MenuButton(int x, int y, int width, int height, MenuButtonType buttonType,
			BufferedImage base_image, BufferedImage hover_image, GameState visibleState)
	{
		this(x,y,width,height,buttonType,
				base_image, hover_image,
				new GameState[]{ visibleState } );
	}
	
	public MenuButton(int x, int y, int width, int height, MenuButtonType buttonType,
			BufferedImage base_image, BufferedImage hover_image,
			GameState... visibleStatesList)
	{
		bounds = new Rectangle(x,y,width,height);
		this.buttonType = buttonType;
		this.image = base_image;
		this.hover_image = hover_image;
		visibleStates = new ArrayList<GameState>();
		for(int i = 0; i < visibleStatesList.length; i++)
			visibleStates.add(visibleStatesList[i]);
	}
	
	public MenuButton(int x, int y, int width, int height, MenuButtonType buttonType,
			GameState visibleState)
	{
		this(x,y,width,height,buttonType,
				null, null, new GameState[]{ visibleState } );
	}
	
	public MenuButton(int x, int y, int width, int height, MenuButtonType buttonType,
			GameState... visibleStates)
	{
		this(x,y,width,height,buttonType,
				null, null, visibleStates );
	}
		
	public void render(Graphics g)
	{
		if(image != null)
		{
			if(hoverOver)
				g.drawImage(hover_image, bounds.x, bounds.y,bounds.width, bounds.height, null);
			else
				g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height,  null);
		}else
		{
			g.setColor(Color.blue);
			g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
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
	
	public MenuButtonType getButtonType() {
		return buttonType;
	}
	
	public void setX(int x)
	{
		bounds.x = x;
	}
	
	public void setY(int y)
	{
		bounds.y = y;
	}
	
	public void setWidth(int w)
	{
		bounds.width = w;
	}
	
	public void setHeight(int h)
	{
		bounds.height = h;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
	public boolean isHoverOver(Point p) 
	{
		return isHoverOver(p.x, p.y);
	}
	
	public boolean isHoverOver(int x, int y)
	{
		return(hoverOver = bounds.contains(x,y));
		
	}
	
}