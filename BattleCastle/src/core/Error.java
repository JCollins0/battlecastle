package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Error {

	//Text
	public static final Font ERROR_FONT = new Font("Arial",Font.BOLD,20);
	private String text;
	private int x, y;
	
	//Display Time
	private boolean shouldRemove;
	private int timeToDisplay;
	
	/**
	 * Constructs a new Error
	 * @param text the message
	 * @param timeToDisplay the time delay
	 */
	public Error(String text, int timeToDisplay)
	{
		this.text = text;
		this.timeToDisplay = timeToDisplay;
	}
	
	/**
	 * Draw error on screen
	 * @param g screen Graphics
	 */
	public void render(Graphics g)
	{
		g.setFont(ERROR_FONT);
		g.setColor(Color.RED);
		g.drawString(text, x, y);
	}
	
	/**
	 * Updates error object
	 */
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
	
	/**
	 * Decide if error object has been on screen to long
	 * @return True if it should be removed, False if not
	 */
	public boolean shouldRemove()
	{
		return shouldRemove;
	}
	
	/**
	 * Sets the position of Error Message on screen
	 * @param x the x position
	 * @param y the y position
	 */
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Get y position on screen
	 * @return y
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Get x position on screen
	 * @return x
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * Get text of message
	 * @return text
	 */
	public String getText()
	{
		return text;
	}
}
