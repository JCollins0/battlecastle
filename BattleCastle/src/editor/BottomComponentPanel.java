package editor;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public class BottomComponentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2866630970878104110L;
	
	private final Dimension BCP_SIZE=new Dimension(1024,256);

	public BottomComponentPanel() {
		init();
	}
	
	public void init(){
		setPreferredSize(BCP_SIZE);
		setBackground(Color.RED);
	}

}
