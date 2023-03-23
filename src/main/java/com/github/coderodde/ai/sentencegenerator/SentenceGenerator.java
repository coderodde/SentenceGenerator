package com.github.coderodde.ai.sentencegenerator;

import com.github.coderodde.ai.sentencegenerator.cmd.impl.GenerateRandomSentenceCommand;
import com.github.coderodde.ai.sentencegenerator.cmd.impl.ListWordRangeCommand;
import com.github.coderodde.ai.sentencegenerator.impl.DirectedWordGraphNode;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Potilaskone
 */
public final class SentenceGenerator {

    private static final class CommandNames {
        private static final String GENERATE_SENTENCE       = "gen";
        private static final String GET_NUMBER_OF_WORDS     = "words";
        private static final String GET_NUMBER_OF_SENTENCES = "sentences";
        private static final String LIST_ALL_WORDS          = "list";
        private static final String LIST_WORD_RANGE         = "range";
        private static final String QUIT                    = "quit";
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.exit(1);
        }
        
        List<String> sentences = null;
        
        try {
            sentences = new SentenceProducer(args[0]).getSentences();
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(2);
        }
        
        List<List<String>> words = WordProvider.getWords(sentences);
        List<DirectedWordGraphNode> graph = 
                WordGraphBuilder.buildGraph(words);
        
        Collections.<DirectedWordGraphNode>sort(graph);
        
        Map<String, DirectedWordGraphNode> graphMap = getGraphMap(graph);
        
        repl(graph, graphMap, sentences.size());
    }
    
    private static void repl(List<DirectedWordGraphNode> graph,
                             Map<String, DirectedWordGraphNode> graphMap,
                             int numberOfSentences) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("> ");
            String cmdString = scanner.nextLine();
            
            if (cmdString.startsWith(CommandNames.GENERATE_SENTENCE)) {
                processCommandGenerateSentence(cmdString, graph, graphMap);
            } else if (cmdString.startsWith(
                    CommandNames.GET_NUMBER_OF_SENTENCES)) {
                processCommandGetNumberOfSentences(numberOfSentences);
            } else if (cmdString.startsWith(
                    CommandNames.GET_NUMBER_OF_WORDS)) {
                processCommandGetNumberOfWords(graph.size());
            } else if (cmdString.startsWith(
                    CommandNames.LIST_ALL_WORDS)) {
                processCommandListAllWords(graph);
            } else if (cmdString.startsWith(
                    CommandNames.LIST_WORD_RANGE)) {
                processCommandListWordRange(cmdString, graph);
            } else if (cmdString.startsWith(CommandNames.QUIT)) {
                processCommandQuit();
            } else {
                System.out.println(
                        ">>> Warning: \"" + cmdString + "\" has not parsed.");
            }
        }
    }
    
    private static void 
        processCommandGenerateSentence(
                String cmd,
                List<DirectedWordGraphNode> graph, 
                Map<String, DirectedWordGraphNode> graphMap) {
            
        new GenerateRandomSentenceCommand(graph, graphMap).process(cmd);
    }
        
    private static void 
        processCommandGetNumberOfSentences(int numberOfSentences) {
        System.out.println(">>> " + numberOfSentences);
    }
        
    private static void processCommandGetNumberOfWords(int numberOfWords) {
        System.out.println(">>> " + numberOfWords);
    }
    
    private static void 
        processCommandListAllWords(List<DirectedWordGraphNode> graph) {
        graph.forEach(System.out::println);
    }
    
    private static void 
        processCommandListWordRange(
                String cmd,
                List<DirectedWordGraphNode> graph) {
        
        new ListWordRangeCommand(graph).process(cmd);
    }
        
    private static void processCommandQuit() {
        System.out.println(">>> Bye!");
        System.exit(0);
    }
        
    private static Map<String, DirectedWordGraphNode> 
        getGraphMap(List<DirectedWordGraphNode> graph) {
        Map<String, DirectedWordGraphNode> graphMap = 
                new HashMap<>(graph.size());
        
        for (DirectedWordGraphNode node : graph) {
            graphMap.put(node.getWord(), node);
        }
        
        return graphMap;
    }
}
