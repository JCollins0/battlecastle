package editor;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

public class EditorMainPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3306958127246278193L;
	
	public static EditorCanvas editor_canvas;
	public static BottomComponentPanel editor_bottom;
	public static EditorMouseHandler editor_mouse;
	public static ArrayList<Rectangle> comps;

	public EditorMainPanel() {
		init();
		add(editor_canvas,BorderLayout.NORTH);
		add(editor_bottom,BorderLayout.SOUTH);
	}
	
	public void init(){
		editor_canvas=new EditorCanvas();
		editor_bottom=new BottomComponentPanel();
		editor_mouse=new EditorMouseHandler(this);
		comps=new ArrayList<Rectangle>();
	}

}
