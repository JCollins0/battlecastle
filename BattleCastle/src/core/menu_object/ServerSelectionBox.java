package core.menu_object;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class ServerSelectionBox {
	
	ArrayList<ServerChoice> serverChoices;
	private int yOffset;
	
	
	public ServerSelectionBox()
	{
		serverChoices = new ArrayList<ServerChoice>(); 
	}
	
	public void addServer(ServerChoice choice)
	{
		if(!serverChoices.contains(choice))
			serverChoices.add(choice);
	}
	
	public void render(Graphics g)
	{
		if(yOffset <= 0)
			yOffset = 0;
		else if(yOffset >= serverChoices.size())
			yOffset = serverChoices.size()-1;
	
		for(int i = yOffset; i < yOffset+3; i++)
		{
			if(i < serverChoices.size())
				serverChoices.get(i).render(g);
		}
	}
	
	public void updatePositions(int offset)
	{
		if(serverChoices.size() > 3)
		{
			for(int i = 0; i < serverChoices.size(); i++)
			{
				serverChoices.get(i).setY(serverChoices.get(i).getY()+offset*ServerChoice.HEIGHT);
			}
		}
		yOffset += offset;
		System.out.println(yOffset);
		
	}
	
	public void tick()
	{
		
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
