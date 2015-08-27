package core;

import game.Game;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseMotionListener, MouseListener {

	private Game gameref;
	private static Point mouse;
	
	public MouseHandler(Game gameref){
		this.gameref = gameref;
		mouse = new Point(-1,-1);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch( gameref.getCanvas().getCurrentState())
		{
		case MAIN_MENU:
			
			
			break;
		case JOIN_SERVER:
			
			
			break;
		case GAMEPLAY:
			
			
			
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
	}

}
