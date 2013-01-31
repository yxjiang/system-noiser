package edu.fiu.yxjiang.system.noiser.worker;

import java.util.Random;

/**
 * Generate the CPU noise.
 * @author Yexi Jiang (http://users.cs.fiu.edu/~yjian004)
 *
 */
public class CPUEater extends NoiseMaker{
	
	private int availableCores;
	private Thread threads[];
	private int duration;
	private volatile Object lock = new Object();
	
	public CPUEater(int duration) {
		super();
		this.duration = duration;
		availableCores = Runtime.getRuntime().availableProcessors();
		System.out.printf("\tThere are [%d] cores available.\n", availableCores);
		threads = new Thread[availableCores];
	}
	
	public void run() {
		for(int i = 0; i < availableCores; ++i) {
			threads[i] = new ThreadWorker(i);
			threads[i].start();
		}
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized(lock) {
			this.stop = true;
		}
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	class ThreadWorker extends Thread {
		private int idx = 0;
		
		public ThreadWorker(int idx) {
			this.idx = idx;
			System.out.printf("\tThread %d start...\n", idx);
		}
		
		public void run() {
			stop = false;
			Random rnd = new Random();
			while(true) {
				for(int j = 0; j < 2; ++j) {
					double sum = 0;
					for(int i = 1; i < 2000000; ++i	) {
					//	do some computation
						sum += rnd.nextInt(100000);
					}
				}
//				System.out.println("\tCount " + idx);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized(lock) {
					if(stop == true) {
						System.out.printf("\tThread %d terminated...\n", idx);
						return;
					}
				}
			}
		}
	}
	
}
