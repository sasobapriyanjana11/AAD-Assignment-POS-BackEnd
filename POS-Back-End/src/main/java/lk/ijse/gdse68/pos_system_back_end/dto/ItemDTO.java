package lk.ijse.gdse68.pos_system_back_end.dto;

import lombok.*;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Setter
@Getter
@ToString
public class ItemDTO {
    private String code;
    private String name;
    private int qty;
    private BigDecimal price;


}
