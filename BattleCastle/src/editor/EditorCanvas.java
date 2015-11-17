package editor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import core.GameState;
import core.GameTimer;
import core.constants.DataConstants;
import core.constants.ImageFilePaths;
import core.menu_object.MenuTextField;
import game.Game;

public class EditorCanvas extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6879897978771655344L;
	
	private static final int TOOLS_Y=768;
	private static final int BOTTOM_Y=768;

	protected ArrayList<Tile> list,tools,tileAdder,editor;
	private static BufferedImage buffer;
	private int tileAdderX,tileAdderY;
	private boolean running, bottom, grid,drawMouseLoc;
	private EditorMouseHandler mouseHandler;
	private EditorKeyHandler keyHandler;
	private Tile current,trashIndicator;
	
	public EditorCanvas()
	{
		init();
	}
	
	public int getNextX()
	{
		return tileAdderX+=32;
	}
	
	public int resetX()
	{
		return tileAdderX=0;
	}
	
	public int getNextY()
	{
		return tileAdderY+=32;
	}
	
	public void init()
	{
		setPreferredSize(EditorFrame.EDITOR_SIZE);
		setBackground(Color.BLACK);
		buffer=new BufferedImage(EditorFrame.EDITOR_SIZE.width,EditorFrame.EDITOR_SIZE.height,BufferedImage.TYPE_INT_ARGB);
		
//		trashIndicator=new Tile(0,BOTTOM_Y,1024,128,ImageFilePaths.TRASH,null);
		trashIndicator=new Tile(0,BOTTOM_Y,1024,128);
		
		running=true;
		bottom=true;
		grid=true;
		drawMouseLoc=true;
		
		list=readSave();
		if(list==null)
			list=new ArrayList<Tile>();
		tileAdderY=800;
		
		tools=new ArrayList<Tile>();
		Tile addNewTile=new Tile(resetX(),TOOLS_Y,32,32);
		Tile saveTile=new Tile(getNextX(),TOOLS_Y,32,32,ImageFilePaths.SAVE,null);
		Tile cloneTile=new Tile(getNextX(),TOOLS_Y,32,32);
		tools.add(addNewTile);
		tools.add(saveTile);
		tools.add(cloneTile);
		
		
		tileAdder=new ArrayList<Tile>();
		
		Tile addWoodTile=new Tile(resetX(),tileAdderY,32,32,ImageFilePaths.WOOD,null);
		Tile addBrickTile=new Tile(getNextX(),tileAdderY,32,32,ImageFilePaths.GRAY_BRICK,null);
//		Tile addStoneTile=new Tile(getNextX(),tileAdderY,32,32,ImageFilePaths.STONE,null);
		Tile addStoneTile=new Tile(getNextX(),tileAdderY,32,32);
		tileAdder.add(addWoodTile);
		tileAdder.add(addBrickTile);
		tileAdder.add(addStoneTile);
		
		Tile addChestTile=new Tile(resetX(),getNextY(),32,32,ImageFilePaths.CHEST,null);
		tileAdder.add(addChestTile);
		
		
		editor=new ArrayList<Tile>();
		Tile incrementWidth=new Tile(896,768,32,32,ImageFilePaths.INCWIDTH,null);
		Tile decrementWidth=new Tile(896,800,32,32,ImageFilePaths.DECWIDTH,null);
		Tile incrementHeight=new Tile(928,768,32,32,ImageFilePaths.INCHEIGHT,null);
		Tile decrementHeight=new Tile(928,800,32,32,ImageFilePaths.DECHEIGHT,null);
		editor.add(incrementWidth);
		editor.add(decrementWidth);
		editor.add(incrementHeight);
		editor.add(decrementHeight);
		
		mouseHandler = new EditorMouseHandler(this);
		keyHandler = new EditorKeyHandler(this);
		addMouseMotionListener(mouseHandler);
		addMouseListener(mouseHandler);
		addKeyListener(keyHandler);
	}
	
	public void render()
	{
		BufferStrategy bs = getBufferStrategy();
		
		if ( bs == null )
		{
			createBufferStrategy(3);
			return;
		}
		
		Graphics b = buffer.getGraphics();
		b.fillRect(0, 0, EditorFrame.EDITOR_SIZE.width, EditorFrame.EDITOR_SIZE.height);
		/*
		 * Draw to screen
		 */
		if(grid)
		{
			b.setColor(Color.BLACK);
			for(int j=1;j<32;j++)
			{
				if(j<=24)
				{
					b.drawLine(0, j<<5, 1024, j<<5);
				}
				b.drawLine(j<<5,0,j<<5,768);
			}
		}
		
		for(Tile t:list)
			t.draw(b);
		
		
			
		
		if(bottom)
		{
			
			b.setColor(Color.WHITE);
			b.fillRect(0, 768, 1024, 128);
			for(Tile t:tools)
				t.draw(b);
			for(Tile t:tileAdder)
				t.draw(b);
			if(current!=null)
			{
				for(Tile t:editor)
					t.draw(b);
				if(mouseHandler.dragging!=null&&mouseHandler.mouse.y>BOTTOM_Y)
					trashIndicator.draw(b);
			}
		}
		
		
			
		if(drawMouseLoc)
			b.drawString(mouseHandler.mouse.x + "," + mouseHandler.mouse.y, mouseHandler.mouse.x, mouseHandler.mouse.y);
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(buffer, 0, 0, EditorFrame.EDITOR_SIZE.width,
				    EditorFrame.EDITOR_SIZE.height, null);
		
		g.dispose();
		bs.show();
	}
	
	private ArrayList<Tile> readSave()
	{
		ArrayList<Tile> temp = new ArrayList<Tile>();
		Scanner reader=null;
		JSONParser parser=new JSONParser();
		try
		{
			reader=new Scanner(new FileInputStream(DataConstants.CURRENT_LEVEL));
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
	
	@SuppressWarnings("unchecked")
	public void save()
	{
		JSONArray temp=new JSONArray();
		temp.addAll(list);
//		System.out.println(temp);
		PrintWriter printer=null;
		try
		{
			printer=new PrintWriter(new FileOutputStream(DataConstants.CURRENT_LEVEL));
			temp.writeJSONString(printer);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(printer!=null)
				printer.close();
		}
	}
	
	public void selectTile(Tile t)
	{
		current=t;
	}
	
	public void deselectTile()
	{
		current=null;
	}
	
	public void tick()
	{
		for(Tile t:list)
			t.tick();
		if(bottom)
		{
			for(Tile t:tools)
				t.tick();
			for(Tile t:tileAdder)
				t.tick();
			trashIndicator.tick();
		}
	}
	
	public void run() 
	{
		
		GameTimer timer = new GameTimer();
		timer.restart();
		
		while(running)
		{
			if (timer.getElapsedTime() > 30)
			{
				timer.restart();
				tick();
			}
			
			render();
		}
	}
	
	protected void checkToolClicked(Point mouse)
	{
		int i;
		for(i=0;i<tools.size()&&!tools.get(i).contains(mouse);i++);
		switch(i)
		{
		case 0:list.add(new Tile(0,0,32,32));break;
		case 1:save();break;
		default:break;
		}
	}
	
	protected void checkTileAdderClicked(Point mouse)
	{
		int i;
		for(i=0;i<tileAdder.size()&&!tileAdder.get(i).contains(mouse);i++);
		if(i<tileAdder.size())
			
			list.add(tileAdder.get(i).copy());
	}
	
	protected void checkEditorClicked(Point mouse)
	{
		int i;
		for(i=0;i<editor.size()&&!editor.get(i).contains(mouse);i++);
		switch(i)
		{
		case 0:current.width+=32;break;
		case 1:if(current.width!=32)current.width-=32;break;
		case 2:current.height+=32;break;
		case 3:if(current.height!=32)current.height-=32;break;
		default:break;
		}
		if(current!=null)
			snapToGrid(current);
	}
	
	protected void snapToGrid(Tile t)
	{
		int x=t.x;
		int y=t.y;
		int width;
		int height;
		if(x>992)
			x=992;
		else if(x<32-(width=t.width))
		{
			x=32-width;
		}
		else if(x%32<16)
			x-=x%32;
		else
			x+=32-(x%32);
		if(y>736)
			y=736;
		else if(y<32-(height=t.height))
		{
			y=32-height;
		}
		else if(y%32<16)
			y-=y%32;
		else
			y+=32-(y%32);
		t.setLocation(x, y);
	}
	
}
