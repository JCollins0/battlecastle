package shader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import core.MouseHandler;
import utility.Utility;

public class LightShader extends Shader {

	private static BufferedImage light = Utility.loadImage("shader/light");
	private static Color color = new Color(0,0,0,0x40);
	
	public LightShader(ShaderType type, int w, int h) {
		super(type, w, h);
	}
	int offset = 0;
	
	@Override
	public void manipulatePixels() {
		for(int y = offset; y < image.getHeight()-offset; y+=4)
		{
			for(int x= 0; x < image.getWidth(); x++)
			{
				image.setRGB(x, y, 0x40000000);
			}
		}
		
		if(offset == 0)
			offset =9;
		if(offset == 9)
			offset = 0;
			
	}
	
	@Override
	public void applyShader(Graphics g) {
		super.applyShader(g);
//		g.setColor(color);
//		g.fillRect(0, 0, 1024, 786);
//		g.drawImage(light, MouseHandler.mouse.x-100, MouseHandler.mouse.y-100, null);
//		
//		
	}
	

}
