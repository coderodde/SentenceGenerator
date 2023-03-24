package com.github.coderodde.ai.sentencegenerator.cmd.impl;

import com.github.coderodde.ai.sentencegenerator.cmd.AbstractCommand;
import com.github.coderodde.ai.sentencegenerator.impl.DirectedWordGraphNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Potilaskone
 */
public class GenerateRandomSentenceCommand extends AbstractCommand {

    private final List<DirectedWordGraphNode> graph;
    private final Map<String, DirectedWordGraphNode> graphMap;
    private final Set<String> initialWords;
    private final Random random;
    
    public GenerateRandomSentenceCommand(
            List<DirectedWordGraphNode> graph,
            Map<String, DirectedWordGraphNode> graphMap, 
            Set<String> initialWords,
            Random random) {
        
        this.graph = graph;
        this.graphMap = graphMap;
        this.initialWords = initialWords;
        this.random = random;
    }
    
    public GenerateRandomSentenceCommand(
            List<DirectedWordGraphNode> graph,
            Map<String, DirectedWordGraphNode> graphMap,
            Set<String> initialWords) {
        
        this(graph, graphMap, initialWords, new Random());
    }
    
    @Override
    public void process(String line) {
        String[] lineParts = line.trim().split("\\s+");
        
        if (!isWithinRange(lineParts.length, 1, 2)) {
            System.out.println(
                    "Warning: Command \"" + line + "\" not recognized.");
            return;
        }
        
        String commandWord2 = lineParts.length > 1 ? lineParts[1] : null;
        
        int maximumSentenceLength = 
                commandWord2 != null ?
                Integer.parseInt(commandWord2) : 
                Integer.MAX_VALUE; 
        
        int currentSentenceLength = 1;
        
        DirectedWordGraphNode node = graphMap.get(".");
        List<DirectedWordGraphNode> path = new ArrayList<>();
        path.add(node);
        
        while (true) {
            node = node.sampleParent();
            
            if (node == null) {
                break;
            }
            
            path.add(node);
            currentSentenceLength++;
            
            if (currentSentenceLength >= maximumSentenceLength) {
                if (initialWords.contains(node.getWord())) {
                    break;
                }
            } else if (initialWords.contains(node.getWord())){
                double factor = (1.0 * path.size()) / 
                                (1.0 * maximumSentenceLength);
                
                double cutoff = Math.pow(1.0 - factor, 0.1);
                double coin = random.nextDouble();
                
                if (coin > cutoff) {
                    break;
                }
            }
        }
        
        Collections.<DirectedWordGraphNode>reverse(path);
        print(path);
    }
    
    private static boolean isNotTerminalNode(DirectedWordGraphNode node) {
        switch (node.getWord()) {
            case ".":
            case "?":
            case "!":
            case ";":
            case "...":
            case "!!!":
                return false;
                
            default:
                return true;
        }
    }
}
