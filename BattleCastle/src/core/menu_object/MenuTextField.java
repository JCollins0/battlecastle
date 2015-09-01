package core.menu_object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import core.GameState;

public class MenuTextField {
	
	private Rectangle bounds;
	private String allowablecharacters = "";
	private String text;
	private boolean selected;
	private MenuTextFieldType id;
	private GameState visibleState;
	
	public MenuTextField(int x, int y, int width, int height, MenuTextFieldType id, GameState visibleState)
	{
		text = "";
		bounds = new Rectangle(x,y,width,height);
		this.id = id;
		this.visibleState = visibleState;
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
	
	public GameState getVisibleState()
	{
		return visibleState;
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
