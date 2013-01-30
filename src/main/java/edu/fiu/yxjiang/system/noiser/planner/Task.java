package edu.fiu.yxjiang.system.noiser.planner;

import java.io.Serializable;
import java.util.UUID;

/**
 * Noise task.
 * @author Yexi Jiang (http://users.cs.fiu.edu/~yjian004)
 *
 */
public class Task implements Serializable{
	
	private UUID uuid;
	
	private String address;
	private int startTime;
	private int endTime;
	private String type;

	public Task(String address, int startTime, int endTime, String type) {
		this.uuid = UUID.randomUUID();
		this.address = address;
		this.startTime = startTime;
		this.endTime = endTime;
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return "Add noise (" + type + ")" + " to {" + address + "} during time [" + startTime + " - " + endTime + "]";
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Task) {
			Task otherTask = (Task)obj;
			return otherTask.uuid.equals(uuid);
		}
		return false;
	}
	
}
