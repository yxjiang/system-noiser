package edu.fiu.yxjiang.system.noiser.planner;

import java.util.HashSet;
import java.util.Set;

public class NoiseTypes {
	private static Set<String> noiseTypes = new HashSet<String>();
	
	static {
		noiseTypes.add("cpu");
		noiseTypes.add("memory");
		noiseTypes.add("io");
	}
	
	public static boolean isValid(String type) {
		return noiseTypes.contains(type);
	}
	
}
