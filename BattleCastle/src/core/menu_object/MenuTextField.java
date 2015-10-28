package core.menu_object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GameState;
import core.constants.ImageFilePaths;
import utility.Utility;

public class MenuTextField {
	
	private Rectangle bounds;
	private String allowablecharacters = "";
	private String text;
	private boolean selected;
	private MenuTextFieldType id;
	private ArrayList<GameState> visibleStates;
	private BufferedImage image;
	private static final BufferedImage CURSOR = Utility.loadImage(ImageFilePaths.CURSOR);
	private int cursorCount = 0;
	private int cursorDelay = 20;
	private static final Font TEXT_FONT = new Font("Courier New", Font.PLAIN, 50);
	
	public MenuTextField(int x, int y, int width, int height, MenuTextFieldType id, GameState visibleState)
	{
		this(x,y,width,height,id,null,new GameState[]{visibleState});
	}
	
	public MenuTextField(int x, int y, int width, int height, MenuTextFieldType id, GameState... visibleStates)
	{
		this(x,y,width,height,id,null, visibleStates);
	}
	
	public MenuTextField(int x, int y, int width, int height, MenuTextFieldType id, BufferedImage image, GameState visibleState)
	{
		this(x,y,width,height,id,image, new GameState[]{visibleState});
	}
	
	public MenuTextField(int x, int y, int width, int height, MenuTextFieldType id, BufferedImage image, GameState... visibleStatesList)
	{
		text = "";
		bounds = new Rectangle(x,y,width,height);
		this.id = id;
		this.image = image;
		visibleStates = new ArrayList<GameState>();
		for(int i = 0; i < visibleStatesList.length; i++)
			visibleStates.add(visibleStatesList[i]);
	}
	
	public void backspace()
	{
		if (text.length() > 0)
			text = text.substring(0, text.length() - 1);
	}
	
	public void space()
	{
		if(text.length() < 16)
			text = text + " ";
	}
	
	public void addCharacter(char c)
	{
		if ( (allowablecharacters == "" || allowablecharacters.indexOf(c) >= 0 ) && text.length() < 16)
		{
			text = text + c;
		}
	}
	
	public void setDefaultText(String text)
	{
		this.text = text;
	}
	
	public void setAllowableCharacters(String characters)
	{
		allowablecharacters = characters;
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
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
	
	public void render(Graphics g)
	{
		g.setFont(TEXT_FONT);
		if(image != null)
		{
			g.drawImage(image,bounds.x,bounds.y,bounds.width,bounds.height, null);
			if(selected)
			{
				if(cursorCount <= cursorDelay-10)
				{
					if(CURSOR!= null)
					{	
						g.drawImage(CURSOR,bounds.x + bounds.width/2 + g.getFontMetrics(TEXT_FONT).charWidth('a') * text.length()/2,  bounds.y + bounds.height/2 - g.getFontMetrics(TEXT_FONT).getHeight()/2 ,4,g.getFontMetrics(TEXT_FONT).getHeight(),null);
					}
					else
					{
						g.setColor(Color.WHITE);
						//System.out.printf("W:%d, H:%d",8,g.getFontMetrics(TEXT_FONT).getHeight());
						g.fillRect(bounds.x + bounds.width/2 + g.getFontMetrics(TEXT_FONT).charWidth('a') * text.length()/2,  bounds.y + bounds.height/2 - g.getFontMetrics(TEXT_FONT).getHeight()/2 ,4,g.getFontMetrics(TEXT_FONT).getHeight());
				
					}
				}				
			}
		}else
		{
			g.setColor(Color.black);
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		g.setColor(Color.white);
		g.drawString(text,bounds.x + bounds.width/2 - g.getFontMetrics(TEXT_FONT).charWidth('a') * text.length()/2, bounds.y + bounds.height/2 + g.getFontMetrics(TEXT_FONT).getHeight()/4);
	}
	
	public void tick()
	{
		cursorCount++;
		if(cursorCount > cursorDelay)
			cursorCount = 0;
	}

	public MenuTextFieldType getID() {
		return id;
	}
}
