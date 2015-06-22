package topklssminer;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "TopKLSSMiner" Node.
 * The Top k Longest Shared Sequence Miner looks for the top k longest subsequences of the test data inside the training sequences.
 *
 * @author Manuel Wildner
 */
public class TopKLSSMinerNodeView extends NodeView<TopKLSSMinerNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link TopKLSSMinerNodeModel})
     */
    protected TopKLSSMinerNodeView(final TopKLSSMinerNodeModel nodeModel) {
        super(nodeModel);

        // TODO instantiate the components of the view here.

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {

        // TODO retrieve the new model from your nodemodel and 
        // update the view.
        TopKLSSMinerNodeModel nodeModel = 
            (TopKLSSMinerNodeModel)getNodeModel();
        assert nodeModel != null;
        
        // be aware of a possibly not executed nodeModel! The data you retrieve
        // from your nodemodel could be null, emtpy, or invalid in any kind.
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    
        // TODO things to do when closing the view
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {

        // TODO things to do when opening the view
    }

}

