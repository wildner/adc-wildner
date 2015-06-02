package erminer;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "ERMiner" Node.
 * Mining sequential rules using the ERMiner from the spmf-library (http://www.philippe-fournier-viger.com/spmf)
 *
 * @author Manuel Wildner
 */
public class ERMinerNodeView extends NodeView<ERMinerNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link ERMinerNodeModel})
     */
    protected ERMinerNodeView(final ERMinerNodeModel nodeModel) {
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

