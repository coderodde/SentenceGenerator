package com.github.coderodde.ai.sentencegenerator.cmd.impl;

import com.github.coderodde.ai.sentencegenerator.cmd.AbstractCommand;
import com.github.coderodde.ai.sentencegenerator.impl.DirectedWordGraphNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Potilaskone
 */
public class GenerateRandomSentenceCommand extends AbstractCommand {

    private final List<DirectedWordGraphNode> graph;
    private final Map<String, DirectedWordGraphNode> graphMap;
    private final Random random;
    
    public GenerateRandomSentenceCommand(
            List<DirectedWordGraphNode> graph,
            Map<String, DirectedWordGraphNode> graphMap,                
            Random random) {
        
        this.graph = graph;
        this.graphMap = graphMap;
        this.random = random;
    }
    
    public GenerateRandomSentenceCommand(
            List<DirectedWordGraphNode> graph,
            Map<String, DirectedWordGraphNode> graphMap) {
        this(graph, graphMap, new Random());
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
        
        DirectedWordGraphNode node = 
                graph.get(random.nextInt(graph.size()));
        
        List<DirectedWordGraphNode> path = new ArrayList<>();
        path.add(node);
        
        while (isNotTerminalNode(node) 
                && currentSentenceLength < maximumSentenceLength) {
            
            node = node.sampleNext();
            
            if (node == null) {
                break;
            }
            
            path.add(node);
            currentSentenceLength++;
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
