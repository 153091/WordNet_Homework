/** %src\synsets.txt src\hypernyms.txt src\outcast5.txt src\outcast8.txt src\outcast11.txt
 * src\outcast5.txt: table
 src\outcast8.txt: bed
 src\outcast11.txt: potato*/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private WordNet wordnetCopy;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException("WordNet argument is null");
        }

        wordnetCopy = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException("nouns for outcast is null");
        }

        int size = nouns.length;
        for (int i = 0; i < size; i++) {
            if (nouns[i] == null) {
                throw new IllegalArgumentException("Any of nouns is null");
            }
        }

        int[][] allDist = new int[size][size];
        int distMax = -1; // maximal ancestral distance from other nouns
        int idMax = -1; // id of outcast, -1 if NO

        for (int i = 0; i < size; i++) {
            int distance = 0;
            for (int j = 0; j < size; j++) {
                allDist[i][j] = wordnetCopy.distance(nouns[i], nouns[j]);
                // From specification: di = distance(xi, x1) + distance(xi, x2) + ... + distance(xi, xn)
                distance += allDist[i][j];
            }
            if (distance >= distMax) {
                distMax = distance;
                idMax =  i;
            }
        }
        return nouns[idMax];
    }

    // following test client takes from the command line the name of a synset file,
    // the name of a hypernym file, followed by the names of outcast files,
    // and prints out an outcast in each file:
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}