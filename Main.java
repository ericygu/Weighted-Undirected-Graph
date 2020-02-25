package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


class queueHelp {
    int vertex;
    boolean presentInQueue;

    queueHelp(int vertex, boolean presentInQueue){
        this.vertex = vertex;
        this.presentInQueue = presentInQueue;
    }
}

class Container {
    double minDistance;
    int vertex;
    Container(double minDistance, int vertex){
        this.minDistance = minDistance;
        this.vertex = vertex;
    }
}

class comparator implements Comparator<Container> {

    @Override
    public int compare(Container a, Container b)
    {
        if(a.minDistance-b.minDistance <0){
            return -1;
        } else if(a.minDistance-b.minDistance>0 ){
            return 1;
        } else{
            return 0;
        }
    }
}

class Edge {
    int sourceVertex;
    int destinationVertex;
    double weight;
    Edge next;

    Edge(int sourceVertex, int destinationVertex, double weight){
        this.sourceVertex = sourceVertex;
        this.destinationVertex = destinationVertex;
        this.weight = weight;

    }
}

public class Main {

    public static void main(String[] args) {
        System.out.println("First Graph:");
        File file1 = new File("C:\\Users\\ASUS\\Downloads\\graph1.txt");
        completedProblem(file1);

        System.out.println("Second Graph:");
        File file2 = new File("C:\\Users\\ASUS\\Downloads\\graph2.txt");
        completedProblem(file2);

        System.out.println("Third Graph:");
        File file3 = new File("C:\\Users\\ASUS\\Downloads\\graph3.txt");
        completedProblem(file3);
    }

    public static void completedProblem(File file) {
        //Question 1 and 2
        double[][] adjacencyMatrix = createMatrix(file);
        printMatrix(adjacencyMatrix);

        //Question 3
        Edge[] adjacencyList = createList(file);
        ArrayList<Edge[]> connected = connectedComponents(adjacencyList, 1);
        for (int i = 0; i < connected.size(); i++) {
            System.out.println("Connected Component: " + (i + 1));
            printList(connected.get(i));
            System.out.println();
        }

        //Question 4
        ArrayList<VertexHelp[]> unionOfLists = new ArrayList<>();
        for (int i = 0; i < connected.size(); i++) {
            if (connected.get(i).length > 1) {
                unionOfLists.add(prim(connected.get(i)));
            }
        }

        double[][] mstMatrix = new double[adjacencyList.length][adjacencyList.length];
        for (int i = 0; i < unionOfLists.size(); i++) {
            for (int j = 1; j < unionOfLists.get(i).length; j++) {
                mstMatrix[unionOfLists.get(i)[j].vertex][unionOfLists.get(i)[j].parentOfVertex] = adjacencyMatrix[unionOfLists.get(i)[j].vertex][unionOfLists.get(i)[j].parentOfVertex];
                mstMatrix[unionOfLists.get(i)[j].parentOfVertex][unionOfLists.get(i)[j].vertex] = adjacencyMatrix[unionOfLists.get(i)[j].parentOfVertex][unionOfLists.get(i)[j].vertex];
            }
        }
        printMatrix(mstMatrix);
    }

    public static void printList(Edge[] adjacencyList) {
        for (int i = 0; i < adjacencyList.length; i++) {
            System.out.println("Node: " + adjacencyList[i].sourceVertex);
            Edge edge = adjacencyList[i].next;
            while (edge != null) {
                System.out.println("Destination Vertex: " + edge.destinationVertex + " Weight: " + edge.weight);
                edge = edge.next;
            }
        }
    }

    public static void printMatrix(double[][] matrix) {
        System.out.println("Matrix is " + (matrix.length - 1) + " x " + (matrix.length - 1));
        System.out.println("Welcome to the Matrix:");
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static double[][] createMatrix(File file) {
        double[][] adjacencyMatrix = new double[1][1];

        try {
            Scanner scanner = new Scanner(file);
            boolean firstLine = true;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (firstLine) {
                    String[] dimensions = line.split(", ");
                    int vertexCount = Integer.parseInt(dimensions[0]);
                    int edgeCount = Integer.parseInt(dimensions[1]);
                    adjacencyMatrix = new double[vertexCount + 1][vertexCount + 1];
                    firstLine = false;
                } else {
                    String[] parts = line.split(", ");
                    int source = Integer.parseInt(parts[0]);
                    int destination = Integer.parseInt(parts[1]);
                    adjacencyMatrix[source][destination] = Double.parseDouble(parts[2]);
                    adjacencyMatrix[destination][source] = Double.parseDouble(parts[2]);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return adjacencyMatrix;
    }

    public static Edge[] createList(File file) {
        Edge[] adjacencyList = new Edge[1];
        try {
            Scanner scanner = new Scanner(file);
            boolean firstLine = true;
            int edgeCount;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (firstLine) {
                    String[] dimensions = line.split(", ");
                    int vertexCount = Integer.parseInt(dimensions[0]);
                    edgeCount = Integer.parseInt(dimensions[1]);
                    adjacencyList = new Edge[vertexCount + 1];

                    firstLine = false;
                } else {
                    String[] parts = line.split(", ");
                    int source = Integer.parseInt(parts[0]);
                    int destination = Integer.parseInt(parts[1]);
                    double weight = Double.parseDouble(parts[2]);
                    adjacencyList[source] = insertEdge(adjacencyList[source], source, destination, weight);
                    adjacencyList[destination] = insertEdge(adjacencyList[destination], destination, source, weight);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return adjacencyList;
    }

    public static Edge insertEdge(Edge edge, int source, int destination, double weight) {
        if (edge == null) {
            edge = new Edge(source, destination, weight);
            return edge;
        } else {
            edge.next = insertEdge(edge.next, source, destination, weight);
        }
        return edge;
    }

    public static ArrayList<Edge[]> connectedComponents(Edge[] adjacencyList, int vertex) {
        ArrayList<Edge[]> result = new ArrayList<>();
        boolean[] visited = new boolean[adjacencyList.length];
        LinkedList<Integer> queue = new LinkedList<>();
        while (allVisited(visited) != -1) {

            vertex = allVisited(visited);
            visited[vertex] = true;
            queue.add(vertex);
            ArrayList<Integer> values = new ArrayList<>();
            while (!queue.isEmpty()) {
                int s = queue.poll();
                values.add(s);
                Edge edge = adjacencyList[s];
                while (edge != null) {
                    if (visited[edge.destinationVertex] == false) {
                        queue.add(edge.destinationVertex);
                        visited[edge.destinationVertex] = true;
                    }
                    edge = edge.next;
                }
            }
            result.add(adjacencyConnected(adjacencyList, values));
        }
        return result;
    }

    public static int allVisited(boolean[] visited) {
        int result = -1;
        for (int i = 1; i < visited.length; i++) {
            if (visited[i] == false) {
                result = i;
                break;
            }
        }
        return result;
    }

    public static Edge[] adjacencyConnected(Edge[] adjacencyList, ArrayList<Integer> values) {
        Edge[] result = new Edge[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = adjacencyList[values.get(i)];
            if (adjacencyList[values.get(i)] == null) {
                result[i] = new Edge(values.get(i), -1, -1);
            }
        }
        return result;
    }

    public static VertexHelp[] prim(Edge[] adjacencyList) {

        queueHelp[] minimumSpanningTree = new queueHelp[adjacencyList.length];
        Container[] distances = new Container[adjacencyList.length];
        VertexHelp[] parents = new VertexHelp[adjacencyList.length];

        for (int i = 0; i < adjacencyList.length; i++) {
            minimumSpanningTree[i] = new queueHelp(adjacencyList[i].sourceVertex, false);
            distances[i] = new Container(Integer.MAX_VALUE, adjacencyList[i].sourceVertex);
            parents[i] = new VertexHelp(adjacencyList[i].sourceVertex, -1);
        }
        minimumSpanningTree[0].presentInQueue = true;
        distances[0].minDistance = 0;


        PriorityQueue<Container> queue = new PriorityQueue<>(new comparator());
        for (int i = 0; i < adjacencyList.length; i++) {
            queue.add(distances[i]);
        }

        while (!queue.isEmpty()) {
            Container a = queue.poll();
            minimumSpanningTree[location(minimumSpanningTree, a.vertex)].presentInQueue = true;
            Edge edge = findEdge(adjacencyList, a.vertex);
            while (edge != null) {
                if (minimumSpanningTree[location(minimumSpanningTree, edge.destinationVertex)].presentInQueue == false) {
                    // if my adjacent vertex is not in the queue
                    int adjacentLocation = location(minimumSpanningTree, edge.destinationVertex);
                    if (distances[adjacentLocation].minDistance > edge.weight) {
                        queue.remove(distances[adjacentLocation]);
                        distances[adjacentLocation].minDistance = edge.weight;
                        queue.add(distances[adjacentLocation]);
                        parents[linearSearch(parents, edge.destinationVertex)].parentOfVertex = a.vertex;
                    }
                }
                edge = edge.next;
            }
        }

        return parents;
    }

    public static int linearSearch(VertexHelp[] parents, int vertex) {
        for (int i = 0; i < parents.length; i++) {
            if (parents[i].vertex == vertex) {
                return i;
            }
        }
        return -1;
    }

    public static int location(queueHelp[] minimumSpanningTree, int loc) {
        for (int i = 0; i < minimumSpanningTree.length; i++) {
            if (minimumSpanningTree[i].vertex == loc) {
                return i;
            }
        }
        return -1;
    }

    public static Edge findEdge(Edge[] adjacencyList, int vertex) {
        Edge result = null;
        for (int i = 0; i < adjacencyList.length; i++) {
            if (adjacencyList[i].sourceVertex == vertex) {
                result = adjacencyList[i];
                break;
            }
        }
        return result;
    }
}