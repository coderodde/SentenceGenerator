package com.github.coderodde.ai.sentencegenerator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Potilaskone
 */
public final class WordProvider {
    
    public static List<List<String>> getWords(List<String> sentences) {
        List<List<String>> returnList =
                new ArrayList<>(sentences.size());
        
        for (String sentence : sentences) {
            String[] words = sentence.split("\\s+|,|;|:|\"");
            List<String> wordList = new ArrayList<>();
            
            for (String word : words) {
                word = cleanWord(word);
                
                if (word == null) {
                    continue;
                }
                
                if (word.endsWith("!!!")) {
                    wordList.add("!!!");
                    
                    if (word.length() > 3) {
                        wordList.add(
                                word.substring(
                                        0,
                                        word.length() - 3));
                    }
                } else {
                    char lastChar = word.charAt(word.length() - 1);

                    switch (lastChar) {
                        case '.' -> wordList.add(".");
                        case '?' -> wordList.add("?");
                        case '!' -> wordList.add("!");
                        default  -> wordList.add(word);
                    }
                }
            }
            
            returnList.add(wordList);
        }
        
        return returnList;
    }
    
    private static String cleanWord(String word) {
        word = word.replace("“", "")
                   .replace("(", "")
                   .replace(")", "");
        
        if (word.isBlank()) {
            return null;
        }
        
        int i = 0;
        int cutOffIndex = -1;
        
        for (; i < word.length(); i++, cutOffIndex++) {
            char ch = word.charAt(i);
            
            if (ch != '‘') {
                break;
            }
        }
        
        if (cutOffIndex >= 0) {
            word = word.substring(cutOffIndex + 1);
        }
        
        switch (word) {
            case "’":
            case "”":
                return null;
        }
        
        if (!wordIsAlphaNumeric(word)) {
            return null;
        }
        
        return word;
    }
    
    private static boolean wordIsAlphaNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            
            if (!Character.isLetterOrDigit(ch)) {
                return false;
            }
        }
        
        return true;
    }
}
