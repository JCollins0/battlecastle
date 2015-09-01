package core;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import core.menu_object.MenuButton;
import core.menu_object.MenuButtonType;
import core.menu_object.MenuTextField;
import core.menu_object.MenuTextFieldType;

public class MouseHandler implements MouseMotionListener, MouseListener {

	private BattleCastleCanvas canvasref;
	private static Point mouse;
	
	public MouseHandler(BattleCastleCanvas canvasref){
		this.canvasref = canvasref;
		mouse = new Point(-1,-1);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch( canvasref.getCurrentState())
		{
		case MAIN_MENU:
			
			ArrayList<MenuButton> buttonList = canvasref.getMenuButtons();
			
			for(MenuButton button : buttonList)
			{
				if (button.getBounds().contains(mouse))
				{
					if (button.getButtonType() == MenuButtonType.HOST_GAME)
					{
						canvasref.setGame(true);
						
						canvasref.setCurrentState(GameState.GAMEPLAY);
						
					}else if(button.getButtonType() == MenuButtonType.JOIN_GAME)
					{
						canvasref.setGame(false);
						
						canvasref.setCurrentState(GameState.JOIN_SERVER);
					}
					
				}
			}
			
			break;
		case JOIN_SERVER:
			

			ArrayList<MenuTextField> menuTextFieldList = canvasref.getMenuTextFields();
			for (MenuTextField field : menuTextFieldList)
			{
				if (field.getBounds().contains(mouse))
				{
					field.setSelected(true);
				}else
					field.setSelected(false);
			}
			
			ArrayList<MenuButton> buttonList1 = canvasref.getMenuButtons();
			
			for(MenuButton button : buttonList1)
			{
				if (button.getBounds().contains(mouse))
				{
					if (button.getButtonType() == MenuButtonType.CONNECT_TO_IP)
					{
						canvasref.setCurrentState(GameState.GAMEPLAY);
						MenuTextField field = canvasref.getTextFieldByID(MenuTextFieldType.SERVER_IP_FIELD);
						canvasref.getGame().setServerIP(field.getText());
						canvasref.getGame().sendPacketToServer(null);
					}
				}
			}
			
			break;
		case GAMEPLAY:
			
			//handle player mouse clicks
			
			
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
	}
	
}
