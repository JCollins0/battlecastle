package editor;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class EditorMouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

	private EditorCanvas canvasref;
	private Point mouse;
	private int tx,ty;
	private Tile current;
	
	public EditorMouseHandler(EditorCanvas canvasref) {
		this.canvasref = canvasref;
		mouse = new Point(-1,-1);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		mouse=arg0.getPoint();
		//System.out.println(current);
		if(current!=null)
			current.setLocation(mouse.x-tx, mouse.y-ty);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouse=arg0.getPoint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		for(Tile t:canvasref.list)
		{
			if(t.contains(mouse)&&current==null)
			{
				current=t;
				tx=mouse.x-t.x;
				ty=mouse.y-t.y;
				canvasref.activateTileEditor(t);
			}
		}
		canvasref.checkToolClicked(mouse);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(current!=null)
		{
			if(mouse.y>=768)
				canvasref.list.remove(current);
			else
			{
				canvasref.snapToGrid(current);
				current=null;
			}
		}
	}

}
