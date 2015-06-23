package topklssminer;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;

/**
 * <code>NodeDialog</code> for the "TopKLSSMiner" Node.
 * The Top k Longest Shared Sequence Miner looks for the top k longest subsequences of the test data inside the training sequences.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Manuel Wildner
 */
public class TopKLSSMinerNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the LSSMiner node.
     */
    protected TopKLSSMinerNodeDialog() {
    	super();
    	
    	addDialogComponent(new DialogComponentColumnNameSelection(TopKLSSMinerNodeModel.createTestSeqColumnModel(),
				"Column containing the TEST sequences: ", 0, true, StringValue.class));
    	addDialogComponent(new DialogComponentColumnNameSelection(TopKLSSMinerNodeModel.createTrainingSeqColumnModel(),
				"Column containing the TRAINING sequences: ", 1, true, StringValue.class));
        addDialogComponent(new DialogComponentNumber(TopKLSSMinerNodeModel.createMaxTestGapModel(), "Choose max gap in test sequence", 1));
        addDialogComponent(new DialogComponentNumber(TopKLSSMinerNodeModel.createMaxTrainGapModel(), "Choose max gap in training sequence", 1));
        addDialogComponent(new DialogComponentNumber(TopKLSSMinerNodeModel.createMinSeqLengthGapModel(), "Choose min shared sequence length", 1));
        addDialogComponent(new DialogComponentNumber(TopKLSSMinerNodeModel.createTopKModel(), "Choose top k values", 1));
        addDialogComponent(new DialogComponentNumber(TopKLSSMinerNodeModel.createMaxLenDevFromMaxModel(), "Include fraction of maxLength", 0.05));
        addDialogComponent(new DialogComponentBoolean(TopKLSSMinerNodeModel.createAppendSharedSeqLengthModel(), "Append length of shared sequence"));
        addDialogComponent(new DialogComponentBoolean(TopKLSSMinerNodeModel.createAppendSharedSeqModel(), "Append the shared sequence itself"));
    }
}

