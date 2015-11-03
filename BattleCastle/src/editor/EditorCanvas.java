package editor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import core.constants.*;
import core.GameState;
import core.GameTimer;
import game.Game;

public class EditorCanvas extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6879897978771655344L;

	protected ArrayList<Tile> list;
	private ArrayList<Tile> examples;
	public Tile addTile,saveTile,editTile;
	private static BufferedImage buffer;
	private boolean running, bottom, grid;
	private GameState currentState;
	private Game game;
	private EditorMouseHandler mouseHandler;
	private EditorKeyHandler keyHandler;
	
	private JTextField tileX,tileY,tileWidth,tileHeight;
	
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
		bottom=true;
		grid=true;
		
		list=readSave();
		if(list==null)
			list=new ArrayList<Tile>();
		
		examples=new ArrayList<Tile>();
		addTile=new Tile(32,784,32,32);
		saveTile=new Tile(128,128,128,128);
		examples.add(addTile);
		examples.add(saveTile);
		
		tileX=new JTextField(2);
		tileY=new JTextField(2);
		tileWidth=new JTextField(2);
		tileHeight=new JTextField(2);
		
		
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
			for(Tile t:examples)
				t.draw(b);
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
	
	protected void activateTileEditor(Tile t)
	{
		editTile=t;
		
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
	
}
