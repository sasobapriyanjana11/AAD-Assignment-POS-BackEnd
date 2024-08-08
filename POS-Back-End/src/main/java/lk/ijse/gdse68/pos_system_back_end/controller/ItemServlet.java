package lk.ijse.gdse68.pos_system_back_end.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse68.pos_system_back_end.bo.custom.ItemBO;
import lk.ijse.gdse68.pos_system_back_end.bo.custom.impl.ItemBOImpl;
import lk.ijse.gdse68.pos_system_back_end.dto.ItemDTO;
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

@WebServlet(name = "item" ,urlPatterns = "/item" ,loadOnStartup = 3)
public class ItemServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ItemServlet.class.getName());
    ItemBO itemBO=new ItemBOImpl();
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
            throw new ServletException("Cannot find JNDI resource", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = connectionPool.getConnection()) {
            Jsonb jsonb = JsonbBuilder.create();

            ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);
//            System.out.println(itemDTO);
            LOGGER.info("Received POST request: " + itemDTO);

            if (itemDTO.getCode() == null || !itemDTO.getCode().matches("^(I00-)[0-9]{3}$")) {
                LOGGER.warning("Invalid item code: " + itemDTO.getCode());
                resp.getWriter().write("Item id is empty or invalid!!");
                return;
            } else if (itemDTO.getName() == null || !itemDTO.getName().matches("^[A-Za-z ]{4,}$")) {
                LOGGER.warning("Invalid item name: " + itemDTO.getName());
                resp.getWriter().write("Name is empty or invalid!!");
                return;
            } else if (itemDTO.getQty() <=0) {
                LOGGER.warning("Invalid item quantity: " + itemDTO.getQty());
                resp.getWriter().write("Quantity is empty !!");
                return;
//            } else if (itemDTO.getPrice() <= 0) {
 //               LOGGER.warning("Invalid item price: " + itemDTO.getPrice());
//                resp.getWriter().write("price is invalid!!");
//                return;
            }

            boolean isSaved = itemBO.saveItem(connection, itemDTO);
            if (isSaved) {
                LOGGER.info("Item saved successfully.");
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                LOGGER.severe("Failed to save item.");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "failed to save item");
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
                ArrayList<ItemDTO> itemDTOList = itemBO.getAllItems(connection);

                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(itemDTOList);
                resp.getWriter().write(json);
                LOGGER.info("Sent all items.");

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
            String code = req.getParameter("code");
            LOGGER.info("Received GET request for item with code: " + code);

            try (Connection connection = connectionPool.getConnection()) {
                ItemDTO itemDTO = itemBO.getItemById(connection,code);

                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(itemDTO);
                resp.getWriter().write(json);
                LOGGER.info("Sent item with code: " + code);

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
        else {
            LOGGER.warning("Unknown function parameter: " + function);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown function parameter");
        }
    }



    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = connectionPool.getConnection()) {
            Jsonb jsonb = JsonbBuilder.create();
               ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);
            LOGGER.info("Received PUT request: " + itemDTO);
//              System.out.println(itemDTO);

            if (itemDTO.getCode() == null || !itemDTO.getCode().matches("^(I00-)[0-9]{3}$")) {
                LOGGER.warning("Invalid item code for update: " + itemDTO.getCode());
                resp.getWriter().write("item id is empty or invalid!");
                return;
            } else if (itemDTO.getName() == null || !itemDTO.getName().matches("^[A-Za-z ]{4,}$")) {
                LOGGER.warning("Invalid item name for update: " + itemDTO.getName());
                resp.getWriter().write("Name is empty or invalid! ");
                return;
            } else if (itemDTO.getQty() <=0) {
                LOGGER.warning("Invalid item quantity for update: " + itemDTO.getQty());
                resp.getWriter().write("item qty is empty or invalid");
                return;
//            } else if (customerDTO.getSalary() <= 0) {
//                resp.getWriter().write("Salary is empty or invalid!!");
//                return;

            }
            boolean isUpdated = itemBO.updateItem(connection, itemDTO);
            if (isUpdated) {
                LOGGER.info("Item updated successfully.");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

            } else {
                LOGGER.severe("Failed to update item.");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "failed to update item");
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        LOGGER.info("Received DELETE request for item code: " + code);

        try (Connection connection = connectionPool.getConnection()){
            boolean isDeleted = itemBO.deleteItem(connection,code);
            if (isDeleted){
                LOGGER.info("Item deleted successfully.");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else{
                LOGGER.severe("Failed to delete item with code: " + code);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to delete item!");
            }

        }catch (Exception e){
//            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "An error occurred while processing the request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
        }
    }


}
