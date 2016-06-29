package graphTheory.algorithms.steinerProblems.windFarmApproximationAlgorithms;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.instances.steiner.windfarm.WindFarmInstance;
import graphTheory.utils.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by mouton on 29/06/16.
 */
public class PaquetAlgorithm extends WindFarmApproximationAlgorithm {

    private ArrayList<String> paquetFiles;


    public PaquetAlgorithm() {
        this.paquetFiles = new ArrayList<String>();
    }

    public void addPaquet(String file){
        paquetFiles.add(file);
    }

    public void removePaquet(String file){
        paquetFiles.remove(file);
    }

    public void clearPaquet(){
        paquetFiles.clear();
    }

    @Override
    protected void computeWithoutTime() {
        HashMap<Arc,Integer> tree = new HashMap<Arc,Integer>();

        FileManager fm = new FileManager();
        for(String file : paquetFiles){
            fm.openRead(file);

            HashSet<Integer> nodes = new HashSet<Integer>();

            String line;
            while((line = fm.readLine()) != null) {
                String[] ints = line.split("\\s+");
                Integer i1 = Integer.valueOf(ints[0]);
                Integer i2 = Integer.valueOf(ints[1]);
                nodes.add(i1);
                nodes.add(i2);
            }
            fm.closeRead();

            nodes.add(this.getInstance().getRoot());

            DirectedGraph dg = this.getInstance().getGraph();
            DirectedGraph pg = dg.getInducedGraphFromNodes(nodes);

            WindFarmInstance eol = new WindFarmInstance(pg);

            for(Integer node : nodes){
                if(this.getInstance().isRequired(node))
                    eol.setRequired(node);
            }
            eol.setRoot(this.getInstance().getRoot());

            for(Arc arc : pg.getEdges()){
                eol.setCost(arc, this.getInstance().getDoubleCost(arc));
            }

            for(Integer capa : this.getInstance().getStaticCapacities()){
                eol.setStaticCapacityCost(capa, this.getInstance().getStaticCapacityCost(capa));
            }

            for(Integer capa : this.getInstance().getDynamicCapacities()){
                eol.setDynamicCapacityCost(capa, this.getInstance().getDynamicCapacityCost(capa));
            }

            for(Integer node : nodes)
                eol.setMaximumOutputDegree(node, this.getInstance().getMaximumOutputDegree(node));
            eol.setMaximumOutputDegree(this.getInstance().getRoot(), 1);


            for(Integer node : nodes) {
                eol.getGraph().setNodeAbscissa(node, this.getInstance().getGraph().getNodeAbscissa(node));
                eol.getGraph().setNodeOrdinate(node, this.getInstance().getGraph().getNodeOrdinate(node));
            }

            eol.setMaxNbSec(this.getInstance().getStaticCapacities().size());
            eol.setDistanceMin(this.getInstance().getDistanceMin());
            eol.setStaticStaticBranchingNodeCost(this.getInstance().getStaticStaticBranchingNodeCost());
            eol.setDynamicStaticBranchingNodeCost(this.getInstance().getDynamicStaticBranchingNodeCost());

            GFLAC2WindFarmAlgorithm3 gf = new GFLAC2WindFarmAlgorithm3();
            gf.setInstance(eol);
            gf.compute();

            HashMap<Arc, Integer> h1 = gf.getArborescence();
            Double c1 = gf.getCost();

            WindFarmInstance peol2 = eol.simplifyWithAngles(Math.PI / 12);

            gf.setInstance(peol2);
            gf.compute();
            HashMap<Arc, Integer> h2 = gf.getArborescence();
            Double c2 = gf.getCost();
            HashMap<Arc, Integer> best = (c1 != -1 && (c2 == -1 || c1 < c2))?h1:h2;

            if(best == null){
                this.setNoSolution();
                return;
            }

            tree.putAll(best);
        }

        tree = this.getInstance().unviolateMaxNbSecConstraint(tree);
        Double c = 0D;
        for(Map.Entry<Arc,Integer> entry : tree.entrySet()){
            c += this.getInstance().getRealCableCost(entry.getKey(), entry.getValue());
        }

        arborescence = tree;
        cost = c;


    }
}
