package com.github.coderodde.ai.sentencegenerator.cmd.impl;

import com.github.coderodde.ai.sentencegenerator.cmd.AbstractCommand;
import com.github.coderodde.ai.sentencegenerator.impl.DirectedWordGraphNode;
import java.util.List;

public final class ListWordRangeCommand extends AbstractCommand {

    private final List<DirectedWordGraphNode> graph;
    
    public ListWordRangeCommand(List<DirectedWordGraphNode> graph) {
        this.graph = graph;
    }
    
    @Override
    public void process(String line) {
        String[] parts = line.trim().split("\\s+");
        
        if (!isWithinRange(parts.length, 2, 3)) {
            System.out.println("Command \"" + line + "\" could not be parsed.");
            return;
        }
        
        int index1;
        int index2;
        
        try {
            index1 = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            System.out.println(parts[1] + " is not an index expression.");
            return;
        }
        
        if (!isWithinRange(index1, 0, graph.size() - 1)) {
            System.out.println(
                    "Index " 
                            + index1
                            + " is not within bounds: " 
                            + index1
                            + ", words: " 
                            + graph.size() 
                            + ".");
            return;
        }
        
        if (parts.length == 2) {
            System.out.println(graph.get(index1).getWord());
            return;
        }
        
        try {
            index2 = Integer.parseInt(parts[2]);
        } catch (NumberFormatException ex) {
            System.out.println(parts[2] + " is not an index expression.");
            return;
        }
        
        if (!isWithinRange(index2, 0, graph.size() - 1)) {
            System.out.println(
                    "Index " 
                            + index1
                            + " is not within bounds: " 
                            + index1
                            + ", words: " 
                            + graph.size() 
                            + ".");
            return;
        }
        
        if (index1 > index2) {
            System.out.println(">>> Indices are reversed.");
            return;
        }
        
        for (int i = index1; i <= index2; i++) {
            System.out.println(graph.get(i));
        }
    }
}
