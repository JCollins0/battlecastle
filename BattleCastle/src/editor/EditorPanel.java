package editor;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;

public class EditorPanel extends JPanel
{
	
	private ArrayList<Tile> tools;
	
	public EditorPanel(EditorCanvas canvasref)
	{
		init();
	}
	
	private void init()
	{
		setSize(new Dimension(EditorFrame.GAME_SIZE.width, 128));
		
		tools=new ArrayList<Tile>();
		
	}
	
	public void checkToolClicked(EditorCanvas canvasref,Point mouse)
	{
		int i;
		for(i=0;i<tools.size()&&!tools.get(i).contains(mouse);i++);
		switch(i)
		{
		case 0:canvasref.list.add(new Tile(0,0,32,32));break;
		}
	}
	
}
