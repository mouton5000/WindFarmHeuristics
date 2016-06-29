package graphTheory.graph;

// Referenced classes of package graphTheory.graph:
// Parametable, Graph, Node

/**
 * This class is a model for an edge in an undirected graphTheory.graph or
 * an edge in a directed graphTheory.graph
 * 
 * @author Watel Dimitri
 */
public class Arc implements Cloneable {

	/**
	 * The input of the arc (or an extremity if the arc is undirected)
	 */
	private Integer input;

	/**
	 * The output of the arc (or an extremity if the arc is undirected)
	 */
	private Integer output;

	/**
	 * Determine if the arc is an undirected arc (an edge) or a directed arc.
	 */
	private boolean isDirected;

	/**
	 * Create a new arc and define its input, output and if the arc is directed or undirected
	 * @param input : the input node of the arc (or an extremity if the arc is undirected)
	 * @param output : the output node of the arc (or an extremity if the arc is undirected)
	 * @param isDirected : true if and only if the arc is a directed arc
     */
	public Arc(Integer input, Integer output, boolean isDirected) {
		this.input = input;
		this.output = output;
		this.isDirected = isDirected;
	}

	/**
	 * @return the input node of the arc (or an extremity if the arc is undirected)
     */
	public Integer getInput() {
		return input;
	}

	/**
	 * @return the output node of the arc (or an extremity if the arc is undirected)
     */
	public Integer getOutput() {
		return output;
	}

	/**
	 * @return true if and only if the arc is a directed arc
     */
	public boolean isDirected() {
		return isDirected;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof Arc) {
			Arc a = (Arc) o;

			return isDirected == a.isDirected
					&& ((((input == null && a.input == null) || (input != null && input
							.equals(a.input))) && ((output == null && a.output == null) || (output != null && output
							.equals(a.output)))) || (!isDirected && ((input == null && a.output == null) || (input != null && input
							.equals(a.output))))
							&& ((output == null && a.input == null) || (output != null && (output
									.equals(a.input)))));

		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return (new StringBuilder()).append(input).append(" ---")
				.append(isDirected ? ">" : "-").append(" ").append(output)
				.toString();
	}

	@Override
	public int hashCode() {

		int i1 = input;
		int i2 = output;
		if (isDirected)
			return i1 ^ (i2 * 31);
		else
			return i1 ^ i2;
	}

	@Override
	public Object clone() {
		Arc a;
		try {
			a = (Arc) super.clone();
			a.input = input;
			a.output = output;
			return a;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
