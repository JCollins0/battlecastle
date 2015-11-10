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
import game.Game;
import core.menu_object.*;

public class EditorCanvas extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6879897978771655344L;

	protected ArrayList<Tile> list,tools;
	private ArrayList<MenuTextField> bottomTextFields;
	private static BufferedImage buffer;
	private boolean running, bottom, grid;
	private GameState currentState;
	private Game game;
	private EditorMouseHandler mouseHandler;
	private EditorKeyHandler keyHandler;
	
	public EditorCanvas()
	{
		init();
	}
	
	public void init()
	{
		setPreferredSize(EditorFrame.EDITOR_SIZE);
		setBackground(Color.BLACK);
		buffer=new BufferedImage(EditorFrame.EDITOR_SIZE.width,EditorFrame.EDITOR_SIZE.height,BufferedImage.TYPE_INT_ARGB);
		
		running=true;
		bottom=true;
		grid=true;
		
		list=readSave();
		if(list==null)
			list=new ArrayList<Tile>();
		
		tools=new ArrayList<Tile>();
		Tile addNewTile=new Tile(32,768,32,32);
		//Tile saveTiles=new Tile();
		tools.add(addNewTile);
		
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
		if(bottom)
		{
			for(Tile t:tools)
				t.draw(b);
		}
		for(Tile t:list)
			t.draw(b);
		
		
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
		System.out.println(temp);
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
	
	public void activateTileEditor(Tile t)
	{
		
	}
	
	public void tick()
	{
		for(Tile t:list)
			t.tick();
		for(Tile t:tools)
			t.tick();
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
		default:break;
		}
	}
	
	protected void snapToGrid(Tile t)
	{
		int x=t.x;
		int y=t.y;
		if(x>992)
			x=992;
		else if(x<0)
			x=0;
		else if(x%32<16)
			x-=x%32;
		else
			x+=32-(x%32);
		if(y>736)
			y=736;
		else if(y<0)
			y=0;
		else if(y%32<16)
			y-=y%32;
		else
			y+=32-(y%32);
		t.setLocation(x, y);
	}
	
}
