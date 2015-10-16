package editor;

import java.awt.Image;

public class HorizTile extends Tile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4771092299942087280L;

	public HorizTile(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public HorizTile(int x, int y, int width, int height, Image[] pics, State[] states) {
		super(x, y, width, height, pics, states);
	}

	public HorizTile(int x, int y, int width, int height, Image pic, State[] states) {
		super(x, y, width, height, pic, states);
	}

}
