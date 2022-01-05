package main;

import data_load.Reader;
import util.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        draw();
    }

    public static void draw() {
        SwingUtilities.invokeLater(Main::initUI);
    }

    private static void initUI() {

        System.out.println("Do you want to use dijkstra's? (we can use Bellman-Ford's instead)");
        Scanner sc = new Scanner(System.in);
        boolean usingDijkstra= sc.nextBoolean();


        Reader.innitDoc("res/map2.xml");
        ArrayList<Node> listNodes = Reader.readNodes();
        java.util.Map<Pair<Integer, Integer>, Integer> listEdges = Reader.readEdges();
        Map map = new Map(listNodes, listEdges, usingDijkstra);


        JFrame frame = new JFrame("Luxembourg");
        frame.setSize(1366, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(map);
        frame.setVisible(true);
    }
}
