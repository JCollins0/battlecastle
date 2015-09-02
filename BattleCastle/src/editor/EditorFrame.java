package editor;

import java.awt.Dimension;

import javax.swing.JFrame;

public class EditorFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2960012304302088561L;

	public static final String GAME_NAME = "Battle Castle Level Editor";
	public static final String GAME_VERSION = "1.0";
	public static final Dimension GAME_SIZE = new Dimension(1024,1024);
	
	public static EditorMainPanel editor_main;
	
	public static void main(String[] args) {
		new EditorFrame();
	}
	
	public EditorFrame()
	{
		super(String.format("%s v%s",GAME_NAME,GAME_VERSION));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(GAME_SIZE);
		setLocationRelativeTo(null);
		init();
		setResizable(false);
		setVisible(true);
		setFocusable(false);
		editor_main.requestFocus();
	}
	
	public void init(){
		editor_main=new EditorMainPanel();
		add(editor_main);
	}

}
