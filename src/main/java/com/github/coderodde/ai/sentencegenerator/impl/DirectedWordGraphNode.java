package com.github.coderodde.ai.sentencegenerator.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Potilaskone
 */
public final class DirectedWordGraphNode 
        implements Comparable<DirectedWordGraphNode> {
    
    private final String word;
    private final BinaryTreeProbabilityDistribution<DirectedWordGraphNode>
            probabilityDistribution = 
            new BinaryTreeProbabilityDistribution();
    
    private final Set<DirectedWordGraphNode> children = new HashSet<>();
    
    private final Map<DirectedWordGraphNode, Integer> parentMap = 
            new HashMap<>();
    
    public DirectedWordGraphNode(String word) {
        this.word = word;
    }
    
    public String getWord() {
        return word;
    }
    
    public Set<DirectedWordGraphNode> getChildren() {
        return children;
    }
    
    public double getWeight(DirectedWordGraphNode node) {
        return node.getWeight(this);
    }
    
    public void connectToParent(DirectedWordGraphNode parentNode) {
        if (!parentMap.containsKey(parentNode)) {
            parentMap.put(parentNode, 1);
        } else {
            parentMap.put(
                    parentNode, 
                    parentMap.get(parentNode) + 1);
        }
        
        parentNode.children.add(this);
    }
    
    public void computeProbabilityDistribution() {
        for (Map.Entry<DirectedWordGraphNode, Integer> entry
                : parentMap.entrySet()) {
            
            double weight = (1.0) * entry.getValue();
            probabilityDistribution.addElement(entry.getKey(), weight);
        }
    }
    
    public DirectedWordGraphNode sampleParent() {
        if (probabilityDistribution.isEmpty()) {
            return null;
        }
        
        return probabilityDistribution.sampleElement();
    }
    
    public BinaryTreeProbabilityDistribution<DirectedWordGraphNode> 
        getProbabilityDistribution() {
        return probabilityDistribution;
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
}
