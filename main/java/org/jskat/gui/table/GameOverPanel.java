package org.jskat.gui.table;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jskat.data.GameSummary;
import org.jskat.gui.LayoutFactory;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Player;

class GameOverPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private GameResultPanel gameResultPanel;

	public GameOverPanel(ActionMap actions, JSkatGraphicRepository bitmaps) {

		initPanel(actions, bitmaps);
	}

	private void initPanel(ActionMap actions, JSkatGraphicRepository bitmaps) {

		this.setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "[grow][shrink]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		gameResultPanel = new GameResultPanel(bitmaps);
		panel.add(gameResultPanel, "grow, wrap"); //$NON-NLS-1$

		JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		buttonPanel.add(new JButton(actions.get(JSkatAction.CONTINUE_LOCAL_SERIES)), "center, shrink"); //$NON-NLS-1$
		buttonPanel.setOpaque(false);
		panel.add(buttonPanel, "center"); //$NON-NLS-1$

		panel.setOpaque(false);
		this.add(panel, "center"); //$NON-NLS-1$

		setOpaque(false);
	}

	void setUserPosition(Player player) {

		gameResultPanel.setUserPosition(player);
	}

	void setGameSummary(GameSummary summary) {

		gameResultPanel.setGameSummary(summary);
	}

	public void resetPanel() {

		gameResultPanel.resetPanel();
	}
}
