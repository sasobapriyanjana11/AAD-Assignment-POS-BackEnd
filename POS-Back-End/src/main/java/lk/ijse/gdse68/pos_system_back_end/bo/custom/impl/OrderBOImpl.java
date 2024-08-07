package lk.ijse.gdse68.pos_system_back_end.bo.custom.impl;

import lk.ijse.gdse68.pos_system_back_end.bo.custom.OrderBO;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.CustomerDAO;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.ItemDAO;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.OrderDAO;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.OrderDetailDAO;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.impl.ItemDAOImpl;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.impl.OrderDAOImpl;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.impl.OrderDetailsDAOImpl;
import lk.ijse.gdse68.pos_system_back_end.dto.OrderDTO;
import lk.ijse.gdse68.pos_system_back_end.dto.OrderDetailDTO;
import lk.ijse.gdse68.pos_system_back_end.entity.Item;
import lk.ijse.gdse68.pos_system_back_end.entity.Order;
import lk.ijse.gdse68.pos_system_back_end.entity.OrderDetail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderBOImpl implements OrderBO {
    OrderDAO orderDAO=new OrderDAOImpl();
    CustomerDAO customerDAO=new CustomerDAOImpl();
    ItemDAO itemDAO=new ItemDAOImpl();
    OrderDetailDAO orderDetailDAO=new OrderDetailsDAOImpl();


    @Override
    public boolean placeOrder(Connection connection, OrderDTO orderDTO) throws Exception {
//        return orderDAO.save(connection, new Order(orderDTO.getOrderId(),orderDTO.getOrderDate(),orderDTO.getCustId(),orderDTO.getTotal(),orderDTO.getDiscount(),orderDTO.getSubTotal(),orderDTO.getCash(),orderDTO.getBalance()));
        try {
            connection.setAutoCommit(false);

            // Check if customer exists
            boolean customerExists = customerDAO.exists(connection, orderDTO.getCustId());
            if (!customerExists) {
                throw new SQLException("Customer ID does not exist: " + orderDTO.getOrderId());
            }

            // Save order
            boolean isOrderSave = orderDAO.save(connection, new Order(orderDTO.getOrderId(), orderDTO.getOrderDate(), orderDTO.getCustId(), orderDTO.getTotal(), orderDTO.getDiscount(),orderDTO.getSubTotal(),orderDTO.getCash(),orderDTO.getBalance()));
            if (!isOrderSave) {
                throw new SQLException("Failed to save order");
            }

            // Save order details and update item quantities dto.getTotal()

            for (OrderDetailDTO details : orderDTO.getOrder_list()) {
                // Check if item exists and has sufficient quantity
                boolean itemExists = itemDAO.exists(connection, details.getItemCode());
                if (!itemExists) {
                    throw new SQLException("Item code does not exist: " + details.getItemCode());
                }

                Item item = itemDAO.findBy(connection, details.getItemCode());
                if (item.getQty() < details.getQty()) {
                    throw new SQLException("Insufficient quantity for item: " + details.getItemCode());
                }

                // Save order detail
                boolean isOrderDetailSaved = orderDetailDAO.save(connection, new OrderDetail(details.getOrderId(), details.getItemCode(), details.getUnit_price(), details.getQty()));
                if (!isOrderDetailSaved) {
                    throw new SQLException("Failed to save order detail for item: " + details.getItemCode());
                }

                // Reduce item quantity
                boolean isQtyReduced = itemDAO.reduceQty(connection, new Item(details.getItemCode(), details.getQty()));
                if (!isQtyReduced) {
                    throw new SQLException("Failed to reduce quantity for item: " + details.getItemCode());
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            return false;
        }
    }

    private boolean updateItemQty(Connection connection, List<OrderDetailDTO> order_list) throws SQLException {
        for (OrderDetailDTO dto : order_list) {
            Item item = new Item(dto.getItemCode(), dto.getQty());
            System.out.println("updateItemQty::"+ item);
            if (!itemDAO.reduceQty(connection, item)) {
                return false;
            }
        }
        return true;
    }

    public boolean saveOrderDetails(Connection connection, List<OrderDetailDTO> orderList) throws SQLException {
        for (OrderDetailDTO details : orderList) {
            System.out.println("saveOrderDetails orderList:: "+orderList);

            // Check if item exists
            boolean itemExists = itemDAO.exists(connection, details.getItemCode());
            if (!itemExists) {
                throw new SQLException("Item code does not exist: " + details.getItemCode());

            }
            // Save order detail
            boolean isSaved = orderDetailDAO.save(connection, new OrderDetail(details.getOrderId(), details.getItemCode(), details.getUnit_price(), details.getQty()));
            if (!isSaved) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ArrayList<OrderDTO> getAllOrders(Connection connection) throws Exception {
        ArrayList<Order>orderList=orderDAO.getAll(connection);

        ArrayList<OrderDTO> orderDTOList = new ArrayList<>();

        for (Order order : orderList){
            OrderDTO dto = new OrderDTO(
                   order.getOrderId(),
                    order.getOrderDate(),
                    order.getCustId(),
                    order.getTotal(),
                    order.getDiscount(),
                    order.getSubTotal(),
                    order.getCash(),
                    order.getBalance()
            );
            orderDTOList.add(dto);
        }
        return orderDTOList;

    }

    @Override
    public String getLastId(Connection connection) throws SQLException {
        return orderDAO.getLastId(connection);
    }

    @Override
    public OrderDTO getOrderById(Connection connection, String id) throws SQLException {
        Order orders = orderDAO.findBy(connection, id);
        System.out.println("getOrderById ::"+orders);
        return new OrderDTO(
                orders.getOrderId(),
                orders.getOrderDate(),
                orders.getCustId(),
                orders.getTotal(),
                orders.getDiscount(),
                orders.getSubTotal(),
                orders.getCash(),
                orders.getBalance()
        );
    }
}
