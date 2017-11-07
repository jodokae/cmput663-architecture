package architecture.commons;

import java.util.List;

public class Graph<T> {
	private List<T> nodes;
	private List<Dependency<T>> edges;
}
