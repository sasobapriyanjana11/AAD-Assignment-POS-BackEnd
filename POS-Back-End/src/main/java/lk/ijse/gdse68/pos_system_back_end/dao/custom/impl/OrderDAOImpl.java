package lk.ijse.gdse68.pos_system_back_end.dao.custom.impl;

import lk.ijse.gdse68.pos_system_back_end.dao.custom.OrderDAO;
import lk.ijse.gdse68.pos_system_back_end.dao.util.CrudUtil;
import lk.ijse.gdse68.pos_system_back_end.entity.Order;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public boolean save(Connection connection, Order entity) throws SQLException {
        String sql = "INSERT INTO orders (orderId, orderDate, custId, total, discount, subTotal, cash, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(connection, sql,
                entity.getOrderId(),
                entity.getOrderDate(),
                entity.getCustId(),
                entity.getTotal(),
                entity.getDiscount(),
                entity.getSubTotal(),
                entity.getCash(),
                entity.getBalance());
    }



    @Override
    public ArrayList<Order> getAll(Connection connection) throws SQLException {
        String sql = "SELECT * FROM orders";
        ArrayList<Order> orderList = new ArrayList<Order>();
        ResultSet rst = CrudUtil.execute(connection, sql);

        while (rst.next()) {
            Order orders = new Order(
                    rst.getString(1),
                    rst.getDate(2).toLocalDate(),
                    rst.getString(3),
                    rst.getBigDecimal(4),
                    rst.getBigDecimal(5),
                    rst.getBigDecimal(6),
                    rst.getBigDecimal(7),
                    rst.getBigDecimal(8)

            );

            orderList.add(orders);
        }
        return orderList;
    }

    @Override
    public Order findBy(Connection connection, String id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE orderId = ?";
        ResultSet rs = CrudUtil.execute(connection, sql, id);

        Order orders = new Order();
        System.out.println(orders);
        if (rs.next()) {
            orders.setOrderId(rs.getString("orderId"));
            orders.setOrderDate(rs.getDate("orderDate").toLocalDate());
            orders.setCustId(rs.getString("custId"));
            orders.setTotal(rs.getBigDecimal("total"));
            orders.setDiscount(rs.getBigDecimal("discount"));
            orders.setSubTotal(rs.getBigDecimal("subTotal"));
            orders.setCash(rs.getBigDecimal("cash"));
            orders.setBalance(rs.getBigDecimal("balance"));

        }
        return orders;
    }

    @Override
    public String getLastId(Connection connection) throws SQLException {
        String sql = "SELECT orderId FROM orders ORDER BY orderId DESC LIMIT 1";
        ResultSet rs = CrudUtil.execute(connection, sql);

        String lastId = "no_ids";
        if (rs.next()) {
            lastId = rs.getString(1);
        }
        return lastId;
    }

    @Override
    public boolean update(Connection connection, Order entity) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(Connection connection, String id) throws SQLException {
        return false;
    }


}
