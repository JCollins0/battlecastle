package core.menu_object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import core.BattleCastleCanvas;

public class ServerSelectionBox {
	
	ArrayList<ServerChoice> serverChoices;
	private int yOffset;
	private Rectangle bounds;
	private static final int WIDTH = 256, HEIGHT = 256;
	private static final int NUMBER_SERVERS_TO_SHOW = 4;
	private int status;
	private static final Font STATUS_FONT = new Font("Courier New",Font.PLAIN,25);
	public static final int SEARCHING = 0, NOTHING = 1, FOUND = 2;
	
	public ServerSelectionBox(int x, int y)
	{
		serverChoices = new ArrayList<ServerChoice>(); 
		bounds = new Rectangle(x,y,WIDTH,HEIGHT);
	}
	
	public void addServer(ServerChoice choice)
	{
		if(!serverChoices.contains(choice))
			serverChoices.add(choice);
	}
	
	public void render(Graphics g)
	{
		
		g.setColor(Color.CYAN);
		g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
		
		if (status == SEARCHING)
		{
			g.setColor(Color.BLACK);
			g.setFont(STATUS_FONT);
			g.drawString("Searching...", bounds.x, bounds.y);
		}else if(status == NOTHING)
		{
			g.setColor(Color.BLACK);
			g.setFont(STATUS_FONT);
			g.drawString("No Servers Found", bounds.x, bounds.y);
		}
		
		if(yOffset <= 0)
			yOffset = 0;
		else if(yOffset >= serverChoices.size())
			yOffset = serverChoices.size()-1;
	
		for(int i = yOffset; i < yOffset+NUMBER_SERVERS_TO_SHOW; i++)
		{
			if(i < serverChoices.size() && i >= 0)
				serverChoices.get(i).render(g);
		}
		
		
	}
	
	public void updatePositions(int offset)
	{
		if(serverChoices.size() > NUMBER_SERVERS_TO_SHOW)
		{
			for(int i = 0; i < serverChoices.size(); i++)
			{
				serverChoices.get(i).setY(serverChoices.get(i).getY()+offset*ServerChoice.HEIGHT);
			}
		}
		yOffset += offset;
//		System.out.println(yOffset);
		
	}
	
	public void tick()
	{
	
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}

	public ServerChoice getServerChoice(Point mouse) {
		return getServerChoice(mouse.x, mouse.y);
	}
	
	public ServerChoice getServerChoice(int x, int y)
	{
		for(int i = 0; i < serverChoices.size(); i++)
		{
			if (serverChoices.get(i).getBounds().contains(x,y))
			{
				return serverChoices.get(i);
			}
		}
		return null;
	}
}
