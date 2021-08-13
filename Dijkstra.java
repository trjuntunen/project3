import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 
 * Class to help with our custom version of Dijkstra's algorithm.
 * 
 * @author Teddy Juntunen
 *
 */
class Dijkstra {

	private HashMap<String, Integer> cityMap;
	private final int NO_PARENT = -1;

	public Dijkstra(HashMap<String, Integer> cityMap) {
		this.cityMap = cityMap;
	}

	/**
	 * Run dijsktra's shortest path algorithm on the given matrix given a source and
	 * dest
	 */
	public int dijkstra(int[][] matrix, int source, int dest, boolean shouldPrint) {
		int vertices = matrix[0].length;
		int[] shortestDistances = new int[vertices];
		boolean[] added = new boolean[vertices];

		for (int i = 0; i < vertices; i++) {
			shortestDistances[i] = Integer.MAX_VALUE;
			added[i] = false;
		}

		// Distance of source vertex from itself is always 0
		shortestDistances[source] = 0;

		// Parent array to store shortest path tree
		int[] parents = new int[vertices];

		// The starting vertex does not have a parent
		parents[source] = NO_PARENT;

		// Find shortest path for all vertices
		for (int i = 1; i < vertices; i++) {
			int nearestVertex = -1;
			int shortestDistance = Integer.MAX_VALUE;

			// Pick the minimum distance vertex from the set of vertices not yet checked
			for (int j = 0; j < vertices; j++) {
				if (!added[j] && shortestDistances[j] < shortestDistance) {
					nearestVertex = j;
					shortestDistance = shortestDistances[j];
				}
			}

			// Mark the picked vertex as checked
			added[nearestVertex] = true;
			for (int j = 0; j < vertices; j++) {
				int edgeDistance = matrix[nearestVertex][j];
				if (edgeDistance > 0 && ((shortestDistance + edgeDistance) < shortestDistances[j])) {
					parents[j] = nearestVertex;
					shortestDistances[j] = shortestDistance + edgeDistance;
				}
			}
		}
		if (shouldPrint) {
			// Run the algorithm and print the results
			System.out.println(getCityById(source).toUpperCase() + " -> " + getCityById(dest).toUpperCase());
			return printSolution(source, dest, shortestDistances, parents);
		} else {
			// Run the algorithm and don't print the results
			return getSolutionNoPrint(source, dest, shortestDistances, parents);
		}
	}

	/**
	 * Get a city name from the id
	 */
	private String getCityById(int id) {
		for (Entry<String, Integer> entry : cityMap.entrySet()) {
			if (entry.getValue() == id) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Print the whole path on the route
	 */
	private int printSolution(int source, int dest, int[] distances, int[] parents) {
		if (dest != source) {
			printPath(dest, parents);
		}
		return distances[dest];
	}

	private int getSolutionNoPrint(int source, int dest, int[] distances, int[] parents) {
		return distances[dest];
	}

	// Function to print shortest path from source to currentVertex using parents
	// array
	private void printPath(int currentVertex, int[] parents) {
		if (currentVertex == NO_PARENT) { // base case
			return;
		}
		printPath(parents[currentVertex], parents);
		System.out.println("-> " + getCityById(currentVertex).toUpperCase());
	}

}