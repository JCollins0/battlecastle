package editor;

import game.physics.Vector;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class EditorMouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

	private EditorCanvas canvasref;
	protected Point mouse;
	private int tx,ty;
	protected Tile dragging;
	
	public EditorMouseHandler(EditorCanvas canvasref) {
		this.canvasref = canvasref;
		mouse = new Point(-1,-1);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		mouse=arg0.getPoint();
		//System.out.println(current);
		if(dragging!=null)
//			dragging.setLocation(mouse.x-tx, mouse.y-ty);
			dragging.moveTo(mouse.x-tx,mouse.y-ty);
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
			if(768>mouse.getY()&&t.contains(mouse)&&dragging==null)
			{
				dragging=t;
				t.setStatesActive(false);
				tx=mouse.x-t.getX();
				ty=mouse.y-t.getY();
				canvasref.selectTile(t);
			}
		}
		canvasref.checkToolClicked(mouse);
		canvasref.checkEditorClicked(mouse);
		canvasref.checkTileAdderClicked(mouse);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(dragging!=null)
		{
			if(mouse.y>=768)
			{
				canvasref.list.removeFront();
				canvasref.deselectTile();
			}
			else
			{
				canvasref.snapToGrid(dragging);
			}
			dragging.setStatesActive(true);
			dragging=null;
		}
	}

}
