import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * 
 * Class to consolidate all of the data used throughout the program.
 *
 */
public class DataSource {

	private Graph graph;
	private HashMap<String, String> attractions;
	private HashMap<String, Integer> cityMap;
	private Path roadsData;
	private Path attractionsData;

	public DataSource(int vertexCount, Path roadsData, Path attractionsData) {
		this.graph = new Graph(vertexCount);
		this.cityMap = new HashMap<>();
		this.roadsData = roadsData;
		this.attractionsData = attractionsData;
	}

	public void build() {
		buildRoadGraph();
		buildAttractions();
	}

	private void buildRoadGraph() {
		try {
			String dataPath = roadsData.toAbsolutePath().toString();
			int lineCount = 290;
			this.graph = new Graph(lineCount);
			FileReader fr = new FileReader(dataPath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				int source = getId(values[0]);
				int dest = getId(values[1]);
				int mins = Integer.valueOf(values[3]);
				graph.addEdge(source, dest, mins);
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Error building route.");
		}
	}

	private void buildAttractions() {
		try {
			this.attractions = new HashMap<>();
			String dataPath = attractionsData.toAbsolutePath().toString();
			FileReader fr = new FileReader(dataPath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			br.readLine(); // skip first line in attractions data file (headers)
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				String attraction = values[0].trim().toLowerCase();
				String city = values[1].trim().toLowerCase();
				attractions.put(attraction, city);
			}
			System.out.println(attractions);
			br.close();
		} catch (Exception e) {
			System.out.println("Error: At least one of data files given is in invalid path.");
			System.exit(1);
		}
	}

	private int getId(String city) {
		city = city.trim().toLowerCase();
		if (!cityMap.containsKey(city)) {
			cityMap.put(city, cityMap.size());
		}
		return cityMap.get(city);
	}

	public HashMap<String, Integer> getCityMap() {
		return cityMap;
	}

	public Graph getGraph() {
		return graph;
	}

	public HashMap<String, String> getAttractions() {
		return attractions;
	}

}
