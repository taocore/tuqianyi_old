package com.tuqianyi.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.tuqianyi.model.TextLabel;
import com.tuqianyi.servlet.FontsServlet;

public class FontAction extends ActionBase {

	private TextLabel label;
	private InputStream stream;
	
	public String execute() throws Exception
	{
		_log.info("font: " + label.getFont());
		try
		{
		BufferedImage image = FontsServlet.getFontProvider().getFontImage(label.getFont());
		_log.info("image: " + image);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        stream = new ByteArrayInputStream(out.toByteArray());
        _log.info("done. ");
		}
		catch (Exception e)
		{
			error(e);
		}
		return SUCCESS;
	}

	public void setLabel(TextLabel label) {
		this.label = label;
	}

	public TextLabel getLabel() {
		return label;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public InputStream getStream() {
		return stream;
	}

}
