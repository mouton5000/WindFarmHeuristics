package graphTheory.graphDrawer;

import graphTheory.graph.Graph;

import java.util.HashMap;

/**
 * Created by mouton on 29/06/16.
 */
public class SimpleGraphDrawer extends GraphDrawer{


    public SimpleGraphDrawer(Graph g) {
        super(g);
    }

    public SimpleGraphDrawer(Graph g, @SuppressWarnings("rawtypes") HashMap arcDisplayedParam) {
        super(g, arcDisplayedParam);
    }

    @Override
    protected void setVerticesCoordinates() {
    }
}
