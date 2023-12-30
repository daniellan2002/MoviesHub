import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseCast {

    ArrayList<Actor> actors;
    ArrayList<MovieInfo> movies;
    ArrayList<Cast> cast = new ArrayList<>();
    Document dom;

    public ParseCast(ArrayList<Actor> actors, ArrayList<MovieInfo> movies) {
        this.actors = actors;
        this.movies = movies;
    }

    public void runExample() throws IOException {

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
            System.out.println("dom POINT 2");
            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse(new File("casts124.xml"));
            System.out.println("dom POINT 3");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() throws IOException {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();
        System.out.println("cast POINT 4");
        // get a nodelist of actor Elements, parse each into an Actor object
        NodeList nodeList = documentElement.getElementsByTagName("dirfilms");
        System.out.println("cast POINT 4.1");
        System.out.println(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {


            Element element = (Element) nodeList.item(i);

            List<Cast> aInM = parseAM(element);
            //System.out.println("cast POINT 5");

            if (aInM.size() != 0)
            {
                cast.addAll(aInM);
                //System.out.println("cast POINT 6");
            }
        }
    }

    public List<Cast> parseAM(Element element) throws IOException {
        List<Cast> actor_movie_pair = new ArrayList<>();
        Element filmcElement = (Element) element.getElementsByTagName("filmc").item(0);
        NodeList filmNodes = filmcElement.getElementsByTagName("m");
        try {
            FileWriter writer = new FileWriter("inconsistency_report.txt", true);
            for (int i = 0; i < filmNodes.getLength(); i++) {
                Element filmElement = (Element) filmNodes.item(i);
                //String fid = getTextValue(filmElement, "f");
                String title = getTextValue(filmElement, "t");
                if (title == null) continue;
                //System.out.println("cast POINT 7");

                String actorName = getTextValue(filmElement, "a");
                if (actorName == null) continue;

                //System.out.println("cast POINT 8");

                String moviesDBID = "";
                String actorDBID = "";
                for (MovieInfo movie : movies) {
                    if (movie.getTitle().equals(title)) {
                        //System.out.println("cast POINT 9");
                        //System.out.println("Legit movie: " + fid);
                        moviesDBID = movie.getDBID();
                        //System.out.println("Legit moviesDBID: " + moviesDBID);
                        break;
                    }
                }
                if(moviesDBID.equals(""))
                {
                    System.out.println("This movie is not in the movie table: " + title);
                    writer.write("This movie is not in the movie table: " + title + "\n");
                    continue;
                }
                for (Actor act : actors) {
                    if (act.getStagename().equals(actorName)) {
                        actorDBID = act.getDBID();
                        //System.out.println("cast POINT 10");
                        break;
                    }
                }
                if(actorDBID.equals(""))
                {
                    System.out.println("This star is not in the star table: " + actorName);
                   // System.out.println("cast POINT 11");
                    writer.write("This star is not in the star table: " + actorName + "\n");
                    continue;
                }
                //System.out.println("cast POINT 12");
                Cast newCast = new Cast(actorDBID, moviesDBID);
                //System.out.println("** " + actorDBID + "," + moviesDBID);
                actor_movie_pair.add(newCast);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

            return actor_movie_pair;
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

            Node firstChild = nodeList.item(0).getFirstChild();
            if (firstChild != null) {
                textVal = firstChild.getNodeValue();
            } else {
                System.out.println("Element " + tagName + " has no text content.");
            }
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

        System.out.println("Total parsed " + cast.size() + " casts");
    }

}
