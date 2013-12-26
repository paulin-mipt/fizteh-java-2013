package ru.fizteh.fivt.students.paulinMatavina.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUtils {
    public static void tidIsCorrect(String tid) {
        if (tid == null) {
            throw new IllegalArgumentException("no transaction id passed");
        }
        if (tid.length() != 5) {
            throw new IllegalArgumentException("transaction id must contain strictly 5 digits");
        }
        for (int i = 0; i < tid.length(); ++i) {
            if (!Character.isDigit(tid.charAt(i))) {
                throw new IllegalArgumentException("transaction id must contain only digits");
            }
        }
    }
    
    public static void checkParameters(Database database, HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        String tid = request.getParameter("tid");
        try {
            tidIsCorrect(tid);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
        Database.Transaction transaction = database.getTransaction(tid);
        if (transaction == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no transaction " + tid + " found");
            return;
        }
    }
    
    public static void sendInfo(HttpServletResponse response, Object valueToSend) throws IOException {
        try { 
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF8");
            response.getWriter().println(valueToSend.toString());
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
