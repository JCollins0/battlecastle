package core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import core.constants.Keys;
import core.menu_object.MenuTextField;
import game.player.Player;

public class KeyHandler implements KeyListener {

	private BattleCastleCanvas canvasref;
	private static SimpleDateFormat dateFormat;
	public static ArrayList<KeyPress> presses;
	
	static
	{
		 presses = new ArrayList<KeyPress>(); 
		 dateFormat = new SimpleDateFormat("MM-dd-YYYY-k-m-s"); 
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
			//System.out.println(keyCode);
			
			Player myPlayer = canvasref.getGame().getMyPlayer();
			
			switch(keyCode)
			{
			case Keys.UP:
				add(KeyPress.JUMP_D);
				//canvasref.getGame().updateMyPlayer(KeyPress.JUMP_D);
				break;
			case Keys.DOWN:
				add(KeyPress.DOWN_D);
				//	canvasref.getGame().updateMyPlayer(KeyPress.DOWN_D);
				break;
			case Keys.LEFT:
				remove(KeyPress.RIGHT_U);
				remove(KeyPress.LEFT_U);
				add(KeyPress.LEFT_D);
				//canvasref.getGame().updateMyPlayer(KeyPress.LEFT_D);
				break;
			case Keys.RIGHT:
				remove(KeyPress.LEFT_U);
				remove(KeyPress.RIGHT_U);
				add(KeyPress.RIGHT_D);
				//canvasref.getGame().updateMyPlayer(KeyPress.RIGHT_D);
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
				remove(KeyPress.JUMP_D);
				break;
			case Keys.DOWN:
				remove(KeyPress.DOWN_D);
				break;
			case Keys.LEFT:
				remove(KeyPress.LEFT_D);
				add(KeyPress.LEFT_U);
				break;
			case Keys.RIGHT:
				remove(KeyPress.RIGHT_D);
				add(KeyPress.RIGHT_U);
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
	
	public void add(KeyPress press)
	{
		if(presses.contains(press))
			presses.remove(press);
		presses.add(0, press);
	}
	
	public void remove(KeyPress press)
	{
		presses.remove(press);
	}	
}
