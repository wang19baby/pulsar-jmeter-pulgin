package org.wang19.jmeter.pulgin;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AbstractPulsarSampler extends AbstractSampler implements Interruptible {

	private static final String ENCODING = "UTF-8";

	protected SampleResult newSampleResult() {
		SampleResult result = new SampleResult();
		result.setSampleLabel(getName());
		result.setDataEncoding(ENCODING);
		result.setDataType(SampleResult.TEXT);
		return result;
	}

	protected void sampleResultStart(SampleResult result, String data) {
		result.setSamplerData(data);
		result.sampleStart();
	}

	protected void sampleResultSuccess(SampleResult result, String response) {
		result.sampleEnd();
		result.setSuccessful(true);
		result.setResponseCodeOK();
		if (response != null) {
			result.setResponseData(response, ENCODING);
		}
		else {
			result.setResponseData("No response required", ENCODING);
		}
	}

	protected void sampleResultFailed(SampleResult result, String reason) {
		result.sampleEnd();
		result.setSuccessful(false);
		result.setResponseCode(reason);
	}

	protected void sampleResultFailed(SampleResult result, String reason, Exception exception) {
		sampleResultFailed(result, reason);
		result.setResponseMessage("Exception: " + exception);
		result.setResponseData(getStackTrace(exception), ENCODING);
	}

	protected String getStackTrace(Exception exception) {
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
}
