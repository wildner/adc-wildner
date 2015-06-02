package erminer;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ERMiner" Node.
 * Mining sequential rules using the ERMiner from the spmf-library (http://www.philippe-fournier-viger.com/spmf)
 *
 * @author Manuel Wildner
 */
public class ERMinerNodeFactory 
        extends NodeFactory<ERMinerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ERMinerNodeModel createNodeModel() {
        return new ERMinerNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<ERMinerNodeModel> createNodeView(final int viewIndex,
            final ERMinerNodeModel nodeModel) {
        return new ERMinerNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new ERMinerNodeDialog();
    }

}

