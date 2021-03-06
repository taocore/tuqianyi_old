package com.tuqianyi.image;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedString;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.tuqianyi.model.TextLabel;

public class ImageUtils {

	/**
	 * 图片水印
	 * 
	 * @param markFile
	 *            水印图片
	 * @param imageFile
	 *            目标图片
	 * @param x
	 *            修正�?默认在中�?
	 * @param y
	 *            修正�?默认在中�?
	 * @param alpha
	 *            透明�?
	 */
	public static BufferedImage pressImage(BufferedImage image,
			BufferedImage watermark, int x, int y, float alpha) {
		int wideth = image.getWidth();
		int height = image.getHeight();
		BufferedImage result = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = result.createGraphics();
		g.drawImage(image, 0, 0, wideth, height, null);
		// 水印文件
		int wideth_biao = watermark.getWidth();
		int height_biao = watermark.getHeight();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
				alpha));
		g.drawImage(watermark, x, y, wideth_biao, height_biao, null);
		// 水印文件结束
		g.dispose();
//		float[] kernelData2 = { 
//          -0.125f, -0.125f, -0.125f,
//	      -0.125f,2, -0.125f,
//	      -0.125f,-0.125f, -0.125f };
//	    Kernel kernel = new Kernel(3, 3, kernelData2);
//	    ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
//	    result = cOp.filter(result, null);
		return result;
	}

	public static BufferedImage pressText(BufferedImage image, String text, 
			Font font, Color color, Color backColor, int line, 
			Color borderColor, int borderWidth, 
			int x, int y, float alpha, boolean isVertical) {
		Graphics2D g = image.createGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
		int width = metrics.stringWidth(text);
		int height = metrics.getHeight();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	              RenderingHints.VALUE_ANTIALIAS_ON);

		if (backColor != null)
		{
			g.setColor(backColor);
			g.fillRect(0, 0, width, height);
		}
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, font);
//		as.addAttribute(TextAttribute.FOREGROUND, color);
		if (line == TextLabel.LINE_UNDER)
		{
			as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}
		else if (line == TextLabel.LINE_THROUGH)
		{
			as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		}
		x = 0;
		y = metrics.getAscent();
//		int outline = 3;
//		Color outlineColor = Color.white;
//		if (!outlineColor.equals(color))
//		{
//			as.addAttribute(TextAttribute.FOREGROUND, outlineColor);
//			g.drawString(as.getIterator(), x + outline, y);
//			g.drawString(as.getIterator(), x - outline, y);
//			g.drawString(as.getIterator(), x, y + outline);
//			g.drawString(as.getIterator(), x, y - outline);
//			g.drawString(as.getIterator(), x + outline, y + outline);
//			g.drawString(as.getIterator(), x - outline, y + outline);
//			g.drawString(as.getIterator(), x + outline, y - outline);
//			g.drawString(as.getIterator(), x - outline, y - outline);
//		}
		as.addAttribute(TextAttribute.FOREGROUND, color);
		if (isVertical)
		{
			for (int i = 0, n = text.length(); i < n; i++)
			{
				int h = metrics.getHeight();
				int yy = y + h * n;
				g.drawString(as.getIterator(), x, yy);
			}
		}
		else
		{
			g.drawString(as.getIterator(), x, y);
		}
		if (line == TextLabel.LINE_SLASH)
		{
			GeneralPath path = new GeneralPath();
			path.moveTo(0, 0);
			path.lineTo(width, height);
			g.setPaint(color);
			g.setStroke(new BasicStroke(5));
			g.draw(path);
		}
		if (borderWidth > 0)
		{
			g.setColor(borderColor);
			g.setStroke(new BasicStroke(borderWidth));
			g.drawRect(0, 0, width, height);
		}
		g.dispose();
		return image;
	}
	
	public static BufferedImage resize(BufferedImage image, int width, int height)
	{
		double rw = (double)width / image.getWidth();
		double rh = (double)height /image.getHeight();
		AffineTransform transform = AffineTransform.getScaleInstance(rw, rh);
		AffineTransformOp op = new AffineTransformOp(transform, null);
		return (BufferedImage)op.filter(image, null);
	}
	
	/**
	 * 缩放
	 * 
	 * @param filePath
	 *            图片路径
	 * @param height
	 *            高度
	 * @param width
	 *            宽度
	 * @param bb
	 *            比例不对时是否需要补�?
	 */
	public static BufferedImage resize(BufferedImage bi, int height, int width, boolean bb) {
		double ratio = 0.0; // 缩放比例
		Image itemp = bi.getScaledInstance(width, height,
				BufferedImage.SCALE_SMOOTH);
		// 计算比例
		if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
			if (bi.getHeight() > bi.getWidth()) {
				ratio = (new Integer(height)).doubleValue()
						/ bi.getHeight();
			} else {
				ratio = (new Integer(width)).doubleValue() / bi.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform
					.getScaleInstance(ratio, ratio), null);
			itemp = op.filter(bi, null);
		}
		if (bb) {
			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			if (width == itemp.getWidth(null))
				g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
						itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			else
				g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
						itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			g.dispose();
			itemp = image;
		}
		return (BufferedImage) itemp;
	}

	public static int getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}
	
	public static BufferedImage composite(BufferedImage backImage, BufferedImage frontImage, 
			int x, int y, int frontWidth, int frontHeight, float alpha) {
		Graphics2D g = backImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	              RenderingHints.VALUE_ANTIALIAS_ON);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		g.drawImage(frontImage, x, y, frontWidth, frontHeight, null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		g.dispose();
		return backImage;
	}
	
	/**
	 * Rotates image 90 degrees to the left.
	 */
	public static BufferedImage rotateLeft(BufferedImage bufferedImage) {

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(height, width,
				BufferedImage.TYPE_INT_BGR);
		Graphics2D g = result.createGraphics();
		result = g.getDeviceConfiguration().createCompatibleImage(100, 100, Transparency.TRANSLUCENT);
		g.dispose();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = bufferedImage.getRGB(x, y);
				result.setRGB(y, width - x - 1, rgb);
			}
		}
		return result;
	}
	
	/**
	 * Rotates image 90 degrees to the right.
	 */
	public static BufferedImage rotateRight(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(height, width,	BufferedImage.TYPE_INT_BGR);
		Graphics2D g = result.createGraphics();
		result = g.getDeviceConfiguration().createCompatibleImage(100, 100, Transparency.TRANSLUCENT);
		g.dispose();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = bufferedImage.getRGB(x, y);
				result.setRGB(height - y - 1, x, rgb);
			}
		}

		return result;
	}
	
	public static void writeImage(BufferedImage image, String format, float quality, OutputStream out) throws IOException
	{
		ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
		Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, format);
		ImageWriter writer = (ImageWriter)iter.next();
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
		writer.setOutput(ImageIO.createImageOutputStream(out));
		IIOImage iioImage = new IIOImage(image, null, null);//writer.getDefaultImageMetadata(type, param));
        writer.write(writer.getDefaultImageMetadata(type, param), iioImage, param);
        writer.dispose();
	}
	
	public static void writeHighQualityImage(BufferedImage image, float quality, OutputStream out) throws IOException
	{
		System.out.println("WriteHighQualityImage");
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(image);
        jep.setQuality(quality, true);
        encoder.encode(image, jep);
	}
	
	/** 
     *   旋转   -   参数指定目标图旋转角度。 
     *   @param   image BufferedImage 
     *   @param   angle   int 
     *   @param   hints   RenderingHints 
     *   @return   BufferedImage 
     */ 
	public static BufferedImage rotate(
			BufferedImage image, 
			int angle, 
			RenderingHints hints)   
	{ 
		int width = image.getWidth(); 
		int height = image.getHeight(); 

		BufferedImage dstImage = null; 
		AffineTransform affineTransform = new AffineTransform(); 
		if (angle == 180)
		{
			affineTransform.translate(width, height);
			dstImage = new BufferedImage(width, height, image.getType()); 
		}
		else if (angle == 90)
		{
			affineTransform.translate(height, 0); 
			dstImage = new BufferedImage(height, width, image.getType());
		}
		else if (angle == 270)
		{ 
			affineTransform.translate(0, width); 
			dstImage = new BufferedImage(height, width, image.getType()); 
		}
		if (dstImage != null)
		{
			affineTransform.rotate(java.lang.Math.toRadians(angle));
		}
		else
		{
			double angleRadians = java.lang.Math.toRadians(angle);
			int d = width + height;
			int w = (int) (d * Math.abs(Math.cos(angleRadians)));
			int h = (int) (d * Math.abs(Math.sin(angleRadians)));
			dstImage = new  BufferedImage(w, h, image.getType());
			affineTransform.rotate(angleRadians, w/2, h/2);
			affineTransform.translate((w - width)/2, (h - height)/2);
		}
		AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, hints); 
		return affineTransformOp.filter(image, dstImage); 
	}   
}
