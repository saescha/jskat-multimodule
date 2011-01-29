package de.jskat.gui.nn;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import net.miginfocom.swing.MigLayout;
import de.jskat.util.GameType;

/**
 * Overview dialog for training of neural networks
 */
public class NeuralNetworkTrainingOverview extends JDialog {

	JTable overviewTable;

	public NeuralNetworkTrainingOverview() {

		initGUI();
	}

	private void initGUI() {

		setMinimumSize(new Dimension(400, 300));

		setTitle("Training of neural networks");

		Container root = getContentPane();
		root.setLayout(new MigLayout());

		JPanel rootPanel = new JPanel(new MigLayout("fill", "fill", "fill"));

		overviewTable = new JTable(new TrainingOverviewTableModel());
		overviewTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		JScrollPane scrollPane = new JScrollPane(overviewTable);
		rootPanel.add(scrollPane, "grow, center");

		root.add(rootPanel, "center, grow");
	}

	/**
	 * Adds training result
	 * 
	 * @param gameType
	 *            Game type of neural net
	 * @param episodes
	 *            Number of episodes
	 * @param totalWonGames
	 *            Total Number of won games
	 * @param episodeWonGames
	 *            Number of won games in last episode
	 * @param totalDeclarerNetError
	 *            Total error in declarer net
	 * @param totalOpponentNetError
	 *            Total error in opponent net
	 */
	public void addTrainingResult(GameType gameType, Long episodes, Long totalWonGames, Long episodeWonGames,
			Double totalDeclarerNetError, Double totalOpponentNetError) {

		((TrainingOverviewTableModel) overviewTable.getModel()).addTrainingResult(gameType, episodes, totalWonGames,
				episodeWonGames, totalDeclarerNetError, totalOpponentNetError);
	}

	private class TrainingOverviewTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private List<String> header;
		private HashMap<GameType, List<Object>> data;

		protected TrainingOverviewTableModel() {

			header = new ArrayList<String>();
			header.add("Game type");
			header.add("Episodes");
			header.add("Total won games");
			header.add("Episode won games");
			header.add("Average declarer difference");
			header.add("Average opponent difference");

			data = new HashMap<GameType, List<Object>>();

			for (GameType currGameType : GameType.values()) {

				List<Object> list = new ArrayList<Object>();
				list.add(currGameType);
				for (int i = 1; i < getColumnCount(); i++) {
					list.add(0);
				}
				data.put(currGameType, list);
			}
		}

		@Override
		public int getRowCount() {

			return GameType.values().length;
		}

		@Override
		public int getColumnCount() {

			return 6;
		}

		@Override
		public String getColumnName(int column) {

			return header.get(column);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			GameType gameType = GameType.values()[rowIndex];

			return data.get(gameType).get(columnIndex);
		}

		/**
		 * Adds training result
		 * 
		 * @param gameType
		 *            Game type of neural net
		 * @param episodes
		 *            Number of episodes
		 * @param totalWonGames
		 *            Total number of won games
		 * @param episodeWonGames
		 *            Number of won games in last episode
		 * @param totalDeclarerNetError
		 *            Total error in declarer net
		 * @param totalOpponentNetError
		 *            Total error in opponent net
		 */
		public void addTrainingResult(GameType gameType, Long episodes, Long totalWonGames, Long episodeWonGames,
				Double totalDeclarerNetError, Double totalOpponentNetError) {

			TableModel tableModel = overviewTable.getModel();

			tableModel.setValueAt(gameType, gameType.ordinal(), 0);
			tableModel.setValueAt(episodes, gameType.ordinal(), 1);
			tableModel.setValueAt(totalWonGames, gameType.ordinal(), 2);
			tableModel.setValueAt(episodeWonGames, gameType.ordinal(), 3);
			tableModel.setValueAt(totalDeclarerNetError, gameType.ordinal(), 4);
			tableModel.setValueAt(totalOpponentNetError, gameType.ordinal(), 5);

			fireTableDataChanged();
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {

			data.get(GameType.values()[rowIndex]).set(columnIndex, value);
		}
	}
}
