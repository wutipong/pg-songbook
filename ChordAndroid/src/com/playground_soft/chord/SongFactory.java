package com.playground_soft.chord;

import java.util.LinkedList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.preference.PreferenceManager;

import com.playground_soft.chordlib.Document;
import com.playground_soft.chordlib.Element;

public final class SongFactory {
	
	private SongFactory() {
		
	}

	private static class DrawTextCommand {
		private final PointF mLocation;
		private final String mText;
		private final Paint mPaint;

		public DrawTextCommand(PointF location, String text, Paint paint) {
			this.mLocation = new PointF();
			this.getLocation().set(location);
			this.mText = text;
			this.mPaint = paint;
		}

		public PointF getLocation() {
			return mLocation;
		}

		public String getText() {
			return mText;
		}

		public Paint getPaint() {
			return mPaint;
		}

	}

	private static final int PAINT_TYPE_TEXT = 0;
	private static final int PAINT_TYPE_CHORD = 1;
	private static final int PAINT_TYPE_COMMENT = 2;
	private static final int PAINT_TYPE_CHORUS = 3;
	private static final int PAINT_TYPE_TAB = 4;
	private static final int PAINT_TYPE_COUNT = 5;

	private static Paint[] sPaints;
	private static int mBackgroundColor;

	private static final String[] NOTE_NAMES_SHARP = 
			{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	
	private static final String[] NOTE_NAMES_FLAT = 
		{"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"};
	
	public static Drawable create(Document doc, Activity activity, int transpose, boolean isSharp) {
		int width = 0;
		int height = 0;

		prepareResources(activity);
		
		LinkedList<DrawTextCommand> commandList = new LinkedList<DrawTextCommand>();

		PointF chordPos = new PointF();
		PointF textPos = new PointF();

		float chordLineSpacing = sPaints[PAINT_TYPE_CHORD].getFontSpacing();
		float textLineSpacing = sPaints[PAINT_TYPE_TEXT].getFontSpacing();
		
		textPos.y = textLineSpacing + chordLineSpacing;
		chordPos.y = chordLineSpacing;
		
		boolean bChorus = false;
		boolean bTab = false;
		float chordWidth = 0;
		float textWidth = 0;
		boolean skipLineBreakAtTop = true;
		
		for (Element element : doc.elementList) {

			if (skipLineBreakAtTop) {
				if (element.type == Element.Type.Linebreak 
					|| (element.type == Element.Type.Text && element.data.trim().length() == 0)) {
					
					continue;
				} else {
					skipLineBreakAtTop = false;
				}
			}
			
			switch (element.type) {
			default:
				break;
			case Comment:
				commandList.add(new DrawTextCommand(textPos, element.data,
						sPaints[PAINT_TYPE_COMMENT]));
				break;

			case StartOfChorus:
				bChorus = true;
				break;

			case EndOfChorus:
				bChorus = false;
				break;
				
			case StartOfTab:
				bTab = true;
				break;

			case EndOfTab:
				bTab = false;
				break;

			case Chord:
			{
				String chord = transposeChord(element.data, transpose, isSharp);
				
				if (textWidth == 0) {
					textPos.x += chordWidth;
				}
				chordPos.x = textPos.x;
				commandList.add(new DrawTextCommand(chordPos, chord,
						sPaints[PAINT_TYPE_CHORD]));
				chordWidth = sPaints[PAINT_TYPE_CHORD].measureText(chord
						+ " ");
				textWidth = 0;
				
				break;
			}
			case Text: 
				Paint paint;
				if (bTab) {
					paint = sPaints[PAINT_TYPE_TAB];
				} else if (bChorus) {
					paint = sPaints[PAINT_TYPE_CHORUS];
				} else {
					paint = sPaints[PAINT_TYPE_TEXT];
				}
				commandList.add(new DrawTextCommand(textPos, element.data,
						paint));
				textWidth = paint.measureText(element.data);

				if (textWidth > chordWidth) {
					textPos.x += textWidth;
				} else { 
					textPos.x += chordWidth;
				}

				if (textPos.x > width) {
					width = (int) (textPos.x + 1);
				}
				break;
			
			case Linebreak:
				float lineSpacing = textLineSpacing + chordLineSpacing;
				if (bTab) {
					lineSpacing = textLineSpacing;
				} 
				textPos.x = 0;
				textPos.y += lineSpacing;
				chordPos.x = 0;
				chordPos.y = textPos.y - chordLineSpacing;
				height += (int) (lineSpacing + 1);
				textWidth = 0;
				chordWidth = 0;
				break;

			}
		}

		Picture picture = new Picture();
		Canvas canvas = picture.beginRecording(width, height);
		canvas.drawColor(mBackgroundColor);
		for (DrawTextCommand command : commandList) {
			canvas.drawText(command.getText(), command.getLocation().x,
					command.getLocation().y, command.getPaint());
		}

		picture.endRecording();

		return new PictureDrawable(picture);

	}

	private static void prepareResources(Activity activity ) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity); 
		Resources resource = activity.getResources();
		
		int fontSize = Integer.parseInt(
				preferences.getString(resource.getString(R.string.selected_font_size), "16"));
		String theme = preferences.getString(resource.getString(R.string.selected_color_theme),"theme_monochrome");
		int id = resource.getIdentifier(theme, "array", "com.playground_soft.chord");
		TypedArray array = resource.obtainTypedArray(id);
		
		mBackgroundColor = array.getColor(0, 0xFFFFFFFF);
		int textColor = array.getColor(1, 0);
		int chorusColor = array.getColor(2, 0);
		int commentColor = array.getColor(3, 0);
		int chordColor = array.getColor(4, 0);
		
		sPaints = new Paint[PAINT_TYPE_COUNT];

		for (int i = 0; i < PAINT_TYPE_COUNT; i++) {
			
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(fontSize);
			switch (i) {
			case PAINT_TYPE_TEXT:
				paint.setTypeface(Typeface.DEFAULT);
				paint.setColor(textColor);
				break;
				
			case PAINT_TYPE_TAB:
				paint.setTypeface(Typeface.MONOSPACE);
				paint.setColor(textColor);
				break;
				
			case PAINT_TYPE_CHORD:
				paint.setTypeface(Typeface.DEFAULT);
				paint.setColor(chordColor);
				break;
	
			case PAINT_TYPE_COMMENT:
				paint.setTypeface(Typeface.DEFAULT_BOLD);
				paint.setColor(commentColor);
				paint.setUnderlineText(true);
				break;
	
			case PAINT_TYPE_CHORUS:
				paint.setTypeface(Typeface.DEFAULT);
				paint.setColor(chorusColor);
				paint.setTextSkewX(-0.25f);
				break;
				
			default:
				break;
			}
			
			sPaints[i] = paint;
		}
		
		
	}
	private static String transposeChord(String input, int transpose, boolean isSharp) {
		if (transpose == 0) return input;
		
		String [] noteNames;
		String root = "";
		String type = "";
		if (input.length() == 1) {
			noteNames = NOTE_NAMES_SHARP;
			root = input;
		} else if (input.charAt(1) == '#') {
			noteNames = NOTE_NAMES_SHARP;
			root = input.substring(0, 2);
			type = input.substring(2);
		} else if (input.charAt(1) == 'b') {
			noteNames = NOTE_NAMES_FLAT;
			root = input.substring(0, 2);
			type = input.substring(2);
		} else {
			noteNames = NOTE_NAMES_SHARP;
			root = input.substring(0, 1);
			type = input.substring(1);
		}
		
		int noteIndex;
		for (noteIndex = 0; noteIndex < noteNames.length; noteIndex++) {
			if (noteNames[noteIndex].equals(root))
				break;
		}
		noteIndex = (noteIndex + transpose + noteNames.length) % noteNames.length;
		
		if(isSharp) {
			return NOTE_NAMES_SHARP[noteIndex]+type;
		} else {
			return NOTE_NAMES_FLAT[noteIndex]+type;
		}
	}
}
