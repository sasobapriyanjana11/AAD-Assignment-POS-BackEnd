package lk.ijse.gdse68.pos_system_back_end.dao.custom.impl;

import lk.ijse.gdse68.pos_system_back_end.dao.custom.ItemDAO;
import lk.ijse.gdse68.pos_system_back_end.dao.util.CrudUtil;
import lk.ijse.gdse68.pos_system_back_end.entity.Customer;
import lk.ijse.gdse68.pos_system_back_end.entity.Item;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemDAOImpl implements ItemDAO {

    @Override
    public boolean save(Connection connection, Item entity) throws SQLException {
//        String sql = "INSERT INTO item (code,name,qty,price) VALUES (?,?,?,?)";
//        return CrudUtil.execute(connection,sql,entity.getCode(),entity.getName(),entity.getQty(),entity.getPrice());
        System.out.println("Saving Item: " + entity.getCode() + ", " + entity.getName() + ", " + entity.getQty() + ", " + entity.getPrice());
        String sql = "INSERT INTO item (code, name, qty, price) VALUES (?, ?, ?, ?)";
        return CrudUtil.execute(connection, sql, entity.getCode(), entity.getName(), entity.getQty(), entity.getPrice());
    }

    @Override
    public boolean update(Connection connection, Item entity) throws SQLException {
//        String sql = "UPDATE item SET name = ?,qty = ?, price = ? WHERE code = ?";
//        return CrudUtil.execute(connection,sql,entity.getName(),entity.getQty(),entity.getPrice(),entity.getCode());

        System.out.println("Updating Item: " + entity.getCode() + ", " + entity.getName() + ", " + entity.getQty() + ", " + entity.getPrice());
        String sql = "UPDATE item SET name = ?, qty = ?, price = ? WHERE code = ?";
        return CrudUtil.execute(connection, sql, entity.getName(), entity.getQty(), entity.getPrice(), entity.getCode());
    }

    @Override
    public ArrayList<Item> getAll(Connection connection) throws SQLException {
        String sql = "SELECT * FROM item";
        ArrayList<Item> itemArrayList = new ArrayList<Item>();
        ResultSet rst = CrudUtil.execute(connection, sql);

        while(rst.next()){
            Item item = new Item(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getInt(4),
                    rst.getBigDecimal(3)


            );

            itemArrayList.add(item);
        }
        return itemArrayList;
    }

    @Override
    public boolean delete(Connection connection, String code) throws SQLException {
        String sql = "DELETE FROM item WHERE code=?";
        return CrudUtil.execute(connection,sql,code);
    }

    @Override
    public Item findBy(Connection connection, String code) throws SQLException {
        String sql = "SELECT * FROM item WHERE code=?";
        Item item =new Item();
        ResultSet rst = CrudUtil.execute(connection,sql,code);

        if (rst.next()){
            item.setCode(rst.getString(1));
            item.setName(rst.getString(2));
            item.setQty(Integer.parseInt(rst.getString(3)));
            item.setPrice(rst.getBigDecimal(4));
        }
        return item;
    }
}
