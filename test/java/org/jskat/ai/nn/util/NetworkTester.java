package org.jskat.ai.nn.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

// FIXME (jansch 20.06.2011) re-implement it as JUnit-Test

/**
 * Test class for NeuralNetwork
 */
public class NetworkTester {

	private static Log log = LogFactory.getLog(NetworkTester.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("org/jskat/config/log4j.properties")); //$NON-NLS-1$

		// testBooleanFunction();
		testSkat();
	}

	private static void testSkat() {

		List<Double> opponent1 = new ArrayList<Double>();
		List<Double> opponent2 = new ArrayList<Double>();
		List<Double> opponent3 = new ArrayList<Double>();

		opponent1.add(0.5); // A --> first opponent
		opponent1.add(0.5); // K
		opponent1.add(0.5); // Q
		opponent1.add(0.0); // J
		opponent1.add(0.5); // T
		opponent1.add(-1.0); // 9
		opponent1.add(0.0); // 8
		opponent1.add(0.0); // 7
		opponent1.add(0.5); // A --> second opponent
		opponent1.add(0.5); // K
		opponent1.add(0.5); // Q
		opponent1.add(0.0); // J
		opponent1.add(0.5); // T
		opponent1.add(0.0); // 9
		opponent1.add(-1.0); // 8
		opponent1.add(0.0); // 7
		opponent1.add(0.0); // A --> me
		opponent1.add(0.0); // K
		opponent1.add(0.0); // Q
		opponent1.add(0.5); // J
		opponent1.add(0.0); // T
		opponent1.add(0.0); // 9
		opponent1.add(0.0); // 8
		opponent1.add(-1.0); // 7
		opponent2.add(0.5); // A --> first opponent
		opponent2.add(0.5); // K
		opponent2.add(0.5); // Q
		opponent2.add(0.0); // J
		opponent2.add(0.5); // T
		opponent2.add(-1.0); // 9
		opponent2.add(0.0); // 8
		opponent2.add(0.0); // 7
		opponent2.add(0.5); // A --> second opponent
		opponent2.add(0.5); // K
		opponent2.add(0.5); // Q
		opponent2.add(0.0); // J
		opponent2.add(0.5); // T
		opponent2.add(0.0); // 9
		opponent2.add(-1.0); // 8
		opponent2.add(0.0); // 7
		opponent2.add(0.0); // A --> me
		opponent2.add(0.0); // K
		opponent2.add(0.0); // Q
		opponent2.add(-0.5); // J
		opponent2.add(0.0); // T
		opponent2.add(0.0); // 9
		opponent2.add(0.0); // 8
		opponent2.add(0.5); // 7
		opponent3.add(0.5); // A --> first opponent
		opponent3.add(0.5); // K
		opponent3.add(0.5); // Q
		opponent3.add(0.0); // J
		opponent3.add(-0.5); // T
		opponent3.add(-1.0); // 9
		opponent3.add(0.0); // 8
		opponent3.add(0.0); // 7
		opponent3.add(0.5); // A --> second opponent
		opponent3.add(0.5); // K
		opponent3.add(0.5); // Q
		opponent3.add(0.0); // J
		opponent3.add(0.0); // T
		opponent3.add(0.0); // 9
		opponent3.add(-1.0); // 8
		opponent3.add(0.0); // 7
		opponent3.add(0.0); // A --> me
		opponent3.add(0.0); // K
		opponent3.add(0.0); // Q
		opponent3.add(-1.0); // J
		opponent3.add(0.0); // T
		opponent3.add(0.0); // 9
		opponent3.add(0.0); // 8
		opponent3.add(0.5); // 7

		double input[][] = new double[3][opponent1.size()];

		for (int i = 0; i < opponent1.size(); i++) {

			input[0][i] = opponent1.get(i);
			input[1][i] = opponent2.get(i);
			input[2][i] = opponent3.get(i);
		}

		double output[][] = { { 1.0 }, { -1.0 }, { 1.0 } };

		int[] hiddenNeurons = { 2 };
		NetworkTopology topo = new NetworkTopology(input[0].length,
				output[0].length, 1, hiddenNeurons);

		NeuralNetwork net = new NeuralNetwork(topo);

		log.debug(net);

		int goodGuess = 0;
		int i = 0;

		while (goodGuess < input.length) {

			net.adjustWeights(input[i % input.length], output[i % input.length]);

			if (Math.abs(net.getAvgDiff()) < 0.1) {
				goodGuess++;
			} else {
				goodGuess = 0;
			}

			if (i % 1000 == 0) {

				log.debug(i + " iterations " + goodGuess + " good guesses...");
			}
			i++;
		}
		log.debug("Learned pattern after " + i + " iterations.");
		// log.debug(net);
		//
		for (i = 0; i < input.length; i++) {

			double predOutput = net
					.getPredictedOutcome(input[i % input.length]);
			log.debug(input[i % input.length]);
			log.debug(predOutput);
		}

		net.saveNetwork(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "asdf.net"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		log.debug("Re-loading network");

		NeuralNetwork net2 = new NeuralNetwork();
		// net2.loadNetwork("asdf.net");

		goodGuess = 0;
		i = 0;
		while (goodGuess < input.length) {
			net.adjustWeights(input[i % input.length], output[i % input.length]);

			if (Math.abs(net.getAvgDiff()) < 0.1) {
				goodGuess++;
			} else {
				goodGuess = 0;
			}

			if (i % 1000 == 0) {

				log.debug(i + " iterations " + goodGuess + " good guesses...");
			}
			i++;
		}
		log.debug("Learned pattern after " + i + " iterations.");

		for (i = 0; i < input.length; i++) {

			double predOutput = net.getPredictedOutcome(input[i]);
			log.debug(input[i]);
			log.debug(predOutput);
		}
	}

}
