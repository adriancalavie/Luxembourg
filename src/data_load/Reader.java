package data_load;

import main.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.Pair;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Reader {

    static Translator translator = new Translator(5.734153, 6.531256, 50.182918, 49.441140);
    static ArrayList<Node> nodeArrayList = new ArrayList<>();
    static java.util.Map<Pair<Integer, Integer>, Integer> edgeArrayList = new HashMap<>();
    static Document doc = null;

    public static ArrayList<Node> readNodes() {

        assert doc != null;

        org.w3c.dom.Node list = doc.getElementsByTagName("nodes").item(0);


        if (list.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element element = (Element) list;

            NodeList nodes = element.getElementsByTagName("node");

            int size = nodes.getLength();

            for (int i = 0; i < size; ++i) {
                org.w3c.dom.Node node = nodes.item(i);

                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) node;

                    int x = translator.getLat(Double.parseDouble(nodeElement.getAttribute("latitude")) / 100000);
                    int y = translator.getLon(Double.parseDouble(nodeElement.getAttribute("longitude")) / 100000);

                    //System.out.println(x + " " + y);

                    Node n = new Node(Integer.parseInt(nodeElement.getAttribute("id")), x, y);
                    //System.out.println(n.getId());
                    nodeArrayList.add(n);
                }
            }
        }
        return nodeArrayList;
    }

    public static java.util.Map<Pair<Integer, Integer>, Integer> readEdges() {

        assert doc != null;

        org.w3c.dom.Node list = doc.getElementsByTagName("arcs").item(0);

        if (list.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

            Element element = (Element) list;
            NodeList arcs = element.getElementsByTagName("arc");

            int size = arcs.getLength();

            for (int i = 0; i < arcs.getLength(); ++i) {
                org.w3c.dom.Node arc = arcs.item(i);

                if (arc.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element arcElement = (Element) arc;

                    int from = Integer.parseInt(arcElement.getAttribute("from"));
                    int to = Integer.parseInt(arcElement.getAttribute("to"));
                    int cost = Integer.parseInt(arcElement.getAttribute("length"));

                    nodeArrayList.get(from).addEdge(to, cost);
                    edgeArrayList.put(new Pair<>(from, to), cost);
                }
            }
        }

        return edgeArrayList;
    }

    public static void innitDoc(String xmlPath) {

        try {
            File xmlFile = new File(xmlPath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(xmlFile);
            return;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error in Reader.innitDoc.java: " + e);
        }

        doc = null;
    }

}
