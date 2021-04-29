package main;

import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Node implements Comparable<Node> {

    private int id;
    private int priority = 0;
    private Point center = new Point();
    private ArrayList<Pair<Integer, Integer>> connections;

    public Node(int id, int x, int y) {
        this.id = id;
        center.x = x;
        center.y = y;

        connections = new ArrayList<>();
    }

    public void addEdge(Integer to, Integer cost) {
        connections.add(new Pair<>(to, cost));
    }

    public ArrayList<Pair<Integer, Integer>> getConnections() {
        return connections;
    }

    public Point getCenter() {
        return center;
    }

    public int getId() {
        return id;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(Node o) {
        return this.priority - o.priority;
    }


    @Override
    public String toString() {
        return center.x + " " + center.y;
    }
}
