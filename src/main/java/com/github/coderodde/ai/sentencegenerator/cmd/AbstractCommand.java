package com.github.coderodde.ai.sentencegenerator.cmd;

import com.github.coderodde.ai.sentencegenerator.impl.DirectedWordGraphNode;
import java.util.List;

/**
 *
 * @author Potilaskone
 */
public abstract class AbstractCommand {
    
    public abstract void process(String line);
    
    protected void print(List<DirectedWordGraphNode> path) {
        DirectedWordGraphNode node = path.get(0);
        
        StringBuilder stringBuilder = 
                new StringBuilder()
                .append(Character.toUpperCase(
                        node.getWord().charAt(0)))
                .append(node.getWord().substring(1));
        
        for (int i = 1; i < path.size(); i++) {
            stringBuilder.append(" ")
                         .append(path.get(i).getWord());
        }
        
        System.out.println(">>> " + stringBuilder.toString());
    }
    
    protected static boolean isWithinRange(int value, int min, int max) {
        if (value < min || value > max) {
            return false;
        }
        
        return true;
    }
}
