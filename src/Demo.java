import java.io.File;
import java.io.IOException;

public class Demo {
	public static void main(String[] args) {
		File directory = new File (".");
		Road road = new Road();
        Graph graph;
		try {
			graph = road.getWeightsFromFile(directory.getCanonicalPath() + "\\test");
			CockroachNest problem = new CockroachNest(graph, 200,3,2,100);
			CockroachNest problem2 = new CockroachNest(graph);
			long start = System.currentTimeMillis();
			problem.solve();
			//problem2.solve();
			System.out.println("Time spent: " + (System.currentTimeMillis() - start) + " ms.");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
