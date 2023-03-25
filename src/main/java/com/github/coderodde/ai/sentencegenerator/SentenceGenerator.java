package com.github.coderodde.ai.sentencegenerator;

import com.github.coderodde.ai.sentencegenerator.WordGraphBuilder.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

final class SentenceGenerator {

    private static final class CommandNames {
        private static final String GENERATE_SENTENCE       = "gen";
        private static final String GET_NUMBER_OF_WORDS     = "words";
        private static final String GET_NUMBER_OF_SENTENCES = "sentences";
        private static final String LIST_ALL_WORDS          = "list";
        private static final String LIST_WORD_RANGE         = "range";
        private static final String WORD_STAT               = "stat";
        private static final String QUIT                    = "quit";
    }
    
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
            } else if (cmdString.startsWith(CommandNames.WORD_STAT)) {
                processShowNodeStats(cmdString, data.graphMap);
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
            String[] lineParts = cmd.trim().split("\\s+");
        
        if (!isWithinRange(lineParts.length, 1, 2)) {
            System.out.println(
                    ">>> Warning: Command \"" + cmd + "\" not recognized.");
            return;
        }
        
        String commandWord2 = lineParts.length > 1 ? lineParts[1] : null;
        
        int maximumSentenceLength = 
                commandWord2 != null ?
                Integer.parseInt(commandWord2) : 
                Integer.MAX_VALUE; 
        
        int currentSentenceLength = 1;
        
        DirectedWordGraphNode node = data.graphMap.get(".");
        List<DirectedWordGraphNode> path = new ArrayList<>();
        path.add(node);
        
        while (currentSentenceLength < maximumSentenceLength) {
            node = node.sampleParent();
            
            if (node == null) {
                break;
            }
            
            path.add(node);
            currentSentenceLength++;
        }
        
        Collections.<DirectedWordGraphNode>reverse(path);
        print(path);
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
            
        String[] parts = cmd.trim().split("\\s+");
        
        if (!isWithinRange(parts.length, 2, 3)) {
            System.out.println("Command \"" + cmd + "\" could not be parsed.");
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
        
    private static void 
        processShowNodeStats(
                String cmd, 
                Map<String, DirectedWordGraphNode> graphMap) {
            
        String[] parts = cmd.split("\\s+");
        String word = parts[1];
        DirectedWordGraphNode node = graphMap.get(word);
        
        if (node == null) {
            System.out.println("\"" + word + "\": no such word.");
            return;
        }
        
        System.out.println("--- Outgoing word arcs:");
        
        BinaryTreeProbabilityDistribution.FieldLengths fieldLengths = 
                node.getChildProbabilityDistribution().getFieldLengths();
        
        double totalWeight = 
                node.getChildProbabilityDistribution()
                    .getTotalWeight();
        
        List<DirectedWordGraphNode> children = 
                new ArrayList<>(node.getChildren());
        
        Collections.<DirectedWordGraphNode>sort(children);
        
        String fmt =
                "%-" 
                + fieldLengths.maximumWordLength
                + "s, w = %" 
                + (fieldLengths.maximumWeightLength + 1) 
                + "f, p = %f";
        
        for (DirectedWordGraphNode child : children) {
            System.out.println(
                    String.format(
                            fmt, 
                            child.getWord(), 
                            child.getChildWeight(node),
                            child.getChildWeight(node) / totalWeight));
        }
        
        System.out.println("Total of " + children.size() + " outgoing arcs.");
        children.clear();
        
        System.out.println("--- Incoming word arcs:");
        
        fieldLengths = node.getParentProbabilityDistribution()
                           .getFieldLengths();
        
        List<DirectedWordGraphNode> parents =
                new ArrayList<>(node.getParents()); 
        
        Collections.<DirectedWordGraphNode>sort(parents);
        
        fmt =
                "%-" 
                + fieldLengths.maximumWordLength
                + "s, w = %" 
                + (fieldLengths.maximumWeightLength + 1) 
                + "f, p = %f";
        
        totalWeight = node.getParentProbabilityDistribution().getTotalWeight();
        
        for (DirectedWordGraphNode parent : parents) {
            System.out.println(
                    String.format(
                            fmt, 
                            parent.getWord(),
                            parent.getParentWeight(node),
                            parent.getParentWeight(node) / totalWeight));
        }
        
        System.out.println("Total of " + parents.size() + " incoming arcs.");
        parents.clear();
    }
        
    private static void processCommandQuit() {
        System.out.println(">>> Bye!");
        System.exit(0);
    }
    
    private static boolean isWithinRange(int value, int min, int max) {
        return !(value < min || value > max);
    }
    
    private static void print(List<DirectedWordGraphNode> path) {
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
}
