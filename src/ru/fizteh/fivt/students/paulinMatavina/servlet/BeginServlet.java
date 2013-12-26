package ru.fizteh.fivt.students.paulinMatavina.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")

public class BeginServlet extends HttpServlet {
    private Database database;
    public BeginServlet(Database newBase) {
        database = newBase;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                                                throws ServletException, IOException {
        String name = request.getParameter("table");
        if (name == null) {
            String text = name + "no such table";
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text);
            return;
        }
        
        String transactionId = database.makeNewID(name);
        ServletUtils.sendInfo(response, "tid=" + new Integer(transactionId).toString());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF8");
        response.getWriter().println("tid=" + transactionId);
    }

}
