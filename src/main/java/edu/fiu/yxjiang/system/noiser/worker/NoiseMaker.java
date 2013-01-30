package edu.fiu.yxjiang.system.noiser.worker;

public abstract class NoiseMaker extends Thread{
	protected boolean stop;
	
	public synchronized void stopNoiseMaker() {
		this.stop = false;
	}
	
}
