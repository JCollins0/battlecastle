package core.menu_object;

import game.object.MapType;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import utility.Utility;
import core.GameState;

public class MapSelectionObject extends MenuButton {
	
	private MapType map;
	private BufferedImage mapPreview;
	private static final Color hoverColor = new Color(100,100,100,128);
	private String mapName, fileName;
	
	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			MapType mapType, GameState visibleState) {
		super(x, y, width, height, buttonType, visibleState);
		map = mapType;
		mapPreview = Utility.loadImage(mapType.getBackground());
	}

	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			MapType mapType, GameState[] visibleStatesList) {
		super(x, y, width, height, buttonType, visibleStatesList);
		map = mapType;
		mapPreview = Utility.loadImage(mapType.getBackground());
	}
	
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

	public String getMapName() {
		return mapName;
	}

	public MapType getMapType(){
		return map;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
