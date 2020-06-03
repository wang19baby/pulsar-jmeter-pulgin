package org.wang19.jmeter.pulgin;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

public class PulsarProducerSamplerGui extends AbstractPulsarSamplerGui {

	private static final long serialVersionUID = 1L;

	private JTextField pulsarBrokers;
	private JTextField pulsarTopic;
	private JTextArea requestData;

	public PulsarProducerSamplerGui() {
		init();
	}

	@Override
	public String getStaticLabel() {
		return "Pulsar Producer";
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);

		this.pulsarBrokers.setText(element.getPropertyAsString(this.getName() + PulsarProducerSampler.PULSAR_BROKERS));
		this.pulsarTopic.setText(element.getPropertyAsString(this.getName() + PulsarProducerSampler.PULSAR_TOPIC));
		this.requestData.setText(element.getPropertyAsString(this.getName() + PulsarProducerSampler.REQUEST_DATA));
	}

	@Override
	public TestElement createTestElement() {
		PulsarProducerSampler sampler = new PulsarProducerSampler();
		modifyTestElement(sampler);
		return sampler;
	}

	@Override
	public void modifyTestElement(TestElement sampler) {
		super.configureTestElement(sampler);

		if ((sampler instanceof PulsarProducerSampler)) {
			PulsarProducerSampler pulsarSampler = (PulsarProducerSampler) sampler;
			pulsarSampler.setPulsarBrokers(this.pulsarBrokers.getText(),sampler);
			pulsarSampler.setPulsarTopic(this.pulsarTopic.getText(),sampler);
			pulsarSampler.setRequestData(this.requestData.getText(),sampler);
		}
	}

	@Override
	public void clearGui() {
		super.clearGui();

		this.pulsarBrokers.setText("");
		this.pulsarTopic.setText("");
		this.requestData.setText("");
	}

	private void init() {
		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());

		add(makeTitlePanel(), "North");

		VerticalPanel mainPanel = new VerticalPanel();

		this.pulsarBrokers = new JTextField();
		mainPanel.add(labeledField(PulsarProducerSampler.PULSAR_BROKERS, this.pulsarBrokers));

		this.pulsarTopic = new JTextField();
		mainPanel.add(labeledField(PulsarProducerSampler.PULSAR_TOPIC, this.pulsarTopic));

		mainPanel.add(new JLabel(PulsarProducerSampler.REQUEST_DATA));
		this.requestData = new JTextArea();
        JScrollPane js=new JScrollPane(this.requestData);
		this.requestData.setRows(28);
        this.requestData.setColumns(50);
        js.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(js);

		mainPanel.setBorder(BorderFactory.createEtchedBorder());

		add(mainPanel, "Center");
	}

}
