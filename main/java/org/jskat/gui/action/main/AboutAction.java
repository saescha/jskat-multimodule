package org.jskat.gui.action.main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for showing about dialog
 */
public class AboutAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public AboutAction() {

		putValue(Action.NAME, strings.getString("about")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, strings.getString("about_tooltip")); //$NON-NLS-1$

		setIcon(Icon.ABOUT);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.showAboutMessage();
	}
}
