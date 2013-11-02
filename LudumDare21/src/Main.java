import java.applet.*; 
import java.awt.*; 
import java.awt.event.*; 
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.*; 
  
public class Main extends Applet
implements MouseMotionListener,MouseListener,KeyListener,Runnable

{
  
	
	boolean sound = false;
	
	// app vars
	boolean running;
	boolean Down;
	Thread UpdaterThread;
	Graphics bufferg; 
	Image bufferi;
	Dimension bufferdim; 
	Font MyFont;
	
	// epic state engine
	String state;
	int level = 1;
	
	int score = 0;
	
	Image[] pics;
	Image speaker;
	
	// fps fail
	static int fps = 60;
	static int ttw = 1000/fps;
	long nextTick;
	
	// horrible keyboard reflection
	boolean[] keyDown;
	
	
	// game vars
	Ship ship1;
	Ship ship2;
	
	Map map1;
	
	// saving
	double saveXLoc;
	double saveShip1x;
	double saveShip1y;
	double saveShip2x;
	double saveShip2y;
	int    saveScore;
	
	// audio
	AudioClip explosion;
	//AudioClip warning;
	AudioClip music;
	
	public void init() 
    { 

		state = "loading";
		
		this.resize(800, 600);
		
		// keyboard
		keyDown = new boolean[65535];
		
		// double buffer & applet stuff
		
        bufferdim = getSize(bufferdim);
	    bufferi = createImage(bufferdim.width,bufferdim.height); 
	    bufferg = bufferi.getGraphics();
	    setBackground(Color.black);
	    MyFont = new Font("Arial",Font.ITALIC,16);
	    addMouseListener(this);
	    addMouseMotionListener(this); 
	    addKeyListener(this);
	    
	    // 
	    MediaTracker track = new MediaTracker(this);
	    
	    // loading map
	    Image tmp = getImage(getDocumentBase(), "level1.png");
	    track.addImage(tmp, 1337);
	    try {track.waitForAll();} 
	    catch (InterruptedException e) {e.printStackTrace();}
	    
	    map1 = new Map(tmp,this);
	    
	    // load ships
	    System.out.println(getDocumentBase());
	    
	    tmp = getImage(getDocumentBase(), "shiptrans.png");
	    //Image tmp2 = getImage(getDocumentBase(), "shieldr.png");
	    Image tmp3 = getImage(getDocumentBase(), "explosion1.png");
	    speaker = getImage(getDocumentBase(), "speaker.png");
	    track.addImage(tmp, 1234);
	    track.addImage(speaker, 9998);
	    //track.addImage(tmp2, 5432);
	    track.addImage(tmp3, 3214);
	    try {track.waitForAll();} 
	    catch (InterruptedException e) {e.printStackTrace();}
	    
	    // load pictures
	    pics = new Image[9];
	    pics[0] = getImage(getDocumentBase(), "intro.png");
	    pics[1] = getImage(getDocumentBase(), "intro1.png");
	    pics[2] = getImage(getDocumentBase(), "intro2.png");
	    pics[3] = getImage(getDocumentBase(), "intro3.png");
	    pics[4] = getImage(getDocumentBase(), "end.png");
	    track.addImage(pics[0], 1230);
	    track.addImage(pics[1], 1231);
	    track.addImage(pics[2], 1232);
	    track.addImage(pics[3], 1233);
	    track.addImage(pics[4], 1233);
	    try {track.waitForAll();} 
	    catch (InterruptedException e) {e.printStackTrace();}
	    
	    
	    ship1 = new Ship(tmp,tmp3,map1);
	    ship1.setLocation(100, 150);
	    ship1.xLife = 50;
	    ship1.yLife = 20;
	    ship1.mainShip = true;
	    
	    ship2 = new Ship(tmp,tmp3,map1);
	    ship2.setLocation(100, 450);
	    ship2.xLife = 50;
	    ship2.yLife = 320;
	    
	    
	    // load audio
	    explosion = getAudioClip(getDocumentBase(), "explosion.wav");
	    //warning = getAudioClip(getDocumentBase(), "warning.wav");
	    music = getAudioClip(getDocumentBase(), "music.wav");
	    //warning.loop();
	    //music.loop();
	    
	    save();
	    
	    
	    state = "intro";
	    
	    
		//run();
	    running = true;
		UpdaterThread = new Thread(this);
   	    UpdaterThread.start();
    }
	
	
	private void save()
	{
		saveXLoc = map1.xLoc;
		saveShip1x = ship1.xLoc;
		saveShip1y = ship1.yLoc;
		saveShip2x = ship2.xLoc;
		saveShip2y = ship2.yLoc;
		saveScore = score;
	}
	
	private void load()
	{
		score = saveScore;
		
		map1.xLoc = saveXLoc;
		ship1.xLoc=saveShip1x;
		ship1.yLoc=saveShip1y;
		ship2.xLoc=saveShip2x;
		ship2.yLoc=saveShip2y;
		ship1.xVel = 0;
		ship1.yVel = 0;
		ship2.xVel = 0;
		ship2.yVel = 0;
		
		ship1.dead = false;
		ship2.dead = false;
		ship1.life = 100;
		ship2.life = 100;
	}
	
	public void stop() 
    { 
		
    }
	
	public void paint(Graphics g) 
    { 
		bufferg.setColor(Color.black);
        bufferg.fillRect(0,0,bufferdim.width,bufferdim.height);
        bufferg.setFont(MyFont);
        // Paint
        
        if(state.equals("loading"))
        {
        	bufferg.setColor(Color.white);
        	bufferg.drawString("Loading...", 100, 100);
        }
        
        if(state.equals("intro"))
        	bufferg.drawImage(pics[0],0,0,this);
        if(state.equals("intro1"))
        	bufferg.drawImage(pics[1],0,0,this);
        if(state.equals("intro2"))
        	bufferg.drawImage(pics[2],0,0,this);
        if(state.equals("intro3"))
        	bufferg.drawImage(pics[3],0,0,this);
        
        if(state.equals("end"))
        {
        	bufferg.drawImage(pics[4],0,0,this);
        	bufferg.setColor(Color.black);
        	bufferg.drawString("Score: "+score, 370,290);
        }
        
        if(state.equals("playing"))
        {
        	map1.draw(bufferg, this);
            
            // draw beam
            for(int i = 0; i < 10; i++)
            {
            	if(Math.random() > 0.5) bufferg.setColor(ship1.color);
            	else bufferg.setColor(ship2.color);
            	
            	int r2 = bufferg.getColor().getRed()+(int)(Math.random()*100)-50;
            	int g2 = bufferg.getColor().getGreen()+(int)(Math.random()*100)-50;
            	int b2 = bufferg.getColor().getBlue()+(int)(Math.random()*100)-50;
            	
            	if(r2 < 0) r2 = 0; if(r2>255) r2 = 255;
            	if(g2 < 0) g2 = 0; if(g2>255) g2 = 255;
            	if(b2 < 0) b2 = 0; if(b2>255) b2 = 255;
            	
            	bufferg.setColor(new Color(r2,g2,b2));
            	
            	if(i == 0 || i == 9) bufferg.setColor(Color.black);
            	
            	bufferg.drawLine((int)(ship1.xLoc-5+i+25), (int)(ship1.yLoc+25), 
            					 (int)(ship2.xLoc-5+i+25), (int)(ship2.yLoc+25));
            }
            
            if(ship1.dead&&ship2.dead)
            {
            	bufferg.setColor(Color.white);
            	bufferg.drawString("- Press Space to try Again -", 350, 300);
            }
            
            ship1.draw(bufferg, this);
            ship2.draw(bufferg, this);
        }
        
        if(sound)bufferg.drawImage(speaker, 740, 540, this);
        	
        bufferg.setColor(Color.white);
    	bufferg.drawString("Score: "+score, 55,18);
        
        // Paint End
		g.drawImage(bufferi,0,0,this); 
		
    }
	
	public void run() 
    { 
		
		// TODO
		// more levels?
		// end screen, make a proper one?
		// maybe a story picture between each level?
		// SOUND SOUND SOUND SOUND SOUND
		
		
		boolean space = false;
		boolean m     = false;
		boolean saved = false;
		boolean wLoop = false;
		
         while (running) 
         {     	 
        	 
        	 if(keyDown[KeyEvent.VK_SPACE]) space = true;
        	 else if (space)
        	 {
        		 if(state.equals("intro")) state = "intro1";
        		 else if(state.equals("intro1")) state = "intro2";
        		 else if(state.equals("intro2")) state = "intro3";
        		 else if(state.equals("intro3")) state = "playing";
        		 
        		space = false;
        		System.out.println("space");
        		
        		// load if dead
        		if(ship1.dead&&ship2.dead)
        		{
        			load();
        			//score = score/2;
        			score -= 750;
        		}
        	 }
        	 
        	 if(keyDown[KeyEvent.VK_M]) m = true;
        	 else if (m)
        	 {
        		 
        		 if(sound) 
        		 {
        			 sound = false;
        			 explosion.stop();
        			 music.stop();
        			 
        		 }
        		 else 
        		 {
        			 music.loop();
        			 sound = true;
        		 }
        		 
        		 m = false;
        	 }
        	 
        	 if(state.equals("playing"))
        	 {
        		 
        		 
        		 score++;
        		 if(ship1.life < 90) score--;
        		 if(ship1.life < 50) score -= 2;
        		 if(ship1.life < 30) score -= 5;
        		 
        		 if(!ship1.dead)if(!ship2.dead)
        		 if(ship1.color.equals(Color.white)||ship2.color.equals(Color.white))
        		 {
        			 // next level
        			 level ++;
        			 
        			 if(level == 4) state = "end";
        			 else
        			 {
        				 Image tmp = getImage(getDocumentBase(), "level"+level+".png");
        				 MediaTracker track = new MediaTracker(this);
        				 track.addImage(tmp, 1337);
        				 try {track.waitForAll();} 
        				 catch (InterruptedException e) {e.printStackTrace();}
        				    
        				 map1 = new Map(tmp,this);
        				 
        				 
        				 ship1 = new Ship(ship2.img,ship2.explosion ,map1);
        				 ship1.setLocation(100, 150);
        				 ship1.xLife = 50;
        				 ship1.yLife = 20;
        				 ship1.mainShip = true;
        				    
        				 ship2 = new Ship(ship1.img,ship1.explosion ,map1);
        				 ship2.setLocation(100, 450);
        				 
        				 
        				 save();
        				 
        			 }
        		 }
        		 
        		 
        		 nextTick = (System.currentTimeMillis() + ttw);
            	 //System.out.println(System.currentTimeMillis()+":"+nextTick);
            	 
            	 // Update Stuff
            	 map1.advanceX(1);
            	 
            	 
            	// TODO couple team somehow
            	 ship1.color = ship2.color;
            	 if(ship2.dead) ship1.dead = true;
            	 if(ship1.dead) ship2.dead = true;
            	 
            	 
            	 // Saving at 0,255,60
            	 if(!ship1.dead)
                 if(!saved)
            	 if(ship1.getTmpColor().equals(new Color(0,255,60)))
            	 if(ship2.getTmpColor().equals(new Color(0,255,60)))
            	 {
            		 save();
            		 saved = true;
            	 }
            	 
            	 if(!ship1.getTmpColor().equals(new Color(0,255,60)) || !ship2.getTmpColor().equals(new Color(0,255,60)))
            	 {
            		 saved = false;
            	 }
            	 
            	 
            	 ship1.update();
            	 ship2.update();
            	 
            	 
            	 
            	 // sounds
            	 if(ship1.dead)if(ship2.dead)
            		 			if(sound)explosion.play();
            	 
            	 //if(ship1.life < 90) if(!wLoop) {warning.play(); wLoop = true;}
            	 //else {warning.stop(); wLoop = false;}
            	 
            	 // wasd - ship 1 - for now =)
            	 if(keyDown[KeyEvent.VK_W]) ship1.addVelocity( 0,-1);
            	 if(keyDown[KeyEvent.VK_S]) ship1.addVelocity( 0, 1);
            	 if(keyDown[KeyEvent.VK_A]) ship1.addVelocity(-1, 0);
            	 if(keyDown[KeyEvent.VK_D]) ship1.addVelocity( 1, 0);
            	 // arrows - ship 2 - for now =)
            	 if(keyDown[KeyEvent.VK_UP]   ) ship2.addVelocity( 0,-1);
            	 if(keyDown[KeyEvent.VK_DOWN] ) ship2.addVelocity( 0, 1);
            	 if(keyDown[KeyEvent.VK_LEFT] ) ship2.addVelocity(-1, 0);
            	 if(keyDown[KeyEvent.VK_RIGHT]) ship2.addVelocity( 1, 0);
            	 
            	 if(keyDown[KeyEvent.VK_SPACE]) space = true;
            	 else if (space)
            	 if(ship1.life == 100 && ship2.life == 100)
            	 {
            		//
            		space = false;
            		System.out.println("space");
            	 }
            	 
            	 while(nextTick > System.currentTimeMillis()) { /* blergh */ }
        	 }
        	 
        	 repaint();
        	 
         }
    }
	
    public void update(Graphics g) 
    { 
     paint(g); 
    }
    
	public void destroy() 
    { 
     running = false; 
     UpdaterThread = null; 
    }

	
	// Key Events
	public void keyPressed(KeyEvent e) {
		
		keyDown[e.getKeyCode()] = true;
		//System.out.println("Down: "+e.getKeyCode());
	}


	@Override
	public void keyReleased(KeyEvent e) {

		keyDown[e.getKeyCode()] = false;
		//System.out.println("Up"+e.getKeyCode());
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	
	}  	
	
	
	
	// Mouse Events
    public void mouseMoved(MouseEvent me)  
    {  
    // Nothing 
    }
    
    public void mouseDragged(MouseEvent me)  
    { 
    // Nothing 
    } 

    public void mouseClicked (MouseEvent me) 
    {
    // Nothing 
    } 
    
    public void mouseEntered (MouseEvent me) 
    {
    // Nothing 
    } 
    
    public void mousePressed (MouseEvent me) 
    {
    Down = true;	
    } 
    
    public void mouseReleased (MouseEvent me) 
    {
    Down = false;
    }  
    
    public void mouseExited (MouseEvent me) 
    {
    // Nothing 
    }
	
}
