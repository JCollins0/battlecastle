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

public class KeyHandler implements KeyListener {

	//Canvas reference
	private BattleCastleCanvas canvasref;
	
	//Date Stamp Format
	private static SimpleDateFormat dateFormat;
	
	//Client Key Presses
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
			
			if(keyCode == Keys.UP)
				add(KeyPress.JUMP_D);
			if(keyCode == Keys.DOWN)
				add(KeyPress.DOWN_D);
			
			if(keyCode == Keys.LEFT)
			{
				remove(KeyPress.RIGHT_U);
				remove(KeyPress.LEFT_U);
				add(KeyPress.LEFT_D);
			}
			if(keyCode == Keys.RIGHT)
			{	
				remove(KeyPress.LEFT_U);
				remove(KeyPress.RIGHT_U);
				add(KeyPress.RIGHT_D);
			}
			if(keyCode == Keys.DASH_L)
			{
				add(KeyPress.DASH_L);
			}
			if(keyCode == Keys.DASH_R)
			{
				add(KeyPress.DASH_R);
			}
			if(keyCode == KeyEvent.VK_R) // reset TODO: assign a (Keys) variable
				if(canvasref.getGame() != null)
					canvasref.getGame().reset();
			if(keyCode == KeyEvent.VK_B)
				canvasref.getGame().getMyPlayer().debugMe(); //TODO: temporary for testing remove this later
			
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
			
			if(keyCode == Keys.UP)
				remove(KeyPress.JUMP_D);
			if(keyCode == Keys.DOWN)
				remove(KeyPress.DOWN_D);
			if(keyCode == Keys.LEFT)
			{		
				remove(KeyPress.LEFT_D);
				add(KeyPress.LEFT_U);
			}
			if(keyCode == Keys.RIGHT)
			{	
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
