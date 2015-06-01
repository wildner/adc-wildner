package seqrulemining;

import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
/**
 * <code>NodeDialog</code> for the "TNSRuleMiner" Node.
 * Mining the Top-K Non-Redundant Sequential Rules (TNS) from the spmf-library (http://www.philippe-fournier-viger.com/spmf)
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Manuel Wildner
 */
public class TNSRuleMinerNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring TNSRuleMiner node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected TNSRuleMinerNodeDialog() {
        super();
        
//        addDialogComponent(new DialogComponentNumber(
//                new SettingsModelIntegerBounded(
//                    TNSRuleMinerNodeModel.CFGKEY_COUNT,
//                    TNSRuleMinerNodeModel.DEFAULT_COUNT,
//                    Integer.MIN_VALUE, Integer.MAX_VALUE),
//                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
        addDialogComponent(new DialogComponentColumnNameSelection(TNSRuleMinerNodeModel.createSeqColumnModel(),
						"Column containing the sequences: ", 0, true, StringValue.class));
        addDialogComponent(new DialogComponentNumber(TNSRuleMinerNodeModel.createKModel(), "Choose k", 1));
        addDialogComponent(new DialogComponentNumber(TNSRuleMinerNodeModel.createMinConfModel(), "Choose minConf", 0.05));
        addDialogComponent(new DialogComponentNumber(TNSRuleMinerNodeModel.createDeltaModel(), "Choose delta", 1));
    }
}

