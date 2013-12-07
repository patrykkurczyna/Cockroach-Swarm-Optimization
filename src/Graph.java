public class Graph {
	private double[][] matrix;
	private int amount;

	Graph(int n, double[][] tab) {
		this.matrix = tab;
		this.amount = n;
	}
	
	//returns sum of costs of certain solution
	public double getSolution(int[] vector) {
		double suma = 0;
		for (int i = 0; i < this.amount - 1; i++) {
			suma += this.matrix[vector[i]][vector[(i + 1)]];
		}
		suma += this.matrix[vector[(this.amount - 1)]][vector[0]];
		return suma;
	}

	public int getAmount() {
		return this.amount;
	}

	public void print() {
		for (int i = 0; i < this.amount; i++) {
			for (int j = 0; j < this.amount; j++) {
				System.out.print(this.matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
}
