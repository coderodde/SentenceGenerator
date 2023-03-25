package com.github.coderodde.ai.sentencegenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Potilaskone
 */
public final class SentenceProducer {
    
    private static final String SPLIT_REGEX = "\\s*(\\.|\\?|;|!|—)\\s*";
    
    private final File file;
    
    public SentenceProducer(String fileName) {
        this.file = new File(fileName);
    }
    
    public List<String> getSentences() throws IOException {
        String allText = Files.readString(file.toPath());
        return splitEntireTextToSentences(allText);
    }
    
    private static List<String> splitEntireTextToSentences(String text) {
        char[] chars = text.toCharArray();
        List<String> sentences = new ArrayList<>();
        
        outerLoop:
        for (int i = 0; i < chars.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            
            for (int j = i; j < chars.length; j++, i++) {
                char ch = chars[j];
                
                switch (ch) {
                    case '.', '?', '!' -> {
                        stringBuilder.append(ch);
                        String newSentence = stringBuilder.toString();
                        sentences.add(newSentence);
                        continue outerLoop;
                    }
                    
                    default -> stringBuilder.append(ch);
                }
            }
        }
        
        return sentences;
    }
}
