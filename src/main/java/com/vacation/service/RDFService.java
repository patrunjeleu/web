package com.vacation.service;

import com.vacation.model.Destination;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;

import java.io.*;
import java.util.*;

public class RDFService {
    private static final String NAMESPACE = "http://www.vacation.com/ontology#";
    private static final String RESOURCE_BASE = "http://www.vacation.com/";

    private Model model;
    private String rdfFilePath;

    public RDFService(String contextPath) {
        this.rdfFilePath = contextPath + "/data/destinations.rdf";
        loadRDF();
    }

    // Load RDF file
    public void loadRDF() {
        try {
            model = ModelFactory.createDefaultModel();
            File file = new File(rdfFilePath);
            if (file.exists()) {
                try (InputStream in = new FileInputStream(file)) {
                    model.read(in, null, "RDF/XML");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            model = ModelFactory.createDefaultModel();
        }
    }

    // Save RDF to file
    public void saveRDF() {
        try (OutputStream out = new FileOutputStream(rdfFilePath)) {
            RDFDataMgr.write(out, model, RDFFormat.RDFXML_PRETTY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get model (for graph visualization)
    public Model getModel() {
        return model;
    }

    // Load RDF from uploaded file
    public void loadRDFFromFile(InputStream inputStream) {
        try {
            model = ModelFactory.createDefaultModel();
            model.read(inputStream, null, "RDF/XML");
            saveRDF();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get all destinations from RDF
    public List<Map<String, String>> getAllDestinations() {
        List<Map<String, String>> destinations = new ArrayList<>();

        String queryString =
            "PREFIX vac: <" + NAMESPACE + "> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "SELECT ?dest ?name ?description ?budget ?duration " +
            "WHERE { " +
            "  ?dest rdf:type vac:Destination . " +
            "  ?dest vac:name ?name . " +
            "  OPTIONAL { ?dest vac:description ?description } " +
            "  OPTIONAL { ?dest vac:budget ?budget } " +
            "  OPTIONAL { ?dest vac:duration ?duration } " +
            "} ORDER BY ?name";

        try {
            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    Map<String, String> dest = new HashMap<>();
                    dest.put("uri", soln.getResource("dest").getURI());
                    dest.put("name", soln.getLiteral("name").getString());
                    if (soln.contains("description"))
                        dest.put("description", soln.getLiteral("description").getString());
                    if (soln.contains("budget"))
                        dest.put("budget", soln.getLiteral("budget").getString());
                    if (soln.contains("duration"))
                        dest.put("duration", soln.getLiteral("duration").getString());
                    destinations.add(dest);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return destinations;
    }

    // Get destination details by URI
    public Map<String, Object> getDestinationDetails(String destinationURI) {
        Map<String, Object> details = new HashMap<>();

        String queryString =
            "PREFIX vac: <" + NAMESPACE + "> " +
            "SELECT ?property ?value " +
            "WHERE { " +
            "  <" + destinationURI + "> ?property ?value . " +
            "}";

        try {
            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    String property = soln.getResource("property").getLocalName();
                    RDFNode value = soln.get("value");

                    if (value.isLiteral()) {
                        details.put(property, value.asLiteral().getValue());
                    } else if (value.isResource()) {
                        details.put(property, value.asResource().getURI());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return details;
    }

    // Get destination requirements (for Part 2, Task 5)
    public Map<String, Object> getDestinationRequirements(String destinationName) {
        Map<String, Object> requirements = new HashMap<>();

        String queryString =
            "PREFIX vac: <" + NAMESPACE + "> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "SELECT ?dest ?budget ?duration ?activity " +
            "WHERE { " +
            "  ?dest rdf:type vac:Destination . " +
            "  ?dest vac:name \"" + destinationName + "\" . " +
            "  ?dest vac:budget ?budget . " +
            "  ?dest vac:duration ?duration . " +
            "  ?dest vac:hasActivity ?activity . " +
            "}";

        try {
            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                ResultSet results = qexec.execSelect();
                List<String> activities = new ArrayList<>();

                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    requirements.put("uri", soln.getResource("dest").getURI());
                    requirements.put("budget", soln.getLiteral("budget").getValue());
                    requirements.put("duration", soln.getLiteral("duration").getValue());
                    activities.add(soln.getResource("activity").getURI());
                }

                requirements.put("activities", activities);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requirements;
    }

    // Add new destination to RDF
    public void addDestination(String name, String description, double budget,
                              int duration, String activity1, String activity2) {
        try {
            // Create destination resource
            String destURI = RESOURCE_BASE + "destinations/" + name.toLowerCase().replaceAll("\\s+", "-");
            Resource destination = model.createResource(destURI);

            // Add properties
            Property typeProperty = RDF.type;
            Resource destinationType = model.createResource(NAMESPACE + "Destination");
            destination.addProperty(typeProperty, destinationType);

            Property nameProperty = model.createProperty(NAMESPACE, "name");
            destination.addProperty(nameProperty, name);

            Property descProperty = model.createProperty(NAMESPACE, "description");
            destination.addProperty(descProperty, description);

            Property budgetProperty = model.createProperty(NAMESPACE, "budget");
            destination.addProperty(budgetProperty, model.createTypedLiteral(budget));

            Property durationProperty = model.createProperty(NAMESPACE, "duration");
            destination.addProperty(durationProperty, model.createTypedLiteral(duration));

            Property hasActivityProperty = model.createProperty(NAMESPACE, "hasActivity");

            // Add activities
            if (activity1 != null && !activity1.isEmpty()) {
                String act1URI = RESOURCE_BASE + "activities/" + activity1.toLowerCase();
                Resource activity1Res = model.createResource(act1URI);
                destination.addProperty(hasActivityProperty, activity1Res);

                // Create activity if it doesn't exist
                if (!model.contains(activity1Res, null)) {
                    activity1Res.addProperty(RDF.type, model.createResource(NAMESPACE + "Activity"));
                    Property actNameProperty = model.createProperty(NAMESPACE, "activityName");
                    activity1Res.addProperty(actNameProperty, activity1);
                }

                Property primaryActivityProperty = model.createProperty(NAMESPACE, "primaryActivity");
                destination.addProperty(primaryActivityProperty, activity1Res);
            }

            if (activity2 != null && !activity2.isEmpty()) {
                String act2URI = RESOURCE_BASE + "activities/" + activity2.toLowerCase();
                Resource activity2Res = model.createResource(act2URI);
                destination.addProperty(hasActivityProperty, activity2Res);

                // Create activity if it doesn't exist
                if (!model.contains(activity2Res, null)) {
                    activity2Res.addProperty(RDF.type, model.createResource(NAMESPACE + "Activity"));
                    Property actNameProperty = model.createProperty(NAMESPACE, "activityName");
                    activity2Res.addProperty(actNameProperty, activity2);
                }
            }

            saveRDF();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update destination in RDF
    public void updateDestination(String destinationURI, String property, String value) {
        try {
            Resource destination = model.getResource(destinationURI);
            Property prop = model.createProperty(NAMESPACE, property);

            // Remove old value
            model.removeAll(destination, prop, null);

            // Add new value
            if (property.equals("budget") || property.equals("duration")) {
                destination.addProperty(prop, model.createTypedLiteral(Double.parseDouble(value)));
            } else {
                destination.addProperty(prop, value);
            }

            saveRDF();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Execute SPARQL query
    public List<Map<String, String>> executeSPARQL(String queryString) {
        List<Map<String, String>> results = new ArrayList<>();

        try {
            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                ResultSet resultSet = qexec.execSelect();

                while (resultSet.hasNext()) {
                    QuerySolution soln = resultSet.nextSolution();
                    Map<String, String> row = new HashMap<>();

                    Iterator<String> varNames = soln.varNames();
                    while (varNames.hasNext()) {
                        String varName = varNames.next();
                        RDFNode node = soln.get(varName);

                        if (node != null) {
                            if (node.isLiteral()) {
                                row.put(varName, node.asLiteral().getString());
                            } else if (node.isResource()) {
                                row.put(varName, node.asResource().getURI());
                            }
                        }
                    }

                    results.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    // Get graph data for visualization (Jung)
    public Map<String, Object> getGraphData() {
        Map<String, Object> graphData = new HashMap<>();
        List<Map<String, String>> nodes = new ArrayList<>();
        List<Map<String, String>> edges = new ArrayList<>();

        try {
            // Get all statements
            StmtIterator iter = model.listStatements();

            Set<String> nodeSet = new HashSet<>();

            while (iter.hasNext()) {
                Statement stmt = iter.next();
                Resource subject = stmt.getSubject();
                Property predicate = stmt.getPredicate();
                RDFNode object = stmt.getObject();

                // Add subject as node
                String subjectId = getNodeId(subject);
                if (nodeSet.add(subjectId)) {
                    Map<String, String> node = new HashMap<>();
                    node.put("id", subjectId);
                    node.put("label", getLabel(subject));
                    node.put("type", getNodeType(subject));
                    nodes.add(node);
                }

                // Add object as node if it's a resource
                if (object.isResource()) {
                    String objectId = getNodeId(object.asResource());
                    if (nodeSet.add(objectId)) {
                        Map<String, String> node = new HashMap<>();
                        node.put("id", objectId);
                        node.put("label", getLabel(object.asResource()));
                        node.put("type", getNodeType(object.asResource()));
                        nodes.add(node);
                    }

                    // Add edge
                    Map<String, String> edge = new HashMap<>();
                    edge.put("source", subjectId);
                    edge.put("target", objectId);
                    edge.put("label", predicate.getLocalName());
                    edges.add(edge);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        graphData.put("nodes", nodes);
        graphData.put("edges", edges);
        return graphData;
    }

    // Get graph data with highlighted nodes for a destination
    public Map<String, Object> getGraphDataWithHighlight(String destinationName) {
        Map<String, Object> graphData = getGraphData();

        // Get destination requirements
        Map<String, Object> requirements = getDestinationRequirements(destinationName);

        if (requirements.containsKey("uri")) {
            Set<String> highlightNodes = new HashSet<>();
            highlightNodes.add(getNodeId(model.getResource((String) requirements.get("uri"))));

            // Add activity nodes
            if (requirements.containsKey("activities")) {
                @SuppressWarnings("unchecked")
                List<String> activities = (List<String>) requirements.get("activities");
                for (String activityURI : activities) {
                    highlightNodes.add(getNodeId(model.getResource(activityURI)));
                }
            }

            graphData.put("highlight", highlightNodes);
        }

        return graphData;
    }

    // Helper methods
    private String getNodeId(Resource resource) {
        if (resource.isAnon()) {
            return resource.getId().toString();
        }
        return resource.getURI();
    }

    private String getLabel(Resource resource) {
        // Try to get name or label
        Property nameProperty = model.createProperty(NAMESPACE, "name");
        Property labelProperty = model.createProperty(NAMESPACE, "activityName");

        if (resource.hasProperty(nameProperty)) {
            return resource.getProperty(nameProperty).getString();
        } else if (resource.hasProperty(labelProperty)) {
            return resource.getProperty(labelProperty).getString();
        } else {
            return resource.getLocalName();
        }
    }

    private String getNodeType(Resource resource) {
        Property typeProperty = RDF.type;
        if (resource.hasProperty(typeProperty)) {
            return resource.getProperty(typeProperty).getResource().getLocalName();
        }
        return "Unknown";
    }
}
