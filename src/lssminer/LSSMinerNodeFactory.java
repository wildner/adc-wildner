package lssminer;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "LSSMiner" Node.
 * The Longest Shared Sequence Miner looks for the longest subsequence of the test data inside the training sequences.
 *
 * @author Manuel Wildner
 */
public class LSSMinerNodeFactory 
        extends NodeFactory<LSSMinerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LSSMinerNodeModel createNodeModel() {
        return new LSSMinerNodeModel();
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
    public NodeView<LSSMinerNodeModel> createNodeView(final int viewIndex,
            final LSSMinerNodeModel nodeModel) {
        return new LSSMinerNodeView(nodeModel);
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
        return new LSSMinerNodeDialog();
    }

}

