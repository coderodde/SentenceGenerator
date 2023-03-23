package com.github.coderodde.ai.sentencegenerator.impl;

import com.github.coderodde.ai.sentencegenerator.AbstractProbabilityDistribution;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Potilaskone
 */
public final class DirectedWordGraphNode 
        implements Comparable<DirectedWordGraphNode> {
    
    private final String word;
    private final AbstractProbabilityDistribution<DirectedWordGraphNode>
            probabilityDistribution = 
            new BinaryTreeProbabilityDistribution();
    
    private final Map<DirectedWordGraphNode, Integer> parentMap = 
            new HashMap<>();
    
    public DirectedWordGraphNode(String word) {
        this.word = word;
    }
    
    public String getWord() {
        return word;
    }
    
    public void connectToParent(DirectedWordGraphNode parentNode) {
        if (!parentMap.containsKey(parentNode)) {
            parentMap.put(parentNode, 1);
        } else {
            parentMap.put(
                    parentNode, 
                    parentMap.get(parentNode) + 1);
        }
    }
    
    public void computeProbabilityDistribution() {
        int totalNumberOfLinks = countTotalNumberOfLinks();
        
        for (Map.Entry<DirectedWordGraphNode, Integer> entry
                : parentMap.entrySet()) {
            
            double weight = (1.0 * entry.getValue()) / totalNumberOfLinks;
            probabilityDistribution.addElement(entry.getKey(), weight);
        }
    }
    
    public DirectedWordGraphNode sampleNext() {
        if (probabilityDistribution.isEmpty()) {
            return null;
        }
        
        return probabilityDistribution.sampleElement();
    }
    
    @Override
    public String toString() {
        return word;
    }
    
    @Override
    public boolean equals(Object o) {
        DirectedWordGraphNode other = (DirectedWordGraphNode) o;
        return word.equals(other.word);
    }
    
    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public int compareTo(DirectedWordGraphNode o) {
        return word.compareTo(o.word);
    }
    
    private int countTotalNumberOfLinks() {
        int count = 0;
        
        for (Integer i : parentMap.values()) {
            count += i;
        }
        
        return count;
    }
}
