package topklssminer;

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
import org.knime.core.data.def.IntCell;
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
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of LSSMiner.
 * The Top k Longest Shared Sequence Miner looks for the top k longest subsequences of the test data inside the training sequences.
 *
 * @author Manuel Wildner
 */
public class TopKLSSMinerNodeModel extends NodeModel {
    
	/**
	 * The settings models for the dialog components to handle user settings.
	 */

	private SettingsModelString m_testSeqColumnSelection = createTestSeqColumnModel();
	private SettingsModelString m_trainingSeqColumnSelection = createTrainingSeqColumnModel();
	private SettingsModelIntegerBounded m_maxTestGapSelection = createMaxTestGapModel();
	private SettingsModelIntegerBounded m_maxTrainGapSelection = createMaxTrainGapModel();
	private SettingsModelIntegerBounded m_minSeqLengthSelection = createMinSeqLengthGapModel();
	private SettingsModelIntegerBounded m_topK = createTopKModel();
	private SettingsModelDoubleBounded m_maxLengthDev = createMaxLenDevFromMaxModel();
	private SettingsModelBoolean m_appendSharedSeqLength = createAppendSharedSeqLengthModel();
	private SettingsModelBoolean m_appendSharedSeq = createAppendSharedSeqModel();
	
	private int maxTestGap = 1;
	private int maxTrainGap = 5;
	// TODO write a settingsmodel for it
	private int minSeqLength = 5;
	private int topK = 1;
	private double maxLengthDevFromMax = 1;
	private boolean appendSeqLength = true;
	private boolean appendSeq = false;
	
    /**
     * Constructor for the node model.
     */
    protected TopKLSSMinerNodeModel() {
    
        super(2, 1);
    }


	/**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

		if (inData == null || inData[0] == null || inData[1] == null) {
			return inData;
		}

		// stores meta data about the tables
		DataTableSpec inDataSpec0 = inData[0].getDataTableSpec(); // test data
		int rowNum0 = inData[0].getRowCount();
		DataTableSpec inDataSpec1 = inData[1].getDataTableSpec(); // training data
		int rowNum1 = inData[1].getRowCount();
		
		/*
		 * store the positions of needed columns.
		 */
		int seqColPos0 = inDataSpec0.findColumnIndex(m_testSeqColumnSelection
				.getStringValue());
		int seqColPos1 = inDataSpec1.findColumnIndex(m_trainingSeqColumnSelection
				.getStringValue());
		
		/*
		 * update parameters which are specified by the user
		 */
		maxTestGap = m_maxTestGapSelection.getIntValue();
		maxTrainGap = m_maxTrainGapSelection.getIntValue();
		minSeqLength = m_minSeqLengthSelection.getIntValue();
		topK = m_topK.getIntValue();
		maxLengthDevFromMax = m_maxLengthDev.getDoubleValue();
		appendSeqLength = m_appendSharedSeqLength.getBooleanValue();
		appendSeq = m_appendSharedSeq.getBooleanValue();
		
		int numOutColumns;
		if (appendSeq ^ appendSeqLength) {
			numOutColumns = 3;
		} else if(appendSeq && appendSeqLength) {
			numOutColumns = 4;
		} else {
			numOutColumns = 2;
		}

		RowIterator rowIter1 = inData[1].iterator();

		// the structure of the output table
		DataColumnSpec[] allColSpecs = new DataColumnSpec[numOutColumns];
		allColSpecs[0] = new DataColumnSpecCreator("RowID(Training)", IntCell.TYPE)
				.createSpec();
		allColSpecs[1] = new DataColumnSpecCreator("RowID(Test)", IntCell.TYPE)
				.createSpec();
		if(appendSeqLength) {
			allColSpecs[2] = new DataColumnSpecCreator("SharedSeqLength", IntCell.TYPE)
			.createSpec();
		}
		if(appendSeq) {
			if(appendSeqLength) {
				allColSpecs[3] = new DataColumnSpecCreator("SharedSeqence", StringCell.TYPE)
				.createSpec();
			} else {
				allColSpecs[2] = new DataColumnSpecCreator("SharedSeqence", StringCell.TYPE)
				.createSpec();
			}
		}
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

		// stores the current longest shared sequence length
//		int[] lssLength = new int[rowNum0];
		TopDataRows[] topData = new TopDataRows[rowNum0];
		for(int i = 0; i < topData.length; i++) {
			topData[i] = new TopDataRows(topK);
		}
		
		// look for sequences in the training data
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		int rowCountOut = 0;
		while(rowIter1.hasNext()) {
			DataRow row1 = rowIter1.next();
			String[] trainingTokens = ((StringCell) (row1
					.getCell(seqColPos1))).getStringValue().split(",");
			int rowCountTest = 0;
			RowIterator rowIter0 = inData[0].iterator();
			while(rowIter0.hasNext()) {
				DataRow row0 = rowIter0.next();
				String[] testTokens = ((StringCell) (row0
						.getCell(seqColPos0))).getStringValue().split(",");
				int testPointer = testTokens.length - 1;
				int foundCount = 0;
				int trainGapCount = 0;
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = trainingTokens.length - 1; i >= 0; i--) {
					if (trainGapCount <= maxTrainGap && testPointer - maxTestGap >= 0) {
						for (int j = 0; j <= maxTestGap; j++) {
							if(trainingTokens[i].equals(testTokens[testPointer - j])) {
								if(appendSeq) {
									stringBuilder.insert(0, trainingTokens[i] + ",");
								}
								foundCount++;
								testPointer -= j + 1;
								break;
							}
						}
					}
				}
//				if(foundCount >= minSeqLength
//						&& foundCount >= lssLength[rowCountTest] - topK) {
//					if (foundCount > lssLength[rowCountTest]) {
//						lssLength[rowCountTest] = foundCount;
//					}
				if(foundCount >= minSeqLength
						&& foundCount > topData[rowCountTest].getMinSharedSequenceLength()) {
					/*
					 * Create the new row
					 */
					RowKey key = new RowKey("Row" + rowCountOut);
					int rowNumberTrain = Integer.parseInt(row1.getKey().getString().substring(3));
					// the cells of the current row, the types of the cells must
					// match the column spec (see above)
					DataCell[] cells = new DataCell[numOutColumns];
					cells[0] = new IntCell(rowNumberTrain);
					cells[1] = new IntCell(Integer.parseInt(row0.getKey().getString().substring(3)));
					if(appendSeqLength) {
						cells[2] = new IntCell(foundCount);
					}
					if(appendSeq) {
						stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
//						stringBuilder.reverse();
						if(appendSeqLength) {
							cells[3] = new StringCell(stringBuilder.toString());
						} else {
							cells[2] = new StringCell(stringBuilder.toString());
						}
					}
					DataRow row = new DefaultRow(key, cells);
					
//					// TopDataRow not yet initialized 
//					if (topData[rowCountTest] == null) {
//						topData[rowCountTest] = new TopDataRows(topK);
//					}
					
					// row is only added, when foundCount is higher then the MinSharedSequenceLength
					topData[rowCountTest].addRow(row, foundCount);
						
//					container.addRowToTable(row);
					// check if the execution monitor was canceled
					exec.checkCanceled();
					exec.setProgress(rowNumberTrain / (double) rowNum1, "Adding row "
							+ rowCountOut);
					rowCountOut++;
				}
				rowCountTest++;
			}
		}
		
		// fill output table
		for (TopDataRows rows : topData) {
			int max = rows.getMaxSharedSequenceLength();
			for (int i = 0; i < rows.getTopKDataRows().size(); i++) {
				if (rows.getSSL(i) >= max * maxLengthDevFromMax) {
					// Drop all rows whose seqLength is below a certain fraction of the maxLength of the corresponding test rows
					container.addRowToTable(rows.getTopKDataRows().get(i));
				}
			}
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
		m_maxTrainGapSelection.saveSettingsTo(settings);
		m_minSeqLengthSelection.saveSettingsTo(settings);
		m_topK.saveSettingsTo(settings);
		m_maxLengthDev.saveSettingsTo(settings);
		m_appendSharedSeqLength.saveSettingsTo(settings);
		m_appendSharedSeq.saveSettingsTo(settings);
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
		m_maxTrainGapSelection.loadSettingsFrom(settings);
		m_minSeqLengthSelection.loadSettingsFrom(settings);
		m_topK.loadSettingsFrom(settings);
		m_maxLengthDev.loadSettingsFrom(settings);
		m_appendSharedSeqLength.loadSettingsFrom(settings);
		m_appendSharedSeq.loadSettingsFrom(settings);
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
		m_maxTrainGapSelection.validateSettings(settings);
		m_minSeqLengthSelection.validateSettings(settings);
		m_topK.validateSettings(settings);
		m_maxLengthDev.validateSettings(settings);
		m_appendSharedSeqLength.validateSettings(settings);
		m_appendSharedSeq.validateSettings(settings);
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

	protected static SettingsModelIntegerBounded createMaxTrainGapModel() {
		return new SettingsModelIntegerBounded("max_train_gap_selection", 5, 0, 200);
	}
	
	protected static SettingsModelIntegerBounded createMinSeqLengthGapModel() {
		return new SettingsModelIntegerBounded("min_seq_length_selection", 5, 1, 200);
	}
	
	protected static SettingsModelIntegerBounded createTopKModel() {
		return new SettingsModelIntegerBounded("top_k_selection", 1, 1, 200);
	}
	
	protected static SettingsModelBoolean createAppendSharedSeqLengthModel() {
		return new SettingsModelBoolean("append_shared_seq_length", true);
	}
	
	protected static SettingsModelBoolean createAppendSharedSeqModel() {
		return new SettingsModelBoolean("append_shared_seq", false);
	}
	
	protected static SettingsModelDoubleBounded createMaxLenDevFromMaxModel() {
		return new SettingsModelDoubleBounded("max_len_deviation_from_max", 1, 0, 1);
	}
}


