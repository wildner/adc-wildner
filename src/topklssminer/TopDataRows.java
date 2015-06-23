package topklssminer;

import java.util.ArrayList;

import org.knime.core.data.DataRow;

public class TopDataRows {
	
	private ArrayList<Item> topKDataRows;
	private int minIndex = -1;
	private int k;
	
	
	public TopDataRows(int k) {
		this.k = k;
		topKDataRows = new ArrayList<>(k);
	}


	public ArrayList<DataRow> getTopKDataRows() {
		ArrayList<DataRow> output = new ArrayList<>();
		for (Item item : topKDataRows) {
			output.add(item.getRow());
		}
		return output;
	}
	
	/**
	 * This method returns the minimum shared sequence length.
	 */
	public int getMinSharedSequenceLength() {
		if(topKDataRows.size() < k || minIndex == -1) {
			return 0;
		} else {
			return topKDataRows.get(minIndex).getSharedseqLength();
		}
	}
	
	/**
	 * This method returns the maximum shared sequence length.
	 */
	public int getMaxSharedSequenceLength() {
		if(topKDataRows.size() < 1 || minIndex == -1) {
			return 0;
		} else {
			int max = 0;
			for (int i = 0; i < topKDataRows.size(); i++) {
				if(topKDataRows.get(i).getSharedseqLength() > max) {
					max = topKDataRows.get(i).getSharedseqLength();
				}
			}
			return max;
		}
	}

	public int getSSL(int index) {
		return topKDataRows.get(index).getSharedseqLength();
	}

	public void addRow(DataRow row, int foundCount) {
		if (topKDataRows.size() >= k ) {
			
			// get Min
//			for (int i = 0; i < topKDataRows.size(); i++) {
//				Item item = topKDataRows.get(i);
				// new Item has longer shared sequence?  --> replace Min ( set DataRow and Count, updateMinIndex)
				if (foundCount > topKDataRows.get(minIndex).getSharedseqLength()) {
					topKDataRows.get(minIndex).setRow(row);
					topKDataRows.get(minIndex).setSharedseqLength(foundCount);
					
					updateMinIndex();
				}
//			}
		}
//		else if (topKDataRows.size() < k) {
		else {
			Item item = new Item(foundCount, row);
			topKDataRows.add(item);
			
			updateMinIndex();
		}
		
	}
	
	
	private void updateMinIndex() {
//		if (topKDataRows.size() < 1) {
//			this.minIndex = -1;
//			return;
//		}
		int minFoundLength = topKDataRows.get(0).getSharedseqLength();
		this.minIndex = 0;
		for (int i = 1; i < topKDataRows.size(); i++) {
			if (topKDataRows.get(i).getSharedseqLength() < minFoundLength) {
				minIndex = i;
				minFoundLength = topKDataRows.get(i).getSharedseqLength();
			}
		}
		
	}
	
	/**
	 * I created this helper class, because 'foundCount' might not always be present in the DataRow,
	 * when it is not activated.  
	 * @author michaelhundt
	 *
	 */
	private class Item {
		private int sharedseqLength;
		private DataRow row;
		
		public Item( int sharedLength, DataRow row) {
			this.sharedseqLength = sharedLength;
			this.row = row;
		}

		public int getSharedseqLength() {
			return sharedseqLength;
		}

		public void setSharedseqLength(int sharedseqLength) {
			this.sharedseqLength = sharedseqLength;
		}

		public DataRow getRow() {
			return row;
		}

		public void setRow(DataRow row) {
			this.row = row;
		}
		
	}
	
	
}
