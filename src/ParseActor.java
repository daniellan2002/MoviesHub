import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseActor {

    List<Actor> actors = new ArrayList<>();
    Document dom;

    public void runExample() {

        // parse the xml file and get the dom object
        parseXmlFile();

        // get each actor element and create an Actor object
        parseDocument();

        // iterate through the list and print the data
        printData();

    }

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            //System.out.println("dom POINT 2");
            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse(new File("actors63.xml"));
            //System.out.println("dom POINT 3");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();
        System.out.println("dom POINT 4");
        // get a nodelist of actor Elements, parse each into an Actor object
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        System.out.println("dom POINT 4.1");
        System.out.println(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the actor element
            Element element = (Element) nodeList.item(i);
            //System.out.println("dom POINT 5");
            // get the Actor object
            Actor actor = parseActor(element);
            //System.out.println("dom POINT 6");
            // add it to the list
            actors.add(actor);
        }
    }

    /**
     * It takes an actor Element, reads the values in, creates
     * an Actor object for return
     */
    private Actor parseActor(Element element) {

        // get values of the different attributes in the <actor> element
        String stagename = getTextValue(element, "stagename");
        int dob = 0; // initialize to a default value
        Node dobNode = element.getElementsByTagName("dob").item(0);
        if (dobNode != null && dobNode.getFirstChild() != null && dobNode.getFirstChild().getNodeValue() != null && !dobNode.getFirstChild().getNodeValue().isEmpty()) {
            try {
                dob = Integer.parseInt(dobNode.getFirstChild().getNodeValue());
            } catch (NumberFormatException e) {
                System.out.println("Invalid value for dob: " + dobNode.getFirstChild().getNodeValue());
            }
        }

        //System.out.println(stagename);
        //System.out.println(dob);
        return new Actor(stagename, dob);
    }


    /**
     * It takes an XML element and the tag name, looks for the tag and gets
     * the text content
     * i.e. for <actor><name>John</name></actor> XML snippet, if
     * the Element points to actor node and tagName is name it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            // here we expect only one <name> would be present in the <actor>
            textVal = nodeList.item(0).getFirstChild().getNodeValue();
        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns an int value
     */
    private int getIntValue(Element element, String tagName) {
        // in a production application you would catch the exception
        return Integer.parseInt(getTextValue(element, tagName));
    }

    /**
     * Iterates through the list and prints the
     * content to console
     */
    private void printData() {

        System.out.println("Total parsed " + actors.size() + " actors");
    }

//    public static void main(String[] args) {
//        // create an instance
//        ParseActor domParserExample = new ParseActor();
//
//        // call run example
//        domParserExample.runExample();
//        if (Actor.namesTable.containsKey("Marlon Brando")) {
//            System.out.println("Marlon Brando is in the star table!");
//        }
//
//    }
}
