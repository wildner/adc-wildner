package tns;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "TNSRuleMiner" Node.
 * Mining the Top-K Non-Redundant Sequential Rules (TNS) from the spmf-library (http://www.philippe-fournier-viger.com/spmf)
 *
 * @author Manuel Wildner
 */
public class TNSRuleMinerNodeView extends NodeView<TNSRuleMinerNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link TNSRuleMinerNodeModel})
     */
    protected TNSRuleMinerNodeView(final TNSRuleMinerNodeModel nodeModel) {
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
        TNSRuleMinerNodeModel nodeModel = 
            (TNSRuleMinerNodeModel)getNodeModel();
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

