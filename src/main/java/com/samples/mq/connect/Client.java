
package com.samples.mq.connect;

import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ibm.mq.headers.pcf.PCFMessageAgent;

/**
 * @author vandan.gognaibm.com
 *
 */
public class Client {
	private static String BASE_RESOURCE_PATH = "src/resources";

	public static void main(String[] args) {
		MQQueueManager mqQueueManager = null;
		PCFMessageAgent pcfAgent = null;
		try {
			FileReader reader = new FileReader(String.format("%s/config.properties", BASE_RESOURCE_PATH));
			Properties configProps = new Properties();
			configProps.load(reader);

			/*
			 * REQD FOR mutual SSL communication if enabled on the server side If mutual SSL
			 * is not required, comment out the next 4 lines below
			 */
			System.setProperty("javax.net.ssl.trustStore",
					String.format("%s/%s", BASE_RESOURCE_PATH, configProps.getProperty("SSL_TRUST_STORE_FILE")));
			System.setProperty("javax.net.ssl.trustStorePassword", configProps.getProperty("SSL_TRUST_STORE_PASSWORD"));
			System.setProperty("javax.net.ssl.keyStore",
					String.format("%s/%s", BASE_RESOURCE_PATH, configProps.getProperty("SSL_KEY_STORE_FILE")));
			System.setProperty("javax.net.ssl.keyStorePassword", configProps.getProperty("SSL_KEY_STORE_PASSWORD"));

			// REQUIRED for Oracle \ Open JDK
			System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");

			mqQueueManager = new MQQueueManager(configProps.getProperty("QUEUE_MANAGER_NAME"),
					getConnectionProperties(configProps));
			pcfAgent = new PCFMessageAgent(mqQueueManager);

			String sampleQueueName = configProps.getProperty("SAMPLE_QUEUE_NAME");
			createQueue(pcfAgent, sampleQueueName);
			System.out.println(String.format("Created Queue '%s' successfully\n", sampleQueueName));

			deleteQueue(pcfAgent, sampleQueueName);
			System.out.println(String.format("Deleted Queue '%s' successfully\n", sampleQueueName));

			System.out.println("Listing all Queues: ");
			listQueues(pcfAgent);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			try {
				pcfAgent.disconnect();
				mqQueueManager.disconnect();
			} catch (MQException | MQDataException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param pcfAgent - agent to send the request to the MQ server instance
	 * @throws PCFException    -
	 * @throws MQDataException
	 * @throws IOException
	 */
	public static void listQueues(PCFMessageAgent pcfAgent) throws PCFException, MQDataException, IOException {
		int index = 0;
		int[] queueAttributes = new int[] { CMQC.MQCA_Q_NAME, CMQC.MQIA_Q_TYPE };
		PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
		pcfMessage.addParameter(CMQC.MQCA_Q_NAME, "*");
		pcfMessage.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);
		pcfMessage.addParameter(CMQCFC.MQIACF_Q_ATTRS, queueAttributes);
		PCFMessage[] responseList = pcfAgent.send(pcfMessage);		
		for (PCFMessage response : responseList) {
			System.out.println(String.format("%d - queue name: '%s', type: '%s'", ++index,
					response.getStringParameterValue(CMQC.MQCA_Q_NAME).trim(),
					response.getIntParameterValue(CMQC.MQIA_Q_TYPE)));
		}
		System.out.println(String.format("Retrieved %d queues successfully", responseList.length));
	}

	/**
	 * @param pcfAgent  - agent to send the request to the MQ server instance
	 * @param queueName - name of the new queue to be created
	 * @throws PCFException
	 * @throws MQDataException
	 * @throws IOException
	 */
	public static void createQueue(PCFMessageAgent pcfAgent, String queueName)
			throws PCFException, MQDataException, IOException {
		PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_CREATE_Q);
		pcfMessage.addParameter(CMQC.MQCA_Q_NAME, queueName);
		pcfMessage.addParameter(CMQC.MQIA_Q_TYPE, 1);
		pcfAgent.send(pcfMessage);
	}

	/**
	 * @param pcfAgent  - agent to send the request to the MQ server instance
	 * @param queueName - name of the queue to be deleted
	 * @throws PCFException
	 * @throws MQDataException
	 * @throws IOException
	 */
	public static void deleteQueue(PCFMessageAgent pcfAgent, String queueName)
			throws PCFException, MQDataException, IOException {
		PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_DELETE_Q);
		pcfMessage.addParameter(CMQC.MQCA_Q_NAME, queueName);
		pcfAgent.send(pcfMessage);

	}

	private static Hashtable<String, Object> getConnectionProperties(Properties configurationProperties) {
		Hashtable<String, Object> connectionProps = new Hashtable<String, Object>();
		/**
		 * Select the correct SSL_CIPHER_SUITE_PROPERTY as configured on the MQ instance
		 * such as SSL_ECDHE_RSA_WITH_AES_128_CBC_SHA256 or
		 * TLS_RSA_WITH_AES_128_CBC_SHA256
		 */

		connectionProps.put(MQConstants.SSL_CIPHER_SUITE_PROPERTY, configurationProperties.get("CIPHER_SUITE"));

		connectionProps.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);
		connectionProps.put(MQConstants.HOST_NAME_PROPERTY, configurationProperties.get("HOSTNAME"));
		connectionProps.put(MQConstants.CHANNEL_PROPERTY, configurationProperties.get("CHANNEL"));
		connectionProps.put(MQConstants.PORT_PROPERTY, Integer.valueOf(configurationProperties.get("PORT").toString()));
		connectionProps.put(MQConstants.USER_ID_PROPERTY, configurationProperties.get("USERNAME"));
		connectionProps.put(MQConstants.PASSWORD_PROPERTY, configurationProperties.get("PASSWORD"));
		connectionProps.put(MQConstants.USE_MQCSP_AUTHENTICATION_PROPERTY, Boolean.TRUE);
		return connectionProps;

	}
}
