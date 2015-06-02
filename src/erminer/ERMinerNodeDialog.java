package erminer;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;

import seqrulemining.TNSRuleMinerNodeModel;

/**
 * <code>NodeDialog</code> for the "ERMiner" Node.
 * Mining sequential rules using the ERMiner from the spmf-library (http://www.philippe-fournier-viger.com/spmf)
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Manuel Wildner
 */
public class ERMinerNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the ERMiner node.
     */
    protected ERMinerNodeDialog() {
super();
        
        addDialogComponent(new DialogComponentColumnNameSelection(ERMinerNodeModel.createSeqColumnModel(),
						"Column containing the sequences: ", 0, true, StringValue.class));
        addDialogComponent(new DialogComponentNumber(ERMinerNodeModel.createMinSupModel(), "Choose minSup", 0.05));
        addDialogComponent(new DialogComponentNumber(ERMinerNodeModel.createMinConfModel(), "Choose minConf", 0.05));
    }
}

