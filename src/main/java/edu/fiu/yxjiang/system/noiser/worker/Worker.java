package edu.fiu.yxjiang.system.noiser.worker;

import java.io.IOException;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import sysmon.util.IPUtil;
import edu.fiu.yxjiang.system.noiser.planner.Task;

public class Worker implements MessageListener{

	private static final String SERVICE_PORT = "33156";
	private static final String TOPIC_NAME = "task";
	
	private String brokerAddress;
	private BrokerService broker;
	
	private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private Destination destionation;
	private MessageConsumer consumer;
	
	public Worker() {
		brokerAddress = "tcp://" + IPUtil.getFirstAvailableIP() + ":" + SERVICE_PORT;
		startBroker();
		init();
	}

	private void startBroker() {
		this.broker = new BrokerService();
		this.broker.setPersistent(true);
		try {
			this.broker.addConnector(brokerAddress);
			this.broker.setUseJmx(false);
			this.broker.start();
			System.out.printf("Broker service (%s) started...\n", brokerAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		this.connectionFactory = new ActiveMQConnectionFactory(brokerAddress);
		try {
			this.connection = this.connectionFactory.createConnection();
			this.connection.start();
			this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			this.destionation = this.session.createTopic(TOPIC_NAME);
			this.consumer = this.session.createConsumer(this.destionation);
			this.consumer.setMessageListener(this);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}
	
	public void onMessage(Message message) {
		if(message instanceof ObjectMessage) {
			try {
				Task task = (Task)((ObjectMessage)message).getObject();
				Thread noiseExecutor = new NoiseExecutor(task);
				noiseExecutor.start();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
	
	class NoiseExecutor extends Thread {
		private int duration;
		private String type;
		
		public NoiseExecutor(Task task) {
			this.duration = task.getEndTime() - task.getStartTime();
			this.type = task.getType();
		}
		
		public void run() {
			NoiseMaker noiseMaker = null;
			if(type.equals("cpu")) {
				noiseMaker = new CPUEater();
				System.out.printf("At time (%d), execute [%s] noise.\n", new Date().getTime() / 1000, this.type);
			}
			else if(type.equals("memory")) {
				try {
					Runtime.getRuntime().exec("sh memoryEater.sh " + duration);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.printf("At time (%d), execute [%s] noise.\n", new Date().getTime() / 1000, this.type);
			}
			if(noiseMaker != null) {
				noiseMaker.start();
			}
			try {
				Thread.sleep(duration * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(noiseMaker != null) {
				noiseMaker.stopNoiseMaker();
			}
			System.out.printf("At time (%d), stop [%s] noise.\n", new Date().getTime() / 1000, this.type);
		}
		
	}
	
	public static void main(String[] args) {
		Worker worker = new Worker();
	}
}
