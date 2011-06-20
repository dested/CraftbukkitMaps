package org.bukkit.craftbukkit.maps;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapDrawer;
import org.bukkit.maps.Shape;

public class CraftMapDrawer implements MapDrawer {

	private MapCanvas mapCanvas;
	private int currentLineWidth = 2;
	private byte foreColor;
	private byte backColor;

	public CraftMapDrawer(MapCanvas canv) {
		mapCanvas = canv;
	}

	public void setCanvas(MapCanvas canv) {
		mapCanvas = canv;
	}

	public void setLineWidth(int width) {
		currentLineWidth = width;
	}

	public void drawImage(BufferedImage img) {
		int width = img.getWidth() < 128 ? img.getWidth() : 128;
		int height = img.getHeight() < 128 ? img.getHeight() : 128;

		int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				mapCanvas.setPixel(x, y, new Color(pixels[x + y * width]));
			}
		}

	}

	public void drawImage(int width, int height, int[] pixels) {
		if (pixels == null)
			return;
		width = width < 128 ? width : 128;
		height = height < 128 ? height : 128;

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				mapCanvas.setPixel(x, y, new Color(pixels[x + y * width]));
			}
		}

	}

	public void drawLine(int p1X, int p1Y, int p2X, int p2Y, int thickness) {

		if (thickness == 1) {
			drawLineInternal(p1X, p1Y, p2X, p2Y);
			return;
		}
		int halved = thickness / 2;
		int offsetLow = -halved, offsetHigh = halved;
		if (thickness % 2 == 0)
			--offsetHigh;
		int current = offsetHigh;
		while (current != offsetLow) {
			drawLineInternal(p1X, p1Y + current, p2X, p2Y + current);
			--current;
		}
	}

	public void drawLineInternal(int p1X, int p1Y, int p2X, int p2Y) {
		int dx = Math.abs(p2X - p1X);
		int dy = Math.abs(p2Y - p1Y);
		int sx = 1, sy = 1, e2;
		int error = dx - dy;
		if (p1X > p2X)
			sx = -1;
		if (p1Y > p2Y)
			sy = -1;
		while (true) {
			mapCanvas.setPixel(p1X, p1Y, foreColor);
			if (p1X == p2X && p1Y == p2Y)
				break;
			e2 = error * 2;
			if (e2 > -dy) {
				error -= dy;
				p1X += sx;
			}
			if (e2 < dx) {
				error += dx;
				p1Y += sy;
			}
		}
	}

	public void fill(int x, int y) {
		flood(x, y, mapCanvas.getPixel(x, y), backColor);

	}

	public void flood(int x, int y, byte old, byte col) {
		if (x < 0 || x > 127 || y < 0 || y > 127)
			return;
		if (mapCanvas.getPixel(x, y) == old) {
			mapCanvas.setPixel(x, y, col);
			flood(x + 1, y, old, col);
			flood(x, y + 1, old, col);
			flood(x - 1, y, old, col);
			flood(x, y - 1, old, col);
		}
	}

	public void setForeColor(byte colorIndex) {
		foreColor = colorIndex;
	}

	public void setBackColor(byte colorIndex) {
		backColor = colorIndex;

	}

	public void drawShape(int x, int y, Shape shape) {
		drawShape(x, y, shape, 1);
	}

	public void drawShape(int x, int y, Shape shape, int scale) {
		Point center = shape.getCenter();

		Point[] curs = shape.getCoordinates();

		for (int i = 0; i < curs.length - 1; i++) {
			Point p1 = curs[i];
			Point p2 = curs[i + 1];
			drawLine(p1.x * scale + x + (center.x * scale), p1.y * scale + y
					+ (center.y * scale),
					p2.x * scale + x + (center.x * scale), p2.y * scale + y
							+ (center.y * scale), this.currentLineWidth);

		}

		Point p1 = curs[curs.length - 1];
		Point p2 = curs[0];
		drawLine(p1.x * scale + x + (center.x * scale), p1.y * scale + y
				+ (center.y * scale), p2.x * scale + x + (center.x * scale),
				p2.y * scale + y + (center.y * scale), this.currentLineWidth);

	}

	public void fillShape(int x, int y, Shape shape) {
		fillShape(x, y, shape, 1);
	}

	public void fillShape(int x, int y, Shape shape, int scale) {
		Point center = shape.getCenter();

		Point[] curs = shape.getCoordinates();

		for (int i = 0; i < curs.length - 1; i++) {
			Point p1 = curs[i];
			Point p2 = curs[i + 1];
			drawLine(p1.x * scale + x + (center.x * scale), p1.y * scale + y
					+ (center.y * scale),
					p2.x * scale + x + (center.x * scale), p2.y * scale + y
							+ (center.y * scale), this.currentLineWidth);
		}

		Point p1 = curs[curs.length - 1];
		Point p2 = curs[0];
		drawLine(p1.x * scale + x + (center.x * scale), p1.y * scale + y
				+ (center.y * scale), p2.x * scale + x + (center.x * scale),
				p2.y * scale + y + (center.y * scale), this.currentLineWidth);
		fill(x + center.x * scale, y + center.x * scale);
	}

	public MapCanvas getCanvas() {
		return mapCanvas;
	}

	public byte getForeColor() {
		return foreColor;
	}

	public byte getBackColor() {
		return backColor;
	}

	public void drawRectangle(int startX, int startY, int width, int height) {
		drawLine(startX, startY, startX + width, startY, currentLineWidth);
		drawLine(startX + width, startY, startX + width, startY + height,
				currentLineWidth);
		drawLine(startX + width, startY + height, startX, startY + height,
				currentLineWidth);
		drawLine(startX, startY + height, startX, startY, currentLineWidth);
	}

	public void fillRectangle(int startX, int startY, int width, int height) {
		drawLine(startX, startY, startX + width, startY, currentLineWidth);
		drawLine(startX + width, startY, startX + width, startY + height,
				currentLineWidth);
		drawLine(startX + width, startY + height, startX, startY + height,
				currentLineWidth);
		drawLine(startX, startY + height, startX, startY, currentLineWidth);
		fill(startX + width / 2, startY + height / 2);
	}

	public void drawCircle(int x, int y, int radius) {
		drawCircle(x, y, radius, foreColor);
	}

	public void fillCircle(int x, int y, int radius) {
		byte color = foreColor;
		for (int r = radius; r > 0; r--) {
			drawCircle(x, y, r, color);
			color = backColor;
		}
	}

	private void drawCircle(int centerX, int centerY, int radius, byte color) {
		for (int j = 1; j <= 25; j++) {
		
			for (double i = 0.0; i < 360.0; i += 1) {
				double angle = i * Math.PI / 180;
				int x = (int) (centerX + radius * Math.cos(angle));
				int y = (int) (centerY + radius * Math.sin(angle));
				getCanvas().setPixel(x, y, color);
			}
		}
	}

}