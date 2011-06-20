package org.bukkit.craftbukkit.maps;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.FontAllowedCharacters;

import org.bukkit.maps.MapStringAnchorPosition;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapFont;
import org.bukkit.maps.MapStringDrawer;

public class CraftMapStringDrawer implements MapStringDrawer {
	MapCanvas _canvas;
	MapFont _currentFont = fonts.get("Minecraft");
	int _direction;

	public CraftMapStringDrawer(MapCanvas can) {
		_canvas = can;
	}

	public void setCanvas(MapCanvas canv) {
		_canvas = canv;

	}

	public void setDirection(int direction) {
		_direction = direction;
	}

	public void setFont(MapFont font) {
		_currentFont = font;
	}

	public MapFont getFont() {
		return _currentFont;
	}

	public void drawText(int x, int y, String text) {
		drawText(x, y, text, MapStringAnchorPosition.TopLeft);
	}

	public Rectangle measureString(String string) {

		CraftMapFont font = fonts.get(_currentFont.getName());
		if (font == null)
			return null;

		return new Rectangle((font.getCharacterWidth() + font.getSpaceWidth())
				* string.length(), font.getCharacterHeight());

	}

	public void drawText(int x, int y, String text,
			MapStringAnchorPosition locationPosition) {

		CraftMapFont font = fonts.get(_currentFont.getName());
		if (font == null)
			return;
		byte[][] chars = font.getChars();
		int width = font.getCharacterWidth();

		int cursorX = x, cursorY = y, startX = x;
		int row, column;
		byte[] points = new byte[font.getCharacterSize()];
		for (Character ch : text.toCharArray()) {
			if (!font.characterAllowed(ch)) {
				continue;
			}
			points = chars[font.getCharIndex(ch)];
			startX = cursorX;
			for (byte point : points) {
				row = point / width;
				column = point - (width * row);
				cursorY = y + row;
				cursorX = startX + column;
				_canvas.setPixel(cursorX, cursorY, _canvas.getMapView().getDrawer()
						.getForeColor());
			}
			cursorX = startX + font.getCharacterWidth() + font.getSpaceWidth();
			cursorY = y;
		}

	}

	protected static Map<String, CraftMapFont> fonts = new HashMap<String, CraftMapFont>();
	static {
		addDefault();
	}

	public static void addDefault() {
		CraftMapFont font = new CraftMapFont("Minecraft", 35, 5, 7, 1,
				FontAllowedCharacters.a);
		font.setChars(new byte[][] {
				{/* Space */},
				{/* ! */0, 5, 10, 15, 20, 30 },
				{/* " */1, 3, 6, 8, 10, 12 },
				{/* # */1, 3, 6, 8, 10, 11, 12, 13, 14, 16, 18, 20, 21, 22, 23,
						24, 26, 28, 31, 33 },
				{/* $ */2, 6, 7, 8, 9, 10, 16, 17, 18, 24, 25, 26, 27, 28, 32 },
				{/* % */0, 4, 5, 8, 13, 17, 21, 26, 29, 30, 34 },
				{/* & */2, 6, 8, 12, 16, 17, 19, 20, 22, 23, 25, 28, 31, 32, 34 },
				{/* ' */1, 6, 10 },
				{/* ( */2, 3, 6, 10, 15, 20, 26, 32, 33 },
				{/* ) */0, 1, 7, 13, 18, 23, 27, 30, 31 },
				{/* * */10, 13, 16, 17, 20, 23 },
				{/* + */7, 12, 15, 16, 17, 18, 19, 22, 27 },
				{/* , */25, 30, 35 },
				{/* - */15, 16, 17, 18, 19 },
				{/* . */25, 30 },
				{/* / */4, 8, 13, 17, 21, 26, 30 },
				{/* 0 */1, 2, 3, 5, 9, 10, 13, 14, 15, 17, 19, 20, 21, 24, 25,
						29, 31, 32, 33 },
				{/* 1 */2, 6, 7, 12, 17, 22, 27, 30, 31, 32, 33, 34 },
				{/* 2 */1, 2, 3, 5, 9, 14, 17, 18, 21, 25, 29, 30, 31, 32, 33,
						34 },
				{/* 3 */1, 2, 3, 5, 9, 14, 17, 18, 24, 25, 29, 31, 32, 33 },
				{/* 4 */3, 4, 7, 9, 11, 14, 15, 19, 20, 21, 22, 23, 24, 29, 34 },
				{/* 5 */0, 1, 2, 3, 4, 5, 10, 11, 12, 13, 19, 24, 25, 29, 31,
						32, 33 },
				{/* 6 */2, 3, 6, 10, 15, 16, 17, 18, 20, 24, 25, 29, 31, 32, 33 },
				{/* 7 */0, 1, 2, 3, 4, 5, 9, 14, 18, 22, 27, 32 },
				{/* 8 */1, 2, 3, 5, 9, 10, 14, 16, 17, 18, 20, 24, 25, 29, 31,
						32, 33 },
				{/* 9 */1, 2, 3, 5, 9, 10, 14, 16, 17, 18, 19, 24, 28, 31, 32 },
				{/* : */5, 10, 25, 30 },
				{/* ; */5, 10, 25, 30, 35 },
				{/* < */3, 7, 11, 15, 21, 27, 33 },
				{/* = */10, 11, 12, 13, 14, 25, 26, 27, 28, 29 },
				{/* > */0, 6, 12, 18, 22, 26, 30 },
				{/* ? */1, 2, 3, 4, 5, 10, 12, 13, 15, 17, 18, 20, 22, 23, 24,
						25, 31, 32, 33, 34 },
				{/* @ */1, 2, 3, 5, 9, 10, 11, 12, 13, 14, 15, 19, 20, 24, 25,
						29, 30, 34 },
				{/* A */1, 2, 3, 5, 9, 10, 11, 12, 13, 14, 15, 19, 20, 24, 25,
						29, 30, 34 },
				{/* B */0, 1, 2, 3, 5, 9, 10, 11, 12, 13, 15, 19, 20, 24, 25,
						29, 30, 31, 32, 33 },
				{/* C */1, 2, 3, 5, 9, 10, 15, 20, 25, 29, 31, 32, 33 },
				{/* D */0, 1, 2, 3, 5, 9, 10, 14, 15, 19, 20, 24, 25, 29, 30,
						31, 32, 33 },
				{/* E */0, 1, 2, 3, 4, 5, 10, 11, 12, 15, 20, 25, 30, 31, 32,
						33, 34 },
				{/* F */0, 1, 2, 3, 4, 5, 10, 11, 12, 15, 20, 25, 30 },
				{/* G */1, 2, 3, 4, 5, 10, 13, 14, 15, 19, 20, 24, 25, 29, 31,
						32, 33 },
				{/* H */0, 4, 5, 9, 10, 11, 12, 13, 14, 15, 19, 20, 24, 25, 29,
						30, 34 },
				{/* I */0, 1, 2, 6, 11, 16, 21, 26, 30, 31, 32 },
				{/* J */4, 9, 14, 19, 24, 25, 29, 31, 32, 33 },
				{/* K */0, 4, 5, 8, 10, 11, 12, 15, 18, 20, 24, 25, 29, 30, 34 },
				{/* L */0, 5, 10, 15, 20, 25, 30, 31, 32, 33, 34 },
				{/* M */0, 4, 5, 6, 8, 9, 10, 12, 14, 15, 19, 20, 24, 25, 29,
						30, 34 },
				{/* N */0, 4, 5, 6, 9, 10, 12, 14, 15, 18, 19, 20, 24, 25, 29,
						30, 34 },
				{/* O */1, 2, 3, 5, 9, 10, 14, 15, 19, 20, 24, 29, 31, 32, 33 },
				{/* P */0, 1, 2, 3, 5, 9, 10, 11, 12, 13, 15, 20, 25, 30 },
				{/* Q */1, 2, 3, 5, 9, 10, 14, 15, 19, 20, 24, 25, 28, 31, 32,
						34 },
				{/* R */0, 1, 2, 3, 5, 9, 10, 11, 12, 13, 15, 19, 20, 24, 25,
						29, 30, 34 },
				{/* S */1, 2, 3, 4, 5, 11, 12, 13, 19, 24, 25, 29, 31, 32, 33 },
				{/* T */0, 1, 2, 3, 4, 7, 12, 17, 22, 27, 32 },
				{/* U */0, 4, 5, 9, 10, 14, 15, 19, 20, 24, 25, 29, 31, 32, 33 },
				{/* V */0, 4, 5, 9, 10, 14, 15, 19, 21, 23, 26, 28, 32 },
				{/* W */0, 4, 5, 9, 10, 14, 15, 19, 20, 22, 24, 25, 26, 28, 29,
						30, 34 },
				{/* X */0, 4, 6, 8, 12, 16, 18, 20, 24, 25, 29, 30, 34 },
				{/* Y */0, 4, 6, 8, 12, 17, 22, 27, 32 },
				{/* Z */0, 1, 2, 3, 4, 9, 13, 17, 21, 25, 30, 31, 32, 33, 34 },
				{/* [ */0, 1, 2, 5, 10, 15, 20, 25, 30, 31, 32 },
				{/* \ */0, 6, 11, 17, 23, 28, 34 },
				{/* ] */0, 1, 2, 7, 12, 17, 22, 27, 30, 31, 32 },
				{/* ^ */2, 6, 8, 10, 14 },
				{/* _ */35, 36, 37, 38, 39 },
				{/* ' */0, 5, 11 },
				{/* a */11, 12, 13, 19, 21, 22, 23, 24, 25, 29, 31, 32, 33, 34 },
				{/* b */0, 5, 10, 12, 13, 15, 16, 19, 20, 24, 25, 29, 30, 31,
						32, 33 },
				{/* c */11, 12, 13, 15, 19, 20, 25, 29, 31, 32, 33 },
				{/* d */4, 9, 11, 12, 14, 15, 18, 19, 20, 24, 25, 29, 31, 32,
						33, 34 },
				{/* e */11, 12, 13, 15, 19, 20, 21, 22, 23, 24, 25, 31, 32, 33,
						34 },
				{/* f */2, 3, 6, 10, 11, 12, 13, 16, 21, 26, 31 },
				{/* g */11, 12, 13, 14, 15, 19, 20, 24, 26, 27, 28, 29, 34, 35,
						36, 37, 38 },
				{/* h */0, 5, 10, 12, 13, 15, 16, 19, 20, 24, 25, 29, 30, 34 },
				{/* i */0, 10, 15, 20, 25, 30 },
				{/* j */4, 14, 19, 24, 25, 29, 30, 34, 36, 37, 38 },
				{/* k */0, 5, 10, 13, 15, 17, 20, 21, 25, 27, 30, 33 },
				{/* l */0, 5, 10, 15, 20, 25, 31 },
				{/* m */10, 11, 13, 15, 17, 19, 20, 22, 24, 25, 29, 30, 34 },
				{/* n */10, 11, 12, 13, 15, 19, 20, 24, 25, 29, 30, 34 },
				{/* o */11, 12, 13, 15, 19, 20, 24, 25, 29, 31, 32, 33 },
				{/* p */10, 12, 13, 15, 16, 19, 20, 24, 25, 26, 27, 28, 30, 35 },
				{/* q */11, 12, 14, 15, 18, 19, 20, 24, 26, 27, 28, 29, 34, 39 },
				{/* r */10, 12, 13, 15, 16, 19, 20, 25, 30 },
				{/* s */11, 12, 13, 14, 15, 21, 22, 23, 29, 30, 31, 32, 33 },
				{/* t */1, 6, 10, 11, 12, 16, 21, 26, 32 },
				{/* u */10, 14, 15, 19, 20, 24, 25, 29, 31, 32, 33, 34 },
				{/* v */10, 14, 15, 19, 20, 24, 26, 28, 32 },
				{/* w */10, 14, 15, 19, 20, 22, 24, 25, 27, 29, 31, 32, 33, 34 },
				{/* x */10, 14, 16, 18, 22, 26, 28, 30, 34 },
				{/* y */10, 14, 15, 19, 20, 24, 26, 27, 28, 29, 34, 35, 36, 37,
						38 },
				{/* z */10, 11, 12, 13, 14, 18, 22, 26, 30, 31, 32, 33, 34 },
				{/* { */2, 3, 6, 11, 15, 21, 26, 32, 33 },
				{/* | */0, 5, 10, 20, 25, 30 },
				{/* } */0, 1, 7, 12, 18, 22, 27, 30, 31 },
				{/* ~ */1, 2, 5, 8, 9 },
				{/* ⌂ */12, 16, 18, 20, 24, 25, 29, 30, 31, 32, 33, 34 },
				{/* Ç */1, 2, 3, 5, 9, 10, 15, 20, 24, 26, 27, 28, 34, 37, 38,
						43, 44 },
				{/* ü */1, 3, 10, 14, 15, 19, 20, 24, 25, 29, 31, 32, 33, 34 },
				{/* é */3, 4, 11, 12, 13, 15, 19, 20, 21, 22, 23, 24, 25, 31,
						32, 33, 34 },
				{/* â */1, 2, 3, 5, 9, 11, 12, 13, 19, 21, 22, 23, 24, 25, 29,
						31, 32, 33, 34 },
				{/* ä */1, 3, 11, 12, 13, 19, 21, 22, 23, 24, 25, 29, 31, 32,
						33, 34 },
				{/* à */0, 1, 11, 12, 13, 19, 21, 22, 23, 24, 25, 29, 31, 32,
						33, 34 },
				{/* å */2, 11, 12, 13, 19, 21, 22, 23, 24, 25, 29, 31, 32, 33,
						34 },
				{/* ç */6, 7, 8, 10, 14, 15, 20, 24, 26, 27, 28, 34 },
				{/* ê */1, 2, 3, 5, 9, 11, 12, 13, 15, 19, 20, 21, 22, 23, 24,
						25, 31, 32, 33, 34 },
				{/* ë */1, 3, 11, 12, 13, 15, 19, 20, 21, 22, 23, 24, 25, 31,
						32, 33, 34 },
				{/* è */0, 1, 11, 12, 13, 15, 19, 20, 21, 22, 23, 24, 25, 31,
						32, 33, 34 },
				{/* ï */0, 2, 11, 16, 21, 26, 31 },
				{/* î */1, 2, 3, 5, 9, 12, 17, 22, 27, 32 },
				{/* ì */0, 1, 11, 16, 21, 26, 31 },
				{/* Ä */0, 4, 6, 7, 8, 10, 14, 15, 16, 17, 18, 19, 20, 24, 25,
						29, 30, 34 },
				{/* Å */2, 11, 12, 13, 15, 19, 20, 21, 22, 23, 24, 25, 29, 30,
						34 },
				{/* É */3, 4, 10, 11, 12, 13, 14, 15, 20, 21, 22, 25, 30, 31,
						32, 33, 34 },
				{/* æ */11, 13, 17, 19, 21, 22, 23, 24, 25, 27, 31, 32, 33, 34 },
				{/* Æ */1, 2, 3, 4, 5, 7, 10, 11, 12, 13, 15, 17, 20, 22, 25,
						27, 30, 32, 33, 34 },
				{/* ô */1, 2, 3, 5, 9, 11, 12, 13, 15, 19, 20, 24, 25, 29, 31,
						32, 33 },
				{/* ö */1, 3, 11, 12, 13, 15, 19, 20, 24, 25, 29, 31, 32, 33 },
				{/* ò */0, 1, 11, 12, 13, 15, 19, 20, 24, 25, 29, 31, 32, 33 },
				{/* û */1, 2, 3, 5, 9, 15, 19, 20, 24, 25, 29, 31, 32, 33, 34 },
				{/* ù */0, 1, 10, 14, 15, 19, 20, 24, 25, 29, 31, 32, 33, 34 },
				{/* ÿ */1, 3, 10, 14, 15, 19, 20, 24, 26, 27, 28, 29, 34 },
				{/* Ö */0, 4, 6, 7, 8, 10, 14, 15, 19, 20, 24, 25, 29, 31, 32,
						33 },
				{/* Ü */0, 4, 10, 14, 15, 19, 20, 24, 25, 29, 31, 32, 33 },
				{/* ø */11, 12, 13, 15, 18, 19, 20, 22, 24, 25, 26, 29, 31, 32,
						33 },
				{/* £ */2, 3, 6, 9, 11, 15, 16, 17, 18, 21, 26, 30, 31, 32, 33,
						34 },
				{/* Ø */1, 2, 3, 5, 9, 10, 13, 14, 15, 17, 19, 20, 21, 24, 25,
						29, 31, 32, 33 },
				{/* × */10, 12, 16, 20, 22 },
				{/* ƒ */3, 7, 9, 12, 16, 17, 18, 22, 27, 30, 32, },
				{/* á */3, 4, 11, 12, 13, 19, 21, 22, 23, 24, 25, 29, 31, 32,
						33, 34 },
				{/* í */0, 1, 10, 15, 20, 25, 30 },
				{/* ó */3, 4, 11, 12, 13, 15, 19, 20, 24, 25, 29, 31, 32, 33 },
				{/* ú */3, 4, 10, 14, 15, 19, 20, 24, 25, 29, 31, 32, 33, 34 },
				{/* ñ */0, 1, 2, 3, 4, 10, 11, 12, 13, 15, 19, 20, 24, 25, 29,
						30, 34 },
				{/* Ñ */0, 1, 2, 3, 4, 10, 14, 15, 16, 19, 20, 22, 24, 25, 28,
						29, 30, 34 },
				{/* ª */1, 2, 3, 9, 10, 11, 12, 13, 14, 16, 17, 18, 19, 25, 26,
						27, 28, 29 },
				{/* º */1, 2, 3, 5, 9, 10, 14, 16, 17, 18, 25, 26, 27, 28, 29 },
				{/* ¿ */2, 12, 16, 20, 25, 29, 31, 32, 33 },
				{/* ® */6, 7, 8, 9, 10, 12, 13, 15, 17, 20, 21, 23, 26, 27, 28,
						29 }, {/* ¬ */15, 16, 17, 18, 19, 24, 29 },
				{/* ½ */0, 4, 5, 8, 13, 17, 21, 24, 26, 28, 30, 33, 34 },
				{/* ¼ */0, 4, 5, 8, 13, 17, 21, 23, 24, 26, 28, 29, 30, 34 },
				{/* ¡ */5, 15, 20, 25, 30 },
				{/* « */7, 9, 11, 13, 15, 17, 21, 23, 27, 29 },
				{/* » */5, 7, 11, 13, 17, 19, 21, 23, 25, 27 } });
		fonts.put(font.getName(), font);
	}

}