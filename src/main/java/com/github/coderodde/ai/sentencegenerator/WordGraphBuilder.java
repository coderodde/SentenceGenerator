package com.github.coderodde.ai.sentencegenerator;

import com.github.coderodde.ai.sentencegenerator.impl.DirectedWordGraphNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Potilaskone
 */
public final class WordGraphBuilder {
    
    public static final class Data {
        public final List<DirectedWordGraphNode> graph;
        public final Map<String, DirectedWordGraphNode> graphMap;
        public final Set<String> initialWords;
        public final int numberOfSentences;
        public final int numberOfDistinctWords;
        public final int numberOfWords;
        
        private Data(List<DirectedWordGraphNode> graph,
                     Map<String, DirectedWordGraphNode> graphMap,
                     Set<String> initialWords,
                     int numberOfSentences,
                     int numberOfDistinctWords,
                     int numberOfWords) {
            
            this.graph = graph;
            this.graphMap = graphMap;
            this.initialWords = initialWords;
            this.numberOfSentences = numberOfSentences;
            this.numberOfDistinctWords = numberOfDistinctWords;
            this.numberOfWords = numberOfWords;
        }
    }
    
    public static Data buildGraph(List<List<String>> sentenceList) {
        Map<String, DirectedWordGraphNode> nodeMap = new HashMap<>();
        List<DirectedWordGraphNode> graph = new ArrayList<>();
        Set<String> initialWords = 
                new HashSet<>(sentenceList.size());
        
        int numberOfDistinctWords = 0;
        int numberOfWords = 0;
        
        for (List<String> sentence : sentenceList) {
            for (String word : sentence) {
                numberOfWords++;
                
                if (!nodeMap.containsKey(word)) {
                    numberOfDistinctWords++;
                    
                    DirectedWordGraphNode directedWordGraphNode = 
                            new DirectedWordGraphNode(word);
                    
                    nodeMap.put(word, directedWordGraphNode);
                    graph.add(directedWordGraphNode);
                }
            }
        }
        
        for (List<String> sentence : sentenceList) {
            if (sentence.isEmpty()) {
                continue;
            }
            
            initialWords.add(sentence.get(0).trim());
            
            for (int i = 0; i < sentence.size() - 1; i++) {
                String word1 = sentence.get(i);
                String word2 = sentence.get(i + 1);
                
                nodeMap.get(word2)
                       .connectToParent(
                               nodeMap.get(word1));
            }
        }
        
        for (DirectedWordGraphNode node : nodeMap.values()) {
            node.computeProbabilityDistribution();
        }
        
        return new Data(graph,
                        getGraphMap(graph),
                        initialWords,
                        sentenceList.size(),
                        numberOfDistinctWords, 
                        numberOfWords);
    }
        
    private static Map<String, DirectedWordGraphNode> 
        getGraphMap(List<DirectedWordGraphNode> graph) {
        Map<String, DirectedWordGraphNode> graphMap = 
                new HashMap<>(graph.size());
        
        for (DirectedWordGraphNode node : graph) {
            graphMap.put(node.getWord(), node);
        }
        
        return graphMap;
    }
}
