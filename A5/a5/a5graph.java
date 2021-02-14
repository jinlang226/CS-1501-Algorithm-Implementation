/*************************************************************************
 *  CS 1501 Summer 2020                                                          
 *                                                                               
 *  Author: Jinlang Wang                                                        
 *  Email: jiw159@pitt.edu  
 *  Describtion: Assignment 5, graph application
 * 
 *************************************************************************/


import java.io.*;
import java.util.*;


/**
 * The class to create an airline info system that allows user to give input
 */
public class a5graph 
{
	private static String cities2 [];
	private static ArrayList<String> citiesSorted = null;
	private static LinkedHashMap<String, Integer> cities;
	private static Graph graph, MST;
	private static int minPath;
	static String city1;
	static String city2;

	/**
	* 
	* @param args this projram does not use any arguments. Instead, iteration is dealed with the prompts
	* @throws Exception the user my type the wrong input. For example, the file name is incorrect
	*/
	public static void main(String args[]) throws Exception 
	{
		File fileName;
		int choice;
		Scanner input = new Scanner(System.in);
		System.out.println("Welcome to the Assignment 5!");
		System.out.print("Please enter the file name: ");
		fileName = new File(input.nextLine());
		graph  = readFile(fileName);
		do 
		{
			System.out.println("===================================================================");
			System.out.println("Selections: ");
			System.out.println("1. Show the entire list of direct routes, distances and prices;");
			System.out.println("2. Display a minimum spanning tree for the service routes based on distances;");
			System.out.println("3. Search the shortest path;");
			System.out.println("4. Given a dollar amount, output all trips whose cost is less than or equal to that amount;");
			System.out.println("5. Add a new route to the schedule;");
			System.out.println("6. Remove a route from the schedule;");
			System.out.println("7. Quit the program, rotes should be saved back to the file.");
			System.out.println("Your choice: ");
			choice = input.nextInt();
			System.out.println("===================================================================");
			switch (choice) {
				case 1: 
					graph.choice1();
					break;
				case 2: 
					choice2(graph);
					break;
				case 3: 
						selection3();
		                break;
				case 4:
					System.out.print("Enter Max Dollar Amount: ");
			        double amount = input.nextFloat();
					LinkedList<Integer> temp = new LinkedList();
					for(int i = 0; i < graph.adjacency.length; i++) 
					{
						choice4(i, amount, 0, temp);
					}
					System.out.println();
					break;
				case 5:
					 System.out.println("Adding a new route");
					 System.out.print("Enter City 1: ");
				     city1 = input.next();
					 if(!graph.contains(city1)) 
					 {
				    	 System.out.println(city1 + " not on route");
				    	 break;
				     }
				     System.out.print("Enter City 2: ");
				     city2 = input.next();
					 if(!graph.contains(city2)) 
					 {
				    	 System.out.println(city2 + " not on route");
				    	 break;
				     }
				     System.out.print("Flight Distance is: ");
				     int distance = input.nextInt();
				     System.out.print("price is: ");
				     double price = input.nextInt();
				     choice5(city1, city2, distance, price);
				     break;
				case 6:
					System.out.println("deleting a new route");
					System.out.print("Enter City 1: ");
					city1 = input.next();
					 if(!graph.contains(city1)) 
					 {
				    	 System.out.println(city1 + " not on route");
				    	 break;
				     }
				     System.out.print("Enter City 2: ");
				     city2 = input.next();
					 if(!graph.contains(city2)) 
					 {
				    	 System.out.println(city2 + " not on route");
				    	 break;
				     }
				     choice6(city1, city2);
			}
		
		} while(choice != 7);
		System.out.println("exit this program");
	}
	
	/**
	 * The main menu for the user to type the input
	 */
	public static void selection3() 
	{
		System.out.print("Enter Initial City: ");
		Scanner input = new Scanner(System.in);
        String city1 = input.next();
		if(!graph.contains(city1)) 
		{
        	System.out.println(city1 + " is not on route");
            return;
        } 
        int c1 = cities.get(city1);
        System.out.print("Enter Destination City: ");
        String city2 = input.next();
		if(!graph.contains(city2)) 
		{
            System.out.println(city2 + " is not on route");
            return;
        }
        int c2 = cities.get(city2);  
        System.out.println("Shortest Path By: \n1. Miles\n2. Price\n3. Hops");
        int mode = input.nextInt();
        LinkedList<Integer> shortestPath[] = choice3(c1, mode);
		if(shortestPath[c2].peek() == c1) 
		{
        	System.out.println(cities2[c1] + " to " + cities2[c2]+ ": ");
        	choice3Helper(shortestPath[c2], mode);
		} 
		else 
		{
        	System.out.printf("No path from %s to %s \n", cities2[c1], cities2[c2]);
        }
	}

	/**
	 * add a route between city1 and city2
	 * @param city1 the city that is the departure
	 * @param city2 the city that is the destination 
	 */
	public static void choice6(String city1, String city2) 
	{
		int from = cities.get(city1);
		int to = cities.get(city2);
		if(graph.returnAdjacencyList(from).get(to) == null) 
		{
			System.out.println("Threre is no route from " + city1 + " to " + city2);
			
		} 
		else {
			graph.removeEdge(from, to);
			System.out.println("Successfully remove the route");
		}
	}
	
	/**
	 * remove a route between city1 and city2
	 * @param city1 the city that is the departure
	 * @param city2 the city that is the destination 
	 * @param distance the distance to travel through those two cities
	 * @param price the price to travel through those two cities
	 */
	public static void choice5(String city1, String city2, int distance, double price) 
	{
		int from = cities.get(city1);
		int to = cities.get(city2);
		if(graph.returnAdjacencyList(from).get(to) == null) 
		{
			graph.addEdge(from, to, distance, price);
			System.out.println("Added edge successfully.");
			System.out.println("From " + city1 + " to " + city2  
					+ " with distance of " + distance + ", and price of " + price);
		}
	}
	
	/**
	 * recursive method that calculate all routes given the max amount
	 * @param c the index of a city
	 * @param maxPrice the max dollor input by the user
	 * @param currentPrice the dollar that we currently have
	 * @param output the array that stores the output city
	 * @return the array that stores the potential resulting array
	 */
	private static LinkedList<Integer> choice4(int c, double maxPrice, double currentPrice, LinkedList<Integer> output)
	{
		if(output.contains(c))
		{
            output.removeLast();
            return output;
        }
		if(currentPrice > maxPrice)
		{
            output.removeLast();
            return output;
        } else if(currentPrice == maxPrice){
            output.add(c);
            System.out.print("\n" + currentPrice + ": ");
            printlist(output);
            output.removeLast();
            return output;
        } 
        output.add(c);
		if(currentPrice != 0 && currentPrice < maxPrice)
		{
             System.out.print("\n" + currentPrice + ": ");
             printlist(output);
        }
		for(Map.Entry<Integer, Edge> edge: graph.returnAdjacencyList(c).entrySet()) 
		{
        	if ((maxPrice >= currentPrice + edge.getValue().price) && (!output.contains(edge.getValue().to))){ 
        		int temp = edge.getValue().to;
        		output = choice4(temp, maxPrice, (currentPrice + edge.getValue().price), output);
        	}
        }
        output.removeLast();
        return output;
    }
    
    /**
     * print the result of choice 4
     * @param x the resulting array
     */
	private static void printlist(LinkedList<Integer> output)
	{
        for(int index : output){
        	System.out.print(cities2[index] + " ");
        	if(output.indexOf(index)+1 < output.size()) {
        		System.out.print(graph.getPrice(index, output.get(output.indexOf(index)+1)));
        		System.out.print(" " + "--->" + " ");
        	} 
        	
        }
    }

    /**
     * print the result of choice three
     * @param path the resulting linked list
     */
	public static void printStack(LinkedList<Integer> path) 
	{
		if(path.size() == 1) 
		{
			System.out.print(cities2[path.peek()]);
		}
		String result = null;
		int plen = path.size() - 1, max = plen + 1;
		Integer curr, next = null;
		ListIterator<Integer> li = path.listIterator(0),
		 					  li2 = path.listIterator(1);
		for (int i = 0; i < plen; i++) 
		{
			max--;
			curr = li.next();
			System.out.print(cities2[curr]);
			next = li2.next();
			if(max > 0)
			{ 
				System.out.print(String.format(" ($%,.2f", graph.getPrice(curr, next)));
				System.out.print(") ---> ");
			}
		}
		System.out.print(cities2[next]);
	}
	
	/**
	 * the method that print out all the path given the linked list
	 * @param path the linkedlist version of a path
	 * @param mode the type that the user asked
	 */
	public static void choice3Helper(LinkedList<Integer> path, int mode) 
	{
		if(path.size() == 1) {
			System.out.println(cities2[path.peek()]);
			return;
		}
		int total = 0; 
		int dist = 0;
		int max = path.size();
		double cost = 0, totalCost = 0;
		Integer curr, next = null;
		ListIterator<Integer> list1 = path.listIterator(0);
		ListIterator<Integer> list2 = path.listIterator(1);
		for (int i = 0; i < path.size() - 1; i++) 
		{
			max--;
			curr = list1.next();
			System.out.print(cities2[curr]);

			next = list2.next();
			
			cost = graph.getPrice(curr, next) ;
			if (mode == 1) 
			{
				if(curr == next) 
				{
					dist = 0;
				} 
				else 
				{
					Edge tempE = graph.returnAdjacencyList(curr).get(next);
					if(tempE != null) {
						dist = tempE.distance;
					}
				}
				if(max > 0)
				{ 
					System.out.print(" (" + dist + ")  ---> ");
				}
				total +=  dist;
			} else if (mode == 3) {
				total+=1;
				if(max > 0) {
					System.out.print(" ---> ");
				}
			} else if (mode == 2) {
				if(max > 0)
				{ 
					System.out.printf(" ($ %.2f) ---> ", cost);
				}
				totalCost += cost;
			}
 		}
		System.out.print(cities2[next]);
		System.out.println();
		switch(mode) 
		{
			case 1: 
				System.out.println("Total distance: " + total);
				break;
			case 2:
				System.out.println("Lowest cost: " + totalCost);
				break;
			case 3:
				System.out.println("Fewest hops from " + total);
				break;
		}
	}
	
	/**
	 * return all shortest pass, using Dijkstra's algrithm
	 * @param city starting city
	 * @param mode the choice that the user inputted
	 * @return the possible shortest path from the starting city
	 */
	public static LinkedList<Integer>[] choice3(int city, int mode) 
	{
		int V = graph.V();
		int distance = 0;
		int hops = 0;
		int curr = 0;
		int i = 0;
		double cost = 0;
		Edge[] edge = new Edge[V];
		edge[city] = new Edge(city, city, 0, 0, 0);
		LinkedList<Integer>[] result = new LinkedList[V];
		PriorityQueue<Edge> PQ = new PriorityQueue<Edge>();
		PQ.add(edge[city]);
		while(PQ.peek() != null) 
		{
			curr = PQ.poll().vertexIndex;
			LinkedHashMap<Integer, Edge> vertex = graph.returnAdjacencyList(curr);
			for(Map.Entry<Integer, Edge> e : vertex.entrySet()) 
			{
				i = e.getValue().vertexIndex;
				distance = e.getValue().distance;
				cost = e.getValue().price;
				if(edge[i] == null) 
				{
					edge[i] = new Edge(i, curr, distance +edge[curr].distance,
							1+edge[curr].hops, cost+edge[curr].price);
					PQ.add(edge[i]);
				} 
				else {
					if(mode == 1) 
					{
						int newdistance = distance + edge[curr].distance;
						if(newdistance < edge[i].distance) 
						{
							edge[i].to = curr;
							edge[i].distance = newdistance;
							if(PQ.remove(edge[i]) ) 
							{
								PQ.add(edge[i]);
							}
						}
					} 
					else if(mode == 2) 
					{
						double newcost = cost + edge[curr].price;
						if (newcost < edge[i].price) 
						{
							edge[i].to = curr;
							edge[i].price = newcost;
							if(PQ.remove(edge[i])) PQ.add(edge[i]);
						}
					}
					else if(mode == 3) 
					{
						int newhop = 1 + edge[curr].hops;
						if (newhop < edge[i].hops) 
						{
							edge[i].to = curr;
							edge[i].hops = newhop;
							if(PQ.remove(edge[i])) PQ.add(edge[i]);
						}
					}
				}
			}
		}
		LinkedList<Integer>[] paths = new LinkedList[V];
		int j;
		for (i = 0; i < V; i++)
		{
			j = i;
			paths[i] = new LinkedList<Integer>();
			while(true)
			{
				paths[i].add(new Integer(j));
				if (j == edge[j].to) break;
				j = edge[j].to;
			}
			result[i] = new LinkedList<Integer>();
			while(paths[i].peekLast()!=null)
				result[i].add(paths[i].removeLast());
		}
		return result;
	}
	
	/**
	 * Using the Prim's algorithm to calculate the MST
	 * @param graph
	 */
	public static void choice2(Graph graph) 
	{
		int totalW = 0;
		int unconnected= 0;
		int visitednum = 0;
		int smallestNum = Integer.MAX_VALUE;
		int unconnectedEdges = 0;
		int V = graph.V();
		Graph MST = new Graph(V);
		int vertexNum = 0;
		boolean[] visited = new boolean[V];
		int notVisitedEdges[] = new int[V];
		for(int i = 0; i < V; i++) 
		{
			notVisitedEdges[i] = graph.returnAdjacencyList(i).size();
		}
		Edge temp1 = null;
		Edge temp2 = null;
		visited[0] = true;
		visitednum += 1;
		System.out.printf("\n%-15s%-15s%-15s%-15s", "From", "To", "Distance", "Price");
		while(visitednum < V) 
		{
			smallestNum = Integer.MAX_VALUE;
			for(int i = 0; i < V; i++)
			{
				int smNum = Integer.MAX_VALUE;
				int max = graph.returnAdjacencyList(i).size();
				int tempN = 0;
				//get the unvisited vertex with the shortest distance from a given vertex
				Edge edge1 = null;
				Edge edge2 = null;
				for(Map.Entry<Integer, Edge> edge: graph.returnAdjacencyList(i).entrySet()) 
				{
					edge1 = edge.getValue();
					if(!visited[edge.getKey()] && edge1.distance < smNum) 
					{
						smNum = edge1.distance;
						edge2 = edge1;
					}
				}
				temp1 = edge2;
				if(visited[i] && notVisitedEdges[i]>0 &&  (temp1) != null && temp1.distance < smallestNum) 
				{
					smallestNum = temp1.distance;
					vertexNum = i;
					temp2 = temp1;
				}
			}
			//mark the new vertex, and add the edge
			if(temp2 != null) {
				notVisitedEdges[vertexNum]--; 
				notVisitedEdges[temp2.vertexIndex]--;
				visited[temp2.vertexIndex] = true;
				MST.addEdge(vertexNum, temp2.to, temp2.distance, temp2.price);
				totalW += temp2.distance;
				visitednum++;
				System.out.printf("\n%-15s%-15s%-15d%-15.2f", cities2[vertexNum], 
						cities2[temp2.vertexIndex], temp2.distance, 
						temp2.price);
				//System.out.println();
				temp2=null;
			}
			else 
			{
				 int i = 0;
				 while(true) 
				 {
					 if(visited[i] == false) 
					 {
						 visited[i] = true;
						 break;
					 }
					 i++;
				 }
				 unconnected++;
				 visitednum++;
			}
		}
		System.out.println();
		System.out.println();
		if(unconnected > 0) 
		{
			System.out.println("This graph contains " + (unconnected + 1) + " subtrees");
		}
		System.out.println("The total distance of the graph is " + totalW);
	}
	
	/**
	 * The class to represent an edge weighted graph.
	 * All edges are stored in the adjacency list
	 * The path to the certain edge is stored in the nodes of linkedhashmap 
	 * in the adjacency list
	 */
	static class Graph 
	{
		private int V;
		private int E;
		private LinkedHashMap<Integer, Edge> [] adjacency;
		/**
		 * the constructor to make the new graph class
		 * @param v number of vertices
		 */
		public Graph(int v) 
		{
			V = v;
			E = 0;
			adjacency = new LinkedHashMap[v];
			for(int i = 0; i < v; i++) {
				adjacency[i] = new LinkedHashMap<Integer, Edge>();
			}
		}
		
		/**
		 * to test whether the given input city is in the graph
		 * @param x the user input city
		 * @return boolean sttement about whether the city is in the graph
		 */
		public boolean contains(String x) 
		{
			for(int i = 0; i < V; i++)
			{
				if(cities2[i].equals(x)) 
				{
					//System.out.println(cities2[i]);
					return true;
				}
			}
			return false;
		}
		
		/**
		 * number of vertex 
		 * @return the number of vertex in the graph
		 */
		public int V() { return V;}
		
		/**
		 * add an edge into the graph
		 * @param from the starting city
		 * @param to the destination
		 * @param distance distance between those cities
		 * @param price price between those cities
		 */
		public void addEdge(int from, int to, int distance, double price) 
		{
			//System.out.println(from + ", " + to + ", " + distance);
			adjacency[from].put(new Integer(to), new Edge(to, distance, price));
			adjacency[to].put(new Integer(from), new Edge(from, distance, price));
			E++;
		}
		
		/**
		 * 
		 * @param from the starting city
		 * @param to the destination
		 */
		public void removeEdge(int from, int to) 
		{
			adjacency[from].remove(to);
			adjacency[to].remove(from); 
			//System.out.println("deleting in removeEdge");
		}
		
		/**
		 * return the adjacency list of a given vertex
		 * @param i the given vertex
		 * @return the adjacency list of given vertex
		 */
		public LinkedHashMap<Integer, Edge> returnAdjacencyList(int i) 
		{
			return adjacency[i];
		}
		
		/**
		 * get the distance between two cities
		 * @param i the first city
		 * @param j the second city
		 * @return the disance between first city and second city
		 */
		public int getDist(Integer i, Integer j)
		{
			if(i.equals(j)) return 0;
			Edge temp = adjacency[i].get(j);
			if (temp != null) return temp.distance;

			return -1;
		}
		
		/**
		 * print out all direct routes in the graph
		 */
		public void choice1()
		{
			System.out.printf("\n%-15s%-15s%-15s%-15s", "From", "To", "Distance", "Price");
			for(int i = 0; i < V; i++) {
				 for(Map.Entry<Integer, Edge> edge : adjacency[i].entrySet()) {
					 if(edge.getValue().vertexIndex <= i)
						 System.out.printf("\n%-15s%-15s%-15d%-15.2f", cities2[i], 
							 cities2[edge.getValue().vertexIndex], edge.getValue().distance, 
							 edge.getValue().price);
				 }
			}
			System.out.println();
		}
		
		/**
		 * get the price between two cities
		 * @param i the first city
		 * @param j the second city
		 * @return the price between first city and second city
		 */
		public double getPrice(Integer i, Integer j) {
			if(i.equals(j) || i == null || j == null) {
				return 0;
			}
			else 
			{
				Edge temp = adjacency[i].get(j);
				if(temp != null) 
				{
					return temp.price;
				}
			}
			return 0;
		}
	
	} 
	
	/**
	 * the class to represent the edges of a graph
	 */
	static class Edge implements Comparable <Edge>
	{
		int vertexIndex;
		int to;
		int distance;
		int hops;
		double price;
		
		/**
		 * publoic constructor of edge
		 * @param vertex the given index of the city
		 * @param distance the distance between two cities
		 * @param price the price between two cities
		 */
		public Edge(int vertex, int distance, double price) {
			vertexIndex = vertex;
			this.to = vertex;
			this.distance = distance;
			this.price = price;
			this.hops = 0;
		}
		
		/**
		 * publoic constructor of edge
		 * @param from the first city
		 * @param to the second city
		 * @param distance the distance between cities
		 * @param price the price betwen those cities
		 */
		public Edge(int from, int to, int distance, double price) {
			vertexIndex = from;
			this.to = to;
			this.distance = distance;
			this.price = price;
			this.hops = 0;
		}
		
		/**
		 * publoic constructor of edge
		 * @param from the first city
		 * @param to the second city
		 * @param distance the distance between cities
		 * @param price the price between those cities
		 * @param hop the hop between those two cities
		 */
		public Edge (int from, int to, int distance, int hop, double price) {
			vertexIndex = from;
			this.to = to;
			this.distance = distance;
			this.price = price;
			this.hops = hop;
		}

		@Override
		public int compareTo (Edge other)
		{	
			return distance - other.distance; 
		}
	}
	
	/**
	 * read the file and store values into the graph
	 * @param file the file name
	 * @return return a graph
	 * @throws Exception file not found exception
	 */
	static Graph readFile(File file) throws Exception 
	{
		String adj[];
		Scanner scan = new Scanner(file);
		int dim = scan.nextInt();
		scan.nextLine();
		String cityName;
		Graph G = new Graph(dim);
		cities = new  LinkedHashMap<String,Integer>();
		cities2 = new String[dim];
		citiesSorted = new ArrayList<String>();
		for (int i = 0; i < dim; i++ ) 
		{
			cityName = scan.nextLine();
			cities.put(cityName, new Integer(i));
			cities2[i] = cityName;
			citiesSorted.add(cityName);
		}
		Collections.sort(citiesSorted);
		while(scan.hasNextLine()) {
			String line[] = scan.nextLine().split(" ");
			int cityindex1 = Integer.parseInt(line[0])-1;
			int cityindex2 = Integer.parseInt(line[1])-1;
			int distance = Integer.parseInt(line[2]);
			Double price = Double.parseDouble(line[3]);
			G.addEdge(cityindex1, cityindex2, distance, price);
			
		}
		scan.close();
		return G; 
	}
}
