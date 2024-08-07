package lk.ijse.gdse68.pos_system_back_end.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse68.pos_system_back_end.bo.custom.OrderBO;
import lk.ijse.gdse68.pos_system_back_end.bo.custom.OrderDetailsBO;
import lk.ijse.gdse68.pos_system_back_end.bo.custom.impl.OrderBOImpl;
import lk.ijse.gdse68.pos_system_back_end.bo.custom.impl.OrderDetailsBOImpl;
import lk.ijse.gdse68.pos_system_back_end.dto.OrderDTO;
import lombok.var;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(urlPatterns = "/orderDetails",loadOnStartup = 3)
public class OrderDetailsServlet extends HttpServlet {

    DataSource connectionPool;
    OrderBO orderBO = new OrderBOImpl();
    OrderDetailsBO orderDetailsBO = new OrderDetailsBOImpl();

    @Override
    public void init() throws ServletException {
        try {
            var ctx = new InitialContext(); //get connection to connection pool
            javax.naming.Context envContext = (Context) ctx.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/pos_system_new");
            this.connectionPool = dataSource;
        } catch (NamingException e) {
            throw new ServletException("Cannot find JNDI resource", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String function = req.getParameter("function");
        String orderId = req.getParameter("orderId");

        if ("getById".equals(function) && orderId != null) {
            try (Connection connection = connectionPool.getConnection()) {
                OrderDTO orderDTO = orderDetailsBO.getOrderDetailsById(connection, orderId);
                System.out.println(orderDTO);
                if (orderDTO != null) {
                    Jsonb jsonb = JsonbBuilder.create();
                    String json = jsonb.toJson(orderDTO);
                    resp.setContentType("application/json");
                    resp.getWriter().write(json);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters");
        }
    }
}
