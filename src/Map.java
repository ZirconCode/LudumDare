import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage; //tmp
import java.awt.image.PixelGrabber;


public class Map {

	
	public BufferedImage img;
	
	public double xLoc;
	
	public static int xSize = 800;
	public static int ySize = 600;
	
	
	
	public Map(Image pic, Applet a)
	{
		BufferedImage tmp = new BufferedImage(pic.getWidth(a), pic.getHeight(a), BufferedImage.TYPE_INT_RGB);
		tmp.getGraphics().drawImage(pic, 0, 0, a);
		
		img = tmp;
	}
	
	
	public void draw(Graphics g, Applet a)
	{
		
		g.drawImage(img, (int) -xLoc, 0, a);
	}
	
	public void advanceX(double d)
	{
		xLoc += d;
	}
	
	public Color getPixel(int x, int y)
	{
		return new Color(img.getRGB(x+(int)xLoc, y));
	}
	
}
