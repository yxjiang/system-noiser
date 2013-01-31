package edu.fiu.yxjiang.system.noiser.worker;

public abstract class NoiseMaker extends Thread{
	protected boolean stop;
	
	public NoiseMaker() {
		this.stop = false;
	}
	
	public synchronized void stopNoiseMaker() {
		this.stop = true;
	}
	
}
