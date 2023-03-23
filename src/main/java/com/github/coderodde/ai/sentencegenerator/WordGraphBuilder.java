package com.github.coderodde.ai.sentencegenerator;

import com.github.coderodde.ai.sentencegenerator.impl.DirectedWordGraphNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Potilaskone
 */
public final class WordGraphBuilder {
    
    public static List<DirectedWordGraphNode> 
        buildGraph(List<List<String>> sentenceList) {
            
        Map<String, DirectedWordGraphNode> nodeMap = new HashMap<>();
        List<DirectedWordGraphNode> graph = new ArrayList<>();
        
        for (List<String> sentence : sentenceList) {
            for (String word : sentence) {
                if (!nodeMap.containsKey(word)) {
                    DirectedWordGraphNode directedWordGraphNode = 
                            new DirectedWordGraphNode(word);
                    
                    nodeMap.put(word, directedWordGraphNode);
                    graph.add(directedWordGraphNode);
                }
            }
        }
        
        for (List<String> sentence : sentenceList) {
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
        
        return graph;
    }
}
