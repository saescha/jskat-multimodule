package org.jskat.control;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.JSkat;
import org.jskat.ai.JSkatPlayer;
import org.jskat.ai.PlayerType;
import org.jskat.ai.nn.data.SkatNetworks;
import org.jskat.ai.nn.train.NNTrainer;
import org.jskat.control.iss.IssController;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.JSkatApplicationData;
import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.gui.JSkatView;
import org.jskat.gui.action.JSkatAction;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.version.VersionChecker;

/**
 * Controls everything in JSkat
 */
public class JSkatMaster {

	private static Log log = LogFactory.getLog(JSkatMaster.class);

	private static JSkatMaster instance = null;

	private JSkatOptions options;
	private JSkatApplicationData data;
	private JSkatView view;
	private IssController issControl;

	/**
	 * Gets the instance of the JSkat master controller
	 * 
	 * @return JSkat master controller
	 */
	public static JSkatMaster instance() {

		if (instance == null) {

			instance = new JSkatMaster();
		}

		return instance;
	}

	/**
	 * Constructor
	 */
	private JSkatMaster() {

		options = JSkatOptions.instance();
		data = JSkatApplicationData.instance();

		issControl = new IssController(this);
	}

	/**
	 * Checks the version of JSkat
	 */
	public void checkJSkatVersion() {
		String latestVersion = VersionChecker.getLatestVersion();
		log.debug("Latest version web: " + latestVersion); //$NON-NLS-1$
		log.debug("Latest version local: " + JSkat.getVersion()); //$NON-NLS-1$
		if (VersionChecker.isHigherVersionAvailable(latestVersion)) {
			log.debug("Newer version " + latestVersion + " is available on the JSkat website."); //$NON-NLS-1$//$NON-NLS-2$
			view.showNewVersionAvailableMessage(latestVersion);
		}
	}

	/**
	 * Creates a new skat table
	 */
	public void createTable() {

		// TODO check whether a connection to ISS is established
		// TODO ask whether a local or a remote tabel should be created

		String tableName = view.getNewTableName(data.getLocalTablesCreated());

		if (tableName == null) {

			log.debug("Create table was cancelled..."); //$NON-NLS-1$
			return;

		} else {

			if (data.isFreeTableName(tableName)) {

				createLocalTable(tableName);

			} else {

				view.showDuplicateTableNameMessage(tableName);
				// try again
				createTable();
			}
		}
	}

	private void createLocalTable(String tableName) {
		SkatTable table = new SkatTable(data.getTableOptions());
		table.setName(tableName);
		data.addSkatTable(table);

		view.createSkatTablePanel(table.getName());
		data.setActiveTable(table.getName());

		table.setView(view);
	}

	/**
	 * Invites players on ISS to the current table
	 */
	public void invitePlayer() {

		Set<String> issPlayerNames = data.getAvailableISSPlayer();
		issPlayerNames.remove(data.getIssLoginName());

		List<String> player = view.getPlayerForInvitation(issPlayerNames);
		for (String currPlayer : player) {
			getIssController().invitePlayer(data.getActiveTable(), currPlayer);
		}
	}

	/**
	 * Starts a new skat series
	 */
	public void startSeries() {

		log.debug(data.getActiveTable());

		view.showStartSkatSeriesDialog();
	}

	/**
	 * Starts a new series with given parameters
	 * 
	 * @param allPlayer
	 *            Player types
	 * @param playerNames
	 *            Player names
	 * @param numberOfRounds
	 *            Number of rounds to be played
	 * @param unlimited
	 *            TRUE, if unlimited rounds should be played
	 */
	public void startSeries(ArrayList<PlayerType> allPlayer, ArrayList<String> playerNames, int numberOfRounds,
			boolean unlimited, boolean onlyPlayRamsch) {

		log.debug(data.getActiveTable());

		SkatTable table = data.getSkatTable(data.getActiveTable());

		table.removePlayers();

		int playerCount = 0;
		for (PlayerType player : allPlayer) {
			JSkatPlayer newPlayer = null;
			if (player == PlayerType.HUMAN) {
				newPlayer = data.getHumanPlayer(table.getName());
			} else {
				newPlayer = PlayerType.getPlayerInstance(player);
			}
			newPlayer.setPlayerName(playerNames.get(playerCount));
			table.placePlayer(newPlayer);
			playerCount++;
		}

		table.startSkatSeries(numberOfRounds, unlimited, onlyPlayRamsch);
	}

	/**
	 * Pauses a skat series at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void pauseSkatSeries(String tableName) {

		SkatTable table = data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			table.pauseSkatSeries();
		}
	}

	/**
	 * Starts a new skat series
	 */
	public void resumeSkatSeries() {

		log.debug(data.getActiveTable());

		resumeSkatSeries(data.getActiveTable());
	}

	/**
	 * Resumes a skat series at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void resumeSkatSeries(String tableName) {

		SkatTable table = data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			table.resumeSkatSeries();
		}
	}

	/**
	 * Pauses a skat game at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void pauseSkatGame(String tableName) {

		SkatTable table = data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			table.pauseSkatGame();
		}
	}

	/**
	 * Resumes a skat game at a table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void resumeSkatGame(String tableName) {

		SkatTable table = data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			table.resumeSkatGame();
		}
	}

	/**
	 * Checks whether a skat game is waiting
	 * 
	 * @param tableName
	 *            Table name
	 * @return TRUE if the game is waiting
	 */
	public boolean isSkatGameWaiting(String tableName) {

		boolean result = false;

		SkatTable table = data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			result = table.isSkatGameWaiting();
		}

		return result;
	}

	/**
	 * Checks whether a skat series is waiting
	 * 
	 * @param tableName
	 *            Table name
	 * @return TRUE if the series is waiting
	 */
	public boolean isSkatSeriesWaiting(String tableName) {

		boolean result = false;

		SkatTable table = data.getSkatTable(tableName);

		if (table.isSeriesRunning()) {

			result = table.isSkatSeriesWaiting();
		}

		return result;
	}

	/**
	 * Places a skat player on a table
	 * 
	 * @param tableName
	 *            Table ID
	 * @param player
	 *            Skat player
	 * @return TRUE if the placing was successful
	 */
	public synchronized boolean placePlayer(String tableName, JSkatPlayer player) {

		boolean result = false;

		SkatTable table = data.getSkatTable(tableName);

		if (!table.isSeriesRunning()) {

			if (table.getPlayerCount() < table.getMaxPlayerCount()) {

				result = table.placePlayer(player);
			}
		}

		return result;
	}

	/**
	 * Sets the view (for MVC)
	 * 
	 * @param newView
	 *            View
	 */
	public void setView(JSkatView newView) {

		view = newView;
		issControl.setView(view);
	}

	/**
	 * Exits JSkat
	 */
	public void exitJSkat() {

		options.saveJSkatProperties();
		System.exit(0);
	}

	/**
	 * Shows the about message box
	 */
	public void showAboutMessage() {

		view.showAboutMessage();
	}

	/**
	 * Trains the neural networks
	 */
	public void trainNeuralNetworks() {

		view.showTrainingOverview();

		NNTrainer nullTrainer = new NNTrainer();
		nullTrainer.setGameType(GameType.NULL);
		nullTrainer.start();
		NNTrainer grandTrainer = new NNTrainer();
		grandTrainer.setGameType(GameType.GRAND);
		grandTrainer.start();
		NNTrainer clubsTrainer = new NNTrainer();
		clubsTrainer.setGameType(GameType.CLUBS);
		clubsTrainer.start();
		NNTrainer spadesTrainer = new NNTrainer();
		spadesTrainer.setGameType(GameType.SPADES);
		spadesTrainer.start();
		NNTrainer heartsTrainer = new NNTrainer();
		heartsTrainer.setGameType(GameType.HEARTS);
		heartsTrainer.start();
		NNTrainer diamondsTrainer = new NNTrainer();
		diamondsTrainer.setGameType(GameType.DIAMONDS);
		diamondsTrainer.start();
		NNTrainer ramschTrainer = new NNTrainer();
		ramschTrainer.setGameType(GameType.RAMSCH);
		ramschTrainer.start();
	}

	/**
	 * Loads the weigths for the neural networks
	 */
	public void loadNeuralNetworks() {

		SkatNetworks.loadNetworks(System.getProperty("user.home").concat(System.getProperty("file.separator"))
				.concat(".jskat"));
	}

	/**
	 * Resets neural networks
	 */
	public void resetNeuralNetworks() {
		SkatNetworks.resetNeuralNetworks();
	}

	/**
	 * Saves the weigths for the neural networks
	 */
	public void saveNeuralNetworks() {

		SkatNetworks.saveNetworks(System
				.getProperty("user.home").concat(System.getProperty("file.separator")).concat(".jskat")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Shows the help dialog
	 */
	public void showHelp() {

		view.showHelpDialog();
	}

	/**
	 * Shows the license dialog
	 */
	public void showLicense() {

		view.showLicenseDialog();
	}

	/**
	 * Triggers the human player interface to stop waiting
	 * 
	 * @param event
	 *            Action event
	 */
	public void triggerHuman(ActionEvent event) {

		log.debug(event);

		String tableName = data.getActiveTable();
		String command = event.getActionCommand();
		Object source = event.getSource();

		if (isIssTable(tableName)) {

			handleHumanInputForISSTable(tableName, command, source);

		} else {

			data.getHumanPlayer(tableName).actionPerformed(event);
		}
	}

	private void handleHumanInputForISSTable(String tableName, String command, Object source) {

		if (JSkatAction.PASS_BID.toString().equals(command)) {
			// player passed
			issControl.sendPassBidMove(tableName);
		} else if (JSkatAction.MAKE_BID.toString().equals(command)) {
			// player makes bid
			issControl.sendBidMove(tableName);
		} else if (JSkatAction.HOLD_BID.toString().equals(command)) {
			// player hold bid
			issControl.sendHoldBidMove(tableName);
		} else if (JSkatAction.PICK_UP_SKAT.toString().equals(command)) {
			// player wants to pick up the skat
			issControl.sendPickUpSkatMove(tableName);
		} else if (JSkatAction.PLAY_HAND_GAME.toString().equals(command)) {
			// player wants to play a hand game
			// FIXME (jan 02.11.2010) decision is not sent to ISS
		} else if (JSkatAction.DISCARD_CARDS.toString().equals(command)) {

			if (source instanceof CardList) {
				// player discarded cards
				CardList discardSkat = (CardList) source;
				log.debug(discardSkat);

				// FIXME (jan 02.11.2010) Discarded cards are sent with the
				// game announcement to ISS

				// issControl.sendDiscardMove(tableName,
				// discardSkat.get(0), discardSkat.get(1));
			} else {

				log.error("Wrong source for " + command); //$NON-NLS-1$
			}
		} else if (JSkatAction.ANNOUNCE_GAME.toString().equals(command)) {

			if (source instanceof JButton) {
				log.debug("ONLY JBUTTON"); //$NON-NLS-1$
			} else {
				// player did game announcement
				// FIXME (jan 02.11.2010) Discarded cards are sent with the
				// game announcement to ISS
				GameAnnouncement gameAnnouncement = (GameAnnouncement) source;
				issControl.sendGameAnnouncementMove(tableName, gameAnnouncement);
			}
		} else if (JSkatAction.PLAY_CARD.toString().equals(command) && source instanceof Card) {

			Card nextCard = (Card) source;
			issControl.sendCardMove(tableName, nextCard);
		} else {

			log.error("Unknown action event occured: " + command + " from " + source); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private boolean isIssTable(String tableName) {

		return data.isTableJoined(tableName);
	}

	/**
	 * Takes a card from the skat on the active skat table
	 * 
	 * @param e
	 *            Event
	 */
	public void takeCardFromSkat(ActionEvent e) {

		if (!(e.getSource() instanceof Card)) {

			throw new IllegalArgumentException();
		}

		view.takeCardFromSkat(data.getActiveTable(), (Card) e.getSource());
	}

	/**
	 * Put a card into the skat on the active skat table
	 * 
	 * @param e
	 */
	public void putCardIntoSkat(ActionEvent e) {

		if (!(e.getSource() instanceof Card)) {

			throw new IllegalArgumentException();
		}

		view.putCardIntoSkat(data.getActiveTable(), (Card) e.getSource());
	}

	/**
	 * Loads a series
	 */
	public void loadSeries() {
		// TODO saving/loading a skat series (here: load)

	}

	/**
	 * Saves a series
	 * 
	 * @param newName
	 *            TRUE, if a new name should be given to the save file
	 */
	public void saveSeries(boolean newName) {
		// TODO saving/loading a skat series (here: save)

	}

	/**
	 * Gets the controller for playing on the ISS
	 * 
	 * @return ISS controller
	 */
	public IssController getIssController() {

		return issControl;
	}

	/**
	 * Shows the preference dialog
	 */
	public void showPreferences() {

		view.showPreferences();
	}

	/**
	 * Sets the name of the active table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void setActiveTable(String tableName) {

		data.setActiveTable(tableName);
	}

	/**
	 * Sets the login name for ISS
	 * 
	 * @param login
	 *            Login name
	 */
	public void setIssLogin(String login) {

		data.setIssLoginName(login);
	}

	/**
	 * Sends the table seat change signal to ISS
	 */
	public void sendTableSeatChangeSignal() {

		issControl.sendTableSeatChangeSignal(data.getActiveTable());
	}

	/**
	 * Sends the ready to play signal to ISS
	 */
	public void sendReadySignal() {

		issControl.sendReadySignal(data.getActiveTable());
	}

	/**
	 * Sends the talk enabled signal to ISS
	 */
	public void sendTalkEnabledSignal() {

		issControl.sendTalkEnabledSignal(data.getActiveTable());
	}

	/**
	 * Sends the resign signal to ISS
	 */
	public void sendResignSignal() {

		issControl.sendResignSignal(data.getActiveTable());
	}

	/**
	 * Leaves a skat table
	 */
	public void leaveTable() {

		String tableName = data.getActiveTable();

		// FIXME distinguish between ISS and local skat table
		issControl.leaveTable(tableName);
	}

	/**
	 * Updates ISS player information
	 * 
	 * @param playerName
	 *            Player name
	 * @param language
	 *            Language
	 * @param gamesPlayed
	 *            Games played
	 * @param strength
	 *            Playing strength
	 */
	public void updateISSPlayer(String playerName, String language, long gamesPlayed, double strength) {

		data.addAvailableISSPlayer(playerName);
		view.updateISSLobbyPlayerList(playerName, language, gamesPlayed, strength);
	}

	/**
	 * Removes an ISS player
	 * 
	 * @param playerName
	 *            Player name
	 */
	public void removeISSPlayer(String playerName) {

		data.removeAvailableISSPlayer(playerName);
		view.removeFromISSLobbyPlayerList(playerName);
	}

	/**
	 * Opens the ISS homepage in the default browser
	 */
	public void openIssHomepage() {

		openWebPage(getISSHomepageLink());
	}

	private String getISSHomepageLink() {

		String result = "http://www.skatgame.net/iss/"; //$NON-NLS-1$

		SupportedLanguage lang = JSkatOptions.instance().getLanguage();
		switch (lang) {
		case GERMAN:
			result += "index-de.html"; //$NON-NLS-1$
			break;
		case ENGLISH:
			result += "index.html"; //$NON-NLS-1$
			break;
		}

		return result;
	}

	private void openWebPage(String link) {
		try {
			Desktop desktop = java.awt.Desktop.getDesktop();
			URI uri = new URI(link);
			desktop.browse(uri);
		} catch (URISyntaxException except) {
			log.error(except);
		} catch (IOException except) {
			log.error(except);
		}
	}

	/**
	 * Opens the ISS registration form in the default browser
	 */
	public void openIssRegisterPage() {

		openWebPage(getIssRegisterLink());
	}

	private String getIssRegisterLink() {

		String result = "http://skatgame.net:7000/"; //$NON-NLS-1$

		SupportedLanguage lang = JSkatOptions.instance().getLanguage();
		switch (lang) {
		case GERMAN:
			result += "de-register"; //$NON-NLS-1$
			break;
		case ENGLISH:
			result += "en-register"; //$NON-NLS-1$
			break;
		}

		return result;
	}

	/**
	 * Adds training results
	 * 
	 * @param gameType
	 *            Game type
	 * @param episodes
	 *            Number of episodes
	 * @param totalWonGames
	 *            Total number of won games
	 * @param episodeWonGames
	 *            Number of won games in last episode
	 * @param avgDifference
	 *            Average difference
	 */
	public void addTrainingResult(GameType gameType, long episodes, long totalWonGames, long episodeWonGames,
			double avgDifference) {

		view.addTrainingResult(gameType, episodes, totalWonGames, episodeWonGames, avgDifference);
	}

	/**
	 * Shows the welcome dialog
	 */
	public void showWelcomeDialog() {
		if(view!=null) {
			view.showWelcomeDialog();
		}
		else {
			log.warn("no view for welcome message found");
		}
	}
}
