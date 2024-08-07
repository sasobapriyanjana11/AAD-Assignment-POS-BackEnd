package lk.ijse.gdse68.pos_system_back_end.bo.custom.impl;

import lk.ijse.gdse68.pos_system_back_end.bo.custom.OrderDetailsBO;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.OrderDetailDAO;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.impl.OrderDetailsDAOImpl;
import lk.ijse.gdse68.pos_system_back_end.dto.OrderDTO;
import lk.ijse.gdse68.pos_system_back_end.dto.OrderDetailDTO;
import lk.ijse.gdse68.pos_system_back_end.entity.OrderDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsBOImpl implements OrderDetailsBO {

    OrderDetailDAO orderDetailDAO=new OrderDetailsDAOImpl();

    @Override
    public OrderDTO getOrderDetailsById(Connection connection, String id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE orderId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate date = rs.getDate("orderDate").toLocalDate();
                    BigDecimal discount = rs.getBigDecimal("discount");
                    BigDecimal total = rs.getBigDecimal("total");

                    OrderDTO orderDTO = new OrderDTO();
                    orderDTO.setOrderId(rs.getString("orderId"));
                    orderDTO.setCustId(rs.getString("custId"));
                    orderDTO.setOrderDate(date);
                    orderDTO.setDiscount(discount);
                    orderDTO.setTotal(total);

                    List<OrderDetail> orderDetailList = orderDetailDAO.getAllById(connection, id);
                    List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
                    for (OrderDetail orderDetail : orderDetailList) {
                        orderDetailDTOList.add(new OrderDetailDTO(
                                orderDetail.getItemCode(),
                                orderDetail.getUnit_price(),
                                orderDetail.getQty()
                        ));

                    }
                    orderDTO.setOrder_list(orderDetailDTOList);

                    return orderDTO;
                } else {
                    return null; // Return null if no order is found
                }
            }
        }
    }
}
