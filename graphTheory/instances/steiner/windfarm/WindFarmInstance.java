package graphTheory.instances.steiner.windfarm;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.utils.Collections2;
import graphTheory.utils.WeightedQuickUnionPathCompressionUF;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mouton on 01/03/16.
 */
public class WindFarmInstance extends SteinerDirectedInstance implements
        Cloneable {

    public WindFarmInstance(DirectedGraph g) {
        super(g);
        staticCapacityCosts = new HashMap<Integer, Double>();
        dynamicCapacityCosts = new HashMap<Integer, Double>();
        maximumOutputDegree = new HashMap<Integer, Integer>();
    }

    /**
     * For each type of static cable (determined by its capacity), cost of one meter of that cable
     */
    private HashMap<Integer, Double> staticCapacityCosts;

    /**
     * For each type of dynamic cable (determined by its capacity), cost of one meter of that cable
     */
    private HashMap<Integer, Double> dynamicCapacityCosts;

    private Double staticStaticBranchingNodeCost;

    /**
     * For each node, Maximum number of output arcs of that node in the solution
     */
    private HashMap<Integer, Integer> maximumOutputDegree;

    private Double dynamicStaticBranchingNodeCost;

    public Set<Integer> getStaticCapacities(){
        return staticCapacityCosts.keySet();
    }

    public Set<Integer> getDynamicCapacities(){
        return dynamicCapacityCosts.keySet();
    }

    /**
     * Maximum number of distinct types of cable a solution can use
     */
    private Integer maxNbSec;

    /**
     * Minimum distance of dynamic cable a terminal must use before connecting to other nodes.
     */
    private Double distanceMin;

    /**
     * @param a
     * @param capacity
     * @return the cost of a cable with that capacity following the arc a as if the cable was purely static
     */
    public Double getStaticCableCost(Arc a, Integer capacity){
        if(a == null)
            return null;
        return getStaticCableCost(getDoubleCost(a), capacity);
    }

    public Double getStaticCableCost(Double distance, Integer capacity){
        Double staticCapacityCost = getStaticCapacityCost(capacity);
        if(distance == null || staticCapacityCost == null)
            return null;
        return distance * staticCapacityCost;
    }

    /**
     * @param a
     * @param capacity
     * @return the cost of a cable with that capacity following the arc a; depending if a touches a terminal or two, and
     * consequently if the cable is purely static or partly dynamic and partly static.
     */
    public Double getRealCableCost(Arc a, Integer capacity){
        Double staticCapacityCost = getStaticCapacityCost(capacity);
        if(a == null || staticCapacityCost == null)
            return null;
        int nb = 0;
        if(this.isRequired(a.getInput()))
            nb++;
        if(this.isRequired(a.getOutput()))
            nb++;
        Double dynamicCapacityCost;
        if(nb == 0)
            dynamicCapacityCost = 0D;
        else {
            dynamicCapacityCost = getDynamicCapacityCost(capacity);
            if(dynamicCapacityCost == null) {
                Integer minCapa = null;
                for(Integer capa : getDynamicCapacities()){
                    if((minCapa == null ||capa < minCapa) && capacity <= capa)
                        minCapa = capa;
                }
                if(minCapa == null)
                    return null;
                dynamicCapacityCost = getDynamicCapacityCost(minCapa);
            }
        }
        return (getDoubleCost(a) - nb * this.getDistanceMin()) * staticCapacityCost
                + nb * this.getDistanceMin() * dynamicCapacityCost
                + nb * this.getDynamicStaticBranchingNodeCost();
    }

    /**
     * @param a
     * @param capacity
     * @return the cost of a dynamic cable with that capacity following the arc a
     */
    public Double getDynamicCableCost(Arc a, Integer capacity){
        Double capacityCost = getDynamicCapacityCost(capacity);
        if(capacityCost == null)
            return null;
        return getDoubleCost(a) * capacityCost;
    }

    /**
     * @param capacity
     * @return the cost of one meter of cable with this static capacity
     */
    public Double getStaticCapacityCost(Integer capacity) {
        return staticCapacityCosts.get(capacity);
    }

    /**
     * Set the static capacity cost.
     *
     * @param capacity
     * @param cost
     */
    public void setStaticCapacityCost(Integer capacity, Double cost) {
        staticCapacityCosts.put(capacity, cost); }


    /**
     * @param capacity
     * @return the cost of one meter of cable with this dynamic capacity
     */
    public Double getDynamicCapacityCost(Integer capacity) {
        return dynamicCapacityCosts.get(capacity);
    }


    /**
     * Set the dynamic capacity cost.
     *
     * @param capacity
     * @param cost
     */
    public void setDynamicCapacityCost(Integer capacity, Double cost) {
        dynamicCapacityCosts.put(capacity, cost); }

    /**
     * Reset the cost of all the static capacities, and associate the cost of each capacity to the
     * one defined in the map costs.
     *
     * @param staticCapacityCosts
     */
    public void setStaticCapacityCosts(HashMap<Integer, Double> staticCapacityCosts) {
        this.staticCapacityCosts = staticCapacityCosts;
    }

    /**
     * Reset the cost of all the dynamic capacities, and associate the cost of each capacity to the
     * one defined in the map costs.
     *
     * @param dynamicCapacityCosts
     */
    public void setDynamicCapacityCosts(HashMap<Integer, Double> dynamicCapacityCosts) {
        this.dynamicCapacityCosts = dynamicCapacityCosts;
    }

    public Integer getMaximumOutputDegree(Integer node){
        return maximumOutputDegree.get(node);
    }

    public void setMaximumOutputDegree(Integer node, Integer degree){
        maximumOutputDegree.put(node, degree);
    }

    public HashMap<Integer, Integer> getMaximumOutputDegree(){
        return new HashMap<Integer, Integer>(maximumOutputDegree);
    }

    public void setMaximumOutputDegree(HashMap<Integer, Integer> maximumOutputDegree) {
        this.maximumOutputDegree = maximumOutputDegree;
    }

    public Integer getMaxNbSec(){
        return maxNbSec;
    }

    public void setMaxNbSec(Integer maxNbSec) {
        this.maxNbSec = maxNbSec;
    }

    public Double getDistanceMin() {
        return distanceMin;
    }

    public void setDistanceMin(Double distanceMin) {
        this.distanceMin = distanceMin;
    }

    public Double getStaticStaticBranchingNodeCost() {
        return staticStaticBranchingNodeCost;
    }

    public void setStaticStaticBranchingNodeCost(Double staticStaticBranchingNodeCost) {
        this.staticStaticBranchingNodeCost = staticStaticBranchingNodeCost;
    }

    public Double getDynamicStaticBranchingNodeCost() {
        return dynamicStaticBranchingNodeCost;
    }

    public void setDynamicStaticBranchingNodeCost(Double dynamicStaticBranchingNodeCost) {
        this.dynamicStaticBranchingNodeCost = dynamicStaticBranchingNodeCost;
    }

    /**
     * Remove every cycle of the graph.
     * @param arborescenceFlow
     * @return
     */
    public HashMap<Arc, Integer> unviolateTreeConstraint(HashMap<Arc, Integer> arborescenceFlow){
        HashMap<Arc, Integer> noCycleFlow = new HashMap<Arc, Integer>(arborescenceFlow);
        DirectedGraph arbGraph = this.getGraph().getInducedGraphFromArc(noCycleFlow.keySet());
        outer : while(arbGraph.getNumberOfEdges() != arbGraph.getNumberOfVertices() - 1) {
            System.out.println("Cycle " + arbGraph.getNumberOfVertices() + " " + arbGraph.getNumberOfEdges());
            for(Integer node : arbGraph.getVertices()){
                if(arbGraph.getInputSize(node) >= 2) {
                    System.out.println(node+" "+arbGraph.getInputSize(node));

                    Iterator<Arc> it = arbGraph.getInputArcsIterator(node);
                    Arc a1 = it.next();
                    Arc a2 = it.next();
                    Integer v1, v2;

                    LinkedList<Arc> p1 = new LinkedList<Arc>();
                    LinkedList<Arc> p2 = new LinkedList<Arc>();

                    Integer flow, flow1 = noCycleFlow.get(a1), flow2 = noCycleFlow.get(a2);
                    System.out.println(flow1+" "+flow2);

                    if(flow1 <= flow2){
                        v1 = a1.getInput();
                        v2 = a2.getInput();
                        p1.add(a1);
                        p2.add(a2);
                        flow = flow1;
                    }
                    else{
                        v2 = a1.getInput();
                        v1 = a2.getInput();
                        p1.add(a2);
                        p2.add(a1);
                        flow = flow2;
                    }

                    Integer f1 = v1;
                    while(arbGraph.getOutputSize(f1) == 1){
                        a1 = arbGraph.getInputArcsIterator(f1).next();
                        p1.addFirst(a1);
                        f1 = a1.getInput();
                    }

                    Integer f2 = v2;
                    while(arbGraph.getOutputSize(f2) == 1){
                        a2 = arbGraph.getInputArcsIterator(f2).next();
                        p2.addFirst(a2);
                        f2 = a2.getOutput();
                    }

                    HashSet<Integer> h1 = new HashSet<Integer>();
                    h1.add(f1);
                    h1.add(node);
                    HashSet<Integer> h2 = new HashSet<Integer>();
                    h2.add(f2);
                    h1.add(node);

                    while(!h1.contains(f2) && !h2.contains(f1)){
                        if(arbGraph.getInputSize(f1) > 0){
                            a1 = arbGraph.getInputArcsIterator(f1).next();
                            p1.addFirst(a1);
                            f1 = a1.getInput();
                            while(arbGraph.getOutputSize(f1) == 1){
                                a1 = arbGraph.getInputArcsIterator(f1).next();
                                p1.addFirst(a1);
                                f1 = a1.getInput();
                            }
                            h1.add(f1);
                        }
                        if(arbGraph.getInputSize(f2) > 0){
                            a2 = arbGraph.getInputArcsIterator(f2).next();
                            p2.addFirst(a2);
                            f2 = a2.getOutput();
                            while(arbGraph.getOutputSize(f2) == 1){
                                a2 = arbGraph.getInputArcsIterator(f2).next();
                                p2.addFirst(a2);
                                f2 = a2.getOutput();
                            }
                            h2.add(f2);
                        }
                    }

                    Integer f = (h1.contains(f2))?f2:f1;
                    System.out.println(f+" "+h1+" "+ h2+" "+p1 +" "+p2);
                    while(!p1.isEmpty() && !p1.getFirst().getInput().equals(f))
                        p1.removeFirst();
                    while(!p2.isEmpty() && !p2.getFirst().getInput().equals(f))
                        p2.removeFirst();
                    System.out.println(p1+" "+p2);

                    for(Arc a : p1){
                        Integer flowa = noCycleFlow.get(a);
                        if(flowa.equals(flow)){
                            noCycleFlow.remove(a);
                            arbGraph.removeEdge(a);
                        }
                        else
                            noCycleFlow.put(a, flowa - flow);
                    }
                    continue outer;
                }
            }
        }
        return noCycleFlow;
    }


    /**
     * Given an arborescence of arc, each one with the minimum capacity a cable following this arc should be
     * associated with, this method returns a new map associating each arc with a new capacity such that
     * - no more than this.maxNbSec distinct capacities is chosen
     * - the cost of the arborescence is minimized.
     * @param arborescenceFlows
     * @return
     */
    public HashMap<Arc,Integer> unviolateMaxNbSecConstraint(HashMap<Arc, Integer> arborescenceFlows){
        if(arborescenceFlows == null)
            return null;
        if(arborescenceFlows.isEmpty())
            return new HashMap<Arc,Integer>();

        List<Arc> keys = new ArrayList<Arc>(arborescenceFlows.keySet());
        Collections.sort(keys, (arc, t1) -> -1 * arborescenceFlows.get(arc).compareTo(arborescenceFlows.get(t1)));

        int nbArcs = keys.size();
        Arc firstArc = keys.get(0);
        int maxFlow = arborescenceFlows.get(firstArc);

        Double[][][] next = new Double[nbArcs][maxFlow][maxNbSec];
        Integer[][][] nextCapa = new Integer[nbArcs][maxFlow][maxNbSec];

        next[0][maxFlow-1][0] = this.getRealCableCost(firstArc, maxFlow);
        nextCapa[0][maxFlow-1][0] = null;
        for(int k = 1; k< maxNbSec; k++) {
            next[0][maxFlow-1][k] = Double.POSITIVE_INFINITY;
            nextCapa[0][maxFlow-1][k] = null;
        }
        for(int j = maxFlow-2; j>=0; j--){
            for(int k = 0; k< maxNbSec; k++) {
                next[0][j][k] = Double.POSITIVE_INFINITY;
                nextCapa[0][j][k] = null;
            }
        }

        for(int i = 1; i<nbArcs; i++){
            Arc a = keys.get(i);
            Integer flow = arborescenceFlows.get(a);
            for(int j = maxFlow-1; j>=flow-1; j--){
                for(int k = 0; k < Math.min(i+1, maxNbSec); k++) {
                    Double minValue = Double.POSITIVE_INFINITY;
                    Integer minCapa = null;

                    Double cableCost = getRealCableCost(a, j+1);

                    if (k != 0) {
                        for(int jp = j+1; jp < maxFlow; jp++) {
                            Double candidate = cableCost + next[i-1][jp][k - 1];
                            if (candidate < minValue) {
                                minValue = candidate;
                                minCapa = jp;
                            }
                        }
                    }

                    Double candidate = cableCost + next[i-1][j][k];
                    if (candidate < minValue) {
                        minValue = candidate;
                        minCapa = j;
                    }
                    next[i][j][k] = minValue;
                    nextCapa[i][j][k] = minCapa;
                }
                for(int k = i+1; k < maxNbSec; k++){
                    next[i][j][k] = Double.POSITIVE_INFINITY;
                    nextCapa[i][j][k] = null;
                }
            }
            for(int j = flow-2; j>=0; j--){
                for(int k = 0; k< maxNbSec; k++) {
                    next[i][j][k] = Double.POSITIVE_INFINITY;
                    nextCapa[i][j][k] = null;
                }
            }
        }

        HashMap<Arc,Integer> arborescenceCapacity = new HashMap<Arc,Integer>();

        Double minCost = Double.POSITIVE_INFINITY;
        Integer minCapa = null, nextMinCapa = null;
        Integer minNbSec = null;
        for(int j = 0; j< maxFlow; j++){
            for(int k = 0; k<maxNbSec; k++){
                if(next[nbArcs-1][j][k] < minCost){
                    minCapa = j;
                    minNbSec = k;
                    minCost = next[nbArcs-1][j][k];
                }
            }
        }


        for(int i = nbArcs-1; i>=0; i--){
            Arc a = keys.get(i);
            arborescenceCapacity.put(a, minCapa+1);
            if(i == 0)
                break;
            nextMinCapa = nextCapa[i][minCapa][minNbSec];
            if(!nextMinCapa.equals(minCapa))
                minNbSec--;
            minCapa = nextMinCapa;
        }

        return arborescenceCapacity;

    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Noeuds\n\n");
        for (Integer n : graph.getVertices()) {
            s.append(n);
            if (isRequired(n))
                s.append(" ").append("x");
            s.append("\n");
        }

        s.append("\nArcs\n\n");
        for (Arc a : graph.getEdges())
            s.append(a).append(" ").append(getCost(a)).append("\n");

        s.append("\nCapacities\n\n");
        for (Integer capacity : staticCapacityCosts.keySet())
            s.append(capacity).append(" ").append(getStaticCapacityCost(capacity)).append("\n");

        return s.toString();
    }


    public HashSet<ResultError> testSolution(HashMap<Arc,Integer> arborescence){

        HashSet<ResultError> err = new HashSet<ResultError>();

        Iterator<Integer> it = this.getGraph().getVerticesIterator();
        WeightedQuickUnionPathCompressionUF unionFind = new WeightedQuickUnionPathCompressionUF(Collections2.max(it));
        // not an arborescence
        err.addAll(arborescence.keySet().stream().filter(a -> !unionFind.union(a.getInput() - 1, a.getOutput() - 1)).map(a -> ResultError.NOT_AN_ARBORESCENCE).collect(Collectors.toList()));

        HashMap<Integer, HashSet<Integer>> adj = new HashMap<Integer, HashSet<Integer>>();
        HashMap<Integer, Integer> prev = new HashMap<Integer, Integer>();
        for(Arc a : arborescence.keySet()){
            Integer u = a.getInput();
            Integer v = a.getOutput();
            if(v.equals(this.getRoot()))
                err.add(ResultError.ROOT_NOT_THE_ROOT); // The root is not the root
            HashSet<Integer> neigh = adj.get(u);
            if(neigh == null)
            {
                neigh = new HashSet<Integer>();
                adj.put(u,neigh);
            }
            neigh.add(v);

            prev.put(v, u);
        }


        LinkedList<Integer> toCheck = new LinkedList<Integer>();
        HashSet<Integer> visited = new HashSet<Integer>();
        toCheck.add(this.getRoot());

        LinkedList<Integer> toCheckReverse = new LinkedList<Integer>();


        while(!toCheck.isEmpty()){
            Integer u = toCheck.pollFirst();
            if(visited.contains(u))
                continue;
            visited.add(u);
            toCheckReverse.addFirst(u);

            HashSet<Integer> neigh = adj.get(u);
            if(neigh == null)
            {
                neigh = new HashSet<Integer>();
            }

            int deg = neigh.size();
            Integer max = getMaximumOutputDegree(u);
            if(max != null &&  deg > max)
                err.add(ResultError.DEGREE_VIOLATED); // Degree constraint violated

            toCheck.addAll(neigh);
        }

        if(!visited.containsAll(this.getRequiredVertices()))
            err.add(ResultError.TERMINAL_NOT_REACHED); // Some terminals are not reached.
        if(visited.size() != arborescence.size()+1)
            err.add(ResultError.DISCONNECTED_FOREST); // Disconnected forest instead of arborescence


        Integer nbCapacities = new HashSet<Integer>(arborescence.values()).size();

        if(nbCapacities > maxNbSec)
            err.add(ResultError.NBSEC_VIOLATED); // Too much capacities



        visited.clear();
        HashMap<Integer, Integer> flows = new HashMap<Integer, Integer>();

        while(!toCheckReverse.isEmpty()){
            Integer v = toCheckReverse.pollFirst();
            Integer u = prev.get(v);
            if(u == null)
                continue;

            Integer flowV = flows.get(v);
            if(flowV == null) {
                flowV = 0;
            }
            if(this.isRequired(v)) {
                flowV++;
            }
            flows.put(v, flowV);

            Integer flowU = flows.get(u);
            if(flowU == null) {
                flowU = 0;
            }
            flows.put(u, flowU + flowV);
        }


        for(Arc a : arborescence.keySet()) {
            Integer capa = arborescence.get(a);
            if(capa != null && flows.get(a.getOutput()) > capa)
                err.add(ResultError.CAPACITY_VIOLATED); // Capacity constraint violated
        }


        return err;

    }

    public enum ResultError{
        NOT_AN_ARBORESCENCE,
        ROOT_NOT_THE_ROOT,
        DEGREE_VIOLATED,
        TERMINAL_NOT_REACHED,
        DISCONNECTED_FOREST,
        NBSEC_VIOLATED,
        CAPACITY_VIOLATED
    }

}
