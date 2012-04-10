package com.playground_soft.chord;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.preference.PreferenceManager;

import com.playground_soft.chordlib.Document;
import com.playground_soft.chordlib.Element;

public final class SongFactory {

    private static final float TEXT_ITALIC_CONST = -0.25f;

    private SongFactory() {

    }

    private static class DrawTextCommand {
        private final Point mLocation;
        private final String mText;
        private final Paint mPaint;

        public DrawTextCommand(Point location, String text, Paint paint) {
            this.mLocation = new Point();
            this.mLocation.set(location.x, location.y);
            this.mText = text;
            this.mPaint = paint;
        }

        public Point getLocation() {
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

    private static final String[] NOTE_NAMES_SHARP = { "C", "C#", "D", "D#",
            "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    private static final String[] NOTE_NAMES_FLAT = { "C", "Db", "D", "Eb",
            "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };

    private enum TransposeType {
        Sharp, Flat
    };

    private static class DrawLineParam {
        public boolean isChorus = false;
        public boolean isTab = false;

        public int yLineStart = 0;
        public int xLineStart = 0;

        public int pageWidth = 0;
        
        public final int heightChordLine;
        public final int heightTextLine;
        public final TransposeType transposeType;
        public final int transpose;
        public final int pageHeight;
        public final int pageSeperator;

        public DrawLineParam( int heightChordLine,
                int heightTextLine,
                TransposeType transposeType,
                int transpose,
                int pageHeight,
                int pageSeperator) {

            this.heightChordLine = heightChordLine;
            this.heightTextLine = heightTextLine;
            this.transposeType = transposeType;
            this.transpose = transpose;
            this.pageHeight = pageHeight;
            this.pageSeperator = pageSeperator;
        }
    }

    public static Drawable create(Document doc, Activity activity,
            int transpose, boolean isSharp, int pageWidth, int pageHeight) {
      
        prepareResources(activity);

        List<DrawTextCommand> commandList = new LinkedList<DrawTextCommand>();

        LinkedList<Element> lineElements = new LinkedList<Element>();

        DrawLineParam param = new DrawLineParam(
                (int)sPaints[PAINT_TYPE_CHORD].getFontSpacing(),
                (int)sPaints[PAINT_TYPE_TEXT].getFontSpacing(),
                isSharp? TransposeType.Sharp: TransposeType.Flat,
                transpose,
                pageHeight,
                (int)sPaints[PAINT_TYPE_TEXT].measureText("    "));

        for (Element element : doc.elementList) {
            switch (element.type) {
            case Linebreak :
                commandList = addLine(commandList, lineElements, param);
                lineElements.clear();
                break;

            default:
                lineElements.add(element);
                break;
            }
        }

        Picture picture = new Picture();
        Canvas canvas = picture.beginRecording(param.pageWidth, param.pageHeight);
        canvas.drawColor(mBackgroundColor);
        
        for (DrawTextCommand command : commandList) {
            Point pos = command.getLocation();

            canvas.drawText(command.getText(), pos.x,
                    pos.y, command.getPaint());
        }

        picture.endRecording();

        return new PictureDrawable(picture);

    }

    private static List<DrawTextCommand> addLine(
            List<DrawTextCommand> commandList,
            List<Element> lineElements,
            DrawLineParam param) {

        boolean hasText = false;
        boolean hasChord = false;
        boolean hasComment = false;

        for(Element element: lineElements) {
            if(element.type == Element.Type.Text){
                hasText = true;
            } else if(element.type == Element.Type.Chord) {
                hasChord = true;
            } else if(element.type == Element.Type.Comment) {
                hasComment = true;
            }
        }
        
        int line1 = 0;
        int line2 = 0;

        if (hasText && hasChord) {
            line1 = param.heightChordLine;
            line2 = param.heightChordLine + param.heightTextLine;
        } else if (hasText || hasComment) {
            line1 = param.heightTextLine;
            line2 = param.heightTextLine;
        } else if (hasChord) {
            line1 = param.heightChordLine;
            line2 = param.heightChordLine;
        } else if (lineElements.size() == 0){
            param.yLineStart += param.heightTextLine;
            return commandList;
        }
        
        if((line2 + param.yLineStart) > param.pageHeight) {
            param.yLineStart = 0;
            param.xLineStart = param.pageWidth + param.pageSeperator;
            param.pageWidth = 0;
        } else {
            line1 += param.yLineStart;
            line2 += param.yLineStart;
        }
        
        param.yLineStart = line2;

        Point chordPos = new Point();
        chordPos.y = line1;
        chordPos.x = param.xLineStart;
        Point textPos = new Point();
        textPos.y = line2;
        textPos.x = param.xLineStart;

        float textWidth = 0;
        float chordWidth = 0;

        for(Element element: lineElements) {
            switch (element.type) {

            default:
                break;
            case StartOfChorus:
                param.isChorus = true;
                break;

            case EndOfChorus:
                param.isChorus = false;
                break;

            case StartOfTab:
                param.isTab = true;
                break;

            case EndOfTab:
                param.isTab = false;
                break;

            case Chord:
            {
                String chord = transposeChord(element.data, param.transpose, param.transposeType);

                if (textWidth == 0) {
                    textPos.x += chordWidth;
                }
                chordPos.x = textPos.x;
                commandList.add(new DrawTextCommand(chordPos, chord,
                        sPaints[PAINT_TYPE_CHORD]));
                chordWidth = (int) (sPaints[PAINT_TYPE_CHORD].measureText(chord + " ") +1);
                if (chordPos.x + chordWidth > param.pageWidth) {
                    param.pageWidth = (int) (chordPos.x + chordWidth + 1);
                }
                textWidth = 0;

            }
            break;

            case Comment:
                textWidth = addTextToLine(commandList, element, param, textPos,
                        chordWidth, sPaints[PAINT_TYPE_COMMENT]);
                break;

            case Text:
            {
                Paint paint;
                if (param.isTab) {
                    paint = sPaints[PAINT_TYPE_TAB];
                } else if (param.isChorus) {
                    paint = sPaints[PAINT_TYPE_CHORUS];
                } else {
                    paint = sPaints[PAINT_TYPE_TEXT];
                }

                textWidth = addTextToLine(commandList, element, param, textPos,
                        chordWidth, paint);
            }
            break;

            }
        }

        return commandList;
    }

    private static float addTextToLine(List<DrawTextCommand> commandList,
            Element element, DrawLineParam param, Point textPos,
            float chordWidth, Paint paint) {
        float textWidth;
        commandList.add(new DrawTextCommand(textPos, element.data,
                paint));
        textWidth = paint.measureText(element.data);

        if (textWidth > chordWidth) {
            textPos.x += textWidth;
        } else {
            textPos.x += chordWidth;
        }

        if (textPos.x > param.pageWidth) {
            param.pageWidth = (int) (textPos.x + 1);
        }
        return textWidth;
    }


    private static void prepareResources(Activity activity) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(activity);
        Resources resource = activity.getResources();

        int fontSize = Integer.parseInt(preferences.getString(
                resource.getString(R.string.selected_font_size), "16"));
        String theme = preferences.getString(
                resource.getString(R.string.selected_color_theme),
                "theme_monochrome");
        int id = resource.getIdentifier(theme, "array",
                "com.playground_soft.chord");
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
                paint.setTextSkewX(TEXT_ITALIC_CONST);
                break;

            default:
                break;
            }

            sPaints[i] = paint;
        }

    }

    private static String transposeChord(String input, int transpose,
            TransposeType type) {
        if (transpose == 0)
            return input;

        String[] noteNames;
        String root = "";
        String chordType = "";
        if (input.length() == 1) {
            noteNames = NOTE_NAMES_SHARP;
            root = input;
        } else if (input.charAt(1) == '#') {
            noteNames = NOTE_NAMES_SHARP;
            root = input.substring(0, 2);
            chordType = input.substring(2);
        } else if (input.charAt(1) == 'b') {
            noteNames = NOTE_NAMES_FLAT;
            root = input.substring(0, 2);
            chordType = input.substring(2);
        } else {
            noteNames = NOTE_NAMES_SHARP;
            root = input.substring(0, 1);
            chordType = input.substring(1);
        }

        int noteIndex;
        for (noteIndex = 0; noteIndex < noteNames.length; noteIndex++) {
            if (noteNames[noteIndex].equals(root))
                break;
        }
        noteIndex = (noteIndex + transpose + noteNames.length)
                % noteNames.length;

        if (type == TransposeType.Sharp) {
            return NOTE_NAMES_SHARP[noteIndex] + chordType;
        } else {
            return NOTE_NAMES_FLAT[noteIndex] + chordType;
        }
    }
}
