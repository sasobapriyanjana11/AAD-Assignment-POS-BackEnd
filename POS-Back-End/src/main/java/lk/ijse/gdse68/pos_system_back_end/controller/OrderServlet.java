package lk.ijse.gdse68.pos_system_back_end.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
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
import lombok.SneakyThrows;
import lombok.var;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "order", urlPatterns = "/order", loadOnStartup = 3)
public class OrderServlet extends HttpServlet {
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

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String function = req.getParameter("function");
//
//        if ("getAll".equals(function)) {
//            try (Connection connection = connectionPool.getConnection()) {
//                ArrayList<OrderDTO> orderList = orderBO.getAllOrders(connection);
//
//                Jsonb jsonb = JsonbBuilder.create();
//                String json = jsonb.toJson(orderList);
//                resp.getWriter().write(json);
//            } catch (JsonbException | IOException | SQLException e) {
//                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
//            }
//        }
        String function = req.getParameter("function");
        String orderId = req.getParameter("orderId");

        try (Connection connection = connectionPool.getConnection()) {
            if ("getLastId".equals(function)) {
                // Handle 'getLastId' functionality
                String lastId = orderBO.getLastId(connection);
                resp.setContentType("text/plain");
                resp.getWriter().write(lastId);

            } else if ("getById".equals(function) && orderId != null) {
                // Handle 'getById' functionality
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

            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try (Connection connection = connectionPool.getConnection()) {
//            Jsonb jsonb = JsonbBuilder.create();
//            OrderDTO orderDTO = jsonb.fromJson(req.getReader(), OrderDTO.class);
//
//            boolean isSaved = orderBO.placeOrder(connection, orderDTO);
//            if (isSaved) {
//                resp.setStatus(HttpServletResponse.SC_CREATED);
//            } else {
//                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save order");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
//        }
//    }

        try (Connection connection = connectionPool.getConnection()) {

            Jsonb jsonb = JsonbBuilder.create();
            OrderDTO orderDTO = jsonb.fromJson(req.getReader(), OrderDTO.class);
            System.out.println(orderDTO);

            if (orderDTO.getOrderId() == null || !orderDTO.getOrderId().matches("^(ORD-)[0-9]{3}$")) {
                resp.getWriter().write("Order id is empty or invalid!");
                return;
//           } else if (orderDTO.getDate() == null || !orderDTO.getDate().toString().matches("\\d{4}-\\d{2}-\\d{2}")) {
//               resp.getWriter().write("Date is empty or invalid!");
//               return;
            } else if (orderDTO.getCustId() == null || !orderDTO.getCustId().matches("^(C00-)[0-9]{3}$")) {
                resp.getWriter().write("Customer id is empty or invalid!");
                return;
            } else if (orderDTO.getDiscount() == null || !orderDTO.getDiscount().toString().matches("\\d+(\\.\\d+)?")) {
                resp.getWriter().write("Discount is empty or invalid!");
                return;
            } else if (orderDTO.getTotal() == null || !orderDTO.getTotal().toString().matches("\\d+(\\.\\d+)?")) {
                resp.getWriter().write("Total is empty or invalid!");
                return;
            } else if (orderDTO.getOrder_list() == null || orderDTO.getOrder_list().isEmpty()) {
                resp.getWriter().write("Order details list is empty!");
                return;
            }

            boolean isOrder = orderBO.placeOrder(connection, orderDTO);
            if (isOrder) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("Save Order Successfully!!");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add Order");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "transaction failed");
        }

    }
}
