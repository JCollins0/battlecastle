package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import core.BattleCastleCanvas;
import core.constants.ConfigConstants;
import core.constants.DataConstants;
import core.constants.Keys;

public class ConfigLoader {

	private static final String[] generalHeading =
				{ 
					"##############################",
					"#   GENERAL CONFIG OPTIONS   #",
					"##############################"
				};
	
	private static final String[] keyBindingHeading =
				{
					"##############################################",
					"#######         KEY BINDINGS         #########",
					"##  **Uppercase Letters Only!**             ##",
					"##  *Special Keys (Key = Config_Value)      ##",
					"##    DOWN_ARROW = DOWN, LEFT_ARROW = LEFT, ##",
					"##    RIGHT_ARROW = RIGHT, UP_ARROW = UP,   ##",
					"##    SPACE_KEY = SPACE                     ##",
					"##############################################"
				};
	
	private static final String[] sliderHeading = 
				{
					"##############################",
					"#   Volume range (0-100)     #",
					"##############################"
				};
	
	
	private BattleCastleCanvas canvasref;
	
	public ConfigLoader(BattleCastleCanvas canvasref) {
		this.canvasref = canvasref;
	}

	public void loadConfig()
	{
		try
		{
			File f = new File(DataConstants.USER_CONFIG);
			if(!f.exists())
			{
				f.createNewFile();
				throw new FileNotFoundException();
			}
			FileInputStream stream = new FileInputStream(f);
			Scanner reader = new Scanner(stream);
			while(reader.hasNextLine())
			{
				String[] keyVal = reader.nextLine().split(":");
//				System.out.println(Arrays.toString(keyVal));
				switch(keyVal[0])
				{
				case ConfigConstants.LAST_SERVER :
					if(keyVal.length > 1)
						canvasref.getServerIpField().setText(keyVal[1].trim());
					break;
				case ConfigConstants.USER:
					if(keyVal.length > 1)
						canvasref.getUserNameField().setText(keyVal[1].trim());
					break;
				case ConfigConstants.DEBUG:
					if(keyVal.length > 1)
						canvasref.debug = Boolean.parseBoolean(keyVal[1].trim());
					break;
				case ConfigConstants.JUMP_KEY:
					if(keyVal.length > 1)
						Keys.UP = Keys.getKeyFromConfigString(keyVal[1].trim());
					break;
				case ConfigConstants.LEFT_KEY:
					if(keyVal.length > 1)
						Keys.LEFT = Keys.getKeyFromConfigString(keyVal[1].trim());
					break;
				case ConfigConstants.RIGHT_KEY:
					if(keyVal.length > 1)
						Keys.RIGHT = Keys.getKeyFromConfigString(keyVal[1].trim());
					break;
				case ConfigConstants.DOWN_KEY:
					if(keyVal.length > 1)
						Keys.DOWN = Keys.getKeyFromConfigString(keyVal[1].trim());
				case ConfigConstants.DASH_KEY:
					if(keyVal.length > 1)
						Keys.DASH = Keys.getKeyFromConfigString(keyVal[1].trim());
					break;
				case ConfigConstants.VOLUME_LEVEL:
					if(keyVal.length > 1)
						canvasref.getVolumeSlider().setValue(Integer.parseInt(keyVal[1].trim()));
					break;
					
				}
			}
			reader.close();
			stream.close();
			
		}catch(Exception e)
		{
			try {
				FileOutputStream stream = new FileOutputStream(DataConstants.USER_CONFIG);
				PrintWriter writer = new PrintWriter(stream);
				
				for(int i = 0; i < generalHeading.length; i++)
					writer.println(generalHeading[i]);
				writer.println(ConfigConstants.LAST_SERVER + ":");
				writer.println(ConfigConstants.USER + ":");
				writer.println(ConfigConstants.DEBUG + ":");
				
				for(int i = 0; i < keyBindingHeading.length; i++)
					writer.println(keyBindingHeading[i]);
				writer.println(ConfigConstants.LEFT_KEY + ":");
				writer.println(ConfigConstants.JUMP_KEY + ":");
				writer.println(ConfigConstants.RIGHT_KEY + ":");
				writer.println(ConfigConstants.DOWN_KEY + ":");
				writer.println(ConfigConstants.DASH_KEY + ":");
				
				for(int i = 0; i < sliderHeading.length; i++)
					writer.println(sliderHeading[i]);
				writer.println(ConfigConstants.VOLUME_LEVEL + ":");
				
				writer.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void saveConfig()
	{
		try
		{
			FileOutputStream stream = new FileOutputStream(DataConstants.USER_CONFIG);
			PrintWriter writer = new PrintWriter(stream);
			
			for(int i = 0; i < generalHeading.length; i++)
				writer.println(generalHeading[i]);
			writer.println(String.format("%s:%s", ConfigConstants.LAST_SERVER, canvasref.getServerIpField().getText()));
			writer.println(String.format("%s:%s", ConfigConstants.USER, canvasref.getUserNameField().getText()));
			writer.println(String.format("%s:%s", ConfigConstants.DEBUG, String.valueOf(canvasref.debug)));
			
			for(int i = 0; i < keyBindingHeading.length; i++)
				writer.println(keyBindingHeading[i]);				
			writer.println(String.format("%s:%s", ConfigConstants.LEFT_KEY,  Keys.getKeyConfigString(Keys.LEFT)));
			writer.println(String.format("%s:%s", ConfigConstants.JUMP_KEY,  Keys.getKeyConfigString(Keys.UP)));
			writer.println(String.format("%s:%s", ConfigConstants.RIGHT_KEY, Keys.getKeyConfigString(Keys.RIGHT)));
			writer.println(String.format("%s:%s", ConfigConstants.DOWN_KEY,  Keys.getKeyConfigString(Keys.DOWN)));
			writer.println(String.format("%s:%s", ConfigConstants.DASH_KEY,  Keys.getKeyConfigString(Keys.DASH)));
			
			for(int i = 0; i < sliderHeading.length; i++)
				writer.println(sliderHeading[i]);
			writer.println(String.format("%s:%d", ConfigConstants.VOLUME_LEVEL, canvasref.getVolumeSlider().getValue()));
			
			writer.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
