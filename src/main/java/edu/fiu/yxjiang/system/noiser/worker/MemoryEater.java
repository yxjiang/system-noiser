package edu.fiu.yxjiang.system.noiser.worker;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemoryEater extends NoiseMaker {

	private static final double PERCENTAGE = 0.8;
	private static final String MEM_FILE = "/proc/meminfo";

	private long totalMemory;
	private long freeMemory;
	private long bufferMemory;
	private long cacheMemory;
	
	private int timeout;

	public MemoryEater(int timeout) {
		this.timeout = timeout;
		this.refresh();
	}

	private void refresh() {
		try {
			Scanner scanner = new Scanner(new FileReader(MEM_FILE));
			scanner.next();
			this.totalMemory = scanner.nextLong();
			scanner.next();

			scanner.next();
			this.freeMemory = scanner.nextLong();
			scanner.next();

			scanner.next();
			this.bufferMemory = scanner.nextLong();
			scanner.next();

			scanner.next();
			this.cacheMemory = scanner.nextLong();
			scanner.next();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printMemInfo() {
		refresh();
		System.out.printf("Total: %d, Buffer: %d, Cache: %d Free: %d, %f\n",
				totalMemory, bufferMemory, cacheMemory, freeMemory, (double) freeMemory
						/ totalMemory);
	}

	public void run() {
		int sec = 0;

		List<ArrayList<Double>> memoryConsumer = new ArrayList<ArrayList<Double>>();
		while (true) {
			refresh();
			if (freeMemory > (double) totalMemory * 0.2) {
				memoryConsumer.add(new ArrayList<Double>(300000000));
			}
			printMemInfo();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(sec++ > timeout) {
				return;
			}
		}

	}
	
	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.printf("usage: java -Xmx6g -jar edu.fiu.yxjiang.system.noiser.worker.MemoryEater timeout\n");
			return;
		}
		int to = Integer.parseInt(args[0]);
		MemoryEater eater = new MemoryEater(to);
		eater.start();
	}

}
