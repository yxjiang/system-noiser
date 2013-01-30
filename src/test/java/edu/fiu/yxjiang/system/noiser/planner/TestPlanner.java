package edu.fiu.yxjiang.system.noiser.planner;

import org.junit.Before;
import org.junit.Test;

public class TestPlanner {
	
	@Before
	public void before() {
		
	}
	
	@Test
	public void testParseFile() {
		String filename = "/home/yxjiang/Downloads/testTasks.txt";
		Planner planner = new Planner(filename);
		planner.run();
	}
	
}
