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
public class SentenceProducer {
    
    private static final String SPLIT_REGEX = "\\.|\\?|;|!|—";
    
    private final File file;
    
    public SentenceProducer(String fileName) {
        this.file = new File(fileName);
    }
    
    public List<String> getSentences() throws IOException {
        String allText = Files.readString(file.toPath());
        String[] sentences = allText.split(SPLIT_REGEX);
        
        List<String> sentenceList =
                new ArrayList<>(sentences.length);
        
        for (String sentence : sentences) {
            sentenceList.add(sentence.trim().toLowerCase());
        }
        
        return sentenceList;
    }
}
