package org.jskat.ai.nn.util;

/**
 * Holds information about the topology of a neural network
 */
public class NetworkTopology {

	private int inputSignals;
	private int outputSignals;
	private int hiddenLayers;
	private int[] hiddenNeurons;

	/**
	 * Constructor
	 * 
	 * @param inputs
	 *            Number of input neurons
	 * @param outputs
	 *            Number of output neurons
	 * @param hiddenLayerCount
	 *            Number of hidden layers
	 * @param hiddenNeuronCounts
	 *            Number of hidden neurons per hidden layer
	 */
	public NetworkTopology(int inputs, int outputs, int hiddenLayerCount,
			int[] hiddenNeuronCounts) {

		if (hiddenNeuronCounts.length != hiddenLayerCount) {

			throw new IllegalArgumentException(
					"Number of hidden layers and number of hidden neurons don't correspond."); //$NON-NLS-1$
		}

		this.inputSignals = inputs;
		this.outputSignals = outputs;
		this.hiddenLayers = hiddenLayerCount;
		this.hiddenNeurons = hiddenNeuronCounts;
	}

	/**
	 * Gets the number of input neurons
	 * 
	 * @return Number of input neurons
	 */
	int getInputNeuronCount() {

		return this.inputSignals;
	}

	/**
	 * Gets the number of hidden layers
	 * 
	 * @return Number of hidden layers
	 */
	int getHiddenLayerCount() {

		return this.hiddenLayers;
	}

	/**
	 * Gets the number of hidden neurons in a hidden layer
	 * 
	 * @param layerID
	 *            ID of the hidden layer
	 * @return Number of hidden neurons in the hidden layer
	 */
	int getHiddenNeuronCount(int layerID) {

		return this.hiddenNeurons[layerID];
	}

	/**
	 * Gets the number of output neurons
	 * 
	 * @return Number of output neurons
	 */
	int getOutputNeuronCount() {

		return this.outputSignals;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();

		// result.append("<topo>\n");
		// result.append("\t<numberoflayers>").append(this.hiddenLayers +
		// 2).append("</numberoflayers>\n");
		// result.append("\t<layer id=\"0\" type=\"input\">\n");
		// result.append("\t\t<numberofneurons>").append(getInputNeuronCount()).append("</numberofneurons>\n");
		// result.append("\t</layer>\n");
		// for (int i = 0; i < this.hiddenLayers; i++) {
		//
		// result.append("\t<layer id=\"" + (i + 1) + "\" type=\"hidden\">\n");
		// result.append("\t\t<numberofneurons>").append(getHiddenNeuronCount(i)).append("</numberofneurons>\n");
		// }
		// result.append("\t</layer>\n");
		// result.append("\t<layer id=\"" + (this.hiddenLayers + 1) +
		// "\" type=\"output\">\n");
		// result.append("\t\t<numberofneurons>").append(getOutputNeuronCount()).append("</numberofneurons>\n");
		// result.append("\t</layer>\n");
		// result.append("</topo>\n");

		result.append("input\n"); //$NON-NLS-1$
		result.append(getInputNeuronCount()).append('\n');

		result.append("hidden ").append(this.hiddenLayers).append('\n'); //$NON-NLS-1$
		for (int i = 0; i < this.hiddenLayers; i++) {

			result.append(getHiddenNeuronCount(i)).append('\n');
		}

		result.append("output\n"); //$NON-NLS-1$
		result.append(getOutputNeuronCount()).append('\n');

		return result.toString();
	}
}
