package core.menu_object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GameState;

public class MenuTextField {
	
	private Rectangle bounds;
	private String allowablecharacters = "";
	private String text;
	private boolean selected;
	private MenuTextFieldType id;
	private ArrayList<GameState> visibleStates;
	private BufferedImage image;
	
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
		text = text + " ";
	}
	
	public void addCharacter(char c)
	{
		if ( allowablecharacters == "" || allowablecharacters.indexOf(c) >= 0)
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
		g.setColor(Color.black);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(Color.white);
		g.drawString(text,bounds.x + bounds.width/3, bounds.y + bounds.height/2);
	}

	public MenuTextFieldType getID() {
		return id;
	}
}
