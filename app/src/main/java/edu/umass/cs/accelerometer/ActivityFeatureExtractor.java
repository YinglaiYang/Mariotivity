/**
 * 
 */
package edu.umass.cs.accelerometer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Abhinav Parate
 * 
 */
public class ActivityFeatureExtractor {

	//LinkedLists to keep accelerometer readings for a window
	private LinkedList<Double> xVector = new LinkedList<Double>();
	private LinkedList<Double> yVector = new LinkedList<Double>();
	private LinkedList<Double> zVector = new LinkedList<Double>();
	private LinkedList<Double> speedVector = new LinkedList<Double>();
	private LinkedList<Double> energyVector = new LinkedList<Double>();
	private LinkedList<Double> energyXYVector = new LinkedList<Double>();
	private LinkedList<Long> timeVector = new LinkedList<Long>();

	private long WINDOW_IN_MILLISEC = 5000;
	private double lastAccX = 0, lastAccY = 0, lastAccZ = 0;


	/**
	 * Constructor for the extractor
	 * @param WINDOW window size in milliseconds
	 */
	public ActivityFeatureExtractor(long WINDOW) {
		WINDOW_IN_MILLISEC = WINDOW;
	}

	/**
	 * Extract Features
	 * @param timestamp time when accelerometer reading was obtained
	 * @param ortAccX oriented x-axis accelerometer
	 * @param ortAccY oriented y-axis accelerometer
	 * @param ortAccZ oriented z-axis accelerometer
	 * @param accX original x-axis accelerometer
	 * @param accY original y-axis accelerometer
	 * @param accZ original z-axis accelerometer
	 * @return null if the window has been buffered
	 */
	public Double[] extractFeatures(long timestamp, double ortAccX, double ortAccY, double ortAccZ,
			double accX, double accY, double accZ) {
		addTime(timestamp);
		double speed = Math.sqrt(Math.pow(accX-lastAccX,2)+Math.pow(accY-lastAccY,2)+Math.pow(accZ-lastAccZ,2));
		addValues(ortAccX,ortAccY,ortAccZ, speed);
		addEnergyValues(ortAccX,ortAccY,ortAccZ);
		lastAccX = accX; lastAccY = accY; lastAccZ = accZ;
		//Return null if features not extracted
		if((timestamp-timeVector.get(0))< WINDOW_IN_MILLISEC)
			return null;

		if(xVector.isEmpty()||xVector.size()<2)
			return null;

		return extractFeatures();
	}

	/**
	 * Extract features over a window
	 * @return array of features
	 */
	private Double[] extractFeatures() {
		Double[] features = new Double[43];
		for(int i=0;i<features.length;i++)
			features[i] = 0.0;

		Long[] times = timeVector.toArray(new Long[0]);
		times[times.length-1] = times[0]+5000;


		//features of the x acceleration
		Double[] values = xVector.toArray(new Double[0]);

		double mean = computeMean(values);
		double dev = computeStdDev(values,mean);
		double result[] = computeFFTFeatures(values);

		features[0] = mean;
		features[1] = dev;
		features[2] = computeCrossingRate(values,mean);
		//FFT
		for(int i=3;i<7;i++)
			features[i] = result[i-3];//0-3
		//might change where these values go in the array
		for(int i=1;i<values.length;i++){
			//change in x velocity from time i-1 to time i
			features[7] += (values[i-1]*(times[i]-times[i-1]));
			//two times the x distance from time i-1 to time i
			features[8] += Math.abs(values[i-1]*Math.pow(times[i]-times[i-1],2));
		}

		//features of the y acceleration
		values = yVector.toArray(new Double[0]);

		mean = computeMean(values);
		dev = computeStdDev(values,mean);
		result = computeFFTFeatures(values);
		features[9] = mean;
		features[10] = dev;
		features[11] = computeCrossingRate(values,mean);
		for(int i=12;i<16;i++)
			features[i] = result[i-12];
		//might change where these values go in the array
		for(int i=1;i<values.length;i++){
			//change in y velocity from time i-1 to time i
			features[16] += values[i-1]*(times[i]-times[i-1]);
			//two times the y distance from time i-1 to time i
			features[17]  += Math.abs(values[i-1]*Math.pow(times[i]-times[i-1],2));
		}

		//features of the z acceleration
		values = zVector.toArray(new Double[0]);

		mean = computeMean(values);
		dev = computeStdDev(values,mean);
		result = computeFFTFeatures(values);
		features[18] = mean;
		features[19] = dev;
		features[20] = computeCrossingRate(values,mean);
		for(int i=21;i<25;i++)
			features[i] = result[i-21];
		//may change where these values go in the array
		for(int i=1;i<values.length;i++){
			//change in z velocity from time i-1 to time i
			features[25] += values[i-1]*(times[i]-times[i-1]);
			//two times the z distance from time i-1 to time i
			features[26] += Math.abs(values[i-1]*(times[i]-times[i-1]));
		}

		//features of the speed
		values = speedVector.toArray(new Double[0]);
		mean = computeMean(values);
		dev = computeStdDev(values,mean);
		result = computeFFTFeatures(values);
		features[27] = mean;
		features[28] = dev;
		features[29] = computeCrossingRate(values,mean);
		for(int i=30;i<33;i++)
			features[i] = result[i-30];

		//features of the energy
		values = energyVector.toArray(new Double[0]);
		mean = computeMean(values);
		dev = computeStdDev(values,mean);
		result = computeFFTFeatures(values);
		features[33] = mean;
		features[34] = dev;
		features[35] = computeCrossingRate(values,mean);
		for(int i=36;i<40;i++)
			features[i] = result[i-36];

		//features of energyXY
		values = energyXYVector.toArray(new Double[0]);
		mean = computeMean(values);
		dev = computeStdDev(values,mean);
		features[40] = mean;
		features[41] = dev;
		features[42] = computeCrossingRate(values,mean);

		clearValues();
		return features;
	}

	/**
	 * Clear values for the next window 
	 */
	private void clearValues(){
		xVector.clear();
		yVector.clear();
		zVector.clear();
		speedVector.clear();
		energyVector.clear();
		energyXYVector.clear();
		timeVector.clear();
	}

	/**
	 * Compute mean crossing rates
	 * @param values
	 * @param mean
	 * @return
	 */
	private double computeCrossingRate(Double values[], double mean){
		if(values.length<=1)
			return 0.0;
		double rate = 0.0;
		for(int i=0;i<values.length;i++){
			if(i>0 && ((values[i]>mean && values[i-1]<mean)|| (values[i]<mean && values[i]>mean)))
				rate = rate +1;
		}
		rate = rate/(values.length-1);
		return rate;

	}

	/**
	 * Compute FFT features
	 * @param values
	 * @return
	 */
	private double[] computeFFTFeatures(Double values[]){
		/***************************************************************
		 * fft.c
		 * Douglas L. Jones 
		 * University of Illinois at Urbana-Champaign 
		 * January 19, 1992 
		 * http://cnx.rice.edu/content/m12016/latest/
		 * 
		 *   fft: in-place radix-2 DIT DFT of a complex input 
		 * 
		 *   input: 
		 * n: length of FFT: must be a power of two 
		 * m: n = 2**m 
		 *   input/output 
		 * x: double array of length n with real part of data 
		 * y: double array of length n with imag part of data 
		 * 
		 *   Permission to copy and use this program is granted 
		 *   as long as this header is included. 
		 ****************************************************************/
		int i,j,k,n1,n2,a;
		double c,s,t1,t2;

		int n = 1,m=0;
		for(m=0;;m++){
			if(n>=values.length)
				break;
			n = n*2;
		}


		double x[] = new double[n];
		double y[] = new double[n];

		for(i=0;i<values.length;i++)
			x[i] = values[i];

		double cos[] = new double[n/2];
		double sin[] = new double[n/2];

		for(i =0;i<n/2;i++) {
			cos[i] = Math.cos(-2*Math.PI*i/n);
			sin[i] = Math.sin(-2*Math.PI*i/n);
		}
		// Bit-reverse
		j = 0;
		n2 = n/2;
		for (i=1; i < n - 1; i++) {
			n1 = n2;
			while ( j >= n1 ) {
				j = j - n1;
				n1 = n1/2;
			}
			j = j + n1;

			if (i < j) {
				t1 = x[i];
				x[i] = x[j];
				x[j] = t1;
				t1 = y[i];
				y[i] = y[j];
				y[j] = t1;
			}
		}

		// FFT
		n1 = 0;
		n2 = 1;

		for (i=0; i < m; i++) {
			n1 = n2;
			n2 = n2 + n2;
			a = 0;

			for (j=0; j < n1; j++) {
				c = cos[a];
				s = sin[a];
				a +=  1 << (m-i-1);

				for (k=j; k < n; k=k+n2) {
					t1 = c*x[k+n1] - s*y[k+n1];
					t2 = s*x[k+n1] + c*y[k+n1];
					x[k+n1] = x[k] - t1;
					y[k+n1] = y[k] - t2;
					x[k] = x[k] + t1;
					y[k] = y[k] + t2;
				}
			}
		}

		Coefficient coeffs[] = new Coefficient[x.length];
		for(i=0;i<coeffs.length;i++)
			coeffs[i] = new Coefficient(x[i],y[i],(360.0*i)/coeffs.length);
		Arrays.sort(coeffs);

		Coefficient coeffs2[] = new Coefficient[x.length];
		for(i=0;i<x.length;i++)
			coeffs2[i] = coeffs[x.length-1-i];
		double result[] = new double[10];
		int len = (coeffs2.length>5?5:coeffs2.length);
		boolean NEW = false;
		for(i=0,j=0;i<len;i++,j++){
			if(NEW && i>0 && j<coeffs2.length && Math.abs(coeffs2[j].abs-coeffs2[j-1].abs)<=0.00001){
				i--;
				continue;
			}
			if(NEW && j>=coeffs2.length)
				break;
			result[2*i] = coeffs2[j].abs;
			result[2*i+1] = coeffs2[j].freq;

		}
		return result;

	}

	class Coefficient implements Comparable<Coefficient>{
		double re;
		double im;
		double freq;
		double abs;

		Coefficient(double x, double y, double frequency){
			re = x;
			im = y;
			freq = frequency;
			abs = Math.hypot(x, y);
		}

		public int compareTo(Coefficient c){
			if((this.abs - c.abs)>0.0)//0001)
				return 1;
			/*else if(Math.abs(this.abs-c.abs)<0.00001) {
				if(this.freq<c.freq)
					return 1;
				else
					return -1;
			}*/
			else return -1;
		}
	}

	/**
	 * Compute Mean
	 * @param values
	 * @return
	 */
	private double computeMean(Double values[]){
		double mean = 0.0;
		for(int i=0;i<values.length;i++)
			mean += values[i];
		return mean/values.length;
	}

	/**
	 * Compute Standard Deviation
	 * @param values
	 * @param mean
	 * @return
	 */
	private double computeStdDev(Double values[],double mean){
		double dev = 0.0;
		double diff = 0.0;
		for(int i=0;i<values.length;i++){
			diff = values[i]-mean;
			dev += diff*diff;
		}
		return Math.sqrt(dev/values.length);
	}



	/**
	 * Add accelerometer readings to the window
	 * @param acc_x
	 * @param acc_y
	 * @param acc_z
	 * @param vectorial_speed
	 */
	private void addValues(double acc_x, double acc_y, double acc_z, double vectorial_speed){
		xVector.add(acc_x);
		yVector.add(acc_y);
		zVector.add(acc_z);
		speedVector.add(vectorial_speed);
		energyXYVector.add(Math.sqrt(acc_x*acc_x+acc_y*acc_y));
	}


	/**
	 * Add values to the energy vector for a window
	 * @param acc_x
	 * @param acc_y
	 * @param acc_z
	 */
	private void addEnergyValues(double acc_x, double acc_y, double acc_z){
		energyVector.add(Math.sqrt(acc_x*acc_x+acc_y*acc_y+acc_z*acc_z));
	}


	/**
	 * Add a timestamp for the reading
	 * @param time
	 */
	private void addTime(long time){
		timeVector.add(time);
	}

	private HashSet<String> mergeFiles(String inputDir) {
		String accelFile = inputDir+"/accel.csv";
		String emaFile = inputDir+"/ema.csv";
		String mergeFile = inputDir+"/merge.csv";

		HashSet<String> activities = new HashSet<String>();
		LinkedList<Long> timeList =  new LinkedList<Long>();
		LinkedList<Boolean> isStartList = new LinkedList<Boolean>();
		LinkedList<String> labelList = new LinkedList<String>();

		try{
			BufferedReader br = new BufferedReader(new FileReader(emaFile));
			String s = null;

			//First read all the events
			while((s=br.readLine())!=null) {
				String tokens[] = s.split(",");
				long timeInMillis = Long.parseLong(tokens[0]);
				String label = tokens[4].trim().toLowerCase();
				if(!label.equals("start") && !label.equals("end")) {
					System.out.println("Did not find start or end, Skipping this entry");
					continue;
				}
				boolean isStart = label.equals("start");
				//Read next line to get the activity
				s = br.readLine();
				if(s==null) break;
				tokens = s.split(",");
				String activity = tokens[4].trim().toLowerCase();
				activities.add(activity);

				timeList.add(timeInMillis);
				labelList.add(activity);
				isStartList.add(isStart);
			}
			br.close();
			System.out.println("Finished Reading EMA File: "+emaFile);

		}catch(IOException e){
			e.printStackTrace();
			System.out.println("ERROR: Problem occurred while reading EMA file");
			System.exit(1);
		}

		//Now sanitize the event list
		boolean keep[] = new boolean[timeList.size()];
		for(int i=0;i<keep.length;i++)
			keep[i] = true;
		for(int i=0;i<keep.length;i++) {
			boolean currentLabel = isStartList.get(i);
			boolean nextLabel = (i<keep.length-1?isStartList.get(i+1):true);
			boolean prevLabel = (i>0?isStartList.get(i-1):false);
			if(currentLabel == true && nextLabel == true)
				keep[i] = false; // Encountered two consecutive starts; ignoring the first start
			if(currentLabel == false && prevLabel == false)
				keep[i] = false; // Encountered two consecutive ends; ignoring the second end
		}
		for(int i=keep.length-1;i>=0;i--) {
			if(!keep[i]){
				timeList.remove(i);
				isStartList.remove(i);
				labelList.remove(i);
			}
		}
		System.out.println("Sanitized the event list, number of valid periods found: "+timeList.size()/2);

		try{
			// Now read and write merged file
			BufferedReader br = new BufferedReader(new FileReader(accelFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(mergeFile));
			System.out.println("Writing Merged File: "+mergeFile);
			int cIndex = 0;
			long beginTime = timeList.get(0);
			long endTime = timeList.get(timeList.size()-1);
			String s = null;
			while((s=br.readLine())!=null){
				String tokens[] = s.split(",");
				long time = Long.parseLong(tokens[0]);
				if(time<beginTime)
					continue;
				if(time>endTime)
					break;
				long cStart = timeList.get(cIndex);
				long cEnd = timeList.get(cIndex+1);
				
				while(time>=cEnd) {
					cIndex = cIndex+2;
					if(cIndex>=timeList.size()-1)
						break;
					cStart = timeList.get(cIndex);
					cEnd = timeList.get(cIndex+1);
				}
				if(time<cStart) continue;

				//Now, we have a valid data
				s = s.replace("null", labelList.get(cIndex)); //Update the label
				bw.write(s+"\n");
			}
			System.out.println("Finished Merge");
			br.close();
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return activities;
	}
	
	private void generateArffFile(String inputDir, HashSet<String> activities) {
		String arffFile = inputDir+"/activity-data.arff";
		String mergeFile = inputDir+"/merge.csv";
		String featureNames[] = {"xMean","xDev","xCrossRate","xFFT1","xFFT2","xFFT3","xFFT4","xVelocityChange",
				"xDistance","yMean","yDev","yCrossRate","yFFT1","yFFT2","yFFT3","yFFT4","yVelocityChange",
				"yDistance","zMean","zDev","zCrossRate","zFFT1","zFFT2","zFFT3","zFFT4","zVelocityChange",
				"zDistance","speedMean","speedDev","speedCrossRate","speedFFT1","speedFFT2","speedFFT3",
				"energyMean","energyDev","energyCrossRate","energyFFT1","energyFFT2","energyFFT3","energyFFT4",
				"energyXYMean","energyXYDev","energyXYCrossRate"};
		
		try{
			ReorientAxis roa = new ReorientAxis();
			BufferedReader br = new BufferedReader(new FileReader(mergeFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(arffFile));
			
			//First write the header information
			bw.write("@relation activity\n");
			for(int i=0;i<featureNames.length;i++)
				bw.write("@attribute "+featureNames[i]+" NUMERIC \n");
			String classes = "";
			Iterator<String>it = activities.iterator();
			int activityCount = 0;
			while(it.hasNext()) {
				String activity = it.next();
				classes+= ((activityCount>0?",":"")+activity);
				activityCount++;
			}
			bw.write("@attribute class{"+classes+"}\n");
			bw.write("@data\n");
			
			String s = null;
			String lastActivity = null;
			while((s=br.readLine())!=null) {
				String tokens[] = s.split(",");
				long time = Long.parseLong(tokens[0]);
				double acc_x = Double.parseDouble(tokens[1]);
				double acc_y = Double.parseDouble(tokens[2]);
				double acc_z = Double.parseDouble(tokens[3]);
				String activity = tokens[4].trim();
				double ort[] = roa.getReorientedValues(acc_x, acc_y, acc_z);
				Double features[] = extractFeatures(time, ort[0], ort[1], ort[2], acc_x, acc_y, acc_z);
				if(features!=null) {
					String featureVector = "";
					for(int i=0;i<features.length;i++)
						featureVector+= ((i>0?",":"")+features[i]);
					if(lastActivity!=null) {
						featureVector+=(","+lastActivity);
						bw.write(featureVector+"\n");
					}
				}
				lastActivity = activity;
			}
			br.close();
			bw.close();
			System.out.println("Successfully generated Arff File:"+arffFile);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void processFiles(String inputDir) {
		HashSet<String> activities = mergeFiles(inputDir);
		generateArffFile(inputDir, activities);
		
	}
	
	public static void main(String args[]) {
		String INPUT_DIR = "/home/msteele/Downloads/weka";
		ActivityFeatureExtractor afe = new ActivityFeatureExtractor(5000);
		afe.processFiles(INPUT_DIR);
	}

}
