package architecture.extraction.reconstruction.pkg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import architecture.extraction.classes.husacct.HusaactGraphFileWriter;
import architecture.extraction.reconstruction.AbstractArchitectureReconstructor;



public class PackageReconstructor extends AbstractArchitectureReconstructor {
		
	private MutableValueGraph<String, String> inputGraph;
	private MutableValueGraph<String, Integer> outputGraph;
	
	public PackageReconstructor(String name) {
		super(name);
		
		inputGraph = ValueGraphBuilder.directed().build();
		outputGraph = ValueGraphBuilder.directed().build();
	}

	@Override
	public void reconstruct(File graph, File output) {
		try (Stream<String> stream = Files.lines(Paths.get(graph.toURI()))) {

			stream.forEach(line -> buildGraph(line));

		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 
		
		System.out.println("Calc Clusters");
		getClusters();		
		System.out.println("Calc Dependencies");
		addDependencies();
		
		try {
			PackageGraphFileWriter writer = new PackageGraphFileWriter(output);
			writer.write(outputGraph);
		} catch (FileNotFoundException e) {
			System.err.println("Could not print Package Architecture");
			e.printStackTrace();
		}
		
	}
	
	private void buildGraph(String line) {
		String[] parts = line.split(" ");
		if(parts.length != 3 || parts[0].equals("depends")) {
			return;
		}
		
		// Adds nodes as well
		inputGraph.putEdgeValue(parts[1], parts[2], parts[0]);
	}
	
	private void getClusters() {
		Set<String> nodes = inputGraph.nodes();		
		
		Set<String> clusters = new HashSet<String>();
				
		for(String node : nodes) {
			if(inputGraph.predecessors(node).isEmpty()) {
				clusters.add(node);
			}
		}
		
		boolean change = true;
		while(change) {
			change = false;
			
			Set<String> tempClusters = new HashSet<String>(clusters);
			for(String node : clusters) {
				Set<String> successors = inputGraph.successors(node);
				String firstSucc = successors.iterator().next();
							
				if(!isPackage(node)) {
					tempClusters.remove(node);
				} else {
					// If only one element, take successor package
					if(successors.size() == 1 && !containsClasses(node)) {
						tempClusters.remove(node);
						tempClusters.add(firstSucc);
						change = true;
					}
				}
			}
			clusters = tempClusters;
		}
		
		
		// TODO verhindere Endlosschleifen
		while(clusters.size() < 10) {
			//System.out.println(clusters.size());
			Set<String> tempClusters = new HashSet<String>(clusters);
			for(String node : clusters) {
				if(containsClasses(node)) {
					continue;
				}
				tempClusters.remove(node);
				tempClusters.addAll(inputGraph.successors(node));				
			}
			clusters = tempClusters;			
		}
		
		
		for(String node: clusters) {
			outputGraph.addNode(node);
		}
		
	}
	
	private void addDependencies() {
		Set<EndpointPair<String>> edges = this.inputGraph.edges();
		for(EndpointPair<String> pair : edges) {
			String type = inputGraph.edgeValueOrDefault(pair.source(), pair.target(), null);
			if(type.equals(HusaactGraphFileWriter.REFERENCES)) {
				String source;
				String target;
				
				//System.out.println(pair.source() + " =>" + pair.target());
				
				if(outputGraph.nodes().contains(pair.source())) {
					source = pair.source();
				} else {
					source = findParent(pair.source());
				}
				
				if(outputGraph.nodes().contains(pair.target())) {
					target = pair.target();
				} else {
					target = findParent(pair.target());
				}
				
				if(source != null && target != null && !source.equals(target)) {
					int oldEdgeValue = outputGraph.edgeValueOrDefault(source, target, 0);
					outputGraph.putEdgeValue(source, target, oldEdgeValue+1);
					//System.out.println(source + " -> " + target);
				}
				
			}
		}
	}
	
	private boolean containsClasses(String node) {
		Set<String> successors = inputGraph.successors(node);
		for(String succ : successors) {
			if(!isPackage(succ)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isPackage(String node) {
		Set<String> successors = inputGraph.successors(node);
		String firstSucc = successors.iterator().next();
		
		return firstSucc != null && firstSucc.contains(node);
	}
	
	private String findParent(String node) {
		Set<String> predecessors = inputGraph.predecessors(node);
		Optional<String> pkg = Optional.empty();
		for(String pred: predecessors) {
			//if(node.contains("IndexDatetime")) {
				//System.out.println(predecessors);
			//}
			if(inputGraph.edgeValueOrDefault(pred, node, null).equals(HusaactGraphFileWriter.CONTAINS) || 
					inputGraph.edgeValueOrDefault(pred, node, null).equals(HusaactGraphFileWriter.SUB_PKG)) {
				pkg = Optional.of(pred);
				//System.out.println(pred);
				break;
			}
		}
		
		if(!pkg.isPresent()) {
			return null;
		}
		
		if(outputGraph.nodes().contains(pkg.get())) {
			return pkg.get();
		} else {
			return findParent(pkg.get());
		}
	}

}
 