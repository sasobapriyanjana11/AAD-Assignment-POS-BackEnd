package lk.ijse.gdse68.pos_system_back_end.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse68.pos_system_back_end.bo.custom.CustomerBO;
import lk.ijse.gdse68.pos_system_back_end.bo.custom.impl.CustomerBOImpl;
import lk.ijse.gdse68.pos_system_back_end.dto.CustomerDTO;
import lombok.SneakyThrows;
import lombok.var;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "customer",urlPatterns = "/customer",loadOnStartup = 3)
public class CustomerServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CustomerServlet.class.getName());
    CustomerBO customerBO = (CustomerBO) new CustomerBOImpl();
    DataSource connectionPool;

    @Override
    public void init() throws ServletException {
        try {
            var ctx = new InitialContext(); //get connection to connection pool
            Context envContext = (Context) ctx.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/pos_system_new");
            this.connectionPool = dataSource;
            LOGGER.info("Database connection pool initialized successfully.");
        } catch (NamingException e) {
            LOGGER.log(Level.SEVERE, "Cannot find JNDI resource", e);
            throw new ServletException("Cannot find JNDI resource", e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = connectionPool.getConnection()) {
            Jsonb jsonb = JsonbBuilder.create();

            CustomerDTO customerDTO = jsonb.fromJson(req.getReader(), CustomerDTO.class);
            LOGGER.info("Received POST request: " + customerDTO);
//            System.out.println(customerDTO);

            if (customerDTO.getId() == null || !customerDTO.getId().matches("^(C00-)[0-9]{3}$")) {
                LOGGER.warning("Invalid customer ID: " + customerDTO.getId());
                resp.getWriter().write("Id is empty or invalid!!");
                return;
            } else if (customerDTO.getName() == null || !customerDTO.getName().matches("^[A-Za-z ]{4,}$")) {
                LOGGER.warning("Invalid customer name: " + customerDTO.getName());
                resp.getWriter().write("Name is empty or invalid!!");
                return;
            } else if (customerDTO.getAddress() == null || !customerDTO.getAddress().matches("^[A-Za-z0-9., -]{5,}$")) {
                LOGGER.warning("Invalid customer address: " + customerDTO.getAddress());
                resp.getWriter().write("Address is empty or invalid!!");
                return;
            } else if (customerDTO.getSalary() <= 0) {
                LOGGER.warning("Invalid customer salary: " + customerDTO.getSalary());
                resp.getWriter().write("Salary is empty or invalid!!");
                return;
            }

            boolean isSaved = customerBO.saveCustomer(connection, customerDTO);
            if (isSaved) {
                LOGGER.info("Customer saved successfully.");
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                LOGGER.severe("Failed to save customer.");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "failed to save customer");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            LOGGER.log(Level.WARNING, "Duplicate values detected", e);
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Duplicate values. Please check again");
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "An error occurred while processing the request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String function = req.getParameter("function");
        LOGGER.info("Received GET request with function: " + function);

        if (function.equals("getAll")) {
            try (Connection connection = connectionPool.getConnection()) {
                ArrayList<CustomerDTO> customerDTOList = customerBO.getAllCustomers(connection);

                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(customerDTOList);
                resp.getWriter().write(json);
                LOGGER.info("Sent all customers.");
            } catch (JsonbException e) {
                LOGGER.log(Level.SEVERE, "JSON processing error", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IO error", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "SQL error", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else if (function.equals("getById")) {
            String id = req.getParameter("id");
            try (Connection connection = connectionPool.getConnection()) {
                CustomerDTO customerDTO = customerBO.getCustomerById(connection, id);

                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(customerDTO);
                resp.getWriter().write(json);
                LOGGER.info("Sent customer with ID: " + id);
            } catch (JsonbException e) {
                LOGGER.log(Level.SEVERE, "JSON processing error", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IO error", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "SQL error", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }


    @SneakyThrows
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        LOGGER.info("Received DELETE request for customer ID: " + id);
        try (Connection connection = connectionPool.getConnection()){
            boolean isDeleted = customerBO.deleteCustomer(connection,id);
            if (isDeleted){
                LOGGER.info("Customer deleted successfully.");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else{
                LOGGER.severe("Failed to delete customer with ID: " + id);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to delete customer!");
            }

        }catch (Exception e){
//            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "An error occurred while processing the request", e);
        }
    }

    @SneakyThrows
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = connectionPool.getConnection()) {
            Jsonb jsonb = JsonbBuilder.create();

            CustomerDTO customerDTO = jsonb.fromJson(req.getReader(), CustomerDTO.class);
//            System.out.println(customerDTO);
            LOGGER.info("Received PUT request: " + customerDTO);

            if (customerDTO.getId() == null || !customerDTO.getId().matches("^(C00-)[0-9]{3}$")) {
                LOGGER.warning("Invalid customer ID for update: " + customerDTO.getId());
                resp.getWriter().write("id is empty or invalid!");
                return;
            } else if (customerDTO.getName() == null || !customerDTO.getName().matches("^[A-Za-z ]{4,}$")) {
                LOGGER.warning("Invalid customer name for update: " + customerDTO.getName());
                resp.getWriter().write("Name is empty or invalid! ");
                return;
            } else if (customerDTO.getAddress() == null || !customerDTO.getAddress().matches("^[A-Za-z0-9., -]{5,}$")) {
                LOGGER.warning("Invalid customer address for update: " + customerDTO.getAddress());
                resp.getWriter().write("Address is empty or invalid");
                return;
            } else if (customerDTO.getSalary() <= 0) {
                LOGGER.warning("Invalid customer salary for update: " + customerDTO.getSalary());
                resp.getWriter().write("Salary is empty or invalid!!");
                return;

            }
            boolean isUpdated = customerBO.updateCustomer(connection, customerDTO);
            if (isUpdated) {
                LOGGER.info("Customer updated successfully.");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

            } else {
                LOGGER.severe("Failed to update customer.");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "failed to update customer");
            }


        } catch (SQLIntegrityConstraintViolationException e) {
            LOGGER.log(Level.WARNING, "Duplicate values detected during update", e);
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Duplicate values. Please check again");
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "An error occurred while processing the request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
        }
    }
}
