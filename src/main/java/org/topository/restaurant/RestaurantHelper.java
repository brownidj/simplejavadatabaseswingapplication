package org.topository.restaurant;

import org.topository.db.DbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantHelper {

    public static final RestaurantHelper INSTANCE = new RestaurantHelper();

    public static RestaurantHelper getInstance() {
        return RestaurantHelper.INSTANCE;
    }

    private RestaurantHelper() {
    }

    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * from RESTAURANTS ORDER by ID";

        try (Connection connection = DbHelper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                final Restaurant restaurant = new Restaurant(
                        resultSet.getLong("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("CUISINE"),
                        resultSet.getString("CITY"),
                        resultSet.getString("COUNTRY"),
                        resultSet.getString("COUNTRYcode"),
                        resultSet.getString("URL"));
                restaurants.add(restaurant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }
}
