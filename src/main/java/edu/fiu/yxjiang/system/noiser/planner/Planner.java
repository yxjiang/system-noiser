package edu.fiu.yxjiang.system.noiser.planner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * The noiser planner is in charge of reading the noise plan
 * and assigning the noises generation tasks to workers.
 * @author Yexi Jiang (http://users.cs.fiu.edu/~yjian004)
 *
 */
public class Planner {
	
	private static NoiseTypes noiseTypes = new NoiseTypes();
	
	private int relativeTimestamp;
	private List<Task> tasks;
	
	public Planner(String planFile) {
		this.relativeTimestamp = 0;
		this.tasks = new ArrayList<Task>();
		parsePlan(planFile);
	}
	
	private void parsePlan(String planFile) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(planFile)));
			String line;
			while((line = br.readLine()) != null) {
				if(line.trim().length() == 0) {
					continue;
				}
				String tokens[] = line.split("\t");
				if(tokens.length != 4) {
					continue;
				}
				try{
					int startTime = Integer.parseInt(tokens[1]);
					int endTime = Integer.parseInt(tokens[2]);
					if(endTime <= startTime) {
						continue;
					}
					if(noiseTypes.isValid(tokens[3])) {
						Task task = new Task(tokens[0], startTime, endTime, tokens[3]);
						tasks.add(task);
					}
				} catch(NumberFormatException e) {
					continue;
				}
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.printTasks();
	}
	
	public void printTasks() {
		for(int i = 0; i < tasks.size(); ++i) {
			Task task = tasks.get(i);
			System.out.printf("#%d: %s\n", i + 1, task);
		}
	}
	
	/**
	 * Check the tasks list every second and send triggered tasks to workers.
	 */
	public void run() {
		while(true) {
			System.out.println(new Date().getTime() / 1000);
			if(tasks.size() == 0) {
				break;
			}
			List<Task> triggeredTasks = new ArrayList<Task>();
			for(int i = 0; i < tasks.size(); ++i) {
				Task task = tasks.get(i);
				long startTime = task.getStartTime();
				if(startTime == relativeTimestamp) {
					triggeredTasks.add(task);
					Thread sender = new Sender(task);
					sender.start();
				}
			}
			for(Task task : triggeredTasks) {
				tasks.remove(task);
			}
			++relativeTimestamp;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Send the task to specified worker.
	 * @author Yexi Jiang (http://users.cs.fiu.edu/~yjian004)
	 *
	 */
	class Sender extends Thread {
		
		private static final String WORKER_BROKER_PORT = "33156";
		private static final String TOPIC_NAME = "task";
		private Task task;
		
		public Sender(Task task) {
			this.task = task;
		}
		
		public void run() {
			String workerBrokerAddress = "tcp://" + task.getAddress() + ":" + WORKER_BROKER_PORT;
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(workerBrokerAddress);
			try {
				Connection connection = connectionFactory.createConnection();
				connection.start();
				
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Destination destination = session.createTopic(TOPIC_NAME);
				MessageProducer producer = session.createProducer(destination);
//				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				ObjectMessage message = session.createObjectMessage();
				message.setObject(task);
				producer.send(message);
				System.err.printf("Send task to [%s].\n", task.getAddress());
				producer.close();
				session.close();
				connection.close();
			} catch (JMSException e) {
				System.err.printf("Send task to [%s] failed.\n", task.getAddress());
			}
		}
	}
	
	public static void main(String[] args) {
		String filename = "/home/yxjiang/Downloads/testTasks.txt";
		Planner planner = new Planner(filename);
		planner.run();
	}
	
}
