package game.object;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import core.constants.ImageFilePaths;
import utility.Utility;

public class GameMap {
	
	private BufferedImage background;
	private Point[] playerStartLocations;
	private String imageName;
	
	public GameMap()
	{
		
	}
	
	public GameMap(String imageName)
	{
		background = Utility.loadImage(imageName);
		this.imageName = imageName;
	}
	
	public GameMap(String imageName, Point[] playerLocations)
	{
		this(imageName);
		this.playerStartLocations = playerLocations;
	}
	
	public void render(Graphics g)
	{
		if(background != null)
			g.drawImage(background, 0, 0, null);
	}
	
	public void tick()
	{
		
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
	
	public void loadLevelData(String data)
	{
		//JOptionPane.showMessageDialog(null, data);
	}
	
	public Point getPlayerStartPoint(int playerNum) {
		if(playerStartLocations == null)
		{
			System.out.println("ADD SOME PLAYER LOCATIONS: " );
			return new Point(-64,-64);
		}
		
		switch(playerNum)
		{
		case 0: return new Point(32,32);
		case 1: return new Point(128,64);
		case 2: return new Point(256,32);
		case 3: return new Point(128,128);
		}
		
		return null;
	}
	
	public static GameMap map1, map2, map3;
	
	static
	{
		map1 = new GameMap(ImageFilePaths.MAP_1_BACKGROUND, new Point[]{new Point(64,64),new Point(256,64),new Point(480,64),new Point(640,64)});
		map2 = new GameMap(ImageFilePaths.MAP_2_BACKGROUND, new Point[]{new Point(),new Point(),new Point(),new Point()});
		map3 = new GameMap(ImageFilePaths.MAP_3_BACKGROUND, new Point[]{new Point(),new Point(),new Point(),new Point()});
	}
}
