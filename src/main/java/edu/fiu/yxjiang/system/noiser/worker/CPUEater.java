package edu.fiu.yxjiang.system.noiser.worker;

/**
 * Generate the CPU noise.
 * @author Yexi Jiang (http://users.cs.fiu.edu/~yjian004)
 *
 */
public class CPUEater extends NoiseMaker{
	
	private int availableCores;
	
	public CPUEater() {
		availableCores = Runtime.getRuntime().availableProcessors();
		System.out.printf("\tThere are [%d] cores available.", availableCores);
//		System.out.println("Available cores " + availableCores);
	}
	
	public void run() {
		for(int i = 0; i < availableCores; ++i) {
			ThreadWorker threadWorker = new ThreadWorker(i);
			threadWorker.start();
		}
	}
	
	class ThreadWorker extends Thread {
		private int idx = 0;
		
		public ThreadWorker(int idx) {
			this.idx = idx;
		}
		
		public void run() {
			System.out.printf("\tThread %d started...\n", idx);
			while(true) {
				for(int i = 1; i < Integer.MAX_VALUE; ++i	) {
					//	do some computation
					double ans = Math.sqrt(i) + Math.log(i);
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized(this) {
					if(stop == true) {
						System.out.printf("\tThread %d terminated...\n", idx);
						return;
					}
				}
			}
		}
	}
	
}
