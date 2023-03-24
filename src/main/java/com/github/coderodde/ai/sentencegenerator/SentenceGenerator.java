package com.github.coderodde.ai.sentencegenerator;

import com.github.coderodde.ai.sentencegenerator.WordGraphBuilder.Data;
import com.github.coderodde.ai.sentencegenerator.cmd.AbstractCommand;
import com.github.coderodde.ai.sentencegenerator.cmd.impl.GenerateRandomSentenceCommand;
import com.github.coderodde.ai.sentencegenerator.cmd.impl.ListWordRangeCommand;
import com.github.coderodde.ai.sentencegenerator.impl.DirectedWordGraphNode;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
    
    private static final class Commands {
        private final AbstractCommand generateRandomSentenceCommand;
        private final AbstractCommand listWordRangeCommand;
        
        Commands(AbstractCommand generateRandomSentence,
                 AbstractCommand listWordRange) {
            generateRandomSentenceCommand = generateRandomSentence;
            listWordRangeCommand = listWordRange;
        }
    }
    
    private static Commands commands;
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.exit(1);
        }
        
        List<String> sentences = null;
        long totalPreprocessingDuration = 0L;
        
        try {
            long startTime = System.currentTimeMillis();
            sentences = new SentenceProducer(args[0]).getSentences();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            totalPreprocessingDuration += duration;
            System.out.println("Producing sentences took " + duration + " ms.");
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(2);
        }
        
        long startTime = System.currentTimeMillis();
        List<List<String>> words = WordProvider.getWords(sentences);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        totalPreprocessingDuration += duration;
        
        System.out.println("Producing word data took " + duration + " ms.");
        
        startTime = System.currentTimeMillis();
        Data data = WordGraphBuilder.buildGraph(words);
        Collections.<DirectedWordGraphNode>sort(data.graph);
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        totalPreprocessingDuration += duration;
        
        System.out.println("Building word graph took " + duration + " ms.");
        System.out.println(
                "Total preprocessing took " 
                        + totalPreprocessingDuration 
                        + " ms.");
        
        
        
        commands = new Commands(
                new GenerateRandomSentenceCommand(
                        data.graph, 
                        data.graphMap,
                        data.initialWords),
                new ListWordRangeCommand(data.graph));
        
        repl(data);
    }
    
    private static void repl(Data data) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("> ");
            String cmdString = scanner.nextLine();
            
            if (cmdString.startsWith(CommandNames.GENERATE_SENTENCE)) {
                processCommandGenerateSentence(cmdString, data);
            } else if (cmdString.startsWith(
                    CommandNames.GET_NUMBER_OF_SENTENCES)) {
                
                processCommandGetNumberOfSentences(
                        data.numberOfSentences);
                
            } else if (cmdString.startsWith(
                    CommandNames.GET_NUMBER_OF_WORDS)) {
                processCommandGetNumberOfWords(cmdString, data);
            } else if (cmdString.startsWith(
                    CommandNames.LIST_ALL_WORDS)) {
                processCommandListAllWords(data.graph);
            } else if (cmdString.startsWith(
                    CommandNames.LIST_WORD_RANGE)) {
                processCommandListWordRange(cmdString, data.graph);
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
                Data data) {
        commands.generateRandomSentenceCommand.process(cmd);
    }
        
    private static void 
        processCommandGetNumberOfSentences(int numberOfSentences) {
        System.out.println(">>> " + numberOfSentences);
    }
        
    private static void processCommandGetNumberOfWords(String cmd, Data data) {
        String[] parts = cmd.trim().split("\\s+");
        boolean distinct = false;
        
        if (parts.length == 2) {
            distinct = (parts[1].equals("-d"));
        }
        
        System.out.println(
                ">>> " 
                        + (distinct ? 
                                data.numberOfDistinctWords :
                                data.numberOfWords));
    }
    
    private static void 
        processCommandListAllWords(List<DirectedWordGraphNode> graph) {
        graph.forEach(System.out::println);
    }
    
    private static void 
        processCommandListWordRange(
                String cmd,
                List<DirectedWordGraphNode> graph) {
        commands.listWordRangeCommand.process(cmd);
    }
        
    private static void processCommandQuit() {
        System.out.println(">>> Bye!");
        System.exit(0);
    }
}
