package org.wang19.jmeter.pulgin;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPulsarSamplerGui extends AbstractSamplerGui {

	@Override
	public String getLabelResource() {
		return getClass().getSimpleName();
	}

	protected Container labeledField(String label, JComponent component) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel(label));
		hp.add(component);
		return hp;
	}
}
