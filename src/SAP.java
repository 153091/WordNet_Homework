import edu.princeton.cs.algs4.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Shortest Ancestral Path
 */

public class SAP {


    private Digraph graph;
    // vertex v and v's ancestors+distTo
    private HashMap<Integer, HashMap<Integer, Integer>> st = new HashMap<>();
    // <vertex v, <vertex w, shortest ancestral length from v>>
    private HashMap<Integer, HashMap<Integer, Integer>> stLength = new HashMap<>();
    // <vertex v, <vertex w, shortest common ancestor>>
    private HashMap<Integer, HashMap<Integer, Integer>> stAncestor = new HashMap<>();


    /**Done*/
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("input digraph is null");
        graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w)

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w)

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w)

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w)

        /**Done*/
    // get ancestors of v + distTo
    private HashMap<Integer, Integer> acquireAncestors(int v) {
        HashMap<Integer, Integer> ancestors = new HashMap<>();
        if (st.containsKey(v)) {
            ancestors = st.get(v);
        }
        else {
            BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(graph, v);
            for (int w = 0; w < graph.V(); w++) {
                if (bfs.hasPathTo(w)) {
                    ancestors.put(w, bfs.distTo(w));
                }
            }
            st.put(v, ancestors);
        }
        return ancestors;
    }

    /**Done*/
    // seek of common ancestor of v and w
    private int seekCommonAncestor(int v, int w) {
        int pathLength = Integer.MAX_VALUE; // associated length sum of DistTo(ancestor)
        int ancestorShort = -1; // shortest common ancestor
        HashMap<Integer, Integer> ancestorsV = acquireAncestors(v); // ancestors + distTo
        HashMap<Integer, Integer> ancestorsW = acquireAncestors(w); // ancestors + distTo
        SET<Integer> ansV = new SET<>();
        SET<Integer> ansW = new SET<>();

        for (int i : ancestorsV.keySet()) {
            ansV.add(i);
        }

        for (int i : ancestorsW.keySet()) {
            ansW.add(i);
        }

        SET<Integer> ancestorCommon = ansV.intersects(ansW); // set of common ancestors of v and w
        if (ancestorCommon.isEmpty()) {
            return  -1;
        }
        for (int i : ancestorCommon) {
            int current = ancestorsV.get(i) + ancestorsW.get(i); // sum of length to ancestor
            if (current < pathLength) {
                pathLength = current;
                ancestorShort = i;
            }
        }
        HashMap<Integer, Integer> lengthV = new HashMap<>(); // w, ancestral length form v to w
        HashMap<Integer, Integer> ancestorV = new HashMap<>(); // w, shortest ancestor of v and w
        lengthV.put(w, pathLength);
        stLength.put(v, lengthV);
        ancestorV.put(w, ancestorShort);
        stAncestor.put(v, ancestorV);

        return ancestorShort;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}