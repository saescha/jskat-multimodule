/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.mjl;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.CardList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus J. Luzius <br>
 *         created: 24.01.2011 18:20:09
 *
 */
public abstract class AbstractCardPlayer implements CardPlayer {

	private static final Logger log = LoggerFactory.getLogger(AbstractCardPlayer.class);

	protected CardList cards = null;

	protected AbstractCardPlayer(final CardList cards) {
		this.cards = cards;
	}

	@Override
	public void startGame(final ImmutablePlayerKnowledge knowledge) {
		log.debug("Starting game...");
		cards.sort(knowledge.getGameAnnouncement().getGameType());
	}
}
