package editor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.simple.JSONValue;

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
	public Tile addTile,saveTile;
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
		setPreferredSize(EditorFrame.GAME_SIZE);
		setBackground(Color.BLACK);
		buffer=new BufferedImage(EditorFrame.GAME_SIZE.width,EditorFrame.GAME_SIZE.height,BufferedImage.TYPE_INT_ARGB);
		
		running=true;
		bottom=true;
		grid=true;
		
		list=new ArrayList<Tile>();
		
		examples=new ArrayList<Tile>();
		addTile=new Tile(32,784,32,32);
		saveTile=new Tile(128,128,128,128);
		examples.add(addTile);
		examples.add(saveTile);
		
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
		return null;
	}
	
	public void save()
	{
		String listData=new JSONValue().toJSONString(list);
		PrintWriter printer=null;
		try
		{
			printer=new PrintWriter(new File(DataConstants.CURRENT_LEVEL));
			printer.write(listData);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(printer!=null)
				printer.close();
		}
	}
	
	public void tick()
	{
		
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
