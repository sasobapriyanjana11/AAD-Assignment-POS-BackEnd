package lk.ijse.gdse68.pos_system_back_end.entity;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetail {
    private String orderId;
    private String itemCode;
    private BigDecimal unit_price;
    private int qty;

    public OrderDetail(String itemCode, BigDecimal unit_price, int qty) {
        this.itemCode = itemCode;
        this.unit_price = unit_price;
        this.qty = qty;
    }
    public void setUnit_price(String unitPrice) {
        this.unit_price=unit_price;
    }
}
