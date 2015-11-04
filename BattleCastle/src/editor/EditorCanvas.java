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

	protected ArrayList<Tile> list;
	private ArrayList<MenuTextField> bottomTextFields;
	private static BufferedImage buffer;
	private boolean running, bottom, grid;
	private GameState currentState;
	private Game game;
	protected EditorPanel editPanel;
	private EditorMouseHandler mouseHandler;
	private EditorKeyHandler keyHandler;
	
	public EditorCanvas()
	{
		init();
	}
	
	public void init()
	{
		setPreferredSize(EditorFrame.GAME_SIZE);
		setBackground(Color.BLACK);
		buffer=new BufferedImage(EditorFrame.GAME_SIZE.width,EditorFrame.GAME_SIZE.height,BufferedImage.TYPE_INT_ARGB);
		
		running=true;
		bottom=false;
		grid=true;
		
		list=readSave();
		if(list==null)
			list=new ArrayList<Tile>();
		
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
		b.fillRect(0, 0, EditorFrame.GAME_SIZE.width, EditorFrame.GAME_SIZE.height);
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
			
		}
		for(Tile t:list)
			t.draw(b);
		
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(buffer, 0, 0, EditorFrame.GAME_SIZE.width,
				    EditorFrame.GAME_SIZE.height, null);
		
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
		{
			t.tick();
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
	
	/*public void checkToolClicked(EditorCanvas canvasref,Point mouse)
	{
		int i;
		for(i=0;i<tools.size()&&!tools.get(i).contains(mouse);i++);
		switch(i)
		{
		case 0:canvasref.list.add(new Tile(0,0,32,32));break;
		default:System.out.println("Function not added to index "+i+ "yet!");
		}
	}*/
	
}
