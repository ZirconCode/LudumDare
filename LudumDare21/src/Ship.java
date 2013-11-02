import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Vector;


public class Ship {

	
	public Image img;
	//public Image rainbowShield;
	public Image explosion;
	
	public Map map;
	
	public Color color;
	public double life;
	public boolean dead;
	public boolean mainShip; // true = ship1
	
	public double xLife,yLife;
	
	public double xLoc, yLoc;
	public double xVel, yVel; // yeah?
	
	public static double friction = 0.4;
	public static double VelocityModifier = 0.8; //6.0; noSlide true
	public static double maxSpeed = 10;
	
	public static boolean noSlide = false;
	
	
	public Ship(Image pic, Image exp, Map m)
	{
		img = pic;
		//rainbowShield = rain;
		explosion = exp;
		
		map = m;
		
		life = 100;
		
		//temporary
		//color = new Color(255,234,20); 
		
		color = map.getPixel((int)xLoc+25, (int)yLoc+25);
	}
	
	
	public void draw(Graphics g, Applet a)
	{
		g.drawImage(img,(int)xLoc, (int)yLoc, a);

		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(Math.random()*150)));
		g.fillOval((int)xLoc, (int)yLoc+10, 50, 30);
		
		// lifebar
		
		if(mainShip)
		if(!dead)
		{
			g.setColor(new Color(255-(int)(255*(life/100)),(int)(255*(life/100)),0));
			g.fillRect((int)xLife, (int)yLife, (int)life*2, 10);
			g.setColor(new Color(255,0,0));
			g.drawRect((int)xLife, (int)yLife, 200, 10);
			
		}
		else
		{
			g.setColor(Color.WHITE);
			g.fillRect((int)xLife-5, (int)yLife-15, (int)50, 20);
			g.setColor(new Color(255,0,0));
			g.drawString("Dead", (int)xLife, (int)yLife);
		}
		
		if(dead)
		{
			for(int i = 0; i<10; i++)
				g.drawImage(explosion, (int)xLoc+(int)(Math.random()*50)-25, (int)yLoc+(int)(Math.random()*50)-25, a);
		}
		
		
	}

	
	public void checkCollision()
	{
		
		Vector<Point> points = new Vector<Point>();
//		points.add(new Point(0,0));
//		points.add(new Point(0,50));
//		points.add(new Point(50,0));
//		points.add(new Point(50,50));
		points.add(new Point(25,25));
		
		boolean colorImmunity = false;
		for(Point p:points)
		{
			Color clr = map.getPixel((int)xLoc+p.x, (int)yLoc+p.y);
			//System.out.println(clr);
			
			if(clr.equals(color))
				colorImmunity = true;
			
			
			
			
		}
		
		if(map.getPixel((int)xLoc+25, (int)yLoc+25).equals(Color.BLACK)) dead = true;
		if((int)xLoc+25 < 0 || (int)xLoc+25 > 800) dead = true;
		
		if(!colorImmunity)
		{
			life = life-2;
			//System.out.println("bad:"+life);
		}
		else life = life + 0.2;
		
		if(!mainShip)color = map.getPixel((int)xLoc+25, (int)yLoc+25);
		
		if(life > 100) life = 100;
		if(life < 0  ) {life = 0; dead = true;}
	}
	
	
	public void addVelocity(double dX, double dY)
	{
		xVel += dX*VelocityModifier;
		yVel += dY*VelocityModifier;
	}
	
	public void update()
	{
		checkCollision();
		
		if(dead)
		{
			xVel = 0;
			yVel = 0;
		}
		
		xLoc += xVel;
		yLoc += yVel;
		
		// friction... velocities?
		if(xVel > 0) xVel -= friction;
		if(yVel > 0) yVel -= friction;
		if(xVel < 0) xVel += friction;
		if(yVel < 0) yVel += friction;
		
		if(Math.abs(xVel) < friction) xVel = 0;
		if(Math.abs(yVel) < friction) yVel = 0;
		
		if(xVel >  maxSpeed) xVel =  maxSpeed;
		if(xVel < -maxSpeed) xVel = -maxSpeed;
		if(yVel >  maxSpeed) yVel =  maxSpeed;
		if(yVel < -maxSpeed) yVel = -maxSpeed;
		
		if(noSlide) xVel = yVel = 0;
	}
	
	public Color getTmpColor()
	{
		return map.getPixel((int)xLoc+25, (int)yLoc+25);
	}
	
	public void setLocation(int x, int y)
	{
		xLoc = x;
		yLoc = y;
	}
	
	
}
