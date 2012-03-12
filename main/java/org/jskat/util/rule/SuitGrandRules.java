package org.jskat.util.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;

/**
 * Implements some methods of the interface SkatRules that are the same in suit
 * and grand games
 */
public abstract class SuitGrandRules extends SuitGrandRamschRules {

	private static Log log = LogFactory.getLog(SuitGrandRules.class);

	/**
	 * @see BasicSkatRules#calcGameWon(SkatGameData)
	 */
	@Override
	public final boolean calcGameWon(SkatGameData gameData) {

		boolean result = false;

		if (gameData.getScore(gameData.getDeclarer()) > 60) {
			// the single player has made more than 60 points
			result = true;
		}

		return result;
	}

	/**
	 * @see SuitGrandRamschRules#calcGameResult(SkatGameData)
	 */
	@Override
	public int calcGameResult(SkatGameData gameData) {

		int multiplier = getMultiplier(gameData);

		log.debug("calcSuitResult: after Jacks and Trump: multiplier " + multiplier); //$NON-NLS-1$

		// TODO add option: Hand game is only counted when game was not lost
		// if (gameData.isHand() && !gameData.isGameLost()) {
		if (gameData.isHand()) {
			multiplier++;
		}

		if (gameData.isOuvert()) {
			multiplier++;
		}

		if (gameData.isSchneider()) {
			multiplier++;
			if (gameData.isHand() && gameData.isSchneiderAnnounced()) {
				multiplier++;
			}
			log.debug("calcSuitResult: Schneider: multiplier " + multiplier); //$NON-NLS-1$
		}

		if (gameData.isSchwarz()) {
			multiplier++;
			if (gameData.isHand() && gameData.isSchwarzAnnounced()) {
				multiplier++;
			}
			log.debug("calcSuitResult: Schwarz: multiplier " + multiplier); //$NON-NLS-1$
		}

		int gameValue = SkatConstants.getGameBaseValue(gameData.getGameType(), gameData.isHand(), gameData.isOuvert());

		log.debug("gameValue" + gameValue); //$NON-NLS-1$

		int result = gameValue * multiplier;

		if (gameData.isGameLost()) {

			// penalty if game lost
			result = result * -2;
		}

		return result;
	}

	/**
	 * Gets the multiplier for a suit or grand game
	 * 
	 * @param gameData
	 *            Game data
	 * @return Multiplier
	 */
	@Override
	public int getMultiplier(SkatGameData gameData) {

		int result = 0;

		CardList declarerCards = getDeclarerCards(gameData);

		result = getMultiplier(declarerCards, gameData.getGameType());

		return result;
	}

	private CardList getDeclarerCards(SkatGameData gameData) {
		CardList declarerCards = gameData.getDealtCards().get(gameData.getDeclarer());
		declarerCards.addAll(gameData.getDealtSkat());
		return declarerCards;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPlayWithJacks(SkatGameData gameData) {
		CardList declarerCards = getDeclarerCards(gameData);

		return declarerCards.contains(Card.CJ);
	}

	/**
	 * Gets the multiplier for a {@link CardList} and a {@link GameType}
	 * 
	 * @param cards
	 *            Card list
	 * @param gameType
	 *            Game type
	 * @return Multiplier
	 */
	public abstract int getMultiplier(CardList cards, GameType gameType);

	/**
	 * Checks whether a game was a schneider game<br>
	 * schneider means one party made only 30 points or below
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was a schneider game
	 */
	public boolean isSchneider(SkatGameData gameData) {

		boolean result = false;

		Player singlePlayer = gameData.getDeclarer();

		if (gameData.isGameLost()) {

			if (gameData.getScore(singlePlayer) < 31) {
				// single player lost and has also played schneider
				result = true;
			}
		} else {

			if (gameData.getScore(singlePlayer.getLeftNeighbor()) < 31
					|| gameData.getScore(singlePlayer.getRightNeighbor()) < 31) {
				// one of the opponents has played schneider
				result = true;
			}
		}

		return result;
	}

	/**
	 * Checks whether a game was a schwarz game<br>
	 * schwarz means one party made no trick
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was a schwarz game
	 */
	public boolean isSchwarz(SkatGameData gameData) {

		boolean result = false;

		int trickWinnerCount[] = { 0, 0, 0 };

		for (int i = 0; i < gameData.getTricks().size(); i++) {
			// count all tricks made by the players
			trickWinnerCount[gameData.getTrickWinner(i).ordinal()]++;
		}

		Player singlePlayer = gameData.getDeclarer();

		if (gameData.isGameLost()) {

			if (trickWinnerCount[singlePlayer.ordinal()] == 0) {
				// single player lost and has also played schwarz
				result = true;
			} else {

				if (trickWinnerCount[(singlePlayer.ordinal() + 1) % 3] == 0
						|| trickWinnerCount[(singlePlayer.ordinal() + 2) % 3] == 0) {
					// one of the opponents has played schwarz
					result = true;
				}
			}
		}

		return result;
	}
}
