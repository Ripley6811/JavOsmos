/**
 * @(#)Mass.java
 *
 *
 * @author Ripley6811
 * @version 1.00 2010/12/1
 */

import java.awt.*;

public class Mass {
  //PROPERTIES					//Position will be accessed frequently, so keep separate
  double r = 50.0;			//Radius size in pixels, also directly related to mass
  double[] v = { 0, 0 };		//Velocity in two directions
  Color rgb = new Color( 255, 0, 255 ); 	//RGB color of this mass
  int phase = 0;
  boolean brighten = true;
  boolean moveIt = false;
  boolean isAlive = false;
  int massType = 0;
	

  //INITIALIZERS
  public Mass() {
    if(r >= 10) isAlive = true;
    else isAlive = false;
  }
  public Mass( double x ) {
  	r = x;
    if(r >= 10) isAlive = true;
    else isAlive = false;
  }
  //OTHER METHODS
  public void bounce( double tangent ){
  	
  }
  
  //SET METHODS
  public void setR( double x ){
    r = x; 
    if(r >= 10) isAlive = true;
  }
  public void addR( double x ){
    r += x; 
    if(r >= 10) isAlive = true;
  }
  public void setV( double x, double y ){
    	v[0] = x;
    	v[1] = y;}
  public void addV( double x, double y ){
    	v[0] += x;
    	v[1] += y;}
  public void setColor( Color newcolor ){
  		rgb = newcolor;
  }
  public void phaseStep(){
  	if(isAlive){
	  	if(phase%4 == 0) moveIt = true;
	  	if(brighten) phase++;
	  	else phase--;
	  	if(phase >= (int) r) brighten = false;
	  	if(phase <= 0) brighten = true; 
  	}
  }
    
    
  //GET METHODS
  public double getR(){ return r; }
  public double[] getV() { return v; }
  public Color getColor(){ return rgb; }
  public int getPhase() { phaseStep(); return phase; }
  public boolean moveIt() { 
  	if(moveIt){
  		moveIt = false;
  		return true;
  	}
  	return false;
  }
}