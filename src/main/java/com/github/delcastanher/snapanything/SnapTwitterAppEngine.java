package com.github.delcastanher.snapanything;

import twitter4j.TwitterException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "SnapTwitterAppEngine", value = "/timeline")
public class SnapTwitterAppEngine extends HttpServlet {

    private final Logger LOGGER = Logger.getLogger(SnapTwitterAppEngine.class.getClass().getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        String retweetsOfMe = request.getParameter("rt");
        String oldTweetsOfMe = request.getParameter("ot");
        SnapTwitter snapTwitter;
        try {
            if (retweetsOfMe != null) {
                snapTwitter = new SnapRetweets();
            } else if (oldTweetsOfMe != null) {
                snapTwitter = new SnapSearch();
            } else {
                snapTwitter = new SnapTimeline();
            }
            List<String> deletedStatuses = snapTwitter.getDeletedStatuses();
            response.getWriter().println(deletedStatuses.size() + " tweets deleted");
            LOGGER.log(Level.INFO, deletedStatuses.size() + " tweets deleted");
            for (String status: deletedStatuses){
                LOGGER.log(Level.INFO, status);
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            response.getWriter().println(te.getMessage());
            LOGGER.log(Level.INFO, te.getMessage());
            LOGGER.log(Level.INFO, te.getExceptionCode());
        }
    }
}