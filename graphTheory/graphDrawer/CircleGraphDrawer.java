package graphTheory.graphDrawer;

import graphTheory.graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by mouton on 29/06/16.
 */
public class CircleGraphDrawer extends GraphDrawer{
    public CircleGraphDrawer(Graph g) {
        super(g);
    }

    public CircleGraphDrawer(Graph g, @SuppressWarnings("rawtypes") HashMap arcDisplayedParam) {
        super(g, arcDisplayedParam);
    }

    @Override
    protected void setVerticesCoordinates() {
        ArrayList<Integer> nodes = graph.getVertices();
        ArrayList<Integer> h = nodes.stream().filter(node -> graph.isDrawn(node)).collect(Collectors.toCollection(ArrayList::new));


        int s = h.size();
        int i = 0;
        for (Integer n : h) {
            int x1 = (int) (300D + 200D * Math
                    .cos(((double) (2 * i) * 3.1415926535897931D) / (double) s));
            int y1 = (int) (300D + 200D * Math
                    .sin(((double) (2 * i) * 3.1415926535897931D) / (double) s));

            graph.setNodeAbscissa(n, x1);
            graph.setNodeOrdinate(n, y1);

            i++;
        }
    }
}
