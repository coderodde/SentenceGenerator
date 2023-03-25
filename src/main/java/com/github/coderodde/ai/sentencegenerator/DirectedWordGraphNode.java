package com.github.coderodde.ai.sentencegenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Potilaskone
 */
public final class DirectedWordGraphNode 
        implements Comparable<DirectedWordGraphNode> {
    
    private final String word;
    private final BinaryTreeProbabilityDistribution
            parentProbabilityDistribution = 
            new BinaryTreeProbabilityDistribution();
    
    private final BinaryTreeProbabilityDistribution
            childProbabilityDistribution = 
            new BinaryTreeProbabilityDistribution();
    
    private final Map<DirectedWordGraphNode, Integer> childMap = 
            new HashMap<>();
    
    private final Map<DirectedWordGraphNode, Integer> parentMap = 
            new HashMap<>();
    
    public DirectedWordGraphNode(String word) {
        this.word = word;
    }
    
    public String getWord() {
        return word;
    }
    
    public double getChildWeight(DirectedWordGraphNode child) {
        return getParentProbabilityDistribution().getWeight(child);
    }
    
    public double getParentWeight(DirectedWordGraphNode parent) {
        return getChildProbabilityDistribution().getWeight(parent);
    }
    
    public void connectToParent(DirectedWordGraphNode parentNode) {
        if (!parentMap.containsKey(parentNode)) {
            parentMap.put(parentNode, 1);
        } else {
            parentMap.put(
                    parentNode, 
                    parentMap.get(parentNode) + 1);
        }
        
        if (!parentNode.childMap.containsKey(this)) {
            parentNode.childMap.put(this, 1);
        } else {
            parentNode.childMap.put(
                    this,
                    parentNode.childMap.get(this) + 1);
        }
    }
    
    public Set<DirectedWordGraphNode> getChildren() {
        return childMap.keySet();
    }
    
    public Set<DirectedWordGraphNode> getParents() {
        return parentMap.keySet();
    }
    
    public void computeProbabilityDistribution() {
        for (Map.Entry<DirectedWordGraphNode, Integer> entry
                : parentMap.entrySet()) {
            
            double weight = (1.0) * entry.getValue();
            
            parentProbabilityDistribution.addElement(
                    entry.getKey(), 
                    weight);
        }
        
        for (Map.Entry<DirectedWordGraphNode, Integer> entry 
                : childMap.entrySet()) {
            
            double weight = (1.0) * entry.getValue();
            
            childProbabilityDistribution.addElement(
                    entry.getKey(), 
                    weight);
        }
    }
    
    public DirectedWordGraphNode sampleParent() {
        if (parentProbabilityDistribution.isEmpty()) {
            return null;
        }
        
        return parentProbabilityDistribution.sampleElement();
    }
    
    public BinaryTreeProbabilityDistribution 
        getParentProbabilityDistribution() {
        return parentProbabilityDistribution;
    }
    
    public BinaryTreeProbabilityDistribution 
        getChildProbabilityDistribution() {
        return childProbabilityDistribution;
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
