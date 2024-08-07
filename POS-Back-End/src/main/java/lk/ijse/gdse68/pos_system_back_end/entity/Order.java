package lk.ijse.gdse68.pos_system_back_end.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
@ToString
public class Order {

     String orderId;
     LocalDate orderDate;
      String custId;
     BigDecimal total;
     BigDecimal discount;
     BigDecimal subTotal;
     BigDecimal cash;
     BigDecimal balance;

}
