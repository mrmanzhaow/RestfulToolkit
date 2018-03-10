package com.zhaow.restful.highlight;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

/**
 * 文本高亮
 */
public class JTextAreaHighlight {

    /* 高亮 */
    public static void highlightTextAreaData(JTextArea jTextArea) {


        Highlighter highLighter = jTextArea.getHighlighter();
        DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.DARK_GRAY);
        highLighter.removeAllHighlights();
        String text = jTextArea.getText().trim();
        //TODO: isKeyValueFormat  高亮param？ 高亮 ： 高亮value

        //TODO :isJson  高亮 value ？
        if (text.startsWith("[") || text.startsWith("{")) {
            return;
        }

        int start=0;
        String[] lines = text.split("\n");
        for (String line : lines) {
//            String[] split = line.split(":");
            int index = line.indexOf(":");
            if (index < 0) {
                continue;
            }

            start += index;
            int end = start + 1;
            try {

                highLighter.addHighlight(start, end, highlightPainter);
                start += line.substring(index).length()+1;

            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }


        //高亮 param key
/*        int start=0;
        String[] lines = text.split("\n");
        for (String line : lines) {
            String[] split = line.split(":");
            int end = start + split[0].length();
            try {

                highLighter.addHighlight(start, end, highlightPainter);
                start += line.length()+1;

            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }*/

/*
        String keyWord = "demo";
        int pos = 0;
        while ((pos = text.indexOf(keyWord, pos)) >= 0) {
            try {
                highLighter.addHighlight(pos, pos + keyWord.length(), highlightPainter);
                pos += keyWord.length();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
*/




    }
}
