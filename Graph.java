
public class Graph {

	private int[][] matrix;
	private int vertexCount;

	public Graph(int vertexCount) {
		this.vertexCount = vertexCount;
		this.matrix = new int[vertexCount][vertexCount];
	}

	public boolean addEdge(int source, int dest, int weight) {
		if (source < matrix.length && dest < matrix.length) {
			matrix[source][dest] = weight;
			matrix[dest][source] = weight;
			return true;
		}
		return false;
	}

	public boolean removeEdge(int source, int dest) {
		if (source < matrix.length && dest < matrix.length) {
			matrix[source][dest] = 0;
			matrix[dest][source] = 0;
			return true;
		}
		return false;
	}

	public Object getVertex(int vertex) {
		return null;
	}

	public int[] incident(int vertex) {
		return null;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public void printGraph() {
		for (int i = 0; i < vertexCount; i++) {
			System.out.print("Vertex " + i + " is connected to: ");
			for (int j = 0; j < vertexCount; j++) {
				if (matrix[i][j] > 1) {
					System.out.print(j + " ");
				}
			}
			System.out.println();
		}
	}

}
