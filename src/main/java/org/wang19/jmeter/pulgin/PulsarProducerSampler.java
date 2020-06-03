package org.wang19.jmeter.pulgin;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PulsarProducerSampler extends AbstractPulsarSampler {

	private static final Logger logger = LoggingManager.getLoggerForClass();

	private static final int TIMEOUT = 3000;

	public static String PULSAR_BROKERS = "Pulsar brokers";
	public static String PULSAR_TOPIC = "Pulsar topic";
	public static String REQUEST_DATA = "Request Data";
    public static Map<String,Producer<byte[]>> producerMap = new HashMap<>();

    private PulsarClient client;

    public void PulsarProducerSampler(){
        createPulsarClient();
    }


    public void createPulsarClient(){
        Map<String,Object> clientConf = new HashMap<>(10);
        clientConf.put("numIoThreads",10);
        clientConf.put("numListenerThreads",10);
        try {
            client = PulsarClient.builder()
                    .serviceUrl(getPulsarBrokers(this))
                    .loadConf(clientConf)
                    .build();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

	@Override
	public boolean interrupt() {
		logger.info("Interrupting " + getName());
        try {
            closeProducer();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        return true;
	}

	private void closeProducer() throws PulsarClientException {
		if (producerMap.containsKey(this.getName())) {
            producerMap.get(this.getName()).close();
            producerMap.remove(this.getName());
		}
	}

	@Override
	public SampleResult sample(Entry entry) {

		SampleResult result = newSampleResult();
		String message = getRequestData(this);
        JMeterVariables variables = getThreadContext().getVariables();
        logger.info("Iteration:"+variables.getIteration());
        Set<Map.Entry<String, Object>> iter = variables.entrySet();

        logger.info("set:"+iter.size());
        iter.forEach(e -> {
            logger.info("key: "+ e.getKey()+" value:"+e.getValue());
            message.replace("${"+e.getKey()+"}",e.getValue().toString());
        }); //message.replace("${"+e.toString()+"}",variables.get(e.toString()).toString()));

        logger.info("message:"+message);
        try {

            sampleResultStart(result, message);
            logger.info("Sending message " + message);
            Producer<byte[]> producer = getProducer(this);
            producer.sendAsync(message.getBytes(StandardCharsets.UTF_8)).thenAccept(msgId -> {
                logger.info("Message with ID "+msgId+" successfully sent");
            });
            sampleResultSuccess(result, message);
        } catch (PulsarClientException e) {
            logger.error("Failed consuming pulsar message", e);
            sampleResultFailed(result, "500", e);
        }
		return result;
	}

	private Producer<byte[]> getProducer(TestElement element) throws PulsarClientException {
        Producer<byte[]> producer = null;
		if (!producerMap.containsKey(element.getName())) {
		    if(null == client){
                createPulsarClient();
            }
            producer = client.newProducer()
                    .topic(getPulsarTopic(this))
                    .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS)
                    .sendTimeout(10, TimeUnit.SECONDS)
                    .blockIfQueueFull(true)
                    .create();
            producerMap.put(element.getName(),producer);

			logger.info("Producer created for topic=" + getPulsarTopic(this) + " and brokers=" + getPulsarBrokers(this));
		}else{
            producer = producerMap.get(this.getName());
        }

		return producer;
	}

	//================================================================

	public void setPulsarBrokers(String text,TestElement element) {
        element.setProperty(element.getName() + PULSAR_BROKERS, text);
	}

	public String getPulsarBrokers(TestElement element) {
		return element.getPropertyAsString(element.getName() + PULSAR_BROKERS);
	}

	public void setPulsarTopic(String text,TestElement element) {
        element.setProperty(element.getName() + PULSAR_TOPIC, text);
	}

	public String getPulsarTopic(TestElement element) {
		return element.getPropertyAsString(element.getName() + PULSAR_TOPIC);
	}

	public void setRequestData(String text,TestElement element) {
        element.setProperty(element.getName() + REQUEST_DATA, text);
	}

	public String getRequestData(TestElement element) {
		return element.getPropertyAsString(element.getName() + REQUEST_DATA);
	}

}
