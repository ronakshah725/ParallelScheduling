import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Dependencies{

	public static void main(String[] args) throws IOException {

		int concurrencyLimit = -1;
		if(args.length < 1){
			System.out.println("Usage:java Dependencies conf-file [conc-limit]"); 
			System.exit(1);
		}else if(args.length == 2){
			concurrencyLimit = Integer.parseInt(args[1]);
		}
		System.out.println("Concurrency limit: " + (concurrencyLimit>0 ?concurrencyLimit : "No Limit"));
		
		HashMap<String, Node> programs = readDependencies(args[0]); 
		Set<Node> allNodes= new HashSet<Node>(programs.values());
		printParallel(allNodes, concurrencyLimit);
	}
	
	/*
	 * Topological sort and process(print) parallel nodes
	 * 
	 * */
	private static void printParallel(Set<Node> allNodes, int limit) {
		System.out.println("Process order:");
		int count = 0;
		int NoOfNodes = allNodes.size();
		int printLimit = 0;
		List<Node> inZeroNodes ; //nodes with indegree zero

		while(count < NoOfNodes ){
			inZeroNodes = new LinkedList<>();
			boolean atLeastOne = false;
			printLimit = 0;
			for(Node n : allNodes ){ //Process all unvisited nodes with no depencies i.e no incoming edges 
				if(n.indegree == 0 && !n.isVisited){
					n.isVisited = true;
					inZeroNodes.add(n);
					if(limit > 0 && printLimit++ == limit) //snippet to process parallel nodes as per conc-limit
						System.out.println();
					System.out.print(n);
					atLeastOne = true; // marks if atleast one unvisited node has indegree 0
				}
			}
			if(!atLeastOne) // early exit if no such nodes
				break;
			System.out.println();
			
			count++; 
			ListIterator<Node> iterator = inZeroNodes.listIterator();
			while(iterator.hasNext()){ // for each indegree 0 nodes, reduce destination nodes indegree 
				Node n = iterator.next(); // this enables to process new nodes with no dependencies
				for(Edge e : n.outEdges){						
					e.to.indegree--;
				}
			}
		}
		checkCycle(allNodes);		// check for cycle 
	}

	/*
	 * Check if cycle exists
	 * 
	 * */
	private static void checkCycle(Set<Node> allNodes) {
		boolean cycle = false;
		for(Node n : allNodes){ 
			if(n.indegree != 0){ // all  nodes after processing should have indegree 0, else there is a cycle
				cycle = true;
				break;
			}
		}
		if(cycle)	
			System.out.println("Cycle present, dependency ordering not possible !! ");
		else
			System.out.println("Done !!");
	}

	/*
	 * Read Configuration file and make a graph
	 * return a Hashmap with key: program name, value: program node with outbound edges
	 * */
	public static HashMap<String, Node>  readDependencies(String confFileName) throws IOException{
		BufferedReader br;
		br = new BufferedReader(new FileReader(confFileName));
		Pattern pattern = Pattern.compile("(.*)(:)(.*)");
		String line;

		HashMap<String, Node> nodes = new HashMap<>();

		while((line = br.readLine()) != null ){
			
			if(line.startsWith("#")) continue; //comments start with #
			Matcher matcher = pattern.matcher(line);	
			while (matcher.find()) {
				String name = matcher.group(1).trim(); //
				Node child;
				String[] parents = matcher.group(3).trim().split("[\\s]*,[\\s]*");

				if(!nodes.containsKey(name)){
					child = new Node(name);
					nodes.put(name, child);
				}else
					child = nodes.get(name);
				
				for(String p : parents){
					if(p.equals("")) continue;
					Node parent = nodes.get(p);
					if(parent==null)
						parent = new Node(p);
					parent.addEdge(child);
					nodes.put(p, parent);
				}
			}
		}
		br.close();
		return nodes;
	}
}

class Node{
	public final String name;
	public int indegree;
	public boolean isVisited;

	public final HashSet<Edge> outEdges;

	public Node(String name) {
		this.name = name;
		indegree = 0;
		outEdges = new HashSet<Edge>();
		isVisited = false;
	}

	public void addEdge(Node node){
		Edge e = new Edge(this, node);
		outEdges.add(e);
		node.indegree++;
	}

	@Override
	public String toString() {
		return  name ;
	}
}

class Edge{
	public final Node from;
	public final Node to;

	public Edge(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean equals(Object obj) {
		Edge e = (Edge)obj;
		return e.from == from && e.to == to;
	}

}
