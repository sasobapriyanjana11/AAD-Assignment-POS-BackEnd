package lk.ijse.gdse68.pos_system_back_end.dao.custom.impl;

import lk.ijse.gdse68.pos_system_back_end.dao.custom.OrderDetailDAO;
import lk.ijse.gdse68.pos_system_back_end.dao.util.CrudUtil;
import lk.ijse.gdse68.pos_system_back_end.dto.OrderDetailDTO;
import lk.ijse.gdse68.pos_system_back_end.entity.OrderDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDAOImpl implements OrderDetailDAO {
    @Override
    public boolean save(Connection connection, OrderDetail entity) throws SQLException {
        String sql = "INSERT INTO order_details (orderId, itemCode, unit_price, qty) VALUES (?, ?, ?, ?)";
        return CrudUtil.execute(connection,sql, entity.getOrderId(), entity.getItemCode(), entity.getUnit_price(), entity.getQty());


}


    @Override
    public boolean update(Connection connection, OrderDetail entity) throws SQLException {
        return false;
    }

    @Override
    public ArrayList<OrderDetail> getAll(Connection connection) throws SQLException {
        String sql = "SELECT * FROM order_details";
        ArrayList<OrderDetail> orderDetailsList = new ArrayList<OrderDetail>();
        ResultSet rst = CrudUtil.execute(connection, sql);

        while (rst.next()) {
            OrderDetail orderDetail = new OrderDetail(

                    rst.getString(1),
                    rst.getString(2),
                    rst.getBigDecimal(3),
                    rst.getInt(4)


            );

            orderDetailsList.add(orderDetail);
        }
        return orderDetailsList;
    }

    @Override
    public boolean delete(Connection connection, String id) throws SQLException {
        return false;
    }

    @Override
    public OrderDetail findBy(Connection connection, String id) throws SQLException {
        String sql = "SELECT * FROM order_details WHERE orderId = ?";
        ResultSet rs = CrudUtil.execute(connection, sql, id);

        OrderDetail orderDetail = new OrderDetail();
        System.out.println(orderDetail);
        if (rs.next()) {
            orderDetail.setOrderId(rs.getString("orderId"));
            orderDetail.setItemCode("itemCode");
            orderDetail.setUnit_price("unit_price");
            orderDetail.setQty(Integer.parseInt("itemQty"));

        }
        return orderDetail;
    }

    @Override
    public List<OrderDetail> getAllById(Connection connection, String id) throws SQLException {
        String sql = "SELECT itemCode, unit_price, qty FROM order_details WHERE orderId = ?";
        ResultSet rs = CrudUtil.execute(connection, sql, id);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        while (rs.next()) {
            OrderDetail orderDetail = new OrderDetail(
                    rs.getString("itemCode"),  // Assuming column name is item_code
                    rs.getBigDecimal("unit_price"), // Assuming column name is unit_price
                    rs.getInt("qty") // Assuming column name is qty
            );
            orderDetailList.add(orderDetail);
        }
        return orderDetailList;
    }
}
