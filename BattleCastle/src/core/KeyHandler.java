package core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import core.constants.Keys;
import core.menu_object.MenuTextField;

public class KeyHandler implements KeyListener {

	private BattleCastleCanvas canvasref;
	
	public KeyHandler(BattleCastleCanvas canvasref)
	{
		this.canvasref = canvasref;
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(canvasref.getCurrentState())
		{
		case MAIN_MENU:	
			break;
		case JOIN_SERVER:
		case INPUT_USER_NAME:
			
			MenuTextField selected = canvasref.getSelectedMenuTextField();
			
			if (selected != null)
			{
				switch(e.getKeyCode())
				{
				case KeyEvent.VK_BACK_SPACE:
					selected.backspace();
					break;
				case KeyEvent.VK_SPACE:
					selected.space();
				default:
					selected.addCharacter(e.getKeyChar());
				}
			}
			
			break;
		case GAMEPLAY:
			
			//handle player input
			int keyCode = e.getKeyCode();
			
			switch(keyCode)
			{
			case Keys.UP:
				break;
			case Keys.DOWN:
				break;
			case Keys.LEFT:
				break;
			case Keys.RIGHT:
			}
			
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
