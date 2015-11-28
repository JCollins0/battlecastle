package utility;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import core.constants.DataConstants;
import editor.Tile;

public class Utility {
	private Utility() {}

    public static BufferedImage loadImage(String img) {
        try
        {
//        	System.out.println(img);
      		return ImageIO.read(new File("resources/" + img + ".png"));
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
    
    public static BufferedImage[] loadBufferedArray(String img, int xOffset, int yOffset)
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
    	BufferedImage[] temp=new BufferedImage[imagepix.size()];
    	temp=imagepix.toArray(temp);
    	return temp;
    }
    
    public static ArrayList<Tile> readLevelSaveFromFile(String filePath)
	{
		ArrayList<Tile> temp = new ArrayList<Tile>();
		Scanner reader=null;
		JSONParser parser=new JSONParser();
		try
		{
			reader=new Scanner(new FileInputStream(filePath));
			//System.out.println(reader.nextLine());
			JSONArray ja = (JSONArray)parser.parse(reader.nextLine());
			for(int i = 0;i<ja.size();i++)
			{
				temp.add(Tile.decodeJSON((JSONObject)ja.get(i)));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader!=null)
				reader.close();
		}
		return temp;
	}
    
    public static ArrayList<Tile> readLevelSaveFromString(String data)
    {
    	ArrayList<Tile> temp = new ArrayList<Tile>();
    	Scanner reader=null;
		JSONParser parser=new JSONParser();
		try
		{
			reader=new Scanner(data);
			JSONArray ja = (JSONArray)parser.parse(reader.nextLine());
			for(int i = 0;i<ja.size();i++)
			{
				temp.add(Tile.decodeJSON((JSONObject)ja.get(i)));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader!=null)
				reader.close();
		}
//		System.out.println("Temp SIZE: " + temp.size());
    	return temp;
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
