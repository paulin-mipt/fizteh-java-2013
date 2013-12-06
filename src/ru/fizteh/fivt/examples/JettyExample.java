package ru.fizteh.fivt.examples;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


/**
 * Запустите main и попробуйте открыть браузером этот URL:
 * http://localhost:8008/hello?name=Vasiliy
 */
public class JettyExample {

    /**
     * Этот сервлет будет нежно здороваться с вами, если вы скажете свое имя.
     * В противном случае, он вернет вам код ошибки 400 BAD REQUEST.
     */
    public static class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            // Достать параметр из запроса.
            // Параметры в урле идут после знака вопроса (?), значения следуют за знаком равенства (=),
            // разные пареметры разделены амперсантами (&).
            String name = req.getParameter("name");

            if (name == null) {
                // Послать код ошибки.
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Name expected");
                // Обратите внимание, после отосланного кода ошибки исполнение метода продолжается.
                return;
            }

            // Установим status code в 200 ОК.
            resp.setStatus(HttpServletResponse.SC_OK);

            // Подскажем тип содержимого и кодировку
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF8");

            // В этот writer пишется тело ответа. Это может быть как html-страничка, так и произвольный текст,
            // а также специальным образом закодированные бинарные данные.
            resp.getWriter().println("Здравствуйте, " + name + "!");
        }
    }

    public static void main(String[] args) throws Exception {
        // Создадим HTTP-сервер, слушающий порт 8008
        Server server = new Server(8008);

        // ServletContextHandler умеет располагать сервлеты по относительному пути
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        // Добавим Hello-сервлет
        context.addServlet(new ServletHolder(new HelloServlet()), "/hello");

        // Установим обработчик в сервер и запустим все это
        server.setHandler(context);
        server.start();
    }
}
