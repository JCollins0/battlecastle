package editor;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;

public class EditorFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2960012304302088561L;

	public static final String EDITOR_NAME = "Battle Castle Level Editor";
	public static final String EDITOR_VERSION = "1.0";
	public static final Dimension EDITOR_SIZE = new Dimension(1024,896);
	
	protected ArrayList<Tile> tiles=new ArrayList<Tile>();
	
	public static EditorCanvas editor_canvas;
	
	
	
	public static void main(String[] args) {
		new EditorFrame();
	}
	
	public EditorFrame()
	{
		super(String.format("%s v%s",EDITOR_NAME,EDITOR_VERSION));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//setSize(GAME_SIZE);
		setLocationRelativeTo(null);
		init();
		setResizable(false);
		setVisible(true);
		setFocusable(false);
		editor_canvas.requestFocus();
		Thread gameThread = new Thread(editor_canvas);
		gameThread.start();
		pack();
	}
	
	public void init(){
		editor_canvas=new EditorCanvas();
		add(editor_canvas);
	}

}
