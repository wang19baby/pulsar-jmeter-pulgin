package org.wang19.jmeter.pulgin;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

public class PulsarConsumerSamplerGui extends AbstractPulsarSamplerGui {

	private static final long serialVersionUID = 1L;

	private JTextField pulsarBrokers;
	private JTextField pulsarTopic;
	private JTextField subscription;

	public PulsarConsumerSamplerGui() {
		init();
	}

	@Override
	public String getStaticLabel() {
		return "Pulsar Consumer";
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);

		this.pulsarBrokers.setText(element.getPropertyAsString(PulsarConsumerSampler.PULSAR_BROKERS));
		this.pulsarTopic.setText(element.getPropertyAsString(PulsarConsumerSampler.PULSAR_TOPIC));
		this.subscription.setText(element.getPropertyAsString(PulsarConsumerSampler.SUBSCRIPTION));
	}

	@Override
	public TestElement createTestElement() {
		PulsarConsumerSampler sampler = new PulsarConsumerSampler();
		modifyTestElement(sampler);
		return sampler;
	}

	@Override
	public void modifyTestElement(TestElement sampler) {
		super.configureTestElement(sampler);

		if ((sampler instanceof PulsarConsumerSampler)) {
			PulsarConsumerSampler pulsarSampler = (PulsarConsumerSampler) sampler;
			pulsarSampler.setPulsarBrokers(this.pulsarBrokers.getText());
			pulsarSampler.setPulsarTopic(this.pulsarTopic.getText());
			pulsarSampler.setSubscription(this.subscription.getText());
		}
	}

	@Override
	public void clearGui() {
		super.clearGui();

		this.pulsarBrokers.setText("");
		this.pulsarTopic.setText("");
		this.subscription.setText("");
	}

	private void init() {
		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());

		add(makeTitlePanel(), "North");

		VerticalPanel mainPanel = new VerticalPanel();

		this.pulsarBrokers = new JTextField();
		mainPanel.add(labeledField(PulsarConsumerSampler.PULSAR_BROKERS, this.pulsarBrokers));

		this.pulsarTopic = new JTextField();
		mainPanel.add(labeledField(PulsarConsumerSampler.PULSAR_TOPIC, this.pulsarTopic));

		this.subscription = new JTextField();
		mainPanel.add(labeledField(PulsarConsumerSampler.SUBSCRIPTION, this.subscription));

		mainPanel.setBorder(BorderFactory.createEtchedBorder());

		add(mainPanel, "Center");
	}

}
