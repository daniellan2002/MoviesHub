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

public class ParseMovie {

    ArrayList<MovieInfo> movies = null;
    Document dom;

    public ParseMovie(ArrayList<MovieInfo> movies) {
        this.movies = movies;
    }
    public ParseMovie() {
        this.movies = new ArrayList<>();
    }
    public void runParse() {

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

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse(new File("mains243.xml"));


        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();
       // System.out.println("dom POINT 4");
        // get a nodelist of actor Elements, parse each into an Actor object
        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");
        //System.out.println("dom POINT 4.1");
        System.out.println(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the actor element
            Element element = (Element) nodeList.item(i);
            // get the Actor object
            ArrayList<MovieInfo> movie = parseMovieInfo(element);
            // add it to the list
            if(movie != null)
            {
                movies.addAll(movie);
            }

        }
    }

    /**
     * It takes an actor Element, reads the values in, creates
     * an Actor object for return
     */
    private ArrayList<MovieInfo> parseMovieInfo(Element element) {
        ArrayList<MovieInfo> thisDir = new ArrayList<>();
        // get the director Element
        Element directorElement = (Element) element.getElementsByTagName("director").item(0);
        String dirName = getTextValue(directorElement, "dirname");
        if(dirName == null) return null;

        // get the films Element
        Element filmsElement = (Element) element.getElementsByTagName("films").item(0);

        // get a nodelist of film Elements, parse each into a Film object and add it to the films list
        NodeList filmNodes = filmsElement.getElementsByTagName("film");

        for (int i = 0; i < filmNodes.getLength(); i++) {
            Element filmElement = (Element) filmNodes.item(i);
            String fid = getTextValue(filmElement, "fid");
            String title = getTextValue(filmElement, "t");
            String yearString = getTextValue(filmElement, "year");
            if(yearString == null || title == null)
            {
                return null;
            }
            //System.out.println(yearString);
            int year = 0;
            if (yearString.matches("\\d+")) {
                // Parse the year element as an integer
                year = Integer.parseInt(yearString);
                // Do something with the year value...
            } else {
                // Handle the case where the year element is not a valid integer
                System.out.println("Invalid year format: " + yearString);
                return null;
            }
            ArrayList<Categories> catList = new ArrayList<>();
            Element catsElement = (Element) element.getElementsByTagName("cats").item(0);
            //ArrayList<String> cats = new ArrayList<>();
            if(catsElement != null) {
                NodeList catNode = catsElement.getElementsByTagName("cat");
                for (int j = 0; j < catNode.getLength(); j++) {
                    Element cat = (Element) catNode.item(i);
                    String des = getTextValue(filmElement, "cat");
                    if(des != null) {
                        if (des.matches("^[a-zA-Z0-9]+$")) {
                            if(des.equals("Dram"))
                            {
                                des = "Drama";
                            }

                            Categories newCat = new Categories();
                            newCat.setCategory(des);
                            catList.add(newCat);
                        }
                    }
                }
            }
            MovieInfo aMovie = new MovieInfo(fid, title, year, dirName, catList);
            //aMovie.setCatcat(catList);
            thisDir.add(aMovie);
            //System.out.println(aMovie.toString());
        }

        return thisDir;
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
            //textVal = nodeList.item(0).getFirstChild().getNodeValue();
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

        System.out.println("Total parsed " + movies.size() + " movies");
        for(int i = 0; i < 3; i++)
        {
            System.out.println( "S1: " + movies.get(i).getTitle());
            System.out.println( movies.get(i).getDirector());
            System.out.println( movies.get(i).getYear());
            System.out.println( movies.get(i).getCatcat());
        }
    }

//    public static void main(String[] args) {
//        // create an instance
//        ParseMovie domParserExample = new ParseMovie();
//
//        // call run example
//        domParserExample.runParse();
//        if (MovieInfo.movieHash.containsKey("All the Pretty Horses")) {
//            System.out.println("YES!");
//        }
//    }
}
