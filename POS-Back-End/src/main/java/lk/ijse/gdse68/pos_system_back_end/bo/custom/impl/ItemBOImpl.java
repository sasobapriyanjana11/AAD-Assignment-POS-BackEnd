package lk.ijse.gdse68.pos_system_back_end.bo.custom.impl;

import lk.ijse.gdse68.pos_system_back_end.bo.custom.ItemBO;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.ItemDAO;
import lk.ijse.gdse68.pos_system_back_end.dao.custom.impl.ItemDAOImpl;
import lk.ijse.gdse68.pos_system_back_end.dto.ItemDTO;
import lk.ijse.gdse68.pos_system_back_end.entity.Item;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemBOImpl implements ItemBO {

    ItemDAO itemDAO=new ItemDAOImpl();

    @Override
    public boolean saveItem(Connection connection, ItemDTO itemDTO) throws SQLException {
        return itemDAO.save(connection,new Item(itemDTO.getCode(),itemDTO.getName(),itemDTO.getQty(),itemDTO.getPrice()));
    }

    @Override
    public ArrayList<ItemDTO> getAllItems(Connection connection) throws SQLException {
        ArrayList<Item> itemList = itemDAO.getAll(connection);
        ArrayList<ItemDTO> itemDTOList = new ArrayList<ItemDTO>();

        for (Item item : itemList){
            ItemDTO  dto = new ItemDTO(
                   item.getCode(),
                    item.getName(),
                    item.getQty(),
                    item.getPrice()
            );
            itemDTOList.add(dto);
        }
        return itemDTOList;
    }

    @Override
    public ItemDTO getItemById(Connection connection, String code) throws SQLException {
        Item item= itemDAO.findBy(connection,code);
        return new ItemDTO(
                item.getCode(),
                item.getName(),
                item.getQty(),
                item.getPrice()
        );
    }

    @Override
    public boolean updateItem(Connection connection, ItemDTO itemDTO) throws SQLException {
        return itemDAO.update(connection,new Item(itemDTO.getCode(),itemDTO.getName(),itemDTO.getQty(),itemDTO.getPrice()));
    }

    @Override
    public boolean deleteItem(Connection connection, String code) throws SQLException {
        return itemDAO.delete(connection,code);

    }
}
