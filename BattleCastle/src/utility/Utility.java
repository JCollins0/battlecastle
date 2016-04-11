package utility;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    public static Clip loadAudio(String name) {
    	try{
    		Clip clip = AudioSystem.getClip();
    		clip.open(AudioSystem.getAudioInputStream(new File("resources/audio/" + name + ".wav")));
    		return clip;
    	}catch(Exception ioException)
    	{
	    	JOptionPane.showMessageDialog(null, "Error reading audio file!", "Utility", JOptionPane.ERROR_MESSAGE);
	    	return null;
    	}
    }
    
    public static final Color ignore_Color = new Color(0x0000ff);
    
    public static BufferedImage[][] loadBufferedMatrix(String img, int xOffset, int yOffset)
    {
    	BufferedImage image = loadImage(img);
    	BufferedImage[][] mat = new BufferedImage[image.getHeight()/yOffset][image.getWidth()/xOffset];
    	for (int y = 0; y < image.getHeight(); y += yOffset)
    	{
    		for (int x = 0; x < image.getWidth(); x += xOffset)
    		{
    			mat[y/yOffset][x/xOffset] = image.getSubimage(x, y, xOffset, yOffset);
    		}
    	}
    	return mat;
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
    
    public static ArrayList<BufferedImage> loadBufferedList(String img, int xOffset, int yOffset)
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
    
    public static String generateRandomUUID(int length)
    {
    	String ID = "";
    	int random = (int)(Math.random() * 26 + 65);
		for(int i = 0; i < length; i++)
		{
			ID += (char)random;
			random = (int)(Math.random() * 26 + 65);
		}
		return ID;
    }
    
    public static String generateID(Object... vals)
    {
    	String s = "";
    	for(Object v : vals)
    		s+= String.valueOf(v);
    	return s; 
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
