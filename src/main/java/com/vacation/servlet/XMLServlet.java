package com.vacation.servlet;

import com.google.gson.Gson;
import com.vacation.model.Destination;
import com.vacation.model.User;
import com.vacation.service.XMLService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class XMLServlet extends HttpServlet {
    private XMLService xmlService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        String contextPath = getServletContext().getRealPath("/WEB-INF/classes");
        xmlService = new XMLService(contextPath);
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
                // Get all destinations
                List<Destination> destinations = xmlService.getAllDestinations();
                out.print(gson.toJson(destinations));
            } else if (pathInfo.equals("/destinations")) {
                // Get all destinations
                List<Destination> destinations = xmlService.getAllDestinations();
                out.print(gson.toJson(destinations));
            } else if (pathInfo.equals("/user")) {
                // Get first user
                User user = xmlService.getFirstUser();
                out.print(gson.toJson(user));
            } else if (pathInfo.equals("/activities")) {
                // Get all unique activities
                Set<String> activities = xmlService.getAllActivities();
                out.print(gson.toJson(activities));
            } else if (pathInfo.equals("/byBudget")) {
                // Task 6: Get destinations by budget
                String budgetParam = request.getParameter("budget");
                if (budgetParam != null) {
                    double budget = Double.parseDouble(budgetParam);
                    List<Destination> destinations = xmlService.getDestinationsByBudget(budget);
                    out.print(gson.toJson(destinations));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Budget parameter required\"}");
                }
            } else if (pathInfo.equals("/recommended")) {
                // Task 7: Get recommended destinations
                User user = xmlService.getFirstUser();
                if (user != null) {
                    List<Destination> destinations = xmlService.getRecommendedDestinations(user);
                    Map<String, Object> result = new HashMap<>();
                    result.put("user", user);
                    result.put("destinations", destinations);
                    out.print(gson.toJson(result));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"No user found\"}");
                }
            } else if (pathInfo.equals("/byActivity")) {
                // Task 10: Get destinations by activity
                String activity = request.getParameter("activity");
                if (activity != null) {
                    List<Destination> destinations = xmlService.getDestinationsByActivity(activity);
                    out.print(gson.toJson(destinations));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Activity parameter required\"}");
                }
            } else if (pathInfo.equals("/byName")) {
                // Task 9: Get destination by name
                String name = request.getParameter("name");
                if (name != null) {
                    Destination destination = xmlService.getDestinationByName(name);
                    if (destination != null) {
                        out.print(gson.toJson(destination));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"error\": \"Destination not found\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Name parameter required\"}");
                }
            } else if (pathInfo.equals("/transform")) {
                // Task 8: Transform XML to HTML using XSLT
                response.setContentType("text/html");
                User user = xmlService.getFirstUser();
                String preferredActivity = user != null ? user.getPreferredActivity() : "randonnée";

                String xsltPath = getServletContext().getRealPath("/WEB-INF/classes/xslt/destinations.xsl");
                String html = xmlService.transformToHTML(preferredActivity, xsltPath);
                out.print(html);
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
            if (pathInfo.equals("/addDestination")) {
                // Task 4: Add new destination
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                int duration = Integer.parseInt(request.getParameter("duration"));
                String activity1 = request.getParameter("activity1");
                String activity2 = request.getParameter("activity2");
                double budget = Double.parseDouble(request.getParameter("budget"));

                // Validation
                if (name == null || name.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Name is required\"}");
                    return;
                }
                if (duration <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Duration must be positive\"}");
                    return;
                }
                if (budget <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Budget must be positive\"}");
                    return;
                }

                int newId = xmlService.getNextDestinationId();
                Destination destination = new Destination(newId, name, description, duration,
                        activity1, activity2, budget);

                xmlService.addDestination(destination);

                out.print(gson.toJson(Map.of("success", true, "destination", destination)));

            } else if (pathInfo.equals("/addUser")) {
                // Task 5: Add new user
                String firstName = request.getParameter("firstName");
                String lastName = request.getParameter("lastName");
                int availability = Integer.parseInt(request.getParameter("availability"));
                String preferredActivity = request.getParameter("preferredActivity");
                double budget = Double.parseDouble(request.getParameter("budget"));

                // Validation
                if (firstName == null || firstName.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"First name is required\"}");
                    return;
                }
                if (lastName == null || lastName.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Last name is required\"}");
                    return;
                }
                if (availability <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Availability must be positive\"}");
                    return;
                }
                if (budget <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Budget must be positive\"}");
                    return;
                }

                int newId = xmlService.getNextUserId();
                User user = new User(newId, firstName, lastName, availability,
                        preferredActivity, budget);

                xmlService.addUser(user);

                out.print(gson.toJson(Map.of("success", true, "user", user)));

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
