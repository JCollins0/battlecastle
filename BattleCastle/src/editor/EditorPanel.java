package editor;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class EditorPanel extends JPanel
{
	
	private ArrayList<Tile> tools;
	public Tile addTile,saveTile,editTile;
	
	private JTextField tileX,tileY,tileWidth,tileHeight;
	
	public EditorPanel()
	{
		init();
	}
	
	private void init()
	{
		setSize(new Dimension(EditorFrame.GAME_SIZE.width, 128));
		
		tileX=new JTextField(2);
		tileY=new JTextField(2);
		tileWidth=new JTextField(2);
		tileHeight=new JTextField(2);
		
		tools=new ArrayList<Tile>();
		addTile=new Tile(32,784,32,32);
		saveTile=new Tile(128,128,128,128);
		tools.add(addTile);
		tools.add(saveTile);
		
	}
	
	
	
}
