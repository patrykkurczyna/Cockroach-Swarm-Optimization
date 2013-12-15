import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CockroachNest {
	private Graph   graph;
	private int[][] currentSolution;
	private int     numOfCockroaches = 200;
	private int     numOfPoints;
	private int     numOfIterations = 100;
	private int     visibilityRange = 3;
	private int     stepLength = 2;
	private int[]   globalMinimum;
	private int     minIteration = 0;
	private int     currentIteration;
	private Random rand = new Random();

	private List<Integer> base = new ArrayList<Integer>();

	public CockroachNest(Graph graph, int numOfCockroaches, int visibilityRange, int stepLength, int numOfIterations) {
		this(graph);
		this.numOfCockroaches = numOfCockroaches;
		this.numOfIterations = numOfIterations;
		this.stepLength = stepLength;
		this.visibilityRange = visibilityRange;
	
	}
	
	public CockroachNest(Graph graph){
		this.graph = graph;
		this.numOfPoints = graph.getAmount();
		this.currentSolution = new int[this.numOfCockroaches][this.numOfPoints];

		for (int i = 0; i < this.numOfPoints; i++)
			this.base.add(Integer.valueOf(i));
		// default values of parameters
	}

	public void setValues(int numOfCockroaches, int visibilityRange, int stepLength, int numOfIterations) {
		this.numOfCockroaches = numOfCockroaches;
		this.numOfIterations = numOfIterations;
		this.stepLength = stepLength;
		this.visibilityRange = visibilityRange;
	}
	
	// generates random permutation of points 
	private int[] getRandomPermutation() {
		int[] res = new int[this.numOfPoints];
		List<Integer> toShuffle = new ArrayList<Integer>();
		toShuffle.clear();
		toShuffle.addAll(this.base);
		Collections.shuffle(toShuffle, this.rand);
		int i = 0;
		for (Integer e : toShuffle) {
			res[(i++)] = e.intValue();
		}
		return res;
	}
	
	// generate random permutation of points for each cockroach
	private void generateNest() {
		for (int i = 0; i < this.numOfCockroaches; i++)
			this.currentSolution[i] = getRandomPermutation();
	}
	
	// difference between two solutions
	private int differences(int[] solution1, int[] solution2) {
		int sum = 0;
		for (int j = 0; j < this.numOfPoints; j++) {
			if (solution1[j] != solution2[j]) {
				sum++;
			}
		}
		return sum;
	}

	private boolean isVisible(int ci, int cj) {
		int[] iSolution = Arrays.copyOf(this.currentSolution[ci], this.numOfPoints);

		for (int i = 0; i < this.visibilityRange; i++) {
			if (differences(iSolution, this.currentSolution[cj]) > (this.visibilityRange - i) * 2) {
				return false;
			}
			if (!makeStep(iSolution, this.currentSolution[cj])) {
				return true;
			}
		}

		return iSolution.equals(this.currentSolution[cj]);
	}

	private boolean makeStep(int[] iSolution, int[] jSolution) {
		boolean different = false;
		for (int j = 0; j < this.numOfPoints; j++) {
			if (iSolution[j] != jSolution[j]) {
				different = true;
				for (int k = j + 1; k < this.numOfPoints; k++) {
					if (iSolution[k] == jSolution[k]) {
						iSolution[k] = iSolution[j];
						iSolution[j] = jSolution[k];
					}
				}
			}
		}
		return different;
	}

	private void makeSteps(int ci, int[] solution) {
		int curr_steps = this.rand.nextInt(this.stepLength + 1);
		for (int i = 0; i < curr_steps; i++) {
			if (!makeStep(this.currentSolution[ci], solution))
				break;
			if (this.graph.getSolution(this.currentSolution[ci]) < this.graph.getSolution(this.globalMinimum)) {
				this.globalMinimum = Arrays.copyOf(this.currentSolution[i], this.numOfPoints);
				this.minIteration = this.currentIteration;
			}
		}
	}

	private void swarm() {
		// values of solutions
		double[] values = new double[this.numOfCockroaches];
		for (int i = 0; i < this.numOfCockroaches; i++) {
			values[i] = this.graph.getSolution(this.currentSolution[i]);
		}

		for (int i = 0; i < this.numOfCockroaches; i++) {
			double min = -1.0;
			int minCockroach = -1;
			for (int j = 0; j < this.numOfCockroaches; j++) {
				if ((isVisible(i, j)) && ((values[j] < min) || (min == -1))) {
					min = values[j];
					minCockroach = j;
				}

			}
			// making steps to the best visible solution
			if (i == minCockroach) {
				makeSteps(i, this.globalMinimum);
			} else {
				makeSteps(i, this.currentSolution[minCockroach]);
			}

			values[i] = this.graph.getSolution(this.currentSolution[i]);

			if (values[i] < this.graph.getSolution(this.globalMinimum)) {
				this.globalMinimum = Arrays.copyOf(this.currentSolution[i], this.numOfPoints);
				this.minIteration = this.currentIteration;
			}
		}
	}

	private void disperse() {
		for (int i = 0; i < this.numOfCockroaches; i++) {
			int[] randomSolution = getRandomPermutation();
			int num = this.stepLength + 2;

			for (int j = 0; j < num; j++) {
				if (!makeStep(this.currentSolution[i], randomSolution)) {
					break;
				}

				if (this.graph.getSolution(this.currentSolution[i]) < this.graph.getSolution(this.globalMinimum)) {
					this.globalMinimum = Arrays.copyOf(this.currentSolution[i], this.numOfPoints);
					this.minIteration = this.currentIteration;
				}
			}
		}
	}

	private void beRuthless() {
		int k = this.rand.nextInt(this.numOfCockroaches);

		for (int i = 0; i < this.numOfPoints; i++)
			this.currentSolution[k][i] = this.globalMinimum[i];
	}

	private void doIteration() {
		swarm();
		disperse();
		beRuthless();
	}

	public void solve() {
		generateNest();

		this.globalMinimum = Arrays.copyOf(this.currentSolution[0],this.numOfPoints);
		for (int i = 0; i < this.numOfCockroaches; i++) {
			if (this.graph.getSolution(this.currentSolution[i]) < this.graph.getSolution(this.globalMinimum)) {
				this.globalMinimum = Arrays.copyOf(this.currentSolution[i],this.numOfPoints);
			}

		}
		int minimal = 0;
		for (this.currentIteration = 0; this.currentIteration < this.numOfIterations; this.currentIteration += 1) {
			doIteration();
			if (this.minIteration != minimal){
				System.out.print(this.currentIteration + " Minimalna: "+ this.minIteration + " Rozwi¹zanie: "+ this.graph.getSolution(this.globalMinimum));
				System.out.print(" -> ");
				for (int i = 0; i < this.globalMinimum.length; i++){
					System.out.print(this.globalMinimum[i]+ " ");
				}
				System.out.println();
				minimal = this.minIteration;
				
			}
		}
		
		
		//return new Result(this.globalMinimum, this.minIteration);
	}

	
}
