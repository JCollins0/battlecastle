package game.object;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import utility.Utility;
import core.constants.ImageFilePaths;
import core.constants.LevelTileData;
import editor.Tile;

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
			tiles = Utility.readLevelSaveFromString(tileData);
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
		return "";
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
	
	public static GameMap map1, map2, map3;
	
	static
	{
		map1 = new GameMap(ImageFilePaths.MAP_1_BACKGROUND, new Point[]{new Point(10,10),new Point(10,10),new Point(10,10),new Point(10,10)}, LevelTileData.MAP_1_DATA);
		map2 = new GameMap(ImageFilePaths.MAP_2_BACKGROUND, new Point[]{new Point(),new Point(),new Point(),new Point()}, LevelTileData.MAP_1_DATA);
		map3 = new GameMap(ImageFilePaths.MAP_3_BACKGROUND, new Point[]{new Point(),new Point(),new Point(),new Point()}, LevelTileData.MAP_1_DATA);
	}
}
