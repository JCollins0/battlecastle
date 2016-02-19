package shader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class Shader {
	
	protected BufferedImage image;
	private ShaderType shaderType;
	
	public Shader(ShaderType type, int w, int h)
	{
		image = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
	}
	
	public abstract void manipulatePixels();
	
	public void applyShader(Graphics g)
	{
		g.drawImage(image, 0, 0,image.getWidth(),image.getHeight(),null);
	}

	public ShaderType getShaderType() {
		return shaderType;
	}

	public void setShaderType(ShaderType shaderType) {
		this.shaderType = shaderType;
	}
	
	public enum ShaderType{
		
		LIGHT;
		
	}
}
