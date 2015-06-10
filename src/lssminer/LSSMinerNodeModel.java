package lssminer;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowIterator;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTNS;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule;
import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import ca.pfv.spmf.input.sequence_database_array_integers.SequenceDatabase;

/**
 * This is the model implementation of LSSMiner.
 * The Longest Shared Sequence Miner looks for the longest subsequence of the test data inside the training sequences.
 *
 * @author Manuel Wildner
 */
public class LSSMinerNodeModel extends NodeModel {
    
	/**
	 * The settings models for the dialog components to handle user settings.
	 */

	private SettingsModelString m_testSeqColumnSelection = createTestSeqColumnModel();
	private SettingsModelString m_trainingSeqColumnSelection = createTrainingSeqColumnModel();
	private SettingsModelIntegerBounded m_maxTestGapSelection = createMaxTestGapModel();
	
	private int maxTestGap = 1;
	
    /**
     * Constructor for the node model.
     */
    protected LSSMinerNodeModel() {
    
        // TODO: Specify the amount of input and output ports needed.
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

		if (inData == null || inData[0] == null) {
			return inData;
		}

		// stores meta data about the tables
		DataTableSpec inDataSpec0 = inData[0].getDataTableSpec();
		int rowNum0 = inData[0].getRowCount();
		DataTableSpec inDataSpec1 = inData[0].getDataTableSpec();
		int rowNum1 = inData[0].getRowCount();
		
		/*
		 * store the positions of needed columns.
		 */
		int seqColPos0 = inDataSpec0.findColumnIndex(m_testSeqColumnSelection
				.getStringValue());
		int seqColPos1 = inDataSpec1.findColumnIndex(m_trainingSeqColumnSelection
				.getStringValue());
		
		/*
		 * update maxTestGap which is specified by the user
		 */
		maxTestGap = m_maxTestGapSelection.getIntValue();

		RowIterator rowIter1 = inData[1].iterator();

		DataColumnSpec[] allColSpecs = new DataColumnSpec[2];
		allColSpecs[0] = new DataColumnSpecCreator("RowID(Training)", StringCell.TYPE)
				.createSpec();
		allColSpecs[1] = new DataColumnSpecCreator("SupportTags", StringCell.TYPE)
				.createSpec();
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

		// look for sequences in the training data
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		int rowCount = 0;
		while(rowIter1.hasNext()) {
			DataRow row1 = rowIter1.next();
			String[] trainingTokens = ((StringCell) (row1
					.getCell(seqColPos1))).getStringValue().split(" ");
			RowIterator rowIter0 = inData[0].iterator();
			while(rowIter0.hasNext()) {
				DataRow row0 = rowIter0.next();
				String[] testTokens = ((StringCell) (row0
						.getCell(seqColPos0))).getStringValue().split(" ");
				
			}
			/*
			 * Create the new row
			 */
			RowKey key = new RowKey("Row " + rowCount);
			// the cells of the current row, the types of the cells must
			// match the column spec (see above)
			DataCell[] cells = new DataCell[2];
			cells[0] = new StringCell(row1.getKey().getString());
			cells[1] = new StringCell("Tags of row" + rowCount);
			DataRow row = new DefaultRow(key, cells);
			container.addRowToTable(row);
			// check if the execution monitor was canceled
			exec.checkCanceled();
			exec.setProgress(rowCount / (double) rowNum1, "Adding row "
					+ rowCount);
			rowCount++;
		}
		
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        // TODO: generated method stub
        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_testSeqColumnSelection.saveSettingsTo(settings);
		m_trainingSeqColumnSelection.saveSettingsTo(settings);
		m_maxTestGapSelection.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
		m_testSeqColumnSelection.loadSettingsFrom(settings);
		m_trainingSeqColumnSelection.loadSettingsFrom(settings);
		m_maxTestGapSelection.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
		m_testSeqColumnSelection.validateSettings(settings);
		m_trainingSeqColumnSelection.validateSettings(settings);
		m_maxTestGapSelection.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

	/**
	 * Creation of the different Settings Models to communicate with the node
	 * dialog
	 */
	protected static SettingsModelString createTestSeqColumnModel() {
		return new SettingsModelString("test_seq_column_selection", "POLYLINE");
	}
	
	protected static SettingsModelString createTrainingSeqColumnModel() {
		return new SettingsModelString("training_seq_column_selection", "POLYLINE");
	}

	protected static SettingsModelIntegerBounded createMaxTestGapModel() {
		return new SettingsModelIntegerBounded("max_test_gap_selection", 1, 0, 200);
	}
}

