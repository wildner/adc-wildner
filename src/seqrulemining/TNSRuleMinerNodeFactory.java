package seqrulemining;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "TNSRuleMiner" Node.
 * Mining the Top-K Non-Redundant Sequential Rules (TNS) from the spmf-library (http://www.philippe-fournier-viger.com/spmf)
 *
 * @author Manuel Wildner
 */
public class TNSRuleMinerNodeFactory 
        extends NodeFactory<TNSRuleMinerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public TNSRuleMinerNodeModel createNodeModel() {
        return new TNSRuleMinerNodeModel();
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
    public NodeView<TNSRuleMinerNodeModel> createNodeView(final int viewIndex,
            final TNSRuleMinerNodeModel nodeModel) {
        return new TNSRuleMinerNodeView(nodeModel);
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
        return new TNSRuleMinerNodeDialog();
    }

}

