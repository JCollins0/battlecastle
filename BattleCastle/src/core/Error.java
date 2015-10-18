package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Error {

	
	private String text;
	private boolean shouldRemove;
	private int timeToDisplay;
	private int x, y;
	public static final Font ERROR_FONT = new Font("Arial",Font.BOLD,20);
	
	public Error(String text, int timeToDisplay)
	{
		this.text = text;
		this.timeToDisplay = timeToDisplay;
	}
	
	public void render(Graphics g)
	{
		g.setFont(ERROR_FONT);
		g.setColor(Color.RED);
		g.drawString(text, x, y);
	}
	
	public void tick()
	{
		if(!shouldRemove)
			timeToDisplay--;
		if(timeToDisplay < 0)
		{
			timeToDisplay = 0;
			shouldRemove = true;
		}
	}
	
	public boolean shouldRemove()
	{
		return shouldRemove;
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getY() {
		return y;
	}
}
