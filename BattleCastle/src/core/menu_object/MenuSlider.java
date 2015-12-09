package core.menu_object;

import java.awt.Graphics;
import java.awt.Rectangle;

import utility.AudioHandler;

public class MenuSlider {

	private Rectangle bounds;
	private Slider slider;
	
	public MenuSlider(int x, int y, int width, int height, int min, int max,MenuSliderType type)
	{		
		bounds = new Rectangle(x,y,width + (width / 100 * 10),height);
		slider = new Slider(x,y,width,height,min, max,this);
		
//		slider.setValue((int) (AudioHandler.getVolume() * 100));
	}
	
	public void tick()
	{
		slider.tick();
	}
	
	public void render(Graphics g)
	{
		slider.render(g);
	}
	
	
	private class Slider{
		
		private Rectangle bounds;
		private MenuSlider bar;
		private int min, max;
		private double updateIncrement;
		private int value;
		
		public Slider(int x, int y, int width, int height, int min, int max, MenuSlider slider)
		{
			bounds = new Rectangle(x,y,width,height);
			this.min = min;
			this.max = max;
			bar = slider;
			updateIncrement = (double)width / Math.abs(min-max);
		}
		
		public void tick()
		{
			if(value < min)
				value = min;
			else if(value > max)
				value = max;
		}
		
		public void update(int x)
		{
			bounds.x = (2 * x - bounds.width) / 2;
		}
		
		public void render(Graphics g)
		{
			
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
			bounds.x = (int)(  bar.bounds.x +  Math.abs(value-min) * updateIncrement);
			this.value = value;
		}
	}
}
