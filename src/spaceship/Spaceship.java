
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    Image starImage;
    int score;
    boolean gameover;
//    int nummissile = 75;
//    int missileindex;
//    int missilex;
//    int missiley;
//    int missilespeed;
//    boolean missileactive;  
    Missile missiles[] = new Missile[Missile.nummissile];
    int starhit;
    int highscore;
    int rocketXPos;
    int rocketYPos;
    int flashdistance;
    int flashscale;
    int flashx;
    int flashy;
    boolean flashactive;
    int numstar;
    int starXPos[];
    int starYPos[];
    boolean starActive[];
    int starAccel;
    int rocketAccel;

    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

                    if (gameover)
                        return;
// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

          if (gameover)
            return;
        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (gameover)
            return;
                if (e.VK_UP == e.getKeyCode()) {
                    if ( rocketAccel <= 5)
                    rocketAccel += 1;
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    if(rocketAccel>= -5)
                    rocketAccel -= 1;
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    if(starAccel<= 15)
                    starAccel += 1;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    if(starAccel>= -15)
                    starAccel -= 1;
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    zsound = new sound("ouch.wav");                    
                }
                else if (e.VK_SPACE == e.getKeyCode()) {
                    
                    for(int index = 0;index < Missile.nummissile; index++){
                    if ( starAccel != 0 && missiles[index].active == false){
                        missiles[index].ypos = rocketYPos;
                        missiles[index].xpos = rocketXPos;
                        missiles[index].active = true;
                        if(missiles[index].active){
                        if(starAccel <= 0)
                        missiles[index].missilespeed = 5;
                        else if (starAccel > 0)
                        missiles[index].missilespeed = -5;
                }
                        Missile.currentmissile++;
                    }
                    }
                }
                else if (e.VK_F == e.getKeyCode()) {
                    flashx = rocketXPos;
                    flashy = rocketYPos;
                    flashactive = true;
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);

        if (starAccel > 0)
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
        else
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        for (int index = 0; index <numstar;index++)
            drawStar(starImage,getX(starXPos[index]),getYNormal(starYPos[index]),0,1,1);
        for (int index = 0; index <Missile.nummissile;index++){
            if (missiles[index].active == true)
            drawMissile(getX(missiles[index].xpos),getYNormal(missiles[index].ypos),0,1,1);
        }
        if (flashactive == true)
            drawMissile(getX(flashx),getYNormal(flashy),0,flashscale,flashscale);
        g.setColor(Color.black);
        g.setFont(new Font("Impact",Font.ITALIC,16));
        g.drawString("Score: " + score, 20, 45);         
        g.setColor(Color.black);
        g.setFont(new Font("Impact",Font.ITALIC,16));
        g.drawString("Highscore: " + highscore, 150, 45);  
        g.setFont(new Font("Impact",Font.ITALIC,16));
        g.drawString("Hits: " + starhit, 300, 45);  
        if (gameover)
        {
            g.setColor(Color.white);
            g.setFont(new Font("Impact",Font.ITALIC,50));
            g.drawString("Game Over", 150, 350);             
        }

        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawStar(Image image,int xpos,int ypos,double rot,double xscale,double yscale)
    {
        int width = starImage.getWidth(this);
        int height = starImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    public void drawMissile(int xpos,int ypos,double rot,
            double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.white);
        g.fillOval(-5,-5,10,10);
       
        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        starXPos = new int[numstar];
        starYPos = new int[numstar];
        starActive = new boolean[numstar];
        rocketXPos = getWidth2() / 2;
        rocketYPos = getHeight2() / 2;
        for (int index = 0; index <numstar;index++)
        {
        starXPos[index] = (int)(Math.random()*getWidth2());
        starYPos[index] = (int)(Math.random()*getHeight2());
        starActive[index] = true;
        }
        Missile.currentmissile = 0;
        for (int index = 0; index <Missile.nummissile; index++)
        missiles[index] = new Missile();
        starAccel = 0;
        rocketAccel = 0;
        flashactive = false;
        flashscale = 0;
        score = 0;
        starhit = 0;
        gameover = false;
    }
    
   

/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            readFile();
            
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            
            starImage = Toolkit.getDefaultToolkit().getImage("./starAnim.GIF");
            reset();
           // bgSound = new sound("./starwars.wav");
        }
        
//            if (bgSound.donePlaying){
//                bgSound = new sound("./starwars.wav");
//            }
            if(starAccel == 0)
                rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            else
                rocketImage = Toolkit.getDefaultToolkit().getImage("./animRocket.GIF");
        if (gameover)
            return;
            if (rocketYPos < 0){
                rocketYPos= 0;
                rocketAccel = 0;
            }
            else if (rocketYPos > getHeight2()){
                rocketAccel = 0;
                rocketYPos = getHeight2();
                
            }
            for (int index = 0; index <numstar;index++){
            starXPos[index] += starAccel;
            if(starXPos[index] < 0){
                starXPos[index] = getWidth2();
                starYPos[index] = (int)(Math.random()*getHeight2());
            }
            else if(starXPos[index] > getWidth2()){
                starXPos[index] = 0;
                starYPos[index] = (int)(Math.random()*getHeight2());
            }
            }
            for (int index = 0; index <numstar;index++){
                for (int index1 = 0; index1 <Missile.nummissile;index1++){
            if (getX(missiles[index].xpos) > getX(starXPos[index]) - 10 && 
                    getX(missiles[index].xpos) < getX(starXPos[index]) + 10 && 
                    getYNormal(missiles[index].ypos) > getYNormal(starYPos[index]) - 10 && 
                    getYNormal(missiles[index].ypos) < getYNormal(starYPos[index]) + 10 &&
                    missiles[index].active == true){
                starXPos[index] = (int)(Math.random()*getWidth2());
                starYPos[index] = (int)(Math.random()*getHeight2());
                score++;
                missiles[index].active = true;
                }
            }
            }
             for (int index1 = 0; index1 <Missile.nummissile;index1++){
            for (int index = 0; index <numstar;index++){
            if (getX(rocketXPos) > getX(starXPos[index]) - 10 && 
                    getX(rocketXPos) < getX(starXPos[index]) + 10 && 
                    getYNormal(rocketYPos) > getYNormal(starYPos[index]) - 10 && 
                    getYNormal(rocketYPos) < getYNormal(starYPos[index]) + 10)
            {
                if( starActive[index] == true)
                {
                zsound = new sound("ouch.wav");
                starActive[index] = false;
                starhit++;
//                
                }
            }
            else if (starActive[index] == false)
                starActive[index] = true;
                }
             }
            for (int index = 0; index <Missile.nummissile;index++){
                
                if(missiles[index].xpos > getWidth2())
                    missiles[index].active = false;
                 if(missiles[index].xpos < 0)
                    missiles[index].active = false;
                
        }
            for(int index = 0; index<Missile.nummissile;index++)
            {
             missiles[index].xpos += missiles[index].missilespeed;
            }
            rocketYPos += rocketAccel;
        if(score > highscore)
            highscore = score;
        if(starhit == 3)
            gameover = true;
        if (flashactive)
            flashscale++;
        if (flashscale > 6)
        {
            flashactive = false;
            flashscale = 0;
        }
        
        
        

    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numstar = Integer.parseInt(numStarsString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }


}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}
class Missile
{
    public final static int nummissile = 75;
    public static int currentmissile = 0;
    public int missileindex;
    public int xpos;
    public int ypos;
    public int missilespeed;
    public boolean active;  
    Missile()
    {
        active = false;
    }
}