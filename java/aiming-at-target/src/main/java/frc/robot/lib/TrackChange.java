package org.aa8426.lib;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.Pair;

/** 
 * Class to track changes in a series of values over time. Can calculate rate and acceleration.
 * 
 * Used for balancing. 
 */
public class TrackChange {

	public List<Pair<Double, Double>> vals = new ArrayList<>();	
	public double maxSampleRate = 0.10;	
	
	public Pair<Double, Double> lastSample() {
		return vals.size() == 0 ? null : vals.get(vals.size()-1);	
	}
	
	
	public double getRate() {
		if (vals.size() < 2) {
			return 0.0;
		}
		Pair<Double, Double> lastSample = vals.get(vals.size()-1);
		Pair<Double, Double> secondToLastSample = vals.get(vals.size()-2);		
		double diff = lastSample.getFirst() - secondToLastSample.getFirst();
		double rate = (lastSample.getSecond() - secondToLastSample.getSecond()) * (1/diff);
		return rate;
	}

	public double getRawRate() {
		if (vals.size() < 2) {
			return 0.0;
		}
		Pair<Double, Double> lastSample = vals.get(vals.size()-1);
		Pair<Double, Double> secondToLastSample = vals.get(vals.size()-2);		
		double diff = lastSample.getFirst() - secondToLastSample.getFirst();		
		return diff;
	}

	public boolean haveValidRate() {
		return (vals.size() < 2);
	}
	
	public double getAccel() {
		if (vals.size() < 3) {
			return 0.0;
		}
		Pair<Double, Double> lastSample = vals.get(vals.size()-1);
		Pair<Double, Double> secondToLastSample = vals.get(vals.size()-2);
		Pair<Double, Double> thirdToLastSample = vals.get(vals.size()-3);
		double diff1 = lastSample.getFirst() - secondToLastSample.getFirst();
		double rate1 = (lastSample.getSecond() - secondToLastSample.getSecond()) * (1/diff1);
		
		double diff2 = secondToLastSample.getFirst() - thirdToLastSample.getFirst();
		double rate2 = (secondToLastSample.getSecond() - thirdToLastSample.getSecond()) * (1/diff2);
		
		
		double diff3 = lastSample.getFirst() - thirdToLastSample.getFirst();		
		double accel = (rate1 - rate2) * (1/diff3);
		return accel;
	}
	
	public void addVal(double currentTime, double val) {		
		Pair<Double, Double> lastSample = lastSample();
		if (lastSample != null) {
			if ((currentTime - lastSample.getFirst()) < maxSampleRate) {
				System.out.println("Sampling rate too high ("+(currentTime - lastSample.getFirst())+"), tossed entry: "+val);
				return; // ignore attempts to add samples too quickly
			}
		}
		vals.add(Pair.of(currentTime, val));		
		
		if (vals.size() > 3) { 
			vals.remove(0);
		}
	}
	
	@Override
	public String toString() {
		return String.format("sampleAt: %8.2f, lastVal: %8.2f, rate: %8.2f, accel: %8.2f", 
		        lastSample().getFirst(), lastSample().getSecond(), 
				this.getRate(), 
				this.getAccel());
	}
	
	public static void main(String[] args) {
		TrackChange tc = new TrackChange();
		double totalAngle = 0;
		for(int x=1000;x<4000;x=x+200) {
			totalAngle = totalAngle + (Math.random() * 45);
			tc.addVal(x/1000.0, Math.sin(Math.toRadians(totalAngle)));
			//ct.addVal(x/1000.0, totalAngle);
			System.out.println(tc.toString());
		}
	}


	public boolean isAccelValid() {
		return vals.size() >= 3;
	}


    public boolean isRateValid() {
        return vals.size() >= 2;
    }
}
