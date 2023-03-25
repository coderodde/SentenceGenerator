package com.github.coderodde.ai.sentencegenerator;

import java.util.ArrayList;
import java.util.List;

final class WordProvider {
    
    public static List<List<String>> getWords(List<String> sentences) {
        List<List<String>> returnList =
                new ArrayList<>(sentences.size());
        
        for (String sentence : sentences) {
            List<String> words = splitSentenceToWords(sentence);
            List<String> wordList = new ArrayList<>();
            boolean addedWord = false;
            
            for (String word : words) {
                word = cleanWord(word.toLowerCase());
                
                if (word == null) {
                    continue;
                } 
                
                if (word.isBlank()) {
                    continue;
                }
                
                wordList.add(word);
                addedWord = true;
            }
            
            if (addedWord) {
                char lastChar = sentence.charAt(sentence.length() - 1);

                switch (lastChar) {
                    case '.' -> wordList.add(".");
                    case '?' -> wordList.add("?");
                    case '!' -> wordList.add("!");
                }

                returnList.add(wordList);
            }
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
    
    private static List<String> splitSentenceToWords(String sentence) {
        sentence = sentence.trim();
        String[] wordArray = sentence.split("\\s+|\\.|,|;|:|\"|\r|\n");
        List<String> returnArray = 
                new ArrayList<>(wordArray.length + 1);
        
        for (String word : wordArray) {
            returnArray.add(word);
        }
        
        char lastSentenceCharacter = 
                sentence.charAt(sentence.length() - 1);
        
        switch (lastSentenceCharacter) {
            case '.' -> returnArray.add(".");
            case '?' -> returnArray.add("?");
            case '!' -> returnArray.add("!");
        }
        
        return returnArray;
    }
}
