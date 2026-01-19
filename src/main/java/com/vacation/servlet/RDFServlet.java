package com.vacation.servlet;

import com.google.gson.Gson;
import com.vacation.service.RDFService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class RDFServlet extends HttpServlet {
    private RDFService rdfService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        String contextPath = getServletContext().getRealPath("/WEB-INF/classes");
        rdfService = new RDFService(contextPath);
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all destinations from RDF
                List<Map<String, String>> destinations = rdfService.getAllDestinations();
                out.print(gson.toJson(destinations));

            } else if (pathInfo.equals("/destinations")) {
                // Part 2, Task 4: Get all destinations
                List<Map<String, String>> destinations = rdfService.getAllDestinations();
                out.print(gson.toJson(destinations));

            } else if (pathInfo.equals("/destination")) {
                // Get destination details
                String uri = request.getParameter("uri");
                if (uri != null) {
                    Map<String, Object> details = rdfService.getDestinationDetails(uri);
                    out.print(gson.toJson(details));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"URI parameter required\"}");
                }

            } else if (pathInfo.equals("/requirements")) {
                // Part 2, Task 5: Get destination requirements
                String name = request.getParameter("name");
                if (name != null) {
                    Map<String, Object> requirements = rdfService.getDestinationRequirements(name);
                    out.print(gson.toJson(requirements));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Name parameter required\"}");
                }

            } else if (pathInfo.equals("/graph")) {
                // Part 2, Task 2: Get graph data for visualization
                Map<String, Object> graphData = rdfService.getGraphData();
                out.print(gson.toJson(graphData));

            } else if (pathInfo.equals("/graph/highlight")) {
                // Part 2, Task 5: Get graph with highlighted nodes
                String name = request.getParameter("name");
                if (name != null) {
                    Map<String, Object> graphData = rdfService.getGraphDataWithHighlight(name);
                    out.print(gson.toJson(graphData));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Name parameter required\"}");
                }

            } else if (pathInfo.equals("/sparql")) {
                // Execute SPARQL query
                String query = request.getParameter("query");
                if (query != null) {
                    List<Map<String, String>> results = rdfService.executeSPARQL(query);
                    out.print(gson.toJson(results));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Query parameter required\"}");
                }

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Endpoint not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (pathInfo.equals("/upload")) {
                // Part 2, Task 2: Upload RDF file
                if (ServletFileUpload.isMultipartContent(request)) {
                    DiskFileItemFactory factory = new DiskFileItemFactory();
                    ServletFileUpload upload = new ServletFileUpload(factory);

                    List<FileItem> items = upload.parseRequest(request);
                    for (FileItem item : items) {
                        if (!item.isFormField() && item.getName().endsWith(".rdf")) {
                            InputStream inputStream = item.getInputStream();
                            rdfService.loadRDFFromFile(inputStream);
                            inputStream.close();

                            out.print(gson.toJson(Map.of("success", true,
                                    "message", "RDF file uploaded successfully")));
                            return;
                        }
                    }

                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"No RDF file found in upload\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Request must be multipart\"}");
                }

            } else if (pathInfo.equals("/addDestination")) {
                // Part 2, Task 3: Add new destination to RDF
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                double budget = Double.parseDouble(request.getParameter("budget"));
                int duration = Integer.parseInt(request.getParameter("duration"));
                String activity1 = request.getParameter("activity1");
                String activity2 = request.getParameter("activity2");

                // Validation
                if (name == null || name.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Name is required\"}");
                    return;
                }

                rdfService.addDestination(name, description, budget, duration, activity1, activity2);

                out.print(gson.toJson(Map.of("success", true,
                        "message", "Destination added to RDF successfully")));

            } else if (pathInfo.equals("/updateDestination")) {
                // Part 2, Task 3: Update destination in RDF
                String uri = request.getParameter("uri");
                String property = request.getParameter("property");
                String value = request.getParameter("value");

                if (uri == null || property == null || value == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"URI, property, and value are required\"}");
                    return;
                }

                rdfService.updateDestination(uri, property, value);

                out.print(gson.toJson(Map.of("success", true,
                        "message", "Destination updated successfully")));

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Endpoint not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}
