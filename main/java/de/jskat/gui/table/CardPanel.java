/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData.GameState;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Rank;
import de.jskat.util.Suit;

/**
 * Panel for showing a Card
 */
class CardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(CardPanel.class);

	private JSkatGraphicRepository bitmaps;

	/**
	 * Holds the game type for the sorting order
	 */
	GameType sortGameType = GameType.GRAND;

	CardList cards;

	private boolean showBackside = true;
	private JPanel parent = null;

	/**
	 * Creates a new instance of CardPanel
	 * 
	 * @param newParent
	 *            Parent panel
	 * @param jSkatBitmaps
	 *            Graphic repository that holds all images used in JSkat
	 * @param newShowBackside
	 *            TRUE if the Card should hide its face
	 */
	CardPanel(JPanel newParent, JSkatGraphicRepository jSkatBitmaps,
			boolean newShowBackside) {

		setLayout(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		parent = newParent;
		setActionMap(parent.getActionMap());
		bitmaps = jSkatBitmaps;
		showBackside = newShowBackside;

		cards = new CardList();

		setOpaque(false);

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// not needed
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// not needed
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// not needed
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// not needed
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				cardClicked(e);
			}
		});
	}

	protected void addCard(Card newCard) {

		cards.add(newCard);
		cards.sort(sortGameType);
		repaint();
	}

	protected void addCards(Collection<Card> newCards) {

		cards.addAll(newCards);
		cards.sort(sortGameType);
		repaint();
	}

	protected void removeCard(Card cardToRemove) {

		cards.remove(cardToRemove);
		repaint();
	}

	protected Card get(int index) {

		return cards.get(index);
	}

	/**
	 * @see JPanel#paintComponent(Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int cardNo = 0;
		for (Card card : cards) {

			Image image = null;

			if (showBackside) {

				image = bitmaps.getCardImage(null, null);
			} else {

				image = bitmaps.getCardImage(card.getSuit(), card.getRank());
			}

			double cardWidth = image.getWidth(this);
			double partialHiddenCardWidth = 0.0;
			if (cards.size() > 1) {

				partialHiddenCardWidth = (getWidth() - cardWidth)
						/ (cards.size() - 1.0);
			}

			double scaleFactor = 1.0;

			AffineTransform transform = new AffineTransform();
			transform.scale(scaleFactor, scaleFactor);
			transform.translate(cardNo * partialHiddenCardWidth, 0);
			g2D.drawImage(image, transform, this);

			cardNo++;
		}
	}

	/**
	 * Clears the card panel
	 */
	void clearCards() {

		cards.clear();
		repaint();
	}

	/**
	 * Flips the card
	 */
	void flipCard() {

		if (showBackside) {

			showCards();
		} else {

			hideCards();
		}
	}

	/**
	 * Shows the card
	 */
	void showCards() {

		showBackside = false;
		repaint();
	}

	/**
	 * Hides the card
	 */
	void hideCards() {

		showBackside = true;
		repaint();
	}

	int getCardCount() {

		return cards.size();
	}

	/**
	 * Tells the JSkatMaster when the panel was clicked by the user
	 */
	void cardClicked(MouseEvent e) {
		// FIXME (jan 04.12.2010) refactor this method, nobody understands it
		int xPosition = e.getX();
		int yPosition = e.getY();

		log.debug("Card panel clicked at: " + xPosition + " x " + yPosition); //$NON-NLS-1$ //$NON-NLS-2$

		if (xPosition > -1 && xPosition < getWidth() && yPosition > -1
				&& yPosition < getHeight()) {

			log.debug("Mouse button release inside panel"); //$NON-NLS-1$

			// get card
			double cardWidth = bitmaps.getCardImage(Suit.CLUBS, Rank.JACK)
					.getWidth(this);

			int cardIndex = -1;
			if (cards.size() > 0) {
				if (cards.size() == 1) {
					log.debug("only on card on hand"); //$NON-NLS-1$
					if (xPosition < cardWidth) {
						cardIndex = 0;
					}
				} else {
					double distanceBetweenCards = cardWidth;
					if (cards.size() > 1) {
						distanceBetweenCards = (getWidth() - cardWidth)
								/ (cards.size() - 1.0);
					}

					if (cardWidth > distanceBetweenCards) {
						// cards without gaps
						log.debug("cards without gaps"); //$NON-NLS-1$
						cardIndex = 0;
						while (!((cardIndex * distanceBetweenCards) < xPosition && ((cardIndex + 1)
								* distanceBetweenCards > xPosition))
								&& cardIndex < (cards.size() - 1)) {
							cardIndex++;
						}
					} else {
						// cards with gaps
						log.debug("cards with gaps"); //$NON-NLS-1$
						double cardGap = (getWidth() - (cardWidth * cards
								.size())) / (cards.size() - 1.0);

						if ((int) ((xPosition / (cardWidth + cardGap))) == (int) ((xPosition + cardGap) / (cardWidth + cardGap))) {
							cardIndex = (int) (xPosition / (cardWidth + cardGap));
						}
					}
				}
			}

			Card card = null;
			if (cardIndex > -1 && cardIndex < cards.size()) {

				card = cards.get(cardIndex);
				log.debug("card index: " + cardIndex + " card: " + cards.get(cardIndex)); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (card != null) {
				// send event only, if the card panel shows a card
				Action action = null;

				if (parent instanceof DiscardPanel) {
					// card panel in discard panel was clicked
					action = getActionMap()
							.get(JSkatAction.TAKE_CARD_FROM_SKAT);
				} else if (parent instanceof JSkatUserPanel) {
					// card panel in player panel was clicked

					GameState state = ((JSkatUserPanel) parent).getGameState();

					if (state == GameState.DISCARDING) {
						// discarding phase
						action = getActionMap().get(
								JSkatAction.PUT_CARD_INTO_SKAT);
					} else if (state == GameState.TRICK_PLAYING) {
						// trick playing phase
						action = getActionMap().get(JSkatAction.PLAY_CARD);
					}
				} else {

					log.debug("Other parent " + parent); //$NON-NLS-1$
				}

				if (action != null) {

					action.actionPerformed(new ActionEvent(Card
							.getCardFromString(card.getSuit().shortString()
									+ card.getRank().shortString()),
							ActionEvent.ACTION_PERFORMED, (String) action
									.getValue(Action.ACTION_COMMAND_KEY)));
				} else {

					log.debug("Action is null"); //$NON-NLS-1$
				}
			}
		}
	}

	void setSortType(GameType newGameType) {

		sortGameType = newGameType;
		cards.sort(sortGameType);
		repaint();
	}
}
