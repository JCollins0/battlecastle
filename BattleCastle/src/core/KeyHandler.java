package core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import core.constants.Keys;
import core.menu_object.MenuTextField;
import game.player.Player;

public class KeyHandler implements KeyListener {

	private BattleCastleCanvas canvasref;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-YYYY-k-m-s"); 
	public static boolean[] arrow_keys_down;
	public static final int 
				UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	
	static
	{
		arrow_keys_down = new boolean[4];
	}
	
	public KeyHandler(BattleCastleCanvas canvasref)
	{
		this.canvasref = canvasref;
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode() == Keys.SCREENSHOT)
		{
			
			FileOutputStream f; 
			try {
				f = new FileOutputStream(dateFormat.format(new Date(System.nanoTime()))+".png");
				ImageIO.write(canvasref.getBuffer(), "png", f);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
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
			
			Player myPlayer = canvasref.getGame().getMyPlayer();
			
			switch(keyCode)
			{
			case Keys.UP:
				if(!arrow_keys_down[UP])
				{
					arrow_keys_down[UP] = true;
					canvasref.getGame().updateMyPlayer(KeyPress.JUMP_D);
				}
				
				break;
			case Keys.DOWN:
				if(!arrow_keys_down[DOWN])
				{
					arrow_keys_down[DOWN] = true;
					canvasref.getGame().updateMyPlayer(KeyPress.DOWN_D);
				}
				break;
			case Keys.LEFT:
				if(!arrow_keys_down[LEFT])
				{
					arrow_keys_down[LEFT] = true;
					canvasref.getGame().updateMyPlayer(KeyPress.LEFT_D);
				}
				break;
			case Keys.RIGHT:
				if(!arrow_keys_down[RIGHT])
				{	
					arrow_keys_down[RIGHT] = true;
					canvasref.getGame().updateMyPlayer(KeyPress.RIGHT_D);
				}
			}
			
			break;
		case SELECT_MAP:
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(canvasref.getCurrentState())
		{
		case GAMEPLAY:
			
			//handle player input
			int keyCode = e.getKeyCode();
			
			switch(keyCode)
			{
			case Keys.UP:
				if(!arrow_keys_down[UP])
				{
					arrow_keys_down[UP] = false;
					canvasref.getGame().updateMyPlayer(KeyPress.JUMP_U);
				}	
				break;
			case Keys.DOWN:
				if(!arrow_keys_down[DOWN])
				{
					arrow_keys_down[DOWN] = false;
					canvasref.getGame().updateMyPlayer(KeyPress.DOWN_U);
				}
				break;
			case Keys.LEFT:
				if(!arrow_keys_down[LEFT])
				{
					arrow_keys_down[LEFT] = false;
					canvasref.getGame().updateMyPlayer(KeyPress.LEFT_U);
				}
				break;
			case Keys.RIGHT:
				if(!arrow_keys_down[RIGHT])
				{
					arrow_keys_down[RIGHT] = false;
					canvasref.getGame().updateMyPlayer(KeyPress.RIGHT_U);
				}
			}
			
			break;
		case INPUT_USER_NAME:	
			break;
		case JOIN_SERVER:
			break;
		case MAIN_MENU:
			break;
		case SELECT_MAP:
			break;
		default:
			break;
		
		}
	}
}
