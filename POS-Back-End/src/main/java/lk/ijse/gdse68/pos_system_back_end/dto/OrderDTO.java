package lk.ijse.gdse68.pos_system_back_end.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
@ToString
public class OrderDTO {
    private String orderId;
    private LocalDate orderDate;
    private String custId;
    private BigDecimal total;
    private BigDecimal discount;
    private BigDecimal subTotal;
    private BigDecimal cash;
    private BigDecimal balance;
    private List<OrderDetailDTO> order_list;

    public OrderDTO(String orderId, LocalDate orderDate, String custId, BigDecimal total, BigDecimal discount, BigDecimal subTotal, BigDecimal cash, BigDecimal balance) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.custId = custId;
        this.total = total;
        this.discount = discount;
        this.subTotal = subTotal;
        this.cash = cash;
        this.balance = balance;
    }

    public OrderDTO(String orderId, LocalDate orderDate, String custId) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.custId = custId;
    }
}

