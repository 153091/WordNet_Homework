import edu.princeton.cs.algs4.*;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class WordNet {

    private HashMap<Integer, SET<String>> stIdString = new HashMap<>();
    private HashMap<String, SET<Integer>> stStringId = new HashMap<>();
    private SAP sap;
    private Digraph graph;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if ((synsets == null) || (hypernyms == null)) {
            throw new IllegalArgumentException("input argument is null");
        }


        // structure of synsets
        try {
            In synsetsIn = new In(synsets);
            while (!synsetsIn.isEmpty()) {
                String s = synsetsIn.readLine();
                String[] fields = s.split(",");
                int id = Integer.parseInt(fields[0]);
                SET<String> set = new SET<>();
                String[] words = fields[1].split(" ");
                for (int i = 0; i < words.length; i++) {
                    set.add(words[i]);

                    SET<Integer> ids;
                    if (!stStringId.containsKey(words[i])) {
                        ids = new SET<>();
                    }
                    else {
                        ids = stStringId.get(words[i]);
                    }
                    ids.add(id);
                    stStringId.put(words[i], ids);
                }
                stIdString.put(id, set);
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in WordNet constructor", e);
        }
        // digraph constructor
        int vertices = stIdString.size();
        graph = new Digraph(vertices);

        // structure for hypernyms
        try {
            In hypernymsIn = new In(hypernyms);
            while (!hypernymsIn.isEmpty()) {
                String s = hypernymsIn.readLine();
                String[] fields = s.split(",");
                int id = Integer.parseInt(fields[0]);
                for (int i = 1; i < fields.length; i++) {
                    int w = Integer.parseInt(fields[i]);
                    graph.addEdge(id, w);
                }
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in WordNet constructor", e);
        }

        // check for roootedDAG
        Topological topological = new Topological(graph);
        if (!topological.hasOrder()) {
            throw new IllegalArgumentException("Digraph has a cycle");
        }

        // check for only one root
        int roots = 0;
        for (int v = 0; v < graph.V(); v++) {
            if (graph.outdegree(v) == 0) {
                roots++;
                if (roots >= 2) {
                    throw new IllegalArgumentException("Digraph has " + roots + " roots");
                }
            }
        }

        // create SAP
        sap = new SAP(graph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return stStringId.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("This word == null");
        }

        return stStringId.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if ((!isNoun(nounA)) || (!isNoun(nounB))) {
            throw new IllegalArgumentException("Any of this words is not a WordNet nouns");
        }

        return sap.length(stStringId.get(nounA), stStringId.get(nounB));

    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if ((!isNoun(nounA)) || (!isNoun(nounB))) {
            throw new IllegalArgumentException("Any of this words is not a WordNet nouns");
        }

        StringBuilder s = new StringBuilder();
        for (String word : stIdString.get(sap.ancestor(stStringId.get(nounA), stStringId.get(nounB)))) {
            s.append(word + " ");
        }
        return s.toString();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        Stopwatch stopwatch = new Stopwatch();
        WordNet wordnet = new WordNet(args[0], args[1]);
        double time = stopwatch.elapsedTime();
        StdOut.println("elapsed time for building SAP" + time + "seconds");

        StdOut.println("The distance between water and coffee is " + wordnet.distance("water", "coffee"));
        StdOut.println("Common ancestor of water and coffe is " + wordnet.sap("water", "coffee"));
    }
}