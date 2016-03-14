package core.menu_object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import core.GameState;
import game.object.MapType;
import utility.Utility;

public class MapSelectionObject extends MenuButton {
	
	private MapType map;
	private BufferedImage mapPreview;
	private static final Color hoverColor = new Color(100,100,100,128);
	private String mapName, fileName;
	
	/**
	 * Construct Map Selection Object
	 * @param x x position
	 * @param y y position
	 * @param width with
	 * @param height height
	 * @param buttonType type of button
	 * @param mapType type of map
	 * @param visibleState state where button is visible
	 */
	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			MapType mapType, GameState visibleState) {
		super(x, y, width, height, buttonType, visibleState);
		map = mapType;
		mapPreview = Utility.loadImage(mapType.getBackground());
	}

	/**
	 * Construct Map Selection Object
	 * @param x x position
	 * @param y y position
	 * @param width with
	 * @param height height
	 * @param buttonType type of button
	 * @param mapType type of map
	 * @param visibleStatesList states where button is visible
	 */
	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			MapType mapType, GameState[] visibleStatesList) {
		super(x, y, width, height, buttonType, visibleStatesList);
		map = mapType;
		mapPreview = Utility.loadImage(mapType.getBackground());
	}
	
	/**
	 * Construct Map Selection Object
	 * @param x x position
	 * @param y y position
	 * @param width with
	 * @param height height
	 * @param buttonType type of button
	 * @param mapType type of map
	 * @param fileName Name of level data file
	 * @param imageName name of background image file
	 * @param visibleStatesList states where button is visible
	 */
	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			MapType mapType, String fileName, String imageName, GameState... visibleStatesList)
	{
		super(x, y, width, height, buttonType, visibleStatesList);
		this.mapName = imageName;
		this.fileName = fileName;
		this.map = mapType;
		try {
			mapPreview = ImageIO.read(new FileInputStream(new File(imageName)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Draw the Map Selection object
	 */
	public void render(Graphics g)
	{
		if(mapPreview != null)
		{
			g.drawImage(mapPreview, bounds.x,bounds.y,bounds.width,bounds.height,null);
			if(hoverOver)
			{
				g.setColor(hoverColor);
				g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
			}
			g.setColor(Color.black);
			g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}else
		{
			g.setColor(Color.red);
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		
	}

	/**
	 * get the name of the map
	 * @return mapName
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * Get type MapType 
	 * @return mapType
	 */
	public MapType getMapType(){
		return map;
	}

	/**
	 * Get the fileName for level data
	 * @return fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the fileName for level data
	 * @param fileName the fileName for level data
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
