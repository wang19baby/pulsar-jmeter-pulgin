package org.wang19.jmeter.pulgin;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.pulsar.client.api.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PulsarConsumerSampler extends AbstractPulsarSampler {

	private static final Logger logger = LoggingManager.getLoggerForClass();

	public static String PULSAR_BROKERS = "Pulsar brokers";
	public static String PULSAR_TOPIC = "Pulsar topic";
	public static String SUBSCRIPTION = "subscription";

	private static int STREAM_COUNT = 1;

	private Consumer consumer;

	private boolean interrupted = false;

	@Override
	public boolean interrupt() {
		logger.info("Interrumping " + getName() + "(" + getSamplerId() + ")");
		interrupted = true;
		return true;
	}

	@Override
	public SampleResult sample(Entry entry) {
		SampleResult result = newSampleResult();
		sampleResultStart(result, null);

		try {
			String message = null;
			boolean firstPool = true;

			String resultData = null;
            getConsumer();
            // Wait for a message
            CompletableFuture<Message> msg = consumer.receiveAsync();
            resultData = new String(msg.get().getData(), StandardCharsets.UTF_8);
            logger.info("Message id: "
                        +msg.get().getMessageId()+" topicName:"
                        +msg.get().getTopicName()+" publishTime:"
                        +msg.get().getPublishTime()+" consumer id:"
                        +consumer.getConsumerName()+" Message received: "
                        +resultData);

            // Acknowledge the message so that it can be deleted by the message broker
            consumer.acknowledge(msg.get());
			sampleResultSuccess(result, message);
		} catch (Exception e) {
			logger.error("Failed consuming pulsar message", e);
			sampleResultFailed(result, "500", e);
		}

		return result;
	}

	private String getSamplerId() {
		return getSubscription() + "-" + Thread.currentThread().getId();
	}

	private Consumer getConsumer() throws PulsarClientException {
		if (consumer == null) {
            Map<String,Object> clientConf = new HashMap<>(10);
            clientConf.put("numIoThreads",10);
            clientConf.put("numListenerThreads",10);
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(getPulsarBrokers())
                    .loadConf(clientConf)
                    .build();

            int maxRedeliveryCount = 10;
			consumer = client.newConsumer()
                    .topic(getPulsarTopic())
                    .subscriptionName(getSubscription())
                    .ackTimeout(10, TimeUnit.SECONDS)
                    .subscriptionType(SubscriptionType.Shared)
                    .deadLetterPolicy(DeadLetterPolicy.builder()
                            .maxRedeliverCount(maxRedeliveryCount)
                            .build())
                    .subscribe();
			logger.info("Connecting to topic=" + getPulsarTopic() + ", group=" + getSubscription() + " and pulsar brokers=" + getPulsarBrokers());
		}

		return consumer;
	}

	public void setPulsarBrokers(String text) {
		setProperty(PULSAR_BROKERS, text);
	}

	public String getPulsarBrokers() {
		return getPropertyAsString(PULSAR_BROKERS);
	}

	public void setSubscription(String text) {
		setProperty(SUBSCRIPTION, text);
	}

	public String getSubscription() {
		return getPropertyAsString(SUBSCRIPTION);
	}

	public void setPulsarTopic(String text) {
		setProperty(PULSAR_TOPIC, text);
	}

	public String getPulsarTopic() {
		return getPropertyAsString(PULSAR_TOPIC);
	}

}
