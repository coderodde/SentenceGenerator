package com.github.coderodde.ai.sentencegenerator;
 
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
 
final class BinaryTreeProbabilityDistribution {
 
    public static final class FieldLengths {
        public final int maximumWordLength;
        public final int maximumWeightLength;
        
        private FieldLengths(int maximumWordLength, int maximumWeightLength) {
            this.maximumWordLength = maximumWordLength;
            this.maximumWeightLength = maximumWeightLength;
        }
    }
    
    private static final class Node {
 
        private final DirectedWordGraphNode element;
        private double weight;
        private final boolean isRelayNode;
        private Node leftChild;
        private Node rightChild;
        private Node parent;
        private int numberOfLeafNodes;
 
        Node(DirectedWordGraphNode element, double weight) {
            this.element           = element;
            this.weight            = weight;
            this.numberOfLeafNodes = 1;
            this.isRelayNode       = false;
        }   
 
        Node(double weight) {
            this.element           = null;
            this.weight            = weight;
            this.numberOfLeafNodes = 1;
            this.isRelayNode       = true;
        }
 
        @Override
        public String toString() {
            if (isRelayNode) {
                return "[" + String.format("%.3f", getWeight()) + " : "
                           + numberOfLeafNodes + "]";
            }
 
            return "(" + String.format("%.3f", getWeight()) + " : "
                       + element + ")";
        }
 
        DirectedWordGraphNode getElement() {
            return element;
        }
 
        double getWeight() {
            return weight;
        }
 
        void setWeight(double weight) {
            this.weight = weight;
        }
 
        int getNumberOfLeaves() {
            return numberOfLeafNodes;
        }
 
        void setNumberOfLeaves(int numberOfLeaves) {
            this.numberOfLeafNodes = numberOfLeaves;
        }
 
        Node getLeftChild() {
            return leftChild;
        }
 
        void setLeftChild(Node block) {
            this.leftChild = block;
        }
 
        Node getRightChild() {
            return rightChild;
        }
 
        void setRightChild(Node block) {
            this.rightChild = block;
        }
 
        Node getParent() {
            return parent;
        }
 
        void setParent(Node block) {
            this.parent = block;
        }
 
        boolean isRelayNode() {
            return isRelayNode;
        }
    }
    
    private final Map<DirectedWordGraphNode, Node> map = new HashMap<>();
    private Node root;
    private double totalWeight;
    private final Random random;
    
    public BinaryTreeProbabilityDistribution() {
        this(new Random());
    }
 
    public BinaryTreeProbabilityDistribution(Random random) {
        this.random = random;
    }
 
    public boolean addElement(DirectedWordGraphNode element, double weight) {
        checkWeightNotNaNAndIsPositive(weight);
        Node node = map.get(element);
         
        if (node == null) {
            node = new Node(element, weight);
            insert(node);
            map.put(element, node);
        } else {
            node.setWeight(node.getWeight() + weight);
            updateMetadata(node, weight, 0);
        }
         
        totalWeight += weight;
        return true;
    }
 
    public boolean contains(DirectedWordGraphNode element) {
        return map.containsKey(element);
    }
 
    public DirectedWordGraphNode sampleElement() {
        checkNotEmpty(map.size());
        double value = totalWeight * random.nextDouble();
        Node node = root;
 
        while (node.isRelayNode()) {
            if (value < node.getLeftChild().getWeight()) {
                node = node.getLeftChild();
            } else {
                value -= node.getLeftChild().getWeight();
                node = node.getRightChild();
            }
        }
 
        return node.getElement();
    }
    
    public double getTotalWeight() {
        return totalWeight;
    }
    
    public double getWeight(DirectedWordGraphNode element) {
        Node node = map.get(element);
        
        if (node == null) {
            throw new NoSuchElementException(
                    "The input element not found in the distribution.");
        }
        
        return node.getWeight();
    }
    
    public double getProbability(DirectedWordGraphNode element) {
        Node node = map.get(element);
        
        if (node == null) {
            throw new NoSuchElementException(
                    "The input element not found in the distribution.");
        }
        
        return node.getWeight() / totalWeight;
    }
     
    public boolean removeElement(DirectedWordGraphNode element) {
        Node node = map.remove(element);
 
        if (node == null) {
            return false;
        }
 
        delete(node);
        totalWeight -= node.getWeight();
        return true;
    }
 
    public void clear() {
        root = null;
        map.clear();
        totalWeight = 0.0;
    }
      
    public boolean isEmpty() {
        return map.isEmpty();
    }
 
    public int size() {
        return map.size();
    }
    
    public FieldLengths getFieldLengths() {
        int maximumWordLength = 0;
        int maximumWeightLength = 0;
        
        for (Node node : map.values()) {
            String word = node.getElement().getWord();
            double weight = node.getWeight();
            
            int wordLength = word.length();
            int weightLength = Double.toString(weight).length();
            
            maximumWordLength = Math.max(maximumWordLength, wordLength);
            maximumWeightLength = Math.max(maximumWeightLength, weightLength);
        }
        
        return new FieldLengths(maximumWordLength, 
                                maximumWeightLength);
    }
    
    public String getEntryString(DirectedWordGraphNode element) {
        Node node = map.get(element);
        
        if (node == null) {
            return null;
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        double probability = node.getWeight() / totalWeight;
        stringBuilder.append(element.toString())
                     .append(", w = ")
                     .append(node.getWeight())
                     .append(", p = ")
                     .append(probability);
        
        return stringBuilder.toString();
    }
    
    
     
//    @Override
//    public String toString() {
//        StringBuilder stringBuilder = new StringBuilder();
//        Node node = getMinimumNode();
//        
//        int maximumWordLength = getMaximumWordLength();
//        
//        while (node != null) {
//            String str = 
//                    String.format(
//                            "%+" + (maximumWordLength + 1) + "s", 
//                            node.element.toString());
//            
//            stringBuilder.append(str)
//                         .append(", w = ")
//                         .append(node.getWeight())
//                         .append(", p = ")
//                         .append(node.getWeight() / totalWeight)
//                         .append("\n");
//            
//            node = getSuccessorOf(node);
//        }
//        
//        return stringBuilder.toString();
//    }
    
    private Node getMinimumNode() {
        if (isEmpty()) {
            return null;
        }
        
        Node node = root;
        
        while (node.getLeftChild() != null) {
            node = node.getLeftChild();
        }
        
        return node;
    }
    
    private Node getMinimumNode(Node node) {
        while (node.getLeftChild() != null) {
            node = node.getLeftChild();
        }
        
        return node;
    }
    
    private Node getSuccessorOf(Node node) {
        if (node.getRightChild() != null) {
            return getMinimumNode(node.getRightChild());
        }
        
        Node parent = node.getParent();
        
        while (parent != null && parent.getRightChild() == node) {
            node = parent;
            parent = parent.getParent();
        }
        
        return parent;
    }
    
    private void bypassLeafNode(Node leafNodeToBypass, 
                                Node newNode) {
        Node relayNode = new Node(leafNodeToBypass.getWeight());
        Node parentOfCurrentNode = leafNodeToBypass.getParent();
 
        relayNode.setLeftChild(leafNodeToBypass);
        relayNode.setRightChild(newNode);
 
        leafNodeToBypass.setParent(relayNode);
        newNode.setParent(relayNode);
 
        if (parentOfCurrentNode == null) {
            root = relayNode;
        } else if (parentOfCurrentNode.getLeftChild() == leafNodeToBypass) {
            relayNode.setParent(parentOfCurrentNode);
            parentOfCurrentNode.setLeftChild(relayNode);
        } else {
            relayNode.setParent(parentOfCurrentNode);
            parentOfCurrentNode.setRightChild(relayNode);
        }
 
        updateMetadata(relayNode, newNode.getWeight(), 1);
    }
 
    private void insert(Node node) {
        if (root == null) {
            root = node;
            return;
        }
 
        Node currentNode = root;
 
        while (currentNode.isRelayNode()) {
            if (currentNode.getLeftChild().getNumberOfLeaves() < 
                    currentNode.getRightChild().getNumberOfLeaves()) {
                currentNode = currentNode.getLeftChild();
            } else {
                currentNode = currentNode.getRightChild();
            }
        }
 
        bypassLeafNode(currentNode, node);
    }
 
    private void delete(Node leafToDelete) {
        Node relayNode = leafToDelete.getParent();
 
        if (relayNode == null) {
            root = null;
            return;
        } 
 
        Node parentOfRelayNode = relayNode.getParent();
        Node siblingLeaf = relayNode.getLeftChild() == leafToDelete ?
                                    relayNode.getRightChild() :
                                    relayNode.getLeftChild();
 
        if (parentOfRelayNode == null) {
            root = siblingLeaf;
            siblingLeaf.setParent(null);
            return;
        }
 
        if (parentOfRelayNode.getLeftChild() == relayNode) {
            parentOfRelayNode.setLeftChild(siblingLeaf);
        } else {
            parentOfRelayNode.setRightChild(siblingLeaf);
        }
 
        siblingLeaf.setParent(parentOfRelayNode);
        updateMetadata(leafToDelete.getParent(), -leafToDelete.getWeight(), -1);
    }
 
    private void updateMetadata(Node node, 
                                double weightDelta, 
                                int nodeDelta) {
        while (node != null) {
            node.setNumberOfLeaves(node.getNumberOfLeaves() + nodeDelta);
            node.setWeight(node.getWeight() + weightDelta);
            node = node.getParent();
        }
    }

    private void checkWeightNotNaNAndIsPositive(double weight) {
        if (Double.isNaN(weight)) {
            throw new IllegalArgumentException("The element weight is NaN.");
        }
 
        if (weight <= 0.0) {
            throw new IllegalArgumentException(
                    "The element weight must be positive. Received " + weight);
        }
 
        if (Double.isInfinite(weight)) {
            // Once here, 'weight' is positive infinity.
            throw new IllegalArgumentException(
                    "The element weight is infinite.");
        }
    }
 
    private void checkNotEmpty(int size) {
        if (size == 0) {
            throw new IllegalStateException(
                    "This probability distribution is empty.");
        }
    }
}
