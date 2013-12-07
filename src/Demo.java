public class Demo {
	public static void main(String[] args) {
		Road road = new Road();
        Graph graph = road.getWeightsFromFile("D:\\Kody\\java2\\CSO\\test");
		CockroachNest problem = new CockroachNest(graph, 200,3,2,100);
		CockroachNest problem2 = new CockroachNest(graph);
		long start = System.currentTimeMillis();
		problem.solve();
		problem2.solve();
		System.out.println("Time spent: " + (System.currentTimeMillis() - start) + " ms.");
	}
}
