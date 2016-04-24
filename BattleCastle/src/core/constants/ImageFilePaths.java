package core.constants;

import java.lang.reflect.Field;

public class ImageFilePaths {
	
	private static final String 
				MENU = "menu/",
				GAME = "game/",
				EDITOR = "editor/",
				ENTITY = GAME + "entity/",
				ITEM = GAME + "item/",
				MAP = GAME + "map/",
				MENU_OBJECT = MENU + "object/",
				TUTORIAL_OBJECT = MENU + "tutorial/",
				PLAYER = ENTITY + "player/",
				POWERUP = ITEM + "powerup/",
				WEAPON = ITEM + "weapon/",
				TILE = GAME + "tile/",
				MAP_1 = MAP + "1/",
				MAP_2 = MAP + "2/",
				MAP_3 = MAP + "3/";
	
	public static final String
				HOST_GAME = MENU_OBJECT + "host_game",
				HOST_GAME_SELECTED = MENU_OBJECT + "host_game_selected",
				JOIN_GAME = MENU_OBJECT + "join_game",
				JOIN_GAME_SELECTED = MENU_OBJECT + "join_game_selected", 
				INFO_BUTTON = MENU_OBJECT + "info",
				INFO_BUTTON_SELECTED = MENU_OBJECT + "info_selected",
				LEVEL_EDITOR = MENU_OBJECT + "level_editor",
				LEVEL_EDITOR_SELECTED = MENU_OBJECT + "level_editor_selected",
				CONNECT_TO_SERVER = MENU_OBJECT + "connect_to_server",
				CONNECT_TO_SERVER_SELECTED = MENU_OBJECT + "connect_to_server_selected",
				CONTINUE = MENU_OBJECT + "continue",
				CONTINUE_SELECTED = MENU_OBJECT + "continue_selected",
				BACK = MENU_OBJECT + "back",
				BACK_SELECTED = MENU_OBJECT + "bacK_selected",
				REFRESH = MENU_OBJECT + "refresh",
				REFRESH_SELECTED = MENU_OBJECT + "refresh_selected",
				TEXT_FIELD = MENU_OBJECT + "text_field",
				CURSOR = MENU_OBJECT + "cursor",
				VOLUME_LABEL = MENU_OBJECT + "volume_lbl",
				USER_NAME_LABEL = MENU_OBJECT + "user_name",
				SERVER_IP_LABEL = MENU_OBJECT + "server_ip",
				SERVER_SELECT_BOX = MENU_OBJECT + "server_select_box",
				SERVER_CHOICE = MENU_OBJECT + "server_choice",
				SLIDER_BAR  = MENU_OBJECT + "slider_bar",
				SLIDER = MENU_OBJECT + "slider",
				PLAYER_SELECT_LABEL = MENU_OBJECT + "player_select",
				TITLE = MENU + "title",
				MENU_BACKGROUND_IMAGE_0 = MENU + "background00",
				MENU_BACKGROUND_IMAGE_1 = MENU + "background01",
				MENU_BACKGROUND_IMAGE_2 = MENU + "background02",
				MAP_1_BACKGROUND = MAP_1 + "map1",
				MAP_2_BACKGROUND = MAP_2 + "map2",
				MAP_3_BACKGROUND = MAP_3 + "map3",
				LEFT_MOUSE_CLICK = TUTORIAL_OBJECT + "left_mouse_click",
				KEY_PRESS = TUTORIAL_OBJECT + "key_press",
				TEMP_PLAYER_ANIMATED = PLAYER + "player_sheet", //TODO : change
				TEMP_PLAYER = PLAYER + "temp_player", //TODO : Change
				TEMP_PLAYER_DEAD = PLAYER + "temp_player_dead", //TODO : change
				ARROW_RED = WEAPON + "red_arrow",
				ARROW_BLUE = WEAPON + "blue_arrow",
				ARROW_GREEN = WEAPON + "green_arrow",
				ARROW_YELLOW = WEAPON + "yellow_arrow",
				TRASH = EDITOR + "trash",
				CHECK = EDITOR + "check",
				SAVE = EDITOR + "save",
				INCWIDTH = EDITOR + "increment_width",
				DECWIDTH = EDITOR + "decrement_width",
				INCHEIGHT = EDITOR + "increment_height",
				DECHEIGHT = EDITOR + "decrement_height",
				TORCH = TILE + "torch",
				CHEST = TILE + "chest",
				GRAY_BRICK = TILE + "gray_brick",
				BROWN_BRICK = TILE + "brown_brick",
				STONE = TILE + "stone",
				WOOD = TILE + "wood",
				PRESSURE_PLATE_SPIKES = TILE + "pp_spikes",
				SPIKES = TILE + "spikes",
				MULTI_COLOR = TILE + "multi_color",
				DELETE_ALL = TILE + "delete_all",
				CLONE = EDITOR + "clone",
				STATE_ADDER = EDITOR + "state_adder",
				PLAYER_RED_FACE = MENU_OBJECT + "player_red_head",
				PLAYER_GREEN_FACE = MENU_OBJECT + "player_green_head",
				PLAYER_BLUE_FACE = MENU_OBJECT + "player_blue_head",
				PLAYER_YELLOW_FACE = MENU_OBJECT + "player_yellow_head",
				SKULL_DARK = PLAYER + "skeleton_head_dark",
				SKULL = PLAYER + "skeleton_head",
				SCORE_BANNER = PLAYER + "score_banner",
				CROWN = PLAYER + "crown";
	
	
	public static String getAbsolutePathFromImageName(String imageName)
	{
		try{
			Field[] fields = ImageFilePaths.class.getFields();
			for(Field f : fields)
			{
				String s = (String)(ImageFilePaths.class.getField(f.getName()).get(f.getName()));
				if(s.contains(imageName))
				{
					System.out.println("Found matching image: " + s );
					return s;
				}
			}
		}catch(Exception e) {}
		return "";
	}
				
		
}
