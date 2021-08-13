import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class RoadTrip {

	ArrayList<ArrayList<Integer>> routes;
	private DataSource data;
	private Dijkstra dijkstra;

	public RoadTrip(Path roadsData, Path attractionsData) {
		this.routes = new ArrayList<>();
		this.data = new DataSource(290, roadsData, attractionsData);
		data.build();
		this.dijkstra = new Dijkstra(data.getCityMap());
	}

	public static void main(String[] args) {
		
		// Validate command line arguments
		if(args.length != 2) {
			System.out.println("Please add the 2 data files as arguments.");
			System.exit(1);
		}
		RoadTrip trip = null;
		try {
			// Make it so the attractions and roads csv have to be present
			// but can be inputed in any order in args.
			if(args[0].toLowerCase().endsWith("attractions.csv") &&
				args[1].toLowerCase().endsWith("roads.csv")) {
					trip = new RoadTrip(Path.of(args[1]), Path.of(args[0]));
			} else if(args[0].toLowerCase().endsWith("roads.csv") &&
					args[1].toLowerCase().endsWith("attractions.csv")) {
						trip = new RoadTrip(Path.of(args[0]), Path.of(args[1]));
			} else {
				System.out.println("Error: Must pass either attractions.csv or roads.csv in any order.");
				System.exit(1);
			}
		} catch(Exception e) {
			System.out.println("Error: At least one of the arguments is in invalid file path.");
			System.exit(1);
		}
		
		Scanner scanner = new Scanner(System.in);

		// Enter program loop
		while (true) {
			// Get starting and ending cities
			String start = trip.getCityFromUser(scanner);
			String end = trip.getCityFromUser(scanner);

			// Get attractions from user input
			ArrayList<String> attractions = trip.getAttractionsFromUser(scanner);

			// Find the fastest route and print the results
			System.out.println("\n=====ROUTE=====");
			int totalTime = trip.route(start, end, attractions);
			System.out.print("Total time: " + totalTime + " minutes");
			System.out.println((totalTime >= 60) ? " (~ " + totalTime / 60 + " hours)\n" : "\n");
		}
	}

	/**
	 * Get a valid city from the user input
	 */
	private String getCityFromUser(Scanner scanner) {
		// Get starting city
		String startPrompt = "Name of starting city (or EXIT to quit): ";
		System.out.println(startPrompt);
		String city = scanner.nextLine().trim().toLowerCase();

		// Continuously loop until city is not blank or city invalid
		while (city.isBlank() || !data.getCityMap().containsKey(city)) {
			// typing "exit" exits the program
			if (city.equalsIgnoreCase("exit")) {
				scanner.close();
				System.exit(0);
			}
			System.out.println("Please enter a valid city and state code. (e.g. miami fl)");
			System.out.println(startPrompt);
			city = scanner.nextLine();

		}
		return city;
	}

	/**
	 * Get a valid ArrayList of attractions to visit on the route from user input
	 */
	private ArrayList<String> getAttractionsFromUser(Scanner scanner) {
		ArrayList<String> attractions = new ArrayList<>();
		boolean enteringAttractions = true;
		while (enteringAttractions) {
			String attrPrompt = "List an attraction along the way (or ENOUGH to stop listing): ";
			System.out.println(attrPrompt);
			String attr = scanner.nextLine().trim().toLowerCase();
			if (attr.equalsIgnoreCase("enough")) {
				enteringAttractions = false;
			} else {
				// Loop until attraction entered is valid
				while ((attr.isBlank() || !data.getAttractions().containsKey(attr))) {
					System.out.println("Please enter a valid attraction:");
					System.out.println(attrPrompt);
					attr = scanner.nextLine().trim().toLowerCase();
					if (attr.equalsIgnoreCase("enough")) {
						// Typing "enough" breaks out of the attractions loop
						enteringAttractions = false;
						break;

					}
				}
				if (!attr.equalsIgnoreCase("enough")) {
					attractions.add(attr);
				}
			}
		}
		return attractions;
	}

	/**
	 * Find the fastest route from the starting city to the destination city while
	 * visiting all of the given attractions.
	 * 
	 * Returns an int representing the amount of minutes the total route took.
	 */
	public int route(String startCity, String destCity, ArrayList<String> attractions) {
		if (!attractions.isEmpty()) {
			int[] attractionCityIds = convertAttractionsToCityIds(attractions);

			buildPossibleRoutes(attractionCityIds.length, attractionCityIds);

			// Add start and end city to all routes to consider after finding permutations
			// of ordering of attractions
			for (ArrayList<Integer> arr : routes) {
				arr.add(0, data.getCityMap().get(startCity));
				arr.add(data.getCityMap().get(destCity));
			}

			// Find route with the least amount of minutes // convert into separate
			// function?
			int min = Integer.MAX_VALUE;
			int min_index = -1;
			for (int i = 0; i < routes.size(); i++) {
				int elapsed = 0;
				for (int j = 0; j < routes.get(i).size() - 1; j++) {
					int source = routes.get(i).get(j);
					int dest = routes.get(i).get(j + 1);
					elapsed += dijkstra.dijkstra(data.getGraph().getMatrix(), source, dest, false);
				}
				if (elapsed < min) {
					min = elapsed;
					min_index = i;
				}
			}

			ArrayList<Integer> minList = routes.get(min_index);

			// Go through fastest route again and print out the result
			int totalCost = 0;
			for (int i = 0; i < minList.size() - 1; i++) {
				totalCost += dijkstra.dijkstra(data.getGraph().getMatrix(), minList.get(i), minList.get(i + 1), true);
			}
			return totalCost;
		} else {
			// The attractions list is not empty, proceed.
			int source = data.getCityMap().get(startCity);
			int dest = data.getCityMap().get(destCity);
			return dijkstra.dijkstra(data.getGraph().getMatrix(), source, dest, true);
		}
	}

	/**
	 * Converts an ArrayList of attractions and returns an array of city id's that
	 * the attractions are in.
	 */
	private int[] convertAttractionsToCityIds(ArrayList<String> arr) {
		int[] results = new int[arr.size()];
		for (int i = 0; i < results.length; i++) {
			results[i] = data.getCityMap().get(data.getAttractions().get(arr.get(i)));
		}
		return results;
	}

	/**
	 * Build all the possible permutations of the given array.
	 */
	public void buildPossibleRoutes(int n, int[] elements) {
		if (n == 1) {
			storeRoute(elements);
		} else {
			for (int i = 0; i < n - 1; i++) {
				buildPossibleRoutes(n - 1, elements);
				if (n % 2 == 0) {
					swap(elements, i, n - 1);
				} else {
					swap(elements, 0, n - 1);
				}
			}
			buildPossibleRoutes(n - 1, elements);
		}
	}

	/**
	 * Add the route to the master routes list.
	 */
	private void storeRoute(int[] input) {
		ArrayList<Integer> line = new ArrayList<>();
		for (int i = 0; i < input.length; i++) {
			line.add(input[i]);
		}
		routes.add(line);
	}

	private void swap(int[] input, int a, int b) {
		int tmp = input[a];
		input[a] = input[b];
		input[b] = tmp;
	}
}
