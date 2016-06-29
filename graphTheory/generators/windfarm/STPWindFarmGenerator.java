package graphTheory.generators.windfarm;

import graphTheory.generators.steinLib.STPGenerator;
import graphTheory.instances.steiner.windfarm.WindFarmInstance;
import graphTheory.steinLib.STPTranslationException;
import graphTheory.steinLib.STPTranslationWindFarmException;
import graphTheory.steinLib.STPWindFarmTranslator;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * This generator generated Windfarm Directed Steiner Instances from an STP file.
 * The format is adapted from the format described at http://steinlib.zib.de/
 * 
 * @author Watel Dimitri
 *
 */
public class STPWindFarmGenerator extends STPGenerator<WindFarmInstance> {

	/**
	 * Constructor
	 * @param instancesDirectoryName : a directory containing a list of stp files.
	 *                               Each file must describe a windfarm instance, and, then, must contain
	 *                               the magic number 33d32946
     */
	public STPWindFarmGenerator(String instancesDirectoryName) {
		super(instancesDirectoryName);
	}

	@Override
	public WindFarmInstance generate() {
		File f = instanceFiles[index];
		Pattern p = Pattern.compile("((\\w|-)+)\\.stp");
		Matcher m = p.matcher(f.getName());
		if (m.matches()) {
			String name = m.group(1);

            WindFarmInstance eol;
			try {
				eol = STPWindFarmTranslator.translateFile(f.getPath());
				incrIndex();
			} catch (STPTranslationException | STPTranslationWindFarmException e) {
				e.printStackTrace();
				incrIndex();
				return null;
			}

			eol.getGraph().defineParam(OUTPUT_NAME_PARAM_NAME, name);
            return eol;
        } else {
			return null;
		}
	}

}