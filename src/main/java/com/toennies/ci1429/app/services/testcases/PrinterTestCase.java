package com.toennies.ci1429.app.services.testcases;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.printer.LabelData;
import com.toennies.ci1429.app.model.printer.Printer;

public class PrinterTestCase extends TestCase<Printer> {

	private final String data2Print;
	private final boolean raw;

	/**
	 * Use this constructor, if you want that a test template get's printed.
	 */
	public PrinterTestCase(Printer device, String template) {
		this(device, template, false);
	}

	/**
	 * @param raw
	 *            pass true value, if you don't want to print a test template.
	 */
	public PrinterTestCase(Printer device, String data2Print, boolean raw) {
		super(device);
		this.data2Print = data2Print;
		this.raw = raw;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DeviceResponse _doTest() throws DeviceException {
		if (raw) {
			// raw print job. Data gets send directly to printer.
			return device.process(data2Print);
		}
		// no raw print job from here...
		if (StringUtils.isEmpty(data2Print)) {
			// No template name was passed to method. Can't continue
			return new DeviceResponse(Status.BAD_REQUEST, "Can't print without template.");
		}

		// After this point, a proper template name is given.
		// But before printing, default template is uploaded to printer.
		device.process(DefaultTemplateReader.readDefaultZPLFile());
		// ... in order to print a label with that template
		// But data2Print may be differ from
		// DefaultTemplateReader.readDefaultZPLFile(). In this case, another
		// template gets printed. But be careful, because this template needs to
		// be uploaded before this step via rest endpoint.
		return device.process(buildTestLabelData(data2Print));
	}

	private LabelData buildTestLabelData(String template) {
		LabelData labelData = new LabelData();
		labelData.setTemplate(template);
		labelData.setFields(new HashMap<String, LabelData.Value>());
		return labelData;
	}

}
