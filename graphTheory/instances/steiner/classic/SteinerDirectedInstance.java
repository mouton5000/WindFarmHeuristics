package graphTheory.instances.steiner.classic;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * 
 * Instance for the Directed Steiner Tree problem : given a graph,
 * one node {@link #root} of that graph, some nodes called terminals
 * or required vertices,  and weight over the arcs, return the
 * minimum cost directed tree rooted in {@link #root} spanning all the terminals.
 * 
 * @author Watel Dimitri
 *
 */
public class SteinerDirectedInstance extends SteinerInstance implements
		Cloneable {

	public SteinerDirectedInstance(DirectedGraph g) {
		super(g);
	}

	Integer root;

	public Integer getRoot() {
		return root;
	}

	public void setRoot(Integer root) {
		if (root != null) {
			Integer r = getRoot();
			if (r != null)
				graph.setCircleSymbol(r);
			this.root = root;
			graph.setSquareSymbol(root);
		}
	}

	public DirectedGraph getGraph() {
		return (DirectedGraph) graph;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Noeuds\n\n");
		for (Integer n : getGraph().getVertices()) {
			s.append(n);
			if (isRequired(n))
				s.append(" ").append("x");
			if (getRoot().equals(n))
				s.append(" ").append("o");
			s.append("\n");
		}

		s.append("\nArcs\n\n");
		for (Arc a : getGraph().getEdges()) {
			s.append(a).append(" ").append(getIntCost(a)).append("\n");
		}

		return s.toString();
	}

	@Override
	public boolean hasSolution() {
		ListIterator<Integer> it = this.getRequiredVerticesIterator();
		while (it.hasNext()) {
			if (!graph.areConnectedByDirectedPath(root, it.next()))
				return false;
		}
		return true;
	}
	
	public boolean isFeasibleSolution(HashSet<Arc> tree){
		
		HashMap<Integer, HashSet<Integer>> adj = new HashMap<Integer, HashSet<Integer>>();
		for(Arc a : tree){
			Integer u = a.getInput();
			Integer v = a.getOutput();
			HashSet<Integer> neigh = adj.get(u);
			if(neigh == null)
			{
				neigh = new HashSet<Integer>();
				adj.put(u,neigh);
			}
			neigh.add(v);
		}
		
		LinkedList<Integer> toCheck = new LinkedList<Integer>();
		HashSet<Integer> visited = new HashSet<Integer>();
		toCheck.add(this.getRoot());
		
		while(!toCheck.isEmpty()){
			Integer u = toCheck.pollFirst();
			if(visited.contains(u))
				continue;
			visited.add(u);
			
			HashSet<Integer> neigh = adj.get(u);
			if(neigh == null)
			{
				neigh = new HashSet<Integer>();
			}
			toCheck.addAll(neigh);
		}
		return visited.containsAll(this.getRequiredVertices());
		
	}

}
