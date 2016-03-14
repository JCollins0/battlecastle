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
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import core.DoubleLinkedList;
import core.GameTimer;
import core.constants.DataConstants;
import core.constants.ImageFileDimensions;
import core.constants.ImageFilePaths;
import core.constants.LevelTileData;

public class EditorCanvas extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6879897978771655344L;
	
	private static final int TOOLS_Y=768;
	private static final int BOTTOM_Y=768;

	protected DoubleLinkedList<Tile> list;
	protected ArrayList<Tile> tools,tileAdder,editor;
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
		
		trashIndicator=new Tile(0,BOTTOM_Y,1024,128,ImageFilePaths.TRASH,ImageFileDimensions.TRASH.x, ImageFileDimensions.TRASH.y, null, true);
//		trashIndicator=new Tile(0,BOTTOM_Y,1024,128);
		
		running=true;
		bottom=true;
		grid=true;
		drawMouseLoc=true;
		
		list=readSave(LevelTileData.MAP_1_DATA);
		if(list==null)
			list=new DoubleLinkedList<Tile>();
		tileAdderY=800;
		
		tools=new ArrayList<Tile>();
		Tile addNewTile=new Tile(resetX(),TOOLS_Y,32,32);
		Tile saveTile=new Tile(getNextX(),TOOLS_Y,32,32,ImageFilePaths.SAVE,ImageFileDimensions.SAVE.x,ImageFileDimensions.SAVE.y,null,true);
		Tile deleteAllTile=new Tile(getNextX(),TOOLS_Y,32,32,ImageFilePaths.DELETE_ALL,ImageFileDimensions.DELETE_ALL.x,ImageFileDimensions.DELETE_ALL.y,null,true);
		Tile toggleStatesActive=new Tile(getNextX(),TOOLS_Y,32,32,ImageFilePaths.DELETE_ALL,ImageFileDimensions.DELETE_ALL.x,ImageFileDimensions.DELETE_ALL.y,null,true);
		tools.add(addNewTile);
		tools.add(saveTile);
		tools.add(deleteAllTile);
		tools.add(toggleStatesActive);
		
		tileAdder=new ArrayList<Tile>();
//		System.out.println(" Tile Adder " + tileAdderY);
//		System.exit(0);
		Tile addWoodTile=new Tile(resetX(),tileAdderY,32,32,ImageFilePaths.WOOD,ImageFileDimensions.WOOD.x,ImageFileDimensions.WOOD.y,null,true);
		Tile addBrickTile=new Tile(getNextX(),tileAdderY,32,32,ImageFilePaths.GRAY_BRICK,ImageFileDimensions.GRAY_BRICK.x,ImageFileDimensions.GRAY_BRICK.y,null,true);
		Tile addStoneTile=new Tile(getNextX(),tileAdderY,32,32,ImageFilePaths.STONE, ImageFileDimensions.STONE.x, ImageFileDimensions.STONE.y, null,true);
		Tile multiColor = new Tile(getNextX(), tileAdderY,32,32,ImageFilePaths.MULTI_COLOR,ImageFileDimensions.RAINBOW.x, ImageFileDimensions.RAINBOW.y,null,true);
		Tile brownBrick = new Tile(getNextX(), tileAdderY,32,32,ImageFilePaths.BROWN_BRICK,ImageFileDimensions.BROWN_BRICK.x, ImageFileDimensions.BROWN_BRICK.y,null,true);
		Tile grayBrick = new Tile(getNextX(), tileAdderY,32,32,ImageFilePaths.GRAY_BRICK,ImageFileDimensions.GRAY_BRICK.x, ImageFileDimensions.GRAY_BRICK.y,null,true);
		tileAdder.add(addWoodTile);
		tileAdder.add(addBrickTile);
		tileAdder.add(addStoneTile);
		tileAdder.add(multiColor);
		tileAdder.add(brownBrick);
		tileAdder.add(grayBrick);
		
		
		Tile addChestTile=new Tile(resetX(),getNextY(),32,32,ImageFilePaths.CHEST,ImageFileDimensions.CHEST.x,ImageFileDimensions.CHEST.y,null,true);
		tileAdder.add(addChestTile);
		
		
		editor=new ArrayList<Tile>();
		Tile incrementWidth=new Tile(896,768,32,32,ImageFilePaths.INCWIDTH,ImageFileDimensions.INCWIDTH.x,ImageFileDimensions.INCWIDTH.y,null,true,true,true);
		Tile decrementWidth=new Tile(896,800,32,32,ImageFilePaths.DECWIDTH,ImageFileDimensions.DECWIDTH.x,ImageFileDimensions.DECWIDTH.y,null,true,true,true);
		Tile incrementHeight=new Tile(928,768,32,32,ImageFilePaths.INCHEIGHT,ImageFileDimensions.INCHEIGHT.x,ImageFileDimensions.INCHEIGHT.y,null,true,true,true);
		Tile decrementHeight=new Tile(928,800,32,32,ImageFilePaths.DECHEIGHT,ImageFileDimensions.DECHEIGHT.x,ImageFileDimensions.DECHEIGHT.y,null,true,true,true);
		Tile cloneTile=new Tile(960,800,32,32,ImageFilePaths.CLONE,ImageFileDimensions.CLONE.x,ImageFileDimensions.CLONE.y,null,true,true,true);
		Tile stateAdder=new Tile(960,768,32,32,ImageFilePaths.CLONE,ImageFileDimensions.CLONE.x,ImageFileDimensions.CLONE.y,null,true,true,true);
		editor.add(incrementWidth);
		editor.add(decrementWidth);
		editor.add(incrementHeight);
		editor.add(decrementHeight);
		editor.add(cloneTile);
		editor.add(stateAdder);
		
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
		
		Iterator<Tile> backit=list.iteratorb();
		while(backit.hasNext())
		{
			backit.next().draw(b);
		}
		
		
			
		
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
	
	public static DoubleLinkedList<Tile> readSave(String filePath)
	{
		DoubleLinkedList<Tile> temp = new DoubleLinkedList<Tile>();
		Scanner reader=null;
		JSONParser parser=new JSONParser();
		try
		{
			reader=new Scanner(new FileInputStream(filePath));
			//System.out.println(reader.nextLine());
			if(reader.hasNext())
			{
				JSONArray ja = (JSONArray)parser.parse(reader.nextLine());
				for(int i = 0;i<ja.size();i++)
				{
					//System.out.println(ja);//something wrong
					temp.addFront(Tile.decodeJSON((JSONObject)ja.get(i)));
					//temp.peekFront().createStates();
					//System.out.println(Tile.decodeJSON((JSONObject)ja.get(i))+"    hi");
				}
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
		//temp.addAll(list);
		Iterator<Tile> iter=list.iteratorb();
		while(iter.hasNext())
		{
			Tile t=iter.next();
			snapToGrid(t);
			temp.add(t);
		}
//		System.out.println(temp);
		PrintWriter printer=null;
		try
		{
			printer=new PrintWriter(new FileOutputStream(LevelTileData.MAP_1_DATA));
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
		//System.out.println(t);
		current=t;
		list.linkToFront(t);
	}
	
	public void deselectTile()
	{
		current=null;
	}
	
	public void toggleAllStates()
	{
		if(list.peekFront().getStatesActive())
			for(Tile t:list)
				t.setStatesActive(false);
		else
			for(Tile t:list)
				t.setStatesActive(true);
	}
	
	public void tick()
	{
		int num=0;
		for(Tile t:list)
		{
			t.tick();
			System.out.println(t+" "+num++);
		}
		if(bottom)
		{
			for(Tile t:tools)
				t.tick();
			for(Tile t:tileAdder)
				t.tick();
			for(Tile t:editor)
			{
				t.tick();
				if(t.contains(mouseHandler.mouse))
					t.setMouseIsOver(true);
				else
					t.setMouseIsOver(false);
				//System.out.println(t+" - "+t.mouseIsOver+" - "+mouseHandler.mouse);
			}
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
		for(i=0;i<tools.size();i++)
			if(tools.get(i).contains(mouse))
				switch(i)
				{
				case 0:list.addFront(new Tile(0,0,32,32));break;
				case 1:save();break;
				case 2:list.clear();break;
				case 3:toggleAllStates();
				default:break;
				}
	}
	
	protected void checkTileAdderClicked(Point mouse)
	{
		int i;
		for(i=0;i<tileAdder.size()&&!tileAdder.get(i).contains(mouse);i++);
		if(i<tileAdder.size())
			list.addFront(tileAdder.get(i).copy());
	}
	
	protected void checkEditorClicked(Point mouse)
	{
		int i;
		for(i=0;i<editor.size();i++)
			if(editor.get(i).contains(mouse))
				switch(i)
				{
				case 0:
					current.setWidth(current.getWidth()+32);
					break;
				case 1:
					if(current.getWidth()!=32)
						current.setWidth(current.getWidth()-32);
					break;
				case 2:
					current.setHeight(current.getHeight()+32);
					break;
				case 3:
					if(current.getHeight()!=32)
						current.setHeight(current.getHeight()-32);
					break;
				case 4:if(current!=null)
						list.addFront(current.copy());
					break;
				case 5:if(current!=null)
					{
						current.removeLastState();
						current.addState(new State(1,1));
					}
					break;
				default:break;
				}
		if(current!=null)
			snapToGrid(current);
	}
	
	protected void snapToGrid(Tile t)
	{
		int x=t.getX();
		int y=t.getY();
		int width;
		int height;
		if(x>992)
			x=992;
		else if(x<32-(width=t.getWidth()))
		{
			x=32-width;
		}
		else if(x%32<16)
			x-=x%32;
		else
			x+=32-(x%32);
		if(y>736)
			y=736;
		else if(y<32-(height=t.getHeight()))
		{
			y=32-height;
		}
		else if(y%32<16)
			y-=y%32;
		else
			y+=32-(y%32);
		t.moveTo(x, y);
	}
	
}
