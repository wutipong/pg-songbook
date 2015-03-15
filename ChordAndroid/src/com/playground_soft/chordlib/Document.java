package com.playground_soft.chordlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
    public final List<Element> elementList;
    public final String title;
    public final String subtitle;

    private Document(List<Element> elementList, String title, String subtitle) {

        this.elementList = elementList;
        this.title = title;
        this.subtitle = subtitle;

    }

    public Element getElement(int i) {
        return elementList.get(i);
    }

    public int count() {
        return elementList.size();
    }

    public static Document createFromFile(File input) throws IOException {

        StringBuilder builder = new StringBuilder();

        InputStream stream = new FileInputStream(input);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));

        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }

        reader.close();
        stream.close();

        return Document.create(builder.toString());
    }

    public static Document create(String input) {
        String title = "", subtitle = "";

        // Remove comment lines
        StringBuilder builder = new StringBuilder();
        for (String s : input.split("\n")) {
            if (s.startsWith("#"))
                continue;
            builder.append(s);
            builder.append("\n");
        }

        input = builder.toString();

        // Processing directives
        Pattern pattern;
        pattern = Pattern.compile("(\\{.*?\\})");
        Matcher matcher = pattern.matcher(input);
        int start = 0;
        int end = 0;
        int lastend = 0;

        List<Element> elementList = new ArrayList<Element>();

        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();

            if (start != lastend) {
                String text = input.substring(lastend, start);
                processText(elementList, text);
            }

            String directive = input.substring(start + 1, end - 1); // remove {
                                                                    // and }
            String[] directives = directive.split(":");
            String name = directives[0].trim();
            String value = "";
            if (directives.length > 1) {
                value = directives[1].trim();
            } else {
                name = directive;
            }
            name = name.replace(":", "");

            if (name.equals("title") || name.equals("t"))
                title = value;

            if (name.equals("subtitle") || name.equals("st"))
                subtitle = value;

            if (name.equals("start_of_chorus") || name.equals("soc"))
                elementList.add(Element.create(Element.Type.StartOfChorus, ""));

            if (name.equals("end_of_chorus") || name.equals("eoc"))
                elementList.add(Element.create(Element.Type.EndOfChorus, ""));

            if (name.equals("comment") || name.equals("c"))
                elementList.add(Element.create(Element.Type.Comment, value));

            if (name.equals("start_of_tab") || name.equals("sot")) {
                elementList.add(Element.create(Element.Type.StartOfTab, ""));
            }

            if (name.equals("end_of_tab") || name.equals("eot")) {
                elementList.add(Element.create(Element.Type.EndOfTab, ""));
            }

            lastend = end;
        }

        if (lastend != input.length()) {
            String text = input.substring(lastend, input.length());
            processText(elementList, text);

        }

        removeEmptyText(elementList);
        removeTopEmptyLines(elementList);

        return new Document(elementList, title, subtitle);
    }

    private static void removeTopEmptyLines(List<Element> elementList) {
        Iterator<Element> iter = elementList.iterator();

        while (iter.hasNext()) {
            Element element = iter.next();
            if (element.type == Element.Type.Linebreak)
                iter.remove();
            else
                break;
        }
    }

    private static void removeEmptyText(List<Element> elementList) {
        Iterator<Element> iter = elementList.iterator();

        while (iter.hasNext()) {
            Element element = iter.next();
            if (element.type == Element.Type.Text
                    && element.data.trim().length() == 0)
                iter.remove();
        }
    }

    public static void processText(List<Element> elementList, String input) {
        input = input.replaceAll("\n", " \n");
        final Pattern pattern = Pattern.compile("(\\[.*?\\])");
        String[] lines = input.split("\n");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);

            int lastend = 0, start = 0, end = 0;
            while (matcher.find()) {
                start = matcher.start();
                end = matcher.end();

                if (lastend != start) {
                    elementList.add(Element.create(Element.Type.Text,
                            line.substring(lastend, start)));
                }

                elementList.add(Element.create(Element.Type.Chord,
                        line.substring(start + 1, end - 1))); // remove [ and ]

                lastend = end;
            }

            if (lastend != line.length())
                elementList.add(Element.create(Element.Type.Text,
                        line.substring(lastend, line.length())));

            elementList.add(Element.create(Element.Type.Linebreak, ""));
        }
    }
}
