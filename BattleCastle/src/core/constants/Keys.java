package core.constants;

import java.awt.event.KeyEvent;

public class Keys {

	public static int UP = KeyEvent.VK_W,
					  DOWN = KeyEvent.VK_S,
					  LEFT = KeyEvent.VK_A,
					  RIGHT = KeyEvent.VK_D,
					  DASH = KeyEvent.VK_F;
							
	public static final int SCREENSHOT = KeyEvent.VK_F2;
	
	/**
	 * Used to get a numeric value from a string representation
	 * @param configString the string from the config
	 * @return the int value
	 */
	public static int getKeyFromConfigString(String configString)
	{
		switch(configString)
		{
		case "LEFT": return KeyEvent.VK_LEFT;
		case "UP": return KeyEvent.VK_UP;
		case "RIGHT": return KeyEvent.VK_RIGHT;
		case "DOWN": return KeyEvent.VK_DOWN;
		case "SPACE": return KeyEvent.VK_SPACE;
		}
		
		char c = Character.toUpperCase(configString.charAt(0));
		if( c >= 'A' && c <= 'Z')
			return c;
		
		return -1;
	}
	
	/**
	 * Used to write string to file
	 * @param keyCode the value for keybinding 
	 * @return string representation of keybinding
	 */
	public static String getKeyConfigString(int keyCode)
	{
		switch(keyCode)
		{
		case KeyEvent.VK_LEFT: return "LEFT";
		case KeyEvent.VK_UP: return "UP";
		case KeyEvent.VK_RIGHT: return "RIGHT";
		case KeyEvent.VK_DOWN: return "DOWN";
		case KeyEvent.VK_SPACE: return "SPACE";
		}
		
		if(keyCode >= 65 && keyCode <= 90)
			return (char)keyCode+"";
		return "";
	}
}
