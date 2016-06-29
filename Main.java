import graphTheory.algorithms.steinerProblems.windFarmApproximationAlgorithms.GFLAC2WindFarmAlgorithm3;
import graphTheory.algorithms.steinerProblems.windFarmApproximationAlgorithms.WindFarmApproximationAlgorithm;
import graphTheory.generators.steinLib.STPGenerator;
import graphTheory.generators.windfarm.STPWindFarmGenerator;
import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.graphDrawer.EnergyAnalogyGraphDrawer;
import graphTheory.instances.steiner.windfarm.WindFarmInstance;
import graphTheory.steinLib.STPTranslationException;
import graphTheory.steinLib.STPTranslationWindFarmException;
import graphTheory.steinLib.STPWindFarmTranslator;
import graphTheory.utils.FileManager;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.*;

/**
 *
 * @author Watel Dimitri
 *
 */
public class Main {


    public static void main(String[] args) {


//        testWindFarmGenerator();
//        testWindFarmNewFormat();
        testWindFarmPaquet();

    }


    public static void testWindFarmGenerator(){

        WindFarmApproximationAlgorithm gf = new GFLAC2WindFarmAlgorithm3();

        FileManager fm = new FileManager();

        HashMap<String, HashMap<Integer, String>> results = new HashMap<String, HashMap<Integer, String>>();

        int degMin = 10;
        int degMax = 10;
        String dir = "small";

        for(int deg = degMin; deg <= degMax; deg++) {
            String degStr = StringUtils.leftPad(String.valueOf(deg), 2, '0');
            STPWindFarmGenerator gen = new STPWindFarmGenerator("SteinLibWindFarm/" + dir + "/deg"+degStr);
            int skip = 0;
            gen.incrIndex(skip);


            for (int i = skip; i < gen.getNumberOfInstances(); i++) {

                WindFarmInstance eol = gen.generate();
                if(eol == null)
                    continue;
                gf.setInstance(eol);
                String name = (String) eol.getGraph().getParam(STPGenerator.OUTPUT_NAME_PARAM_NAME);

                HashMap<Integer, String> resultsOfEol = results.get(name);
                if (resultsOfEol == null) {
                    resultsOfEol = new HashMap<Integer, String>();
                    StringBuilder value = new StringBuilder();
                    value.append(StringUtils.rightPad(""+i, 3));
                    value.append(StringUtils.rightPad(name, 10));
                    value.append(StringUtils.rightPad("" + eol.getGraph().getNumberOfVertices(), 7));
                    value.append(StringUtils.rightPad("" + eol.getGraph().getNumberOfEdges(), 7));
                    value.append(StringUtils.rightPad("" + eol.getNumberOfRequiredVertices(), 7));
                    resultsOfEol.put(-1, value.toString());
                    results.put(name, resultsOfEol);
                }
                StringBuilder value = new StringBuilder();
                value.append(" /// ");
                value.append(StringUtils.rightPad(degStr,4));

                gf.compute();
                if(gf.getCost() > 0){
                    HashSet<WindFarmInstance.ResultError> resErr = eol.testSolution(gf.getArborescence());
                    if(!resErr.isEmpty()) {
                        value.append(StringUtils.rightPad(" " + gf.getTime(),5));
                        value.append(StringUtils.rightPad(" " + gf.getCost().intValue()+ " "+ resErr,7));
                    }
                    else {
                        value.append(StringUtils.rightPad("" + gf.getTime(), 5));
                        value.append(StringUtils.rightPad("" + gf.getCost().intValue(), 7));
                    }
                } else {
                    value.append(StringUtils.rightPad(" ERROR",12));
                }
                resultsOfEol.put(deg, value.toString());


                fm.openErase("./Result/FormatComplexe/Small/"+name+".sol");
                int j = 1;
                for(Arc a : gf.getArborescenceArcs()){
                    Integer capa = gf.getArborescence().get(a);
                    fm.writeln(j+"/"+a.getInput()+"+"+a.getOutput()+"/->"+capa);
                    j++;
                }
                fm.closeWrite();
            }
        }

        System.out.println("ID NAME    n      m      k       /// DEG TIME COST");

        ArrayList<String> names = new ArrayList<String>(results.keySet());
        Collections.sort(names);
        for(String name : names) {
            HashMap<Integer, String> resultsOfEol = results.get(name);
            ArrayList<Integer> degs = new ArrayList<Integer>(resultsOfEol.keySet());
            Collections.sort(degs);
            for (Integer deg : degs) {
                if(deg.equals(-1))
                    continue;
                System.out.println(resultsOfEol.get(-1) + resultsOfEol.get(deg));
            }
        }


    }

    public static void testWindFarmNewFormat(){

        try {
            WindFarmInstance eol = STPWindFarmTranslator.translateFile("SteinLibWindFarm/small/deg10/T10S10.stp");


            FileManager fm1 = new FileManager();
            fm1.openErase("./Result/FormatComplexe/T100S02");

            GFLAC2WindFarmAlgorithm3 gf = new GFLAC2WindFarmAlgorithm3();
            gf.setInstance(eol);
            gf.compute();

            System.out.println(gf.getTime()+" " +gf.getCost()+" "+gf.getArborescence()+" "+eol.testSolution(gf.getArborescence()));

//            int j = 1;
//            for(Arc a : gf.getArborescenceArcs()){
//                Integer capa = gf.getArborescence().get(a);
//                fm1.writeln(j+"/"+a.getInput()+"+"+a.getOutput()+"/->"+capa);
//                j++;
//            }
//
//            fm1.closeWrite();


            for(Integer node : eol.getGraph().getVertices())
                eol.getGraph().setDrawn(node, false);

            for(Arc a : eol.getGraph().getEdges()) {
                if(gf.getArborescence().containsKey(a)) {
                    eol.getGraph().setColor(a, Color.red);
                    eol.getGraph().setDrawn(a.getInput(),true);
                    eol.getGraph().setDrawn(a.getOutput(),true);
                }
                else {
                    eol.getGraph().setDrawn(a, false);
                }
            }

            new EnergyAnalogyGraphDrawer(eol.getGraph(), gf.getArborescence());

        } catch (STPTranslationException | STPTranslationWindFarmException e) {
            e.printStackTrace();
        }



    }

    public static void testSmallWindFarmGenerator(){
        GFLAC2WindFarmAlgorithm3 gf = new GFLAC2WindFarmAlgorithm3();

//        FileManager fm = new FileManager();

        STPWindFarmGenerator gen = new STPWindFarmGenerator("SteinLibWindFarm/small");
        int skip = 0;
        gen.incrIndex(skip);

        String title = StringUtils.rightPad("ID",3)
                + StringUtils.rightPad("NAME",8)
                + StringUtils.rightPad("n",7)
                + StringUtils.rightPad("m",7)
                + StringUtils.rightPad("k",7)
                + StringUtils.rightPad("DEGSS",6)
                + StringUtils.rightPad("NBSEC",6)
                + StringUtils.rightPad("DMIN",6)
                + StringUtils.rightPad("JSTST",6)
                + StringUtils.rightPad("JSTDY",6)
                + StringUtils.rightPad("TIME",5)
                + StringUtils.rightPad("COST",7);

        System.out.println(title);

        FileManager fm1 = new FileManager();
        FileManager fm2 = new FileManager();

        for (int i = skip; i < gen.getNumberOfInstances(); i++) {

            WindFarmInstance eol = gen.generate();
            gf.setInstance(eol);
            String name = (String) eol.getGraph().getParam(STPGenerator.OUTPUT_NAME_PARAM_NAME);

            fm1.openErase("./Result/FormatComplexe/"+name);
            fm2.openErase("./Result/FormatSimple/"+name);

            String value = StringUtils.rightPad(""+i,3)
                    + StringUtils.rightPad(""+name,8)
                    + StringUtils.rightPad(""+eol.getGraph().getNumberOfVertices(),7)
                    + StringUtils.rightPad(""+eol.getGraph().getNumberOfEdges(),7)
                    + StringUtils.rightPad(""+eol.getNumberOfRequiredVertices(),7)
                    + StringUtils.rightPad(""+eol.getMaximumOutputDegree().get(eol.getRoot()),6)
                    + StringUtils.rightPad(""+eol.getMaxNbSec(),6)
                    + StringUtils.rightPad(""+eol.getDistanceMin(),6)
                    + StringUtils.rightPad(""+eol.getStaticStaticBranchingNodeCost(),6)
                    + StringUtils.rightPad(""+eol.getDynamicStaticBranchingNodeCost(),6);
            gf.compute();
            if(gf.getCost() > 0){
                HashSet<WindFarmInstance.ResultError> resErr = eol.testSolution(gf.getArborescence());
                if(!resErr.isEmpty()) {
                    value += StringUtils.rightPad(" " + gf.getTime(),5)
                            + StringUtils.rightPad(" " + gf.getCost().intValue()+ " "+ resErr,7);
                }
                else
                    value += StringUtils.rightPad(" " + gf.getTime(),5)
                            + StringUtils.rightPad(" " + gf.getCost().intValue(),7);
                int j = 1;
                for(Arc a : gf.getArborescenceArcs()){
                    Integer capa = gf.getArborescence().get(a);
                    fm1.writeln(j+"/"+a.getInput()+"+"+a.getOutput()+"/->"+capa);
                    fm2.writeln(a.getInput()+" "+a.getOutput());
                    j++;
                }
            } else {
                value += StringUtils.rightPad(" ERROR",12);
            }

            System.out.println(value);
            fm1.closeWrite();
            fm2.closeWrite();
        }



    }

    public static void testWindFarmPaquet(){
        String folder = "SteinLibWindFarm/paquets/Paquets1/";

        WindFarmInstance eol = null;
        try {
            eol = STPWindFarmTranslator.translateFile(folder + "instance_entier.stp");
        } catch (STPTranslationException | STPTranslationWindFarmException e) {
            e.printStackTrace();
        }

        if(eol == null)
            return;

        HashMap<Arc,Integer> tree = new HashMap<Arc,Integer>();

        FileManager fm = new FileManager();
        for(int i = 0; i<10; i++){
            String paquet = "Paquet0"+i+".txt";
            fm.openRead(folder + paquet);

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

            nodes.add(eol.getRoot());

            DirectedGraph dg = eol.getGraph();
            DirectedGraph pg = dg.getInducedGraphFromNodes(nodes);

            WindFarmInstance peol = new WindFarmInstance(pg);

            for(Integer node : nodes){
                if(eol.isRequired(node))
                    peol.setRequired(node);
            }
            peol.setRoot(eol.getRoot());

            for(Arc arc : pg.getEdges()){
                peol.setCost(arc, eol.getDoubleCost(arc));
            }

            for(Integer capa : eol.getStaticCapacities()){
                peol.setStaticCapacityCost(capa, eol.getStaticCapacityCost(capa));
            }

            for(Integer capa : eol.getDynamicCapacities()){
                peol.setDynamicCapacityCost(capa, eol.getDynamicCapacityCost(capa));
            }

            for(Integer node : nodes)
                peol.setMaximumOutputDegree(node, eol.getMaximumOutputDegree(node));
            peol.setMaximumOutputDegree(eol.getRoot(), 1);


            peol.setMaxNbSec(eol.getStaticCapacities().size());
            peol.setDistanceMin(eol.getDistanceMin());
            peol.setStaticStaticBranchingNodeCost(eol.getStaticStaticBranchingNodeCost());
            peol.setDynamicStaticBranchingNodeCost(eol.getDynamicStaticBranchingNodeCost());
            
            GFLAC2WindFarmAlgorithm3 gf = new GFLAC2WindFarmAlgorithm3();
            gf.setInstance(eol);
            gf.compute();

//            for(Map.Entry<Arc, Integer> entry : gf.getArborescence().entrySet()){
//                tree.put(entry.getKey(), entry.getValue());
//            }

        }

        tree = eol.unviolateMaxNbSecConstraint(tree);
        Double c = 0D;
        for(Map.Entry<Arc,Integer> entry : tree.entrySet()){
            c += eol.getRealCableCost(entry.getKey(), entry.getValue());
        }
        System.out.println(c);
    }
}
