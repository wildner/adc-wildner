package lssminer;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;

import seqrulemining.TNSRuleMinerNodeModel;

/**
 * <code>NodeDialog</code> for the "LSSMiner" Node.
 * The Top k Longest Shared Sequence Miner looks for the longest subsequence of the test data inside the training sequences.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Manuel Wildner
 */
public class LSSMinerNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the LSSMiner node.
     */
    protected LSSMinerNodeDialog() {
    	super();
    	
    	addDialogComponent(new DialogComponentColumnNameSelection(LSSMinerNodeModel.createTestSeqColumnModel(),
				"Column containing the TEST sequences: ", 0, true, StringValue.class));
    	addDialogComponent(new DialogComponentColumnNameSelection(LSSMinerNodeModel.createTrainingSeqColumnModel(),
				"Column containing the TRAINING sequences: ", 1, true, StringValue.class));
        addDialogComponent(new DialogComponentNumber(LSSMinerNodeModel.createMaxTestGapModel(), "Choose max gap in test sequence", 1));
        addDialogComponent(new DialogComponentNumber(LSSMinerNodeModel.createMaxTrainGapModel(), "Choose max gap in training sequence", 1));
        addDialogComponent(new DialogComponentNumber(LSSMinerNodeModel.createMinSeqLengthGapModel(), "Choose min shared sequence length", 1));
        addDialogComponent(new DialogComponentNumber(LSSMinerNodeModel.createMaxSeqLengthVariationGapModel(), "Choose max variation of shared length", 1));
        addDialogComponent(new DialogComponentBoolean(LSSMinerNodeModel.createAppendSharedSeqLengthModel(), "Append length of shared sequence"));
        addDialogComponent(new DialogComponentBoolean(LSSMinerNodeModel.createAppendSharedSeqModel(), "Append the shared sequence itself"));
    }
}

