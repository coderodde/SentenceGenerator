package com.github.coderodde.ai.sentencegenerator.impl;
 
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
 
public class BinaryTreeProbabilityDistribution<E> {
 
    private static final class Node<E> {
 
        private final E element;
        private double weight;
        private final boolean isRelayNode;
        private Node<E> leftChild;
        private Node<E> rightChild;
        private Node<E> parent;
        private int numberOfLeafNodes;
 
        Node(E element, double weight) {
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
 
        E getElement() {
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
 
        Node<E> getLeftChild() {
            return leftChild;
        }
 
        void setLeftChild(Node<E> block) {
            this.leftChild = block;
        }
 
        Node<E> getRightChild() {
            return rightChild;
        }
 
        void setRightChild(Node<E> block) {
            this.rightChild = block;
        }
 
        Node<E> getParent() {
            return parent;
        }
 
        void setParent(Node<E> block) {
            this.parent = block;
        }
 
        boolean isRelayNode() {
            return isRelayNode;
        }
    }
 
    private final Map<E, Node<E>> map = new HashMap<>();
    private Node<E> root;
    private double totalWeight;
    private final Random random;
    
    public BinaryTreeProbabilityDistribution() {
        this(new Random());
    }
 
    public BinaryTreeProbabilityDistribution(Random random) {
        this.random = random;
    }
 
    public boolean addElement(E element, double weight) {
        checkWeightNotNaNAndIsPositive(weight);
        Node<E> node = map.get(element);
         
        if (node == null) {
            node = new Node<>(element, weight);
            insert(node);
            map.put(element, node);
        } else {
            node.setWeight(node.getWeight() + weight);
            updateMetadata(node, weight, 0);
        }
         
        totalWeight += weight;
        return true;
    }
 
    public boolean contains(E element) {
        return map.containsKey(element);
    }
 
    public E sampleElement() {
        checkNotEmpty(map.size());
        double value = totalWeight * random.nextDouble();
        Node<E> node = root;
 
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
    
    public double getWeight(E element) {
        Node<E> node = map.get(element);
        
        if (node == null) {
            throw new NoSuchElementException(
                    "The input element not found in the distribution.");
        }
        
        return node.getWeight();
    }
     
    public boolean removeElement(E element) {
        Node<E> node = map.remove(element);
 
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
    
    public String getEntryString(E element) {
        Node<E> node = map.get(element);
        
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
     
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Node<E> node = getMinimumNode();
        
        int maximumWordLength = getMaximumWordLength();
        
        while (node != null) {
            String str = 
                    String.format(
                            "%+" + (maximumWordLength + 1) + "s", 
                            node.element.toString());
            
            stringBuilder.append(str)
                         .append(", w = ")
                         .append(node.getWeight())
                         .append(", p = ")
                         .append(node.getWeight() / totalWeight)
                         .append("\n");
            
            node = getSuccessorOf(node);
        }
        
        return stringBuilder.toString();
    }
    
    private int getMaximumWordLength() {
        int maximumWordLength = 0;
        Node<E> node = getMinimumNode();
        
        while (node != null) {
            maximumWordLength = 
                    Math.max(
                            maximumWordLength, 
                            node.getElement().toString().length());
            
            node = getSuccessorOf(node);
        }
        
        return maximumWordLength;
    }
    
    private Node<E> getMinimumNode() {
        if (isEmpty()) {
            return null;
        }
        
        Node<E> node = root;
        
        while (node.getLeftChild() != null) {
            node = node.getLeftChild();
        }
        
        return node;
    }
    
    private Node<E> getMinimumNode(Node<E> node) {
        while (node.getLeftChild() != null) {
            node = node.getLeftChild();
        }
        
        return node;
    }
    
    private Node<E> getSuccessorOf(Node<E> node) {
        if (node.getRightChild() != null) {
            return getMinimumNode(node.getRightChild());
        }
        
        Node<E> parent = node.getParent();
        
        while (parent != null && parent.getLeftChild() == node) {
            node = parent;
            parent = parent.getParent();
        }
        
        return parent;
    }
    
    private void bypassLeafNode(Node<E> leafNodeToBypass, 
                                Node<E> newNode) {
        Node<E> relayNode = new Node<>(leafNodeToBypass.getWeight());
        Node<E> parentOfCurrentNode = leafNodeToBypass.getParent();
 
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
 
    private void insert(Node<E> node) {
        if (root == null) {
            root = node;
            return;
        }
 
        Node<E> currentNode = root;
 
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
 
    private void delete(Node<E> leafToDelete) {
        Node<E> relayNode = leafToDelete.getParent();
 
        if (relayNode == null) {
            root = null;
            return;
        } 
 
        Node<E> parentOfRelayNode = relayNode.getParent();
        Node<E> siblingLeaf = relayNode.getLeftChild() == leafToDelete ?
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
 
    private void updateMetadata(Node<E> node, 
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
