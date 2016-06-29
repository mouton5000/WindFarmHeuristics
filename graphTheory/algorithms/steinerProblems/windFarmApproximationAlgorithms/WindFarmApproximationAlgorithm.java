package graphTheory.algorithms.steinerProblems.windFarmApproximationAlgorithms;

import graphTheory.algorithms.Algorithm;
import graphTheory.graph.Arc;
import graphTheory.instances.steiner.windfarm.WindFarmInstance;

import java.util.HashMap;
import java.util.Set;

/**
 * This class merges some elements of all SteinerArborescence Approximation Algorithms:
 * an Steiner DIrected Instance as input and an arborescence and its cost as output.
 * 
 * @author Watel Dimitri
 *
 */
public abstract class WindFarmApproximationAlgorithm extends
		Algorithm<WindFarmInstance> {

	protected HashMap<Arc, Integer> arborescence;
	protected Double cost;

	public  HashMap<Arc, Integer> getArborescence() {
		return arborescence;
	}

	public Set<Arc> getArborescenceArcs() {
		return arborescence.keySet();
	}

	public Double getCost() {
		return cost;
	}



	@Override
	protected void setNoSolution() {
		arborescence = null;
		cost = -1D;
	}

}
