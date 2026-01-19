package com.vacation.service;

import com.vacation.model.Destination;
import com.vacation.model.User;
import net.sf.saxon.s9api.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import java.io.*;
import java.util.*;

public class XMLService {
    private static final String XML_FILE_PATH = "data/destinations.xml";
    private static final String XSD_FILE_PATH = "data/destinations.xsd";
    private static final String XSLT_FILE_PATH = "xslt/destinations.xsl";

    private Document document;
    private String xmlFilePath;

    public XMLService(String contextPath) {
        this.xmlFilePath = contextPath + "/" + XML_FILE_PATH;
        loadXML();
    }

    // Load XML file into memory
    private void loadXML() {
        try {
            File file = new File(xmlFilePath);
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save XML to file
    public void saveXML() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add new destination to XML
    public void addDestination(Destination dest) {
        try {
            Element root = document.getDocumentElement();
            NodeList destinations = root.getElementsByTagName("destinations");
            Element destinationsElement = (Element) destinations.item(0);

            // Create new destination element
            Element destinationElement = document.createElement("destination");
            destinationElement.setAttribute("id", String.valueOf(dest.getId()));

            Element name = document.createElement("name");
            name.setTextContent(dest.getName());
            destinationElement.appendChild(name);

            Element description = document.createElement("description");
            description.setTextContent(dest.getDescription());
            destinationElement.appendChild(description);

            Element duration = document.createElement("duration");
            duration.setTextContent(String.valueOf(dest.getDuration()));
            destinationElement.appendChild(duration);

            Element activity1 = document.createElement("activity1");
            activity1.setTextContent(dest.getActivity1());
            destinationElement.appendChild(activity1);

            Element activity2 = document.createElement("activity2");
            activity2.setTextContent(dest.getActivity2());
            destinationElement.appendChild(activity2);

            Element budget = document.createElement("budget");
            budget.setTextContent(String.valueOf(dest.getBudget()));
            destinationElement.appendChild(budget);

            destinationsElement.appendChild(destinationElement);
            saveXML();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add new user to XML
    public void addUser(User user) {
        try {
            Element root = document.getDocumentElement();
            NodeList users = root.getElementsByTagName("users");
            Element usersElement = (Element) users.item(0);

            // Create new user element
            Element userElement = document.createElement("user");
            userElement.setAttribute("id", String.valueOf(user.getId()));

            Element firstName = document.createElement("firstName");
            firstName.setTextContent(user.getFirstName());
            userElement.appendChild(firstName);

            Element lastName = document.createElement("lastName");
            lastName.setTextContent(user.getLastName());
            userElement.appendChild(lastName);

            Element availability = document.createElement("availability");
            availability.setTextContent(String.valueOf(user.getAvailability()));
            userElement.appendChild(availability);

            Element preferredActivity = document.createElement("preferredActivity");
            preferredActivity.setTextContent(user.getPreferredActivity());
            userElement.appendChild(preferredActivity);

            Element budget = document.createElement("budget");
            budget.setTextContent(String.valueOf(user.getBudget()));
            userElement.appendChild(budget);

            usersElement.appendChild(userElement);
            saveXML();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get first user (for recommendations)
    public User getFirstUser() {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile("//user[1]");

            Node userNode = (Node) expr.evaluate(document, XPathConstants.NODE);
            if (userNode == null) return null;

            Element userElement = (Element) userNode;
            User user = new User();
            user.setId(Integer.parseInt(userElement.getAttribute("id")));
            user.setFirstName(getElementText(userElement, "firstName"));
            user.setLastName(getElementText(userElement, "lastName"));
            user.setAvailability(Integer.parseInt(getElementText(userElement, "availability")));
            user.setPreferredActivity(getElementText(userElement, "preferredActivity"));
            user.setBudget(Double.parseDouble(getElementText(userElement, "budget")));

            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get destinations by budget (Task 6)
    public List<Destination> getDestinationsByBudget(double maxBudget) {
        List<Destination> results = new ArrayList<>();
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            String expression = String.format("//destination[budget <= %f]", maxBudget);
            XPathExpression expr = xpath.compile(expression);

            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                results.add(parseDestination(element));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get destinations matching at least 2 of 3 criteria (Task 7)
    public List<Destination> getRecommendedDestinations(User user) {
        List<Destination> results = new ArrayList<>();
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // Get all destinations
            XPathExpression expr = xpath.compile("//destination");
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                Destination dest = parseDestination(element);

                int matchCount = 0;
                if (dest.getBudget() <= user.getBudget()) matchCount++;
                if (dest.getDuration() <= user.getAvailability()) matchCount++;
                if (dest.getActivity1().equals(user.getPreferredActivity()) ||
                    dest.getActivity2().equals(user.getPreferredActivity())) matchCount++;

                if (matchCount >= 2) {
                    results.add(dest);
                }
            }

            // Sort by budget (ascending)
            results.sort(Comparator.comparingDouble(Destination::getBudget));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get destination by name (Task 9)
    public Destination getDestinationByName(String name) {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            String expression = String.format("//destination[name='%s']", name);
            XPathExpression expr = xpath.compile(expression);

            Node node = (Node) expr.evaluate(document, XPathConstants.NODE);
            if (node != null) {
                return parseDestination((Element) node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get destinations by activity (Task 10)
    public List<Destination> getDestinationsByActivity(String activity) {
        List<Destination> results = new ArrayList<>();
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            String expression = String.format("//destination[activity1='%s' or activity2='%s']",
                activity, activity);
            XPathExpression expr = xpath.compile(expression);

            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                results.add(parseDestination(element));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get all destinations
    public List<Destination> getAllDestinations() {
        List<Destination> results = new ArrayList<>();
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile("//destination");

            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                results.add(parseDestination(element));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get next available ID for destination
    public int getNextDestinationId() {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile("//destination/@id");

            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            int maxId = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                int id = Integer.parseInt(nodes.item(i).getNodeValue());
                if (id > maxId) maxId = id;
            }
            return maxId + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    // Get next available ID for user
    public int getNextUserId() {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile("//user/@id");

            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            int maxId = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                int id = Integer.parseInt(nodes.item(i).getNodeValue());
                if (id > maxId) maxId = id;
            }
            return maxId + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    // Transform XML to HTML using XSLT (Task 8)
    public String transformToHTML(String preferredActivity, String xsltPath) {
        try {
            // Use Saxon for XSLT 2.0 support
            Processor processor = new Processor(false);
            XsltCompiler compiler = processor.newXsltCompiler();
            XsltExecutable stylesheet = compiler.compile(new StreamSource(new File(xsltPath)));

            Xslt30Transformer transformer = stylesheet.load30();

            // Set parameter
            XdmAtomicValue activityValue = new XdmAtomicValue(preferredActivity);
            transformer.setStylesheetParameters(
                Collections.singletonMap(new QName("preferredActivity"), activityValue)
            );

            // Transform
            StringWriter writer = new StringWriter();
            Serializer serializer = processor.newSerializer(writer);
            serializer.setOutputProperty(Serializer.Property.METHOD, "html");
            serializer.setOutputProperty(Serializer.Property.INDENT, "yes");

            transformer.transform(new StreamSource(new File(xmlFilePath)), serializer);

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><p>Error transforming XML: " + e.getMessage() + "</p></body></html>";
        }
    }

    // Get all unique activities
    public Set<String> getAllActivities() {
        Set<String> activities = new HashSet<>();
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            XPathExpression expr1 = xpath.compile("//destination/activity1/text()");
            XPathExpression expr2 = xpath.compile("//destination/activity2/text()");

            NodeList nodes1 = (NodeList) expr1.evaluate(document, XPathConstants.NODESET);
            NodeList nodes2 = (NodeList) expr2.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodes1.getLength(); i++) {
                activities.add(nodes1.item(i).getNodeValue());
            }
            for (int i = 0; i < nodes2.getLength(); i++) {
                activities.add(nodes2.item(i).getNodeValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activities;
    }

    // Helper method to parse destination from Element
    private Destination parseDestination(Element element) {
        Destination dest = new Destination();
        dest.setId(Integer.parseInt(element.getAttribute("id")));
        dest.setName(getElementText(element, "name"));
        dest.setDescription(getElementText(element, "description"));
        dest.setDuration(Integer.parseInt(getElementText(element, "duration")));
        dest.setActivity1(getElementText(element, "activity1"));
        dest.setActivity2(getElementText(element, "activity2"));
        dest.setBudget(Double.parseDouble(getElementText(element, "budget")));
        return dest;
    }

    // Helper method to get element text
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }
}
