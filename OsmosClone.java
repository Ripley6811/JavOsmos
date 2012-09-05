/**
 * @(#)OsmosClone.java
 *
 * OsmosClone application
 *
 * @author Ripley6811
 * @version 1.00 2010/12/1
 */
import javax.swing.*;
import java.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class OsmosClone extends JFrame implements Runnable {

  /*Try separating all the calculations and graphics from this class
   *Just have the listeners and controls here
   *
   *
   */


  //MASS STRUCTURES
  int bodies = 3; //Starting number of masses
  int arraySize = 10000;

  Mass[] m = new Mass[arraySize];
  double mcoord[][] = new double[arraySize][2];

  double radMin = 1.0;		//minimum size of radius
  double childRadius = 0.14;//percentage
  double speedLimit = 0.1;
  double speedRedux = 0.992;//percentage
  double sensitivity = 5.0; //percentage of radius
  double pursueMin = 0.16;	//percentage of radius


  //CONTROLS
  boolean paused = false;
  boolean debug = false;
  boolean accelerate = false;

  int mouseX = 0;
  int mouseY = 0;

  //Frame size and buffer image
  int sizex = 1000;
  int sizey = 1000;
  Image holdImage;
  Graphics holdGraphics;




  public OsmosClone() {
    super( "Osmos Game Clone" );

    addKeyListener(
      new KeyListener() {
        public void keyTyped( KeyEvent e ) {
          char keystroke = (char) e.getKeyChar();
          if (keystroke == 'r') {  // Restart!
            init();
          }
          if (keystroke == 'k') {}
          if (keystroke == 'i') {}
          if (keystroke == 'j') {}
          if (keystroke == 'l') {}
          if (keystroke == 's') {m[0].addV(0,1);}
          if (keystroke == 'w') {m[0].addV(0,-1);}
          if (keystroke == 'a') {m[0].addV(-1,0);}
          if (keystroke == 'd') {m[0].addV(1,0);}
          if (keystroke == 't') {}
          if (keystroke == 'p') {
            if( paused ) paused = false;
            else paused = true;
          }
          if (keystroke == '[') {}
          if (keystroke == ']') {}
          if (keystroke == '+') {}
          if (keystroke == '-') {}
          repaint();
        }
        public void keyReleased( KeyEvent e ) {}
        public void keyPressed( KeyEvent e ) {}
      }
    );
    addMouseListener(
      new MouseListener() {
    	public void mousePressed(MouseEvent e) {
    		accelerate = true;
    	}
    	public void mouseReleased(MouseEvent e) {
    		accelerate = false;
    	}
    	public void mouseEntered(MouseEvent e) {}
    	public void mouseExited(MouseEvent e) {}
    	public void mouseClicked(MouseEvent e) {}
      }
    );
    addMouseMotionListener(
      new MouseMotionListener() {
      	public void mouseMoved(MouseEvent e) {
      		mouseX = e.getX();
      		mouseY = e.getY();
      	}
      	public void mouseDragged(MouseEvent e) {
      		mouseX = e.getX();
      		mouseY = e.getY();
      	}
      }
    );

    setSize( sizex, sizey );
    show();

    init();
    run();
  }


  public void init() {  //FOR SETTING UP THE INITIAL WINDOW AND LOOK, BACKGROUND IMAGES ETC

    //INITIALIZE MASSES
    for( int i = 0; i < bodies; i++ ) {
      m[i] = new Mass(50 - i);
      mcoord[i][0] = 200 * i + 100;
      mcoord[i][1] = i * 200 + 100;
    }



    holdImage = createImage(2000, 1100);
    holdGraphics = holdImage.getGraphics();
    holdGraphics.setColor( Color.black );
    holdGraphics.fillRect(0,0,size().width,size().height);
  }

  public void run() {
	while (true) {

	try {
	  Thread.currentThread().sleep(40);
        } catch (InterruptedException e) {
          // break;
        }
        ///////////////////////
        //INSERT CODE HERE

        //MOVE ALL MASSES, ENSURE THEY STAY IN BOUNDARIES, DECELERATE
        for(int i = 0; i < bodies; i++){
        	//MOVE TO NEW POSITION BASED ON VELOCITY
        	mcoord[i][0] = (mcoord[i][0] + m[i].getV()[0]);
        	mcoord[i][1] = (mcoord[i][1] + m[i].getV()[1]);

        	//CHECK IF WITHIN BOUNDARIES
        	if(mcoord[i][0] < 40 + m[i].getR() && m[i].getV()[0] < 0)
        		m[i].setV( -1 * m[i].getV()[0], m[i].getV()[1] );
        	if(mcoord[i][0] > size().width -40 - m[i].getR() && m[i].getV()[0] > 0)
        		m[i].setV( -1 * m[i].getV()[0], m[i].getV()[1] );
        	if(mcoord[i][1] < 40 + m[i].getR() && m[i].getV()[1] < 0)
        		m[i].setV( m[i].getV()[0], -1 * m[i].getV()[1] );
        	if(mcoord[i][1] > size().height -40 -  m[i].getR() && m[i].getV()[1] > 0)
        		m[i].setV( m[i].getV()[0], -1 * m[i].getV()[1] );

        	//REDUCE VELOCITY IF NECESSARY
        	if(Math.sqrt(m[i].getV()[0]*m[i].getV()[0] + m[i].getV()[1]*m[i].getV()[1]) > speedLimit)
        		m[i].setV(m[i].getV()[0] * speedRedux, m[i].getV()[1] * speedRedux);
        }
        //CHECK IF MASSES MAKE CONTACT AND ABSORB
        if(bodies > 1){ //only if more than one mass present
	        for(int i = 0; i < bodies-1; i++){	//process each mass
	        	for(int j = i + 1; j < bodies; j++){  //only check mass pairs that have not been checked
					//check proximity
	    			double dx = mcoord[i][0] - mcoord[j][0];
	    			double dy = mcoord[i][1] - mcoord[j][1];
	    			double hyp = Math.sqrt((dx * dx) + (dy * dy));
	        		if(hyp < m[i].getR() + m[j].getR()){
	        			if(m[i].getR() > m[j].getR()){
	        				double dr = m[i].getR()+m[j].getR()-hyp;
	        				m[i].setR(Math.sqrt(m[i].getR()*m[i].getR() + 2*dr*m[j].getR() - dr*dr ));
	        				m[j].addR(-1*dr);
	        				//m[i].setR(Math.sqrt(m[i].getR() * m[i].getR() + m[j].getR()*m[j].getR()));
	        				if(m[j].getR() <= 0.0){
	        					m[j] = m[bodies-1];
	        					m[bodies-1] = null;
	        					mcoord[j][0] = mcoord[bodies-1][0];
	        					mcoord[j][1] = mcoord[bodies-1][1];
	        					bodies--;
	        				}
	        			}
	        			else{
	        				double dr = m[i].getR()+m[j].getR()-hyp;
	        				m[j].setR(Math.sqrt(m[j].getR()*m[j].getR() + 2*dr*m[i].getR() - dr*dr ));
	        				m[i].addR(-1*dr);
	        				//m[i].setR(Math.sqrt(m[i].getR() * m[i].getR() + m[j].getR()*m[j].getR()));
	        				if(m[i].getR() <= 0.0){
	        					m[i] = m[bodies-1];
	        					m[bodies-1] = null;
	        					mcoord[i][0] = mcoord[bodies-1][0];
	        					mcoord[i][1] = mcoord[bodies-1][1];
	        					bodies--;
	        				}
	        			}
	        			//bodies--; //reduce mass number either way
						if(debug){
	        				System.out.print("bodies = " + bodies);
	        				System.out.print("radius = " + m[0].getR());
	        				for(int kk = 0; kk < 5; kk++){
	        					System.out.print(" (" + kk + "=" + m[kk] + ")");
	        				}
	        				System.out.println();
						}
	        		}
	        	}
	        }
        }
        //IF MOUSE BUTTON IS PRESSED THEN ACCELERATE THE MASS
        if(accelerate){
        	if(m[0].moveIt()) accelerate(0, mouseX, mouseY);
	   	}
        //RUN THE AI(PURSUE-FLEE) PROCESS FOR ALL OTHER MASSES
        for(int i = 1; i < bodies; i++) if(m[i].moveIt()) runAI(i);


        ///////////////////////
    	//for(int i = 0; i < 999999999; i++){}
        repaint();

    }
  }

  public void accelerate(int miv, int xx, int yy){ //miv = mass index value
    		//System.out.println(e.getX() + "x " + e.getY() + "y" );
    		double dx = mcoord[miv][0] - xx;
    		double dy = mcoord[miv][1] - yy;
    		double hyp = hyp(dx, dy);
    		m[miv].addV(dx / (3*hyp), dy / (3*hyp));
    		if(m[miv].getR()*childRadius >= radMin){   //radius minimum for all masses
    		//CREATE NEW MASS
	        	double smallR = m[miv].getR() * childRadius;
	        	bodies++;
	        	m[miv].setR(Math.sqrt(m[miv].getR() * m[miv].getR() - smallR*smallR)); //new R = sqrt( (old R)^2 - 1)
	        	m[bodies-1] = new Mass(smallR);
	        	m[bodies-1].setV(m[miv].getV()[0] -2*dx / hyp, m[miv].getV()[1] -2*dy / hyp);
	        	//m[bodies-1].addV(m[miv].getV()[0] -2*dx / hyp, m[miv].getV()[1] -2*dy / hyp);
	        	mcoord[bodies-1][0] = mcoord[miv][0] - (m[miv].getR() + m[bodies-1].getR())*dx/hyp;
	        	mcoord[bodies-1][1] = mcoord[miv][1] - (m[miv].getR() + m[bodies-1].getR())*dy/hyp;

				if(debug){
		        			System.out.print("bodies = " + bodies);
		        			System.out.println("radius = " + m[0].getR());
		        			for(int kk = 0; kk < 5; kk++){
		        				System.out.print(" (" + kk + "=" + m[kk] + ")");
		        			}
		        			System.out.println();
				}
    		}
  }

  public void runAI(int miv){ //miv = mass index value
  	//RUNS AI FOR ONE MASS
  	//AVOID LARGER PRIORITY OVER ATTACK SMALLER
  	double dx = 2*size().width;
  	double dy = 2*size().height;
  	double hyp = 2*size().height;
  	int target = -1;
  	boolean runAway = false;
  	boolean attack = false;
  	//find closest mass
  	for(int i = 0; i < bodies; ){
  		if(i == miv) i++;//prevent comparing with itself if miv is 0
  		double testHyp = hyp(mcoord[miv][0],mcoord[miv][1],mcoord[i][0],mcoord[i][1]);
  		if(testHyp < sensitivity * m[miv].getR() ){ //dist from center of miv to edge of target

  			if(testHyp - m[i].getR() < hyp
  				&& m[i].getR() > m[miv].getR()
  				&& hyp(mcoord[i][0]+m[i].getV()[0],
  				   mcoord[i][1]+m[i].getV()[1],
  				   mcoord[miv][0]+m[miv].getV()[0],
  				   mcoord[miv][1]+m[miv].getV()[1] )
  				<= testHyp){
	  				hyp = testHyp;
	  				target = i;
	  				runAway = true;
  			}
  			if(!runAway
  				&& testHyp - m[i].getR() < hyp){
  					if( target >= 0
  						&& m[i].getR() > m[target].getR()){
			  				hyp = testHyp;
			  				target = i;
  					}
  					if( target < 0 ) {
		  				hyp = testHyp;
		  				target = i;
  					}

  			}
  		}
  		//prevent comparing with itself after increment of i
  		i++;
  		if(i == miv) i++;
  	}

  	//IS IT CLOSE ENOUGH TO CARE AND IS IT LARGER OR SMALLER?
  	if(hyp < sensitivity * m[miv].getR()){
  		if(m[miv].getR() > m[target].getR() && m[target].getR() > m[miv].getR()*pursueMin ){ //pursue
  				accelerate(miv,
  					   	(int)(mcoord[target][0] - 2*(mcoord[target][0]-mcoord[miv][0]) - m[miv].getV()[0]),
  					   	(int)(mcoord[target][1] - 2*(mcoord[target][1]-mcoord[miv][1]) - m[miv].getV()[1]));
  		} else { //flee
  				accelerate(miv,(int)mcoord[target][0],(int)mcoord[target][1]);
  		}
  	}
  }

  public double hyp(double x0, double y0, double x1, double y1){
  	return Math.sqrt((x0 - x1)*(x0 - x1) + (y0 - y1)*(y0 - y1));
  }
  public double hyp(double dx, double dy){
  	return Math.sqrt((dx * dx) + (dy * dy));
  }

  public void paint( Graphics g ) {
  	//DRAW BACKGROUND
    holdGraphics.setColor(Color.black);
    holdGraphics.fillRect(0,0,size().width,size().height);




    //DRAW ALL MASSES
    Color tempcol = new Color(255,255,255);
    for (int j=8; j>-1; j--) {
       for (int i=0; i<bodies; i++) {
        tempcol = new Color(50-50*j/8,50-50*j/8,50-50*j/8);
        holdGraphics.setColor(tempcol);
        if(m[i] != null) holdGraphics.fillOval(
                         ((int)mcoord[i][0]-(int)m[i].getR())-j,
                         ((int)mcoord[i][1]-(int)m[i].getR())-j,
                         ((int)m[i].getR()+(int)m[i].getR())+2*j,
                         ((int)m[i].getR()+(int)m[i].getR())+2*j
        );
      }
    }
    for (int i=0; i<bodies; i++) {
      int phase = m[i].getPhase();
      for (int j=30; j>-1 && (int)m[i].getR() > 0 && (int)m[i].getR() >= phase; j--) {
        if(i == 0)tempcol = new Color( (60+ 2*j+4*j*phase/((int)m[i].getR()))%256, 10*(phase)/((int)m[i].getR()), (60 + 2*j +4*j*phase/((int)m[i].getR())));
        else      tempcol = new Color(50*phase/((int)m[i].getR()), 50+ 2*j*(phase)/((int)m[i].getR()), 60 + 2*j + 4*j*phase/((int)m[i].getR()));
        holdGraphics.setColor(tempcol);
        if(m[i] != null) holdGraphics.fillOval(
                         ((int)mcoord[i][0]-(int)m[i].getR()+3*(30-j)/30),
                         ((int)mcoord[i][1]-(int)m[i].getR()*(30+j)/60),
                         ((int)m[i].getR()+(int)m[i].getR()*j/30),
                         ((int)m[i].getR()*(30+j)/60+(int)m[i].getR()*(30+j)/60)
        );
      }
    }

	//DRAW THE BOUNDARY
	for(int i = 10; i > -1; i--){
	    holdGraphics.setColor(new Color(255*i/10,255*i/10,0));
	    holdGraphics.drawRect(30+i,30+i,size().width -60-2*i,size().height-60 - 2*i);
	}

    g.drawImage(holdImage, 0, 0, this);
  }





  Thread t;

  public void start() {
    t = new Thread(this);
    t.start();
  }

  public void stop() {
    t.stop();
    t = null;
  }


    public static void main(String[] args) {
	    OsmosClone app = new OsmosClone();

	    app.addWindowListener(
	      new WindowAdapter() {
	        public void windowClosing( WindowEvent e ) {
	          System.exit( 0 );
	        }
	      }
	    );
    }
}
