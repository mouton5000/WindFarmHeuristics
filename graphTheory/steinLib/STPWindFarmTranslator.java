package graphTheory.steinLib;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.instances.steiner.windfarm.WindFarmInstance;
import graphTheory.utils.FileManager;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * This class contains static method to translate .stp files into a
 * {@link WindFarmInstance}
 * 
 * @author Watel Dimitri
 * 
 */
public class STPWindFarmTranslator {

	/**
	 * @param fileName
	 *            path of the file describing the instance that will be built by this method.
	 * @return a {@link WindFarmInstance} corresponding the the .stp input file or null if the file
     * is mistaken.
	 * @throws STPTranslationException if the file is mistaken
     * @throws  STPTranslationWindFarmException if the file is mistaken
	 */
	public static WindFarmInstance translateFile(String fileName)
			throws STPTranslationException, STPTranslationWindFarmException {
		FileManager f = new FileManager();
		f.openRead(fileName);
		String s;
		int lineNumber = 0;
		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.EMPTY_FILE, fileName, lineNumber,
					null);
		}

		s = s.toLowerCase();

		// Check if the format of the file is correct by reading the magic number.
		if (!s.contains("33d32946")) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.BAD_FORMAT_CODE, fileName,
					lineNumber, s);
		}

		// Skip comments
		while (!s.contains("section graph")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.NO_SECTION_GRAPH, fileName,
						lineNumber, s);
			}
			s = s.toLowerCase();
		}


        // Check if the section is empty
		s = f.readLine();
		lineNumber++;

		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.EMPTY_SECTION_GRAPH, fileName,
					lineNumber, s);
		}

        // Get the number of nodes
		s = s.toLowerCase();
		s = s.trim();
		Pattern p = Pattern.compile("nodes +(\\d+)");
		Matcher m = p.matcher(s);
		int nov;
		if (!m.matches())
			throw new STPTranslationException(
					STPTranslationExceptionEnum.NODE_NUMBER_BAD_FORMAT, fileName,
					lineNumber, s);
		nov = Integer.valueOf(m.group(1));

		// Check if the graph section contains a number of arcs
		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.NO_SECTION_GRAPH_CONTENT, fileName,
					lineNumber, s);
		}

        // Get the number of arcs
		s = s.toLowerCase();
		s = s.trim();
		p = Pattern.compile("(edges|arcs) +(\\d+)");
		m = p.matcher(s);
		boolean isDirected;
		int noe;
		char letter;
		WindFarmInstance g;
		if (m.matches()) {
			isDirected = m.group(1).equals("arcs");
			if (isDirected) {
				DirectedGraph dg = new DirectedGraph();
				g = new WindFarmInstance(dg);
				letter = 'a';
			} else {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.EDGE_NUMBER_BAD_FORMAT, fileName,
						lineNumber, s);
			}
			noe = Integer.valueOf(m.group(2));
		} else {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.EDGE_NUMBER_BAD_FORMAT, fileName,
					lineNumber, s);
		}

        // Check if the graph section is empty

		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.NO_SECTION_GRAPH_CONTENT,
					fileName, lineNumber, s);
		}
		s = s.toLowerCase();
		s = s.trim();
		while (s.equals("")) {
			s = f.readLine();
			lineNumber++;

			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.NO_SECTION_GRAPH_CONTENT,
						fileName, lineNumber, s);
			}
			s = s.toLowerCase();
			s = s.trim();
		}


		p = Pattern.compile(letter + " +(\\d+) +(\\d+) +(\\d+)(\\.(\\d+))?");
		Double cost;
		Integer n1, n2;

		while (!s.equals("end")) {
            // For each line in the graph section, define a new arc
			m = p.matcher(s);
			if (m.matches()) {
				n1 = Integer.valueOf(m.group(1));
				n2 = Integer.valueOf(m.group(2));

                if(m.group(4) == null){
					cost = Double.valueOf(m.group(3));
				}
				else{
					cost = Double.valueOf(m.group(3)+m.group(4));
				}

				if (!g.getGraph().contains(n1)) {
					g.getGraph().addVertice(n1);
					nov--;
				}
				if (!g.getGraph().contains(n2)) {
					g.getGraph().addVertice(n2);
					nov--;
				}

				Arc a;
				if (isDirected)
					a = g.getGraph().addDirectedEdge(n1, n2);
				else
					a = g.getGraph().addUndirectedEdge(n1, n2);
				g.setCost(a, cost);
			} else {
				throw new STPTranslationWindFarmException(
						STPTranslationWindFarmExceptionEnum.EDGE_DESCRIPTION_BAD_FORMAT,
						fileName, lineNumber, s);
			}

			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.FILE_ENDED_BEFORE_EOF_SG,
						fileName, lineNumber, s);
			}
			s = s.toLowerCase();
			s = s.trim();
			noe--;
		}
        // Check if the number of arcs in the graph and the number of arcs written at the beginning of the section
        // is the same
		if (noe != 0) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.INCOHERENT_NB_EDGES, fileName,
					lineNumber, s);
		}

		// Jump to the terminals section
		while (!s.contains("section terminals")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.NO_SECTION_TERM, fileName,
						lineNumber, s);
			}
			s = s.toLowerCase();
		}

        // Check if the section is empty
		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.EMPTY_SECTION_TERM, fileName,
					lineNumber, s);
		}
		s = s.toLowerCase();
		p = Pattern.compile("terminals +(\\d+)");
		m = p.matcher(s);
		int not;
		int size = g.getGraph().getNumberOfVertices();

        // Get the number of terminals
		if (m.matches()) {
			not = Integer.valueOf(m.group(1));
			if (not > size || not <= 0) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.STRANGE_NB_TERM, fileName,
						lineNumber, s);
			}
		} else {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.TERMINALS_NUMBER_BAD_FORMAT,
					fileName, lineNumber, s);
		}


		boolean rootSet = false;
		s = f.readLine();
		lineNumber++;

        // Check if the section is empty
        if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.NO_SECTION_TERM_CONTENT,
					fileName, lineNumber, s);
		}

        // For each line, add a terminal to the instance or define the root (depending on the line)
		s = s.toLowerCase();
		s = s.trim();
		p = Pattern.compile("(t +(\\d+))|(root +(\\d+))");
		while (!s.equals("end")) {
			m = p.matcher(s);
			if (m.matches()) {
				if (m.group(3) != null) {
                    // Check if the root is defined twice
					if (rootSet || !isDirected) {
						throw new STPTranslationException(
								STPTranslationExceptionEnum.TOO_MUCH_ROOT_SET,
								fileName, lineNumber, s);
					} else {
						rootSet = true;
						((SteinerDirectedInstance) g).setRoot(Integer.valueOf(m
								.group(4)));
					}
				} else {
					n1 = Integer.valueOf(m.group(2));
					g.setRequired(n1, true);
					g.setMaximumOutputDegree(n1, 1);
					not--;
				}
			} else {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.TERMINALS_DESC_BAD_FORMAT,
						fileName, lineNumber, s);
			}
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.FILE_ENDED_BEFORE_EOF_ST,
						fileName, lineNumber, s);
			}
			s = s.toLowerCase();
			s = s.trim();
		}

        // Check if the number of terminals in the instance and the number of terminals written at the
        // beginning of the terminals section is the same
		if (not != 0) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.INCOHERENT_NB_TERMS, fileName,
					lineNumber, s);
		}

		// Jump to the section parameters
		while (!s.contains("section parameters")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationWindFarmException(
						STPTranslationWindFarmExceptionEnum.NO_SECTION_PARAMETERS, fileName,
						lineNumber, s);
			}
			s = s.toLowerCase();
		}


        // Check if the section is empty
		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationWindFarmException(
					STPTranslationWindFarmExceptionEnum.NO_SECTION_PARAMETERS_CONTENT,
					fileName, lineNumber, s);
		}
		s = s.toLowerCase();
		s = s.trim();


		HashSet<String> keywords = new HashSet<String>();
        keywords.add("degss");
        keywords.add("nbsec");
        keywords.add("dmin");
        keywords.add("jonction stst");
        keywords.add("jonction stdyn");
        p = Pattern.compile("(degss|nbsec|dmin|jonction stst|jonction stdyn) +(\\d+)(\\.(\\d+))?");
		while (!s.equals("end")) {
            // For each line, define the parameter corresponding to the line
			m = p.matcher(s);
			if (m.matches()) {
				String parameter = m.group(1);
                // Check if the parameter is not defined twice
                if(!keywords.remove(parameter))
                    throw new STPTranslationWindFarmException(
                            STPTranslationWindFarmExceptionEnum.PARAMETERS_KEYWORD_USED_TWICE,
                            fileName, lineNumber, s);
				if(parameter.equals("degss") && m.group(3) == null){
					Integer deg = Integer.valueOf(m.group(2));
					g.setMaximumOutputDegree(g.getRoot(), deg);
				}
				else if(parameter.equals("nbsec") && m.group(3)==null){
					Integer nbSec = Integer.valueOf(m.group(2));
					g.setMaxNbSec(nbSec);
				}
				else if(parameter.equals("dmin")){
					Double dmin;
					if(m.group(3) == null){
						dmin = Double.valueOf(m.group(2));
					}
					else{
						dmin = Double.valueOf(m.group(2)+m.group(3));
					}
					g.setDistanceMin(dmin);
				}
				else if(parameter.equals("jonction stst")){
					Double jstst;
					if(m.group(3) == null){
						jstst = Double.valueOf(m.group(2));
					}
					else{
						jstst = Double.valueOf(m.group(2)+m.group(3));
					}
					g.setStaticStaticBranchingNodeCost(jstst);
				}
				else if(parameter.equals("jonction stdyn")){
					Double jstdyn;
					if(m.group(3) == null){
						jstdyn = Double.valueOf(m.group(2));
					}
					else{
						jstdyn = Double.valueOf(m.group(2)+m.group(3));
					}
					g.setDynamicStaticBranchingNodeCost(jstdyn);
				}
				else {
					throw new STPTranslationWindFarmException(
							STPTranslationWindFarmExceptionEnum.PARAMETERS_DESC_BAD_FORMAT,
							fileName, lineNumber, s);
				}
			} else {
				throw new STPTranslationWindFarmException(
						STPTranslationWindFarmExceptionEnum.PARAMETERS_DESC_BAD_FORMAT,
						fileName, lineNumber, s);
			}
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationWindFarmException(
						STPTranslationWindFarmExceptionEnum.FILE_ENDED_BEFORE_EOF_SPAR,
						fileName, lineNumber, s);
			}
			s = s.toLowerCase();
			s = s.trim();
		}
        // Check if every pararmeter is defined
        if(!keywords.isEmpty()) {
            String keyword = keywords.iterator().next();
            switch (keyword) {
                case "degss":
                    throw new STPTranslationWindFarmException(
                            STPTranslationWindFarmExceptionEnum.PARAMETERS_KEYWORD_MISSING_DEGSS,
                            fileName, lineNumber, s);
                case "nbsec":
                    throw new STPTranslationWindFarmException(
                            STPTranslationWindFarmExceptionEnum.PARAMETERS_KEYWORD_MISSING_NBSEC,
                            fileName, lineNumber, s);
                case "dmin":
                    throw new STPTranslationWindFarmException(
                            STPTranslationWindFarmExceptionEnum.PARAMETERS_KEYWORD_MISSING_DMIN,
                            fileName, lineNumber, s);
                case "jonction stst":
                    throw new STPTranslationWindFarmException(
                            STPTranslationWindFarmExceptionEnum.PARAMETERS_KEYWORD_MISSING_JONCTIONSTST,
                            fileName, lineNumber, s);
                case "jonction stdyn":
                    throw new STPTranslationWindFarmException(
                            STPTranslationWindFarmExceptionEnum.PARAMETERS_KEYWORD_MISSING_JONCTIONSTDYN,
                            fileName, lineNumber, s);
            }
        }

		// Jump to the capacities section
		while (!s.contains("section capacities")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationWindFarmException(
						STPTranslationWindFarmExceptionEnum.NO_SECTION_CAPACITIES, fileName,
						lineNumber, s);
			}
			s = s.toLowerCase();
		}


        // Check if the section is empty
		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationWindFarmException(
					STPTranslationWindFarmExceptionEnum.NO_SECTION_CAPACITIES_CONTENT,
					fileName, lineNumber, s);
		}
		s = s.toLowerCase();
		s = s.trim();
		p = Pattern.compile("(st|dy) +(\\d+) +(\\d+)(\\.(\\d+))?");
		while (!s.equals("end")) {
			m = p.matcher(s);
			if (m.matches()) {
                // For each line, define a static or a dynamic capacity cost
				Integer capacity = Integer.valueOf(m.group(2));
				Double capacost;
				if(m.group(4) == null){
					capacost = Double.valueOf(m.group(3));
				}
				else{
					capacost = Double.valueOf(m.group(3)+m.group(4));
				}
				if(m.group(1).equals("st")){
					g.setStaticCapacityCost(capacity, capacost);
				}
				else{
					g.setDynamicCapacityCost(capacity, capacost);
				}
			} else {
				throw new STPTranslationWindFarmException(
						STPTranslationWindFarmExceptionEnum.CAPACITIES_DESC_BAD_FORMAT,
						fileName, lineNumber, s);
			}
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationWindFarmException(
						STPTranslationWindFarmExceptionEnum.FILE_ENDED_BEFORE_EOF_SCAP,
						fileName, lineNumber, s);
			}
			s = s.toLowerCase();
			s = s.trim();
		}



		// Jump to the end
		while (!s.contains("eof")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.FILE_ENDED_BEFORE_EOF,
						fileName, lineNumber, s);
			}
			s = s.toLowerCase();
		}


		return g;
	}
}