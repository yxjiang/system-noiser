package edu.fiu.yxjiang.system.noiser.worker;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestCPUEater {

	@Test
	public void testCPUEater() {
		
		for(int i = 0; i < 5; ++i) {
			System.out.printf("Round %d.\n", i + 1);
			CPUEater eater = new CPUEater(5000);
			eater.start();
			try {
				Thread.sleep(5000);
				eater.stopNoiseMaker();
				eater.join();
				System.out.println("send stop.");
				Thread.sleep(6000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		
//		List<String> l = new ArrayList<String>();
//		l.add("aa");
//		
//		class T extends Thread {
//			private volatile List<String> l;
//			public T(List<String> l) {
//				this.l = l;
//			}
//			public void run() {
//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				synchronized(l) {
//					System.out.printf("after sleep, %s\n", l.get(0));
//				}
//			}
//		}
//		
//		Thread t = new T(l);
//		
//		t.start();
//		try {
//			Thread.sleep(1000);
//			l.set(0, "bbb");
//			t.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println("main");
		
		
	}
}
