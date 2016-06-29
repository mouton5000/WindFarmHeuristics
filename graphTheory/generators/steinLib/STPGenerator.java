package graphTheory.generators.steinLib;

import graphTheory.generators.InstanceGenerator;
import graphTheory.instances.steiner.classic.SteinerInstance;

import java.io.File;
import java.util.Arrays;

/**
 *
 * This generator creates SteinerInstances from STP files.
 * The STP format is described at http://steinlib.zib.de/
 *
 * This is an abstract class and should be adapted to every
 * stp format (Directed instances, Undirected instances, Windfarm instances, ...).
 * 
 * @author Watel Dimitri
 *
 * @param <T>
 */
public abstract class STPGenerator<T extends SteinerInstance> extends
		InstanceGenerator<T> {

	protected String instancesDirectoryName;
	protected int index;
    protected File[] instanceFiles;
	/**
	 * Parameter name to which the name of the instance is associated
	 */
	public static final String OUTPUT_NAME_PARAM_NAME = "STPGenerator_outputNameParamName";

	public STPGenerator() {
		this(null);
	}

	/**
	 * 
	 * @param instancesDirectoryName
	 *            : a directory containing stp files, the generator generates one instance
	 *            for each stp file in the directory
	 */
	public STPGenerator(String instancesDirectoryName) {
		super();
        this.setInstancesDirectoryName(instancesDirectoryName);
	}

    /**
     * @return the path of the next generated instance.
     */
	public String getInstancesPath() {
		return instancesDirectoryName + "/" + instanceFiles[index].getName();
	}

    /**
     * Define a new directory containing stp files.
     * Reset the index to 0.
     * @param iDN : a directory containing stp files, the generator generates one instance
     *            for each stp file in the directory
     */
	public void setInstancesDirectoryName(String iDN) {
        index = 0;
		instancesDirectoryName = iDN;
		instanceFiles = new File(instancesDirectoryName).listFiles();
		Arrays.sort(instanceFiles, (o1, o2) -> o1.getName().compareTo(o2.getName()));
	}

	/**
	 * 
	 * @return the number of instances the current directory contains
	 */
	public int getNumberOfInstances() {
		return instanceFiles.length;
	}

	/**
	 * Tell the generator to increase by 1 the index pointing at the 
	 * next generated instance.
	 */
	public void incrIndex() {
		incrIndex(1);
	}

	/**
	 * Tell the generator to increase by value the index pointing at the 
	 * next generated instance.
	 * 
	 * @param value
	 */
	public void incrIndex(int value) {
		index += value;
		index %= instanceFiles.length;
	}
}
