/**
 * 
 */
package edu.umass.cs.accelerometer;


/**
 * @author Abhinav Parate
 * 
 */
public class ReorientAxis {

	
	private boolean accState = false;
	private int readCounter = 0;
	private final int READ_LIMIT = 400;
	private double[][] accReadings =  new double[READ_LIMIT][3];
	private double aggAX = 0.0f;
	private double aggAY = 0.0f;
	private double aggAZ = 0.0f;
	
	public ReorientAxis() {
		
	}
	
	/**
	 * Returns an array consisting of oriented values
	 * @param accX
	 * @param accY
	 * @param accZ
	 * @return
	 */
	public double[] getReorientedValues(double accX,double accY,double accZ){
		
		//Align values to the frame of reference used in Nericell paper
		double acc_x = accX;
		double acc_y = accZ;
		double acc_z = (-1)*accY;
		
		double output[] = new double[3];
		
		if(readCounter == READ_LIMIT){
			//Now, we are ready to orient axis
			//reset counter
			readCounter = 0;
		}
		
		accState = true;
		
		aggAX = aggAX +acc_x - accReadings[readCounter][0];
		aggAY = aggAY +acc_y - accReadings[readCounter][1];
		aggAZ = aggAZ +acc_z - accReadings[readCounter][2];

		accReadings[readCounter][0] = acc_x;
		accReadings[readCounter][1] = acc_y;
		accReadings[readCounter][2] = acc_z;

		
		if(accState){
			//Once, sufficient samples have been collected, we can orient 
			//Now get the oriented axis and corresponding readings
			double g = 9.81;
			double acc_z_o = aggAZ/(READ_LIMIT*g);
			double acc_y_o = aggAY/(READ_LIMIT*g);
			double acc_x_o = aggAX/(READ_LIMIT*g);
			
			acc_z_o = (acc_z_o>1.0?1.0:acc_z_o);
			acc_z_o = (acc_z_o<(-1.0)?-1.0:acc_z_o);
			acc_x = acc_x/g;
			acc_y = acc_y/g;
			acc_z = acc_z/g;
			double theta_tilt = Math.acos(acc_z_o);
			double phi_pre = Math.atan2(acc_y_o, acc_x_o);
			double tan_psi = ( (-1)*acc_x_o*Math.sin(phi_pre) + acc_y_o*Math.cos(phi_pre))/
			((acc_x_o*Math.cos(phi_pre)+acc_y_o*Math.sin(phi_pre))*Math.cos(theta_tilt)+(-1)*acc_z_o*Math.sin(theta_tilt));
			double psi_post = Math.atan(tan_psi);
			double acc_x_pre = acc_x*Math.cos(phi_pre)+ acc_y*Math.sin(phi_pre);
			double acc_y_pre = (-1)*acc_x*Math.sin(phi_pre)+ acc_y*Math.cos(phi_pre);
			double acc_x_pre_tilt = acc_x_pre*Math.cos(theta_tilt)+ (-1)*acc_z*Math.sin(theta_tilt);
			double acc_y_pre_tilt = acc_y_pre;
			double orient_acc_x = (acc_x_pre_tilt*Math.cos(psi_post)+ acc_y_pre_tilt*Math.sin(psi_post))*g;
			double orient_acc_y =( (-1)*acc_x_pre_tilt*Math.sin(psi_post)+ acc_y_pre_tilt*Math.cos(psi_post))*g;
			double orient_acc_z = acc_z*g/(Math.cos(theta_tilt));
			//System.out.println("ORT:"+orient_acc_x+","+orient_acc_y+","+orient_acc_z);
			orient_acc_z = (orient_acc_z>3*g?3*g:orient_acc_z);
			orient_acc_z = (orient_acc_z<(-1)*3*g?(-1)*3*g:orient_acc_z);
			orient_acc_z = Math.sqrt((Math.pow(acc_x, 2)+Math.pow(acc_y, 2)+Math.pow(acc_z, 2))*Math.pow(g, 2)
					-(Math.pow(orient_acc_x, 2)+Math.pow(orient_acc_y, 2)));

			output[0] = orient_acc_x;
			output[1] = orient_acc_y;
			output[2] = orient_acc_z;
				
		}
		readCounter++;
		return output;
	}
	
	
}
