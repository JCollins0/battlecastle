package core.menu_object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utility.Utility;
import core.GameState;
import core.constants.ImageFilePaths;

public class MenuSlider {

	private Rectangle bounds;
	private Slider slider;
	private boolean selected;
	private ArrayList<GameState> visibleStates;
	private MenuSliderType type;
	private static final BufferedImage bar_image = Utility.loadImage(ImageFilePaths.SLIDER_BAR);
	private static final BufferedImage slider_image = Utility.loadImage(ImageFilePaths.SLIDER);
	private static final Font textFont = new Font("Courier New", Font.BOLD, 20);
	
	public MenuSlider(int x, int y, int width, int height, int min, int max, int startValue,MenuSliderType type, GameState... visibleStates)
	{		
		this.visibleStates= new ArrayList<GameState>();
		bounds = new Rectangle(x,y,width+2*(width/Math.abs(min-max)),height);
		slider = new Slider(x,y,width ,height,min, max,this);
		if(startValue < min || startValue > max)
			startValue = Math.min(Math.max(min,startValue),max);
		slider.setValue(startValue);
		this.type = type;
		for(int i = 0; i < visibleStates.length; i++)
			this.visibleStates.add(visibleStates[i]);
	}
	
	public void tick()
	{
		slider.tick();
	}
	
	public void render(Graphics g)
	{
		g.drawImage(bar_image, bounds.x, bounds.y, bounds.width, bounds.height, null);
		slider.render(g);
		g.setFont(textFont);
		g.drawString(slider.getValue() + "", bounds.x + bounds.width/2 - g.getFontMetrics(textFont).charWidth('a') * (slider.getValue()+"").length()/2, bounds.y + bounds.height/2 + g.getFontMetrics(textFont).getHeight()/4);
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
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

	public boolean isHoverOver(Point p) 
	{
		return isHoverOver(p.x, p.y);
	}
	
	public boolean isHoverOver(int x, int y)
	{
		return bounds.contains(x,y);
		
	}
	
	public void update(int x)
	{
		slider.update(x);
	}
	

	public MenuSliderType getType() {
		return type;
	}
	
	public void setValue(int startValue)
	{
		if(startValue < slider.getMin() || startValue > slider.getMax())
			startValue = Math.min(Math.max(slider.getMax(),startValue),slider.getMax());
		slider.setValue(startValue);
	}
	
	public int getValue()
	{
		return slider.getValue();
	}
	
	private class Slider{
		
		private Rectangle bounds;
		private MenuSlider bar;
		private int min, max;
		private double updateIncrement;
		private int value;
		
		public Slider(int x, int y, int width, int height, int min, int max, MenuSlider slider)
		{
			updateIncrement = (double)width / Math.abs(min-max);
			bounds = new Rectangle(x,y,(int)Math.max(1,updateIncrement),height);
			this.min = min;
			this.max = max;
			bar = slider;
		}
		
		public void tick()
		{
			if(value < min)
				value = min;
			else if(value > max)
				value = max;
			if(bounds.x > bar.bounds.x+bar.bounds.width - bounds.width)
				bounds.x = bar.bounds.x+bar.bounds.width-bounds.width;
			if(bounds.x < bar.bounds.x)
				bounds.x = bar.bounds.x;
		}
		
		public void update(int x)
		{
			if(x >= bar.bounds.x && x <= bar.bounds.x + bar.bounds.width - bounds.width)
			{
				bounds.x = x;
				
			}else if(x <= bar.bounds.x)
			{
				bounds.x = bar.bounds.x;
			}else if(x >= bar.bounds.x + bar.bounds.width - bounds.width)
				bounds.x = bar.bounds.x + bar.bounds.width- bounds.width;
			this.value = ((int) (updateIncrement/Math.pow(updateIncrement,2)*(bounds.x-bar.bounds.x)+min));
		//	setValue((int)((bar.bounds.x-bounds.x-bounds.width)/(double)bar.bounds.width * max ) );
		}
		
		public void render(Graphics g)
		{
			g.drawImage(slider_image, bounds.x, bounds.y, bounds.width, bounds.height, null);
		}

		public int getMin() {
			return min;
		}

		public int getMax() {
			return max;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
			int x = (int) ( bar.bounds.x + ( ( getValue() - min) * (Math.pow(updateIncrement, 2) / updateIncrement) ) * (bar.bounds.width+2*updateIncrement)/bar.bounds.width );
			if(x <= bar.bounds.x + bar.bounds.width)
				bounds.x = x;
		}
		
	}

}
