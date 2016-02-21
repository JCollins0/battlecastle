package shader;

public class ScreenShader extends Shader {

	public ScreenShader(ShaderType type, int w, int h) {
		super(type, w, h);
	}

	@Override
	public void manipulatePixels() {
		for(int y = 0; y < image.getHeight(); y+=4)
		{
			for(int x= 0; x < image.getWidth(); x++)
			{
				image.setRGB(x, y, 0x40000000);
			}
		}
	}

}
