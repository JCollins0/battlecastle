package core;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

import core.constants.ConfigConstants;
import core.constants.DataConstants;
import core.menu_object.MenuTextField;
import core.menu_object.MenuTextFieldType;
import game.Game;
import utility.Utility;

public class BattleCastleFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -412527797459057884L;

	public static final String GAME_NAME = "Battle Castle";
	public static final String GAME_VERSION = "1.0";
	public static final Dimension GAME_SIZE = new Dimension(1024,768);
	
	public static BattleCastleCanvas game_canvas;
	
	public static void main(String[] args) {
		new BattleCastleFrame();
	}
	
	public BattleCastleFrame()
	{
		super(String.format("%s v%s",GAME_NAME,GAME_VERSION));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(GAME_SIZE);
		setLocationRelativeTo(null);
		init();
		setResizable(false);
		setVisible(true);
		setFocusable(false);
		
		setIconImage(Utility.loadImage("icon"));
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				if(game_canvas != null)
				{
					Game game = game_canvas.getGame();
					if(game != null)
					{
						game.stopClient();
						game.stopServer();
					}
					
					//write new defaults
					ArrayList<MenuTextField> fields = game_canvas.getMenuTextFields();
					MenuTextField user = null, server = null;
					for(MenuTextField field : fields)
						if(field.getID() == MenuTextFieldType.SERVER_IP_FIELD)
							server = field;
						else if(field.getID() == MenuTextFieldType.USERNAME_FIELD)
							user = field;
					try
					{
						FileOutputStream stream = new FileOutputStream(DataConstants.USER_CONFIG);
						PrintWriter writer = new PrintWriter(stream);
						writer.println(String.format("%s:%s", ConfigConstants.LAST_SERVER, server.getText()));
						writer.println(String.format("%s:%s", ConfigConstants.USER, user.getText()));
						writer.close();
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
				
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				
			}
		});
		
		game_canvas.requestFocus();
		Thread gameThread = new Thread(game_canvas);
		gameThread.start();
	}
	
	private void init()
	{
		game_canvas = new BattleCastleCanvas();
		add(game_canvas);
	}

}
