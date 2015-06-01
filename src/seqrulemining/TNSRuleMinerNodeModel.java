package seqrulemining;

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
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTNS;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule;
import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import ca.pfv.spmf.input.sequence_database_array_integers.SequenceDatabase;

/**
 * This is the model implementation of TNSRuleMiner. Mining the Top-K
 * Non-Redundant Sequential Rules (TNS) from the spmf-library
 * (http://www.philippe-fournier-viger.com/spmf)
 * 
 * @author Manuel Wildner
 */
public class TNSRuleMinerNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger logger = NodeLogger
			.getLogger(TNSRuleMinerNodeModel.class);

	/**
	 * the settings key which is used to retrieve and store the settings (from
	 * the dialog or from a settings file) (package visibility to be usable from
	 * the dialog).
	 */
	static final String CFGKEY_COUNT = "Count";

	/** initial default count value. */
	static final int DEFAULT_COUNT = 100;

	// example value: the models count variable filled from the dialog
	// and used in the models execution method. The default components of the
	// dialog work with "SettingsModels".
//	private final SettingsModelIntegerBounded m_count = new SettingsModelIntegerBounded(
//			TNSRuleMinerNodeModel.CFGKEY_COUNT,
//			TNSRuleMinerNodeModel.DEFAULT_COUNT, Integer.MIN_VALUE,
//			Integer.MAX_VALUE);
	private SettingsModelIntegerBounded m_kSelection = createKModel();
	private SettingsModelDoubleBounded m_minConfSelection = createMinConfModel();
	private SettingsModelIntegerBounded m_deltaSelection = createDeltaModel();
	private SettingsModelString m_SeqColumnSelection = createSeqColumnModel();
	private int k = 10;
	private double minConf = 0.5;
	private int delta = 2;

	// private String testString =
	// "1000 -1 1002 -1 1004 -1 1006 -1 1008 -1 10010 "
	// + "-1 10012 -1 10014 -1 10016 -1 10018 -1 10020 -1 10022 -1 10024 "
	// + "-1 10026 -1 10028 -1 10030 -1 10032 -1 10034 -1 10036 -1 10038 "
	// + "-1 10040 -1 10042 -1 10044 -1 10046 -1 10048 -1 10050 -1 10052 "
	// + "-1 10054 -1 10056 -1 10058 -1 10060 -1 10062 -1 10064 -1 -2";
//	private String testString = "2 -1 3 -1 35 -1 12 -1 4 -1 53 -1 77 -1 41 "
//			+ "-1 1000 -1 1002 -1 1004 -1 1006 -1 1008 -1 133 "
//			+ "-1 10012 -1 10014 -1 10016 -1 10018 -1 10020 -1 10022 -1 10024 -1 -2";

	/**
	 * Constructor for the node model.
	 */
	protected TNSRuleMinerNodeModel() {

		// TODO one incoming port and one outgoing port is assumed
		super(1, 1);
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

		// stores meta data about the table
		DataTableSpec inDataSpec = inData[0].getDataTableSpec();
		int rowNum = inData[0].getRowCount();
		
		/*
		 * store the positions of needed columns.
		 */
		int seqColPos = inDataSpec.findColumnIndex(m_SeqColumnSelection
				.getStringValue());
		/*
		 * update k, minconf and delta which is specified by the user
		 */
		k = m_kSelection.getIntValue();
		minConf = m_minConfSelection.getDoubleValue();
		delta = m_deltaSelection.getIntValue();

		RowIterator rowIter = inData[0].iterator();

		DataColumnSpec[] allColSpecs = new DataColumnSpec[3];
		allColSpecs[0] = new DataColumnSpecCreator("Rule", StringCell.TYPE)
				.createSpec();
		allColSpecs[1] = new DataColumnSpecCreator("Support(absolute)", DoubleCell.TYPE)
				.createSpec();
		allColSpecs[2] = new DataColumnSpecCreator("Confidence", DoubleCell.TYPE)
				.createSpec();
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

		SequenceDatabase database = new SequenceDatabase();
		// let's add m_count rows to it
		while (rowIter.hasNext()) {
			String[] inputTokens = ((StringCell) (rowIter.next()
					.getCell(seqColPos))).getStringValue().split(" ");
			// inputTokens = testString.split(" ");
			database.addSequence(inputTokens);
//			for (String s : inputTokens) {
//				System.out.print(s + " ");
//			}
//			System.out.println("");
		}
		AlgoTNS algoTNS = new AlgoTNS();
		RedBlackTree<Rule> kRules = algoTNS.runAlgorithm(k, database, minConf,
				delta);
//		algoTNS.writeResultTofile(".//output.txt");
		algoTNS.printStats();
		
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		Iterator<Rule> rulesIter = kRules.iterator();
		int rowCount = 0;
		while (rulesIter.hasNext()) {
			Rule rule = (Rule) rulesIter.next();
			RowKey key = new RowKey("Row " + rowCount);
			// the cells of the current row, the types of the cells must
			// match the column spec (see above)
			DataCell[] cells = new DataCell[3];
			cells[0] = new StringCell(rule.toString());
			cells[1] = new DoubleCell(rule.getAbsoluteSupport());
			cells[2] = new DoubleCell(rule.getConfidence());
			DataRow row = new DefaultRow(key, cells);
			container.addRowToTable(row);

			// check if the execution monitor was canceled
			exec.checkCanceled();
			exec.setProgress(rowCount / (double) rowNum, "Adding row "
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
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {

		// TODO: check if user settings are available, fit to the incoming
		// table structure, and the incoming types are feasible for the node
		// to execute. If the node can execute in its current state return
		// the spec of its output data table(s) (if you can, otherwise an array
		// with null elements), or throw an exception with a useful user message

		return new DataTableSpec[] { null };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {

		// TODO save user settings to the config object.

//		m_count.saveSettingsTo(settings);
		m_deltaSelection.saveSettingsTo(settings);
		m_kSelection.saveSettingsTo(settings);
		m_minConfSelection.saveSettingsTo(settings);
		m_SeqColumnSelection.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {

		// TODO load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.

//		m_count.loadSettingsFrom(settings);
		m_deltaSelection.loadSettingsFrom(settings);
		m_kSelection.loadSettingsFrom(settings);
		m_minConfSelection.loadSettingsFrom(settings);
		m_SeqColumnSelection.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {

		// TODO check if the settings could be applied to our model
		// e.g. if the count is in a certain range (which is ensured by the
		// SettingsModel).
		// Do not actually set any values of any member variables.

//		m_count.validateSettings(settings);
		m_deltaSelection.validateSettings(settings);
		m_kSelection.validateSettings(settings);
		m_minConfSelection.validateSettings(settings);
		m_SeqColumnSelection.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {

		// TODO load internal data.
		// Everything handed to output ports is loaded automatically (data
		// returned by the execute method, models loaded in loadModelContent,
		// and user settings set through loadSettingsFrom - is all taken care
		// of). Load here only the other internals that need to be restored
		// (e.g. data used by the views).

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {

		// TODO save internal models.
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

	}

	/**
	 * Creation of the different Settings Models to communicate with the node
	 * dialog
	 */
	protected static SettingsModelString createSeqColumnModel() {
		return new SettingsModelString("seq_column_selection", "POLYLINE");
	}

	protected static SettingsModelIntegerBounded createKModel() {
		return new SettingsModelIntegerBounded("k_selection", 20, 1, Integer.MAX_VALUE);
	}
	
	protected static SettingsModelDoubleBounded createMinConfModel() {
		return new SettingsModelDoubleBounded("min_conf_selection", 0.5, 0, 1);
	}
	
	protected static SettingsModelIntegerBounded createDeltaModel() {
		return new SettingsModelIntegerBounded("delta_selection", 2, 0, Integer.MAX_VALUE);
	}
}
