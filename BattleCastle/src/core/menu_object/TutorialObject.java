package core.menu_object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GameState;
import utility.Utility;

public class TutorialObject {

	private Rectangle bounds;
	private ArrayList<BufferedImage> animatedImages;
	private ArrayList<GameState> visibleStates;
	private int delay;
	private int count;
	private int state;
	private String textToDisplay;
	private String subTitleText;
	private boolean displaySubAtTop;
	private static final Font DISPLAY_FONT = new Font("Courier New",Font.BOLD,20);
	private static final Font SUBTITLE_FONT = new Font("Courier New",Font.BOLD,17);
	
	public TutorialObject(int x, int y, int width, int height, String image, int xOffset, int yOffset, int delay, String subtitleText, boolean displaySubAtTop, GameState... visibleStates)
	{
		this(x, y, width, height, Utility.loadBufferedList(image, xOffset, yOffset), delay, subtitleText, displaySubAtTop, visibleStates);
	}
	
	public TutorialObject(int x, int y , int width, int height, ArrayList<BufferedImage> images, int delay, String subtitleText, boolean displaySubAtTop, GameState... visibleStatesList)
	{
		animatedImages = images;
		bounds = new Rectangle(x, y, width, height);
		this.delay = delay;
		visibleStates = new ArrayList<GameState>();
		this.subTitleText = subtitleText;
		this.displaySubAtTop = displaySubAtTop;
		for(int i = 0; i < visibleStatesList.length; i++)
			visibleStates.add(visibleStatesList[i]);
	}
	
	public void setText(String text)
	{
		this.textToDisplay = text;
	}
	
	public void render(Graphics g)
	{
		
		if(animatedImages != null)
		{
			g.drawImage(animatedImages.get(state),bounds.x, bounds.y, bounds.width, bounds.height, null);
			
			if(textToDisplay != null)
			{
				if(textToDisplay.length() > 1 && bounds.width == 64)
				{
					bounds.width = 96;
				}
				int dx = bounds.x + bounds.width/2 - g.getFontMetrics(DISPLAY_FONT).charWidth('a') * textToDisplay.length()/2;
				int dy = bounds.y + bounds.height/2 + g.getFontMetrics(DISPLAY_FONT).getHeight()/4;
				g.setFont(DISPLAY_FONT);
				g.setColor(Color.black);
				g.drawString(textToDisplay, dx, dy);
			}
			
			g.setFont(SUBTITLE_FONT);
			g.setColor(Color.black);
			int dx = bounds.x + bounds.width/2 - g.getFontMetrics(SUBTITLE_FONT).charWidth('a') * subTitleText.length()/2;
			int dy;
			if(displaySubAtTop)
				dy = bounds.y - g.getFontMetrics(SUBTITLE_FONT).getHeight()/2;
			else
				dy = bounds.y + bounds.height + g.getFontMetrics(SUBTITLE_FONT).getHeight()/2+5;
			
			g.drawString(subTitleText, dx, dy);
		}else
		{
			g.setColor(Color.blue);
			g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
		}
	}
	
	public void tick()
	{
		if(count > delay)
		{
			state++;
			count = 0;
		}
		
		if(state >= animatedImages.size() )
			state = 0;
		
		count++;
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
