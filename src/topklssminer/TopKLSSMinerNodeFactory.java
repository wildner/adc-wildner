package topklssminer;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "TopKLSSMiner" Node.
 * The Top k Longest Shared Sequence Miner looks for the top k longest subsequences of the test data inside the training sequences.
 *
 * @author Manuel Wildner
 */
public class TopKLSSMinerNodeFactory 
        extends NodeFactory<TopKLSSMinerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public TopKLSSMinerNodeModel createNodeModel() {
        return new TopKLSSMinerNodeModel();
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
    public NodeView<TopKLSSMinerNodeModel> createNodeView(final int viewIndex,
            final TopKLSSMinerNodeModel nodeModel) {
        return new TopKLSSMinerNodeView(nodeModel);
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
        return new TopKLSSMinerNodeDialog();
    }

}

