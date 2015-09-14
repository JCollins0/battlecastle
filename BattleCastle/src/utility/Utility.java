package utility;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Utility {
	private Utility() {}

    public static BufferedImage loadImage(String img) {
        try
        {
      		return ImageIO.read(Utility.class.getResource("resources/" + img));
		}
	    catch(Exception ioexception) {
	    	JOptionPane.showMessageDialog(null, "Error reading image file!", "Utility", JOptionPane.ERROR_MESSAGE);
	    	return null;
	    }
    }

    public static AudioClip loadAudio(String name) {
    	try{
    		return Applet.newAudioClip(Utility.class.getResource("resources/" + name));
    	}catch(Exception ioException)
    	{
	    	JOptionPane.showMessageDialog(null, "Error reading audio file!", "Utility", JOptionPane.ERROR_MESSAGE);
	    	return null;
    	}
    }
    
    public static ArrayList<BufferedImage> loadBufferedArray(String img, int xOffset, int yOffset)
    {
    	BufferedImage image = loadImage(img);
    	ArrayList<BufferedImage> imagepix = new ArrayList<BufferedImage>();
    	for (int y = 0; y < image.getHeight(); y += yOffset)
    	{
    		for (int x = 0; x < image.getWidth(); x += xOffset)
    		{
    			imagepix.add(image.getSubimage(x, y, xOffset, yOffset));
    		}
    	}
    	return imagepix;
    }
    
    public static Color randomRGBColor()
    {
    	return new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));
    }
    
    public static Color randomRGBAColor()
    {
    	return new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));
    }
    
    public static BufferedImage[][] loadFont(String image, int width, int height)
    {
    	BufferedImage source = loadImage(image);
    	BufferedImage[][] fontMap = new BufferedImage[source.getHeight()/height][source.getWidth()/width];
    	
    	for(int y = 0; y < fontMap.length; y++)
    	{
    		for(int x = 0; x < fontMap[y].length; x++)
    		{
    			fontMap[y][x] = source.getSubimage(x * width, y * height, width, height);
    		}
    	}
    	
    	return fontMap;
    }

}
