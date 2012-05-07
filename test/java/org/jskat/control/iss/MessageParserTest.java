/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.control.iss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.MoveType;
import org.jskat.data.iss.PlayerStatus;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the parsing of the ISS messages
 */
public class MessageParserTest extends AbstractJSkatTest {

	/**
	 * Test the parsing of the game summary
	 */
	@Test
	public void testParseGameSummary() {

		String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[24072]ID[541932]DT[2010-12-06/18:59:24/UTC]P0[zoot]P1[kermit]P2[foo]R0[]R1[]R2[0.0]MV[w HA.SK.SJ.SA.CQ.S8.C9.H7.H9.DQ.CJ.S9.DJ.S7.D9.SQ.C8.HQ.DK.CA.D8.D7.DT.CT.ST.C7.HK.DA.HT.HJ.H8.CK 1 p 2 18 0 p 2 s w H8.CK 2 D.ST.H8 0 SA 1 S7 2 DA 2 HJ 0 SJ 1 D9 0 DQ 1 DJ 2 D7 1 CJ 2 D8 0 CQ 1 CA 2 C7 0 C9 1 S9 2 DT 0 S8 2 CT 0 H7 1 C8 2 CK 0 H9 1 HQ 2 HK 0 HA 1 SQ 0 SK 1 DK 2 HT ]R[d:2 loss v:-54 m:-2 bidok p:59 t:4 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:0] ;)"; //$NON-NLS-1$

		SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

		assertEquals("zoot", gameData.getPlayerName(Player.FOREHAND)); //$NON-NLS-1$
		assertEquals("kermit", gameData.getPlayerName(Player.MIDDLEHAND)); //$NON-NLS-1$
		assertEquals("foo", gameData.getPlayerName(Player.REARHAND)); //$NON-NLS-1$

		assertEquals(0, gameData.getPlayerBid(Player.FOREHAND));
		assertEquals(0, gameData.getPlayerBid(Player.MIDDLEHAND));
		assertEquals(18, gameData.getPlayerBid(Player.REARHAND));
		assertEquals(18, gameData.getBidValue());
		assertEquals(Player.REARHAND, gameData.getDeclarer());

		assertTrue(gameData.getDealtSkat().contains(Card.H8));
		assertTrue(gameData.getDealtSkat().contains(Card.CK));

		assertEquals(GameType.DIAMONDS, gameData.getGameType());
		assertFalse(gameData.isHand());
		assertFalse(gameData.isOuvert());
		assertFalse(gameData.isSchneider());
		assertFalse(gameData.isSchneiderAnnounced());
		assertFalse(gameData.isSchwarz());
		assertFalse(gameData.isSchwarzAnnounced());

		assertEquals(10, gameData.getTricks().size());

		List<Trick> tricks = gameData.getTricks();

		checkTrick(tricks.get(0), Player.FOREHAND, Card.SA, Card.S7, Card.DA, Player.REARHAND);
		checkTrick(tricks.get(1), Player.REARHAND, Card.HJ, Card.SJ, Card.D9, Player.FOREHAND);
		checkTrick(tricks.get(2), Player.FOREHAND, Card.DQ, Card.DJ, Card.D7, Player.MIDDLEHAND);
		checkTrick(tricks.get(3), Player.MIDDLEHAND, Card.CJ, Card.D8, Card.CQ, Player.MIDDLEHAND);
		checkTrick(tricks.get(4), Player.MIDDLEHAND, Card.CA, Card.C7, Card.C9, Player.MIDDLEHAND);
		checkTrick(tricks.get(5), Player.MIDDLEHAND, Card.S9, Card.DT, Card.S8, Player.REARHAND);
		checkTrick(tricks.get(6), Player.REARHAND, Card.CT, Card.H7, Card.C8, Player.REARHAND);
		checkTrick(tricks.get(7), Player.REARHAND, Card.CK, Card.H9, Card.HQ, Player.REARHAND);
		checkTrick(tricks.get(8), Player.REARHAND, Card.HK, Card.HA, Card.SQ, Player.FOREHAND);
		checkTrick(tricks.get(9), Player.FOREHAND, Card.SK, Card.DK, Card.HT, Player.MIDDLEHAND);

		assertFalse(gameData.isGameWon());
		assertEquals(-54, gameData.getResult().getGameValue());
		assertEquals(59, gameData.getDeclarerScore());
		assertEquals(61, gameData.getOpponentScore());
		assertFalse(gameData.isSchneider());
		assertFalse(gameData.isSchwarz());
		assertFalse(gameData.isOverBidded());
	}

	private static void checkTrick(final Trick trick, final Player trickForeHand, final Card firstCard,
			final Card secondCard, final Card thirdCard, final Player trickWinner) {
		int trickNo = trick.getTrickNumberInGame();
		assertEquals("Wrong trick fore hand for trick " + trickNo, trickForeHand, trick.getForeHand()); //$NON-NLS-1$
		assertEquals("Wrong first card for trick " + trickNo, firstCard, trick.getFirstCard()); //$NON-NLS-1$
		assertEquals("Wrong second card for trick " + trickNo, secondCard, trick.getSecondCard()); //$NON-NLS-1$
		assertEquals("Wrong third card for trick " + trickNo, thirdCard, trick.getThirdCard()); //$NON-NLS-1$
		assertEquals("Wrong trick winner for trick " + trickNo, trickWinner, trick.getTrickWinner()); //$NON-NLS-1$
	}

	/**
	 * Test the parsing of the game summary
	 */
	@Test
	public void testParseGameSummary002() {

		String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[29859]ID[684159]DT[2011-04-05/20:35:55/UTC]P0[foo]P1[xskat:2]P2[xskat]R0[0.0]R1[]R2[]MV[w SQ.DK.ST.S7.CT.HK.S9.SK.H7.C7.DT.CA.CQ.CK.DJ.DA.H8.SA.D7.C8.SJ.HA.CJ.S8.C9.DQ.HJ.HQ.D9.D8.HT.H9 1 18 0 y 1 20 0 y 1 22 0 y 1 23 0 y 1 24 0 y 1 p 2 27 0 p 2 s w HT.H9 2 G.S8.C9 0 DK 1 DA 2 D8 1 DT 2 D9 0 ST 1 SA 2 CJ 0 S7 2 HJ 0 CT 1 DJ 2 DQ 0 C7 1 D7 2 HA 0 H7 1 H8 2 HT 0 HK 1 C8 2 HQ 0 S9 1 CQ 2 H9 0 SQ 1 CK 2 SJ 0 SK 1 CA ]R[d:2 win v:96 m:3 bidok p:85 t:8 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:0] ;)"; //$NON-NLS-1$

		SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

		assertEquals("foo", gameData.getPlayerName(Player.FOREHAND)); //$NON-NLS-1$
		assertEquals("xskat:2", gameData.getPlayerName(Player.MIDDLEHAND)); //$NON-NLS-1$
		assertEquals("xskat", gameData.getPlayerName(Player.REARHAND)); //$NON-NLS-1$

		assertEquals(24, gameData.getPlayerBid(Player.FOREHAND));
		assertEquals(24, gameData.getPlayerBid(Player.MIDDLEHAND));
		assertEquals(27, gameData.getPlayerBid(Player.REARHAND));
		assertEquals(27, gameData.getBidValue());
		assertEquals(Player.REARHAND, gameData.getDeclarer());

		assertTrue(gameData.getDealtSkat().contains(Card.HT));
		assertTrue(gameData.getDealtSkat().contains(Card.H9));

		assertEquals(GameType.GRAND, gameData.getGameType());
		assertFalse(gameData.isHand());
		assertFalse(gameData.isOuvert());
		assertFalse(gameData.isSchneider());
		assertFalse(gameData.isSchneiderAnnounced());
		assertFalse(gameData.isSchwarz());
		assertFalse(gameData.isSchwarzAnnounced());

		assertEquals(10, gameData.getTricks().size());

		List<Trick> tricks = gameData.getTricks();

		checkTrick(tricks.get(0), Player.FOREHAND, Card.DK, Card.DA, Card.D8, Player.MIDDLEHAND);
		checkTrick(tricks.get(1), Player.MIDDLEHAND, Card.DT, Card.D9, Card.ST, Player.MIDDLEHAND);
		checkTrick(tricks.get(2), Player.MIDDLEHAND, Card.SA, Card.CJ, Card.S7, Player.REARHAND);
		checkTrick(tricks.get(3), Player.REARHAND, Card.HJ, Card.CT, Card.DJ, Player.REARHAND);
		checkTrick(tricks.get(4), Player.REARHAND, Card.DQ, Card.C7, Card.D7, Player.REARHAND);
		checkTrick(tricks.get(5), Player.REARHAND, Card.HA, Card.H7, Card.H8, Player.REARHAND);
		checkTrick(tricks.get(6), Player.REARHAND, Card.HT, Card.HK, Card.C8, Player.REARHAND);
		checkTrick(tricks.get(7), Player.REARHAND, Card.HQ, Card.S9, Card.CQ, Player.REARHAND);
		checkTrick(tricks.get(8), Player.REARHAND, Card.H9, Card.SQ, Card.CK, Player.REARHAND);
		checkTrick(tricks.get(9), Player.REARHAND, Card.SJ, Card.SK, Card.CA, Player.REARHAND);

		assertTrue(gameData.isGameWon());
		assertEquals(96, gameData.getResult().getGameValue());
		assertEquals(85, gameData.getDeclarerScore());
		assertEquals(35, gameData.getOpponentScore());
		assertFalse(gameData.isSchneider());
		assertFalse(gameData.isSchwarz());
		assertFalse(gameData.isOverBidded());
	}

	/**
	 * Test the parsing of the game summary<br>
	 * Passed game
	 */
	@Test
	public void testParseGameSummary_PassedGame() {

		String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[32407]ID[756788]DT[2011-05-28/08:46:19/UTC]P0[xskat]P1[bonsai]P2[bernie]R0[]R1[0.0]R2[]MV[w C8.DQ.DJ.HK.S9.SK.SQ.HQ.CK.D9.S8.DT.SJ.C9.CQ.SA.DK.HT.D7.H7.ST.HJ.C7.H8.S7.DA.CJ.CT.D8.H9.CA.HA 1 p 2 p 0 p ]R[passed] ;)"; //$NON-NLS-1$

		SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

		assertTrue(gameData.isGamePassed());
		assertEquals(0, gameData.getResult().getGameValue());

		assertEquals("xskat", gameData.getPlayerName(Player.FOREHAND)); //$NON-NLS-1$
		assertEquals("bonsai", gameData.getPlayerName(Player.MIDDLEHAND)); //$NON-NLS-1$
		assertEquals("bernie", gameData.getPlayerName(Player.REARHAND)); //$NON-NLS-1$

		assertEquals(0, gameData.getPlayerBid(Player.FOREHAND));
		assertEquals(0, gameData.getPlayerBid(Player.MIDDLEHAND));
		assertEquals(0, gameData.getPlayerBid(Player.REARHAND));
		assertEquals(-1, gameData.getBidValue());
		assertEquals(null, gameData.getDeclarer());

		assertTrue(gameData.getDealtSkat().contains(Card.CA));
		assertTrue(gameData.getDealtSkat().contains(Card.HA));

		assertEquals(GameType.PASSED_IN, gameData.getGameType());
		assertFalse(gameData.isHand());
		assertFalse(gameData.isOuvert());
		assertFalse(gameData.isSchneider());
		assertFalse(gameData.isSchneiderAnnounced());
		assertFalse(gameData.isSchwarz());
		assertFalse(gameData.isSchwarzAnnounced());

		assertEquals(0, gameData.getTricks().size());

		assertFalse(gameData.isGameWon());
		assertEquals(0, gameData.getResult().getGameValue());
		assertEquals(0, gameData.getDeclarerScore());
		assertEquals(0, gameData.getOpponentScore());
		assertFalse(gameData.isSchneider());
		assertFalse(gameData.isSchwarz());
		assertFalse(gameData.isOverBidded());
	}

	/**
	 * Test the parsing of the game summary<br>
	 * Declarer shows cards and plays last card directly afterwards
	 */
	@Test
	public void testParseGameSummary_ShowingCards() {

		String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[43795]ID[1039093]DT[2012-01-17/00:07:25/UTC]P0[SkatCLE]P1[SkatKCT]P2[xskat]R0[0.0]R1[0.0]R2[]MV[w DA.S7.DK.CA.D9.CQ.CK.H9.S8.C7.HJ.DT.HA.CT.S9.C9.ST.H8.D7.DJ.HQ.DQ.SK.HK.SQ.H7.C8.D8.SJ.HT.SA.CJ 1 18 0 p 2 p 1 s w SA.CJ 1 G.CT.DT 0 CA 1 C9 2 C8 0 CK 1 D7 2 HT 0 CQ 1 DJ 2 SJ 2 H7 0 H9 1 H8 0 S8 1 S9 2 SK 2 D8 0 DA 1 SC 1 HJ 2 RE 0 RE ]R[d:1 win v:48 m:1 bidok p:84 t:5 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:1] ;)"; //$NON-NLS-1$
		SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

		assertEquals(GameType.GRAND, gameData.getGameType());
	}

	/**
	 * Test the parsing of the game summary<br>
	 * 
	 */
	@Test
	public void testParseGameSummary_PlayerLeft() {

		String gameSummary = "(;GM[Skat]PC[Internet Skat Server]SE[146]ID[30]DT[2007-11-04/15:39:49/UTC]P0[bonsai]P1[bar]P2[foo]R0[0.0]R1[0.0]R2[null]MV[w SJ.D9.CA.DJ.SA.CJ.C8.SQ.HT.H8.ST.S8.DA.D7.S9.D8.C7.H7.DT.S7.CT.HJ.C9.SK.H9.HA.HK.DQ.CK.HQ.DK.CQ 1 18 0 p w LE.2 ]R[d:-1 penalty v:0 m:0 bidok p:0 t:0 s:0 z:0 p0:0 p1:0 p2:1 l:2 to:-1] ;)"; //$NON-NLS-1$
		SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);
	}

	/**
	 * Test the parsing of the game summary<br>
	 * 
	 */
	@Test
	@Ignore
	public void testParseGameSummary_Fail() {

		String gameSummary = "(;GM[Skat]PC[Internet Skat Server]SE[2171]ID[18358]DT[2008-05-25/17:57:24/UTC]P0[kermit2]P1[kermit1]P2[mic]R0[0.0]R1[null]R2[0.0]MV[w C9.D7.HT.HA.SA.HQ.SJ.S7.C8.HJ.H7.CQ.DQ.D9.H9.S8.H8.CT.HK.SQ.CA.CK.SK.CJ.D8.DT.DK.C7.DJ.S9.ST.DA 1 18 0 p 2 20 1 p 2 s w ST.DA 2 G.DT.ST 0 ?? w LE.1 ]R[d:2 win v:96 m:1 bidok p:120 t:10 s:1 z:1 p0:0 p1:0 p2:0 l:1 to:-1] ;)";
		SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);
	}

	/**
	 * Test the leaving of a player
	 */
	@Test
	public void testParseTableUpdatePlayerLeft() {

		String tableUpdate = "table .4 foo state 3 foo . . . foo . 0 0 0 0 0 0 1 0 xskat $ 2 1 83 157 0 0 1 0 xskat:2 $ 0 0 0 0 0 0 1 0 . . 0 0 0 0 0 0 0 0 false 0"; //$NON-NLS-1$

		StringTokenizer token = new StringTokenizer(tableUpdate);
		token.nextToken(); // table
		token.nextToken(); // .4
		String creator = token.nextToken(); // foo
		token.nextToken(); // state
		List<String> detailParams = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			detailParams.add(token.nextToken());
		}

		TablePanelStatus status = MessageParser.getTableStatus(creator, detailParams);

		assertEquals(3, status.getMaxPlayers());
		assertEquals(3, status.getPlayerInformations().size());

		PlayerStatus playerStatus = status.getPlayerInformation("xskat"); //$NON-NLS-1$
		assertNotNull(playerStatus);
		assertEquals(2, playerStatus.getGamesPlayed());
		assertEquals(1, playerStatus.getGamesWon());
		assertEquals(83, playerStatus.getLastGameResult());
		assertEquals(157, playerStatus.getTotalPoints());
		assertTrue(playerStatus.isPlayerLeft());
		assertFalse(playerStatus.isReadyToPlay());
		assertTrue(playerStatus.isTalkEnabled());
	}

	/**
	 * Tests the resigning of a player
	 */
	@Test
	public void testParseTableUpdatePlayerResign() {

		String playerResign = "table .4 foo play 1 RE 124.1 173.9 177.8"; //$NON-NLS-1$

		StringTokenizer token = new StringTokenizer(playerResign);
		token.nextToken(); // table
		token.nextToken(); // .4
		String creator = token.nextToken(); // foo
		token.nextToken(); // play
		List<String> detailParams = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			detailParams.add(token.nextToken());
		}

		MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

		assertEquals(MoveType.RESIGN, moveInfo.getType());
	}

	/**
	 * Tests showing cards
	 */
	@Test
	public void testParseTableUpdatePlayerShowCards() {

		String playerResign = "table .3 foo play 2 SC.HT.HA.SJ.SQ.SK.CJ 164.1 177.0 156.4"; //$NON-NLS-1$

		StringTokenizer token = new StringTokenizer(playerResign);
		token.nextToken(); // table
		token.nextToken(); // .3
		String creator = token.nextToken(); // foo
		token.nextToken(); // play
		List<String> detailParams = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			detailParams.add(token.nextToken());
		}

		MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

		assertEquals(MoveType.SHOW_CARDS, moveInfo.getType());
		CardList ouvertCards = moveInfo.getOuvertCards();
		assertEquals(6, ouvertCards.size());
		assertTrue(ouvertCards.contains(Card.HT));
		assertTrue(ouvertCards.contains(Card.HA));
		assertTrue(ouvertCards.contains(Card.SJ));
		assertTrue(ouvertCards.contains(Card.SQ));
		assertTrue(ouvertCards.contains(Card.SK));
		assertTrue(ouvertCards.contains(Card.CJ));
	}

	/**
	 * Tests the announcing of an ouvert game
	 */
	@Test
	public void testParseTableUpdateOuvertGame() {

		String ouvertGame = "table .1 foo play 2 SO.D7.DK.HJ.HQ.HK.S7.S9.ST.SK.C7 180.0 174.2 160.9"; //$NON-NLS-1$

		StringTokenizer token = new StringTokenizer(ouvertGame);
		token.nextToken(); // table
		token.nextToken(); // .1
		String creator = token.nextToken(); // foo
		token.nextToken(); // play
		List<String> detailParams = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			detailParams.add(token.nextToken());
		}

		MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

		assertEquals(MoveType.GAME_ANNOUNCEMENT, moveInfo.getType());

		GameAnnouncement announcement = moveInfo.getGameAnnouncement();
		assertEquals(GameType.SPADES, announcement.getGameType());
		assertTrue(announcement.isOuvert());

		CardList ouvertCards = moveInfo.getOuvertCards();
		assertEquals(10, ouvertCards.size());
		assertTrue(ouvertCards.contains(Card.D7));
		assertTrue(ouvertCards.contains(Card.DK));
		assertTrue(ouvertCards.contains(Card.HJ));
		assertTrue(ouvertCards.contains(Card.HQ));
		assertTrue(ouvertCards.contains(Card.HK));
		assertTrue(ouvertCards.contains(Card.S7));
		assertTrue(ouvertCards.contains(Card.S9));
		assertTrue(ouvertCards.contains(Card.ST));
		assertTrue(ouvertCards.contains(Card.SK));
		assertTrue(ouvertCards.contains(Card.C7));
	}
}
