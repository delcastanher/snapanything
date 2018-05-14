package com.github.delcastanher.snapanything;

import twitter4j.TwitterException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "TimelineAppEngine", value = "/timeline")
public class TimelineAppEngine extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        String retweetsOfMe = request.getParameter("rt");
        SnapTwitter snap = new SnapTwitter();
        try {
            if (retweetsOfMe != null) {
                snap.snapRetweets();
            } else {
                snap.snapTimeline();
            }
            List<String> deletedStatuses = snap.getDeletedStatuses();
            response.getWriter().println(deletedStatuses.size() + " tweets deleted");
            for (String status: deletedStatuses){
                response.getWriter().println(status);
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            response.getWriter().println("Failed to get timeline: " + te.getMessage());
        }
    }
}