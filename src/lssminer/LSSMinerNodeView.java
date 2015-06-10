package lssminer;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "LSSMiner" Node.
 * The Longest Shared Sequence Miner looks for the longest subsequence of the test data inside the training sequences.
 *
 * @author Manuel Wildner
 */
public class LSSMinerNodeView extends NodeView<LSSMinerNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link LSSMinerNodeModel})
     */
    protected LSSMinerNodeView(final LSSMinerNodeModel nodeModel) {
        super(nodeModel);
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
        // TODO: generated method stub
    }

}

