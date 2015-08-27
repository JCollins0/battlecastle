package core;

import game.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import core.menu_object.MenuTextField;

public class KeyHandler implements KeyListener {

	private Game gameref;
	
	public KeyHandler(Game gameref)
	{
		this.gameref = gameref;
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(gameref.getCanvas().getCurrentState())
		{
		case MAIN_MENU:
			
			MenuTextField text = gameref.getCanvas().getMenuText();
			switch(e.getKeyCode())
			{
			case KeyEvent.VK_BACK_SPACE:
				text.backspace();
				break;
			case KeyEvent.VK_SPACE:
				text.space();
			default:
				text.addCharacter(e.getKeyChar());
			}
			break;
		case JOIN_SERVER:
			break;
		case GAMEPLAY:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
