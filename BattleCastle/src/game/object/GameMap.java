package game.object;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.Type;

import core.constants.ImageFilePaths;
import core.constants.LevelTileData;
import editor.Tile;
import utility.Utility;

public class GameMap {
	
	private BufferedImage background;
	private Point[] playerStartLocations;
	private String imageName;
	private ArrayList<Tile> tiles;
	
	public GameMap()
	{
	
	}
	
	public GameMap(String imageName)
	{
		
		this(imageName,null);
	}
	
	public GameMap(String imageName, Point[] playerLocations)
	{
		this(imageName,playerLocations,null);	
	}
	
	public GameMap(String imageName, Point[] playerLocations, String tileData)
	{
		background = Utility.loadImage(imageName);
		this.imageName = imageName;
		this.playerStartLocations = playerLocations;
		if(tileData != null && !tileData.equals(""))
			tiles = Utility.readLevelSaveFromFile(tileData);
	}
	
	public void render(Graphics g)
	{
		if(background != null)
			g.drawImage(background, 0, 0, null);
		
		if(tiles != null)
			for(int i = 0; i < tiles.size(); i++)
			{
				tiles.get(i).draw(g);
				//System.out.println("Drawing Tiles");
			}
		
	}
	
	public void tick()
	{
		if(tiles != null)
			for(int i = 0; i < tiles.size(); i++)
			{
				tiles.get(i).tick();
			}
	}
	
	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public BufferedImage getBackground() {
		return background;
	}

	public void loadBackground(String name){
		try {
			background = ImageIO.read(new FileInputStream(new File(name)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setBackground(BufferedImage background) {
		this.background = background;
	}

	public Point[] getPlayerStartLocations() {
		return playerStartLocations;
	}

	public void setPlayerStartLocations(Point[] playerStartLocations) {
		this.playerStartLocations = playerStartLocations;
	}

	public String toString()
	{
		return imageName + "";
	}
	
	public void loadLevelData(String filePath)
	{
		//JOptionPane.showMessageDialog(null, data);
		tiles = Utility.readLevelSaveFromFile(filePath);
		//System.out.println(tiles);
	}
	
	public Point getPlayerStartPoint(int playerNum) {
		if(playerStartLocations == null)
		{
			System.out.println("ADD SOME PLAYER LOCATIONS: " );
			return new Point(-64,-64);
		}
		
		switch(playerNum)
		{
		case 0: return new Point(10,10);
		case 1: return new Point(10,10);
		case 2: return new Point(10,10);
		case 3: return new Point(10,10);
		}
		
		return null;
	}
	
	public GameMap getByType(String type)
	{
		Field[] fields = GameMap.class.getFields();
		int count = 0;
		for(Field f : fields)
		{
//			System.out.println(f.getName());
			//System.out.println(f.getType().toString());
			count++;
		}
		
		GameMap[] fieldSubSet = new GameMap[count];
		for(int i = 0; i < fields.length; i++)
		{
			//System.out.println(GameMap.class.getCanonicalName());
			//System.out.println(fields[i].getType().getCanonicalName().toString());
			if(GameMap.class.getCanonicalName().toString().equals(fields[i].getType().getCanonicalName().toString()));
				try {
					Field f = this.getClass().getField(fields[i].getName());
					fieldSubSet[--count] = (GameMap) f.get(this);
//					System.out.println( ((GameMap)f.get(this)).toString());
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
		}
		
		System.out.println(Arrays.toString(fieldSubSet));
		
		for(int i = 0; i < fieldSubSet.length; i++)
		{
//			System.out.println(type.getBackground());
//			System.out.println(fieldSubSet[i].imageName);
//			
			if(fieldSubSet[i].imageName.equals(type))
			{
//				System.out.println(fieldSubSet[i].imageName);
				return fieldSubSet[i];
			}
		}
		
		return null;
	}
	
	public static GameMap map1 = new GameMap(ImageFilePaths.MAP_1_BACKGROUND, new Point[]{new Point(10,10),new Point(10,10),new Point(10,10),new Point(10,10)}, LevelTileData.MAP_1_DATA)
			, map2= new GameMap(ImageFilePaths.MAP_2_BACKGROUND, new Point[]{new Point(),new Point(),new Point(),new Point()}, LevelTileData.MAP_1_DATA)
			, map3 = new GameMap(ImageFilePaths.MAP_3_BACKGROUND, new Point[]{new Point(),new Point(),new Point(),new Point()}, LevelTileData.MAP_1_DATA);
	
}
