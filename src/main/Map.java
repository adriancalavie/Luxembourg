package main;


import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.util.*;

public class Map extends JPanel {
    private final ArrayList<Node> nodes;
    private Node pointStart;
    private Node pointEnd;
    private boolean isMarking = false;
    private boolean isWorking = false;
    private java.util.Map<Node, Node> cameFrom = new HashMap<>();
    private java.util.Map<Node, Integer> costSoFar = new HashMap<>();

    private boolean usingDijkstra;

    public Map(ArrayList<Node> nodeList, java.util.Map<Pair<Integer, Integer>, Integer> edges, boolean usingDijkstra) {
        this.nodes = nodeList;
        this.pointStart = null;
        this.pointEnd = null;
        this.usingDijkstra = usingDijkstra;
        this.addMouseListener(new MouseHandler());

        repaint();
    }

    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, 2000, 2000);

        g.setColor(Color.BLACK);

        for (Node n : nodes) {
            //g.drawLine(n.getCenter().x, n.getCenter().y, n.getCenter().x, n.getCenter().y);

            for (Pair<Integer, Integer> connection : n.getConnections()) {
                g.drawLine(n.getCenter().x, n.getCenter().y, nodes.get(connection.getKey()).getCenter().x, nodes.get(connection.getKey()).getCenter().y);
            }
        }


        if (pointStart != null) {
            g2.setColor(new Color(255, 135, 135));
            g2.setStroke(new BasicStroke(10));
            g2.drawLine(pointStart.getCenter().x, pointStart.getCenter().y, pointStart.getCenter().x, pointStart.getCenter().y);
            g.drawString("Start id:" + pointStart.getId(), 600, 100);
        }

        if (pointEnd != null) {
            g2.setColor(new Color(255, 135, 135));
            g2.setStroke(new BasicStroke(10));
            g2.drawLine(pointEnd.getCenter().x, pointEnd.getCenter().y, pointEnd.getCenter().x, pointEnd.getCenter().y);
            g.drawString("End id:" + pointEnd.getId(), 750, 100);
        }

        if (isMarking) {

            Node current = pointEnd;


            while (cameFrom.containsKey(current)) {
                Node next = cameFrom.get(current);


                assert current != null;
                assert next != null;

                g2.setStroke(new BasicStroke(3));

                g2.drawLine(current.getCenter().x, current.getCenter().y, next.getCenter().x, next.getCenter().y);

                if (next == pointStart) {
                    break;
                }

                current = next;
            }

            g.drawString("Total cost: " + costSoFar.get(pointEnd), 600, 150);

            isWorking = false;
            pointStart = null;
            pointEnd = null;


        }
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point current = e.getPoint();

            Node currNode = null;

            if (pointStart == null || pointEnd == null) {
                currNode = new Node(0, current.x, current.y);
                isMarking = false;
            }

            if (pointStart == null) {
                pointStart = findNearest(currNode);
                isWorking = true;
                repaint();
            } else if (pointEnd == null) {
                pointEnd = findNearest(currNode);
                repaint();
                if (pointEnd != pointStart) {

                    long startTime = System.nanoTime();

                    if (usingDijkstra)
                        dijkstra();
                    else
                        bellmanFord();

                    long endTime = System.nanoTime();

                    long time = endTime - startTime;

                    System.out.println("Time elapsed in milliseconds: " + time / 1000000);
                } else {
                    pointStart = pointEnd = null;
                }
            }

            repaint();
        }

    }

    private Node findNearest(Node x) {
        double distance = Double.MAX_VALUE;
        Node seeked = null;

        for (Node node : nodes) {
            double newDist = euclideanDist(node.getCenter(), x.getCenter());
            if (newDist < distance) {
                distance = newDist;
                seeked = node;
            }
        }

        return seeked;
    }

    private double euclideanDist(Point p1, Point p2) {
        int x1 = p1.x;
        int y1 = p1.y;

        int x2 = p2.x;
        int y2 = p2.y;
        return Math.sqrt((double) (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private void dijkstra() {
        if (isWorking) {

            cameFrom = new HashMap<>();
            costSoFar = new HashMap<>();

            PriorityQueue<Node> frontier = new PriorityQueue<>();

            pointStart.setPriority(0);
            frontier.add(pointStart);

            cameFrom.put(pointStart, null);
            costSoFar.put(pointStart, 0);

            while (!frontier.isEmpty()) {
                Node current = frontier.remove();

                if (current == pointEnd) {
//                    System.out.println("Found! Total cost: " + costSoFar.get(current));
                    isMarking = true;
                    repaint();
                    break;
                }

                for (Pair<Integer, Integer> nextConnection : current.getConnections()) {

                    Node next = nodes.get(nextConnection.getKey());

                    int newCost = costSoFar.get(current) + nextConnection.getValue();

                    if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {

                        costSoFar.put(next, newCost);
                        next.setPriority(newCost);
                        frontier.add(next);
                        cameFrom.put(next, current);
                    }
                }
            }

            if (!isMarking) {
                System.out.println("No road found");
                pointEnd = pointStart = null;
            }
        }
    }

    private void bellmanFord() {

        //step1
        for (Node node : nodes) {
            costSoFar.put(node, Integer.MAX_VALUE);
            cameFrom.put(node, null);
        }

//        System.out.println("finished step 1");

        cameFrom.put(pointStart, null);
        costSoFar.put(pointStart, 0);

        Queue<Integer> queue = new LinkedList<>();

        ArrayList<Boolean> inQueue = new ArrayList<>(Collections.nCopies(nodes.size(), false));
        ArrayList<Integer> counter = new ArrayList<>(Collections.nCopies(nodes.size(), 0));

        queue.add(pointStart.getId());
        inQueue.set(pointStart.getId(), true);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            inQueue.set(u, false);

            for (Pair<Integer, Integer> connection : nodes.get(u).getConnections()) {

                Integer cost = connection.getValue();
                Integer newCost = costSoFar.get(nodes.get(u)) + cost;
                Integer v = connection.getKey();

                if (newCost < costSoFar.get(nodes.get(v))) {
                    costSoFar.put(nodes.get(v), newCost);
                    cameFrom.put(nodes.get(v), nodes.get(u));

                    if (!inQueue.get(v)) {
                        queue.add(v);
                        inQueue.set(v, true);
                        counter.set(v, counter.get(v) + 1);

                        if(counter.get(v)> nodes.size())
                        {
                            System.out.println("Negative cycle found!");
                            return;
                        }
                    }
                }
            }
        }

        isMarking = true;
        System.out.println(costSoFar.get(pointEnd));

//        step 2:
//
//        int size = nodes.size();
//
//        BigInteger max =  new BigInteger(String.valueOf(BigInteger.valueOf(size).multiply(BigInteger.valueOf(edges.size()))));
//        BigInteger counter = new BigInteger("0");
//
//        for (; ; ) {
//
//            boolean any = false;
//            for (java.util.Map.Entry<Pair<Integer, Integer>, Integer> entry : edges.entrySet()) {
//                Integer from = entry.getKey().getKey();
//                Integer to = entry.getKey().getValue();
//                Integer cost = entry.getValue();
//
//                int newCost = costSoFar.get(nodes.get(from)) + cost;
//
//                if (costSoFar.get(nodes.get(from)) != Integer.MAX_VALUE && newCost < costSoFar.get(nodes.get(to))) {
//                    costSoFar.put(nodes.get(to), newCost);
//                    cameFrom.put(nodes.get(to), nodes.get(from));
//                    any = true;
//                }
////               counter = counter.add(BigInteger.valueOf(1));
////                BigInteger big = counter.multiply(BigInteger.valueOf(100)).divide(max);
////                System.out.println( big + "% (" + counter + " out of " + max + ")");
//            }
//
//            if (!any) {
//                isMarking = true;
//                System.out.println(costSoFar.get(pointEnd));
//                repaint();
//                break;
//            }
//        }
//
//
//        step 3: check for negative weight cycles
//        for (java.util.Map.Entry<Pair<Integer, Integer>, Integer> entry : edges.entrySet()) {
//
//            Integer from = entry.getKey().getKey();
//            Integer to = entry.getKey().getValue();
//            Integer cost = entry.getValue();
//
//            int newCost = costSoFar.get(nodes.get(from)) + cost;
//            if (costSoFar.get(nodes.get(from)) != Integer.MAX_VALUE && newCost < costSoFar.get(nodes.get(to))) {
//                System.err.println("Graph contains a negative-weight cycle");
//                break;
//            }
//        }
    }
}
