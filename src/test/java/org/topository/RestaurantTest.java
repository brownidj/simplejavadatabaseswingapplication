package org.topository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topository.db.DbHelper;
import org.topository.restaurant.Restaurant;
import org.topository.restaurant.RestaurantHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class RestaurantTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantTest.class);

    @Before
    public void setUp() throws SQLException {
        DbHelper.getInstance().init();
        try (Connection connection = DbHelper.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE RESTAURANTS");
            statement.execute("ALTER TABLE RESTAURANTS ALTER COLUMN ID RESTART WITH 1");
            RestaurantTest.LOGGER.debug("Truncated table");
        }
    }

    @Test
    public void createTable() {
        try (Connection connection = DbHelper.getConnection(); Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM RESTAURANTS")) {
                while (rs.next()) {
                    RestaurantTest.LOGGER.debug("  >> [{}] {} ({})",
                            rs.getLong(1));
//                            rs.getString(2),
//                            rs.getString(6));
                }
                Assert.assertFalse("Should always only return 1 rows", rs.next());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void save() throws SQLException {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("David Browning");
        restaurant.setCuisine("Eclectic");
        restaurant.setCity("Townsville");
        restaurant.setCountry("Australia");
        restaurant.setUrl("davidbrowning@yahoo.com");
        Assert.assertEquals(-1, restaurant.getId());
        restaurant.save();
        Assert.assertEquals(1, restaurant.getId());

        try (Connection connection = DbHelper.getConnection(); Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM RESTAURANTS")) {
                Assert.assertTrue("Should always return 1 row", resultSet.next());
                Assert.assertEquals(1L, resultSet.getLong(1));
                Assert.assertFalse("Should always only return 1 row", resultSet.next());
            }

            try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM RESTAURANTS")) {
                Assert.assertTrue("Should always return 1 row", resultSet.next());
                Assert.assertEquals(1L, resultSet.getLong(1));
//                Assert.assertEquals("David Browning", resultSet.getString(2));
                Assert.assertFalse("Should always only return 1 row", resultSet.next());
            }
        }

        restaurant.setName("Browning David");
        restaurant.save();
        Assert.assertEquals(1, restaurant.getId());
        Assert.assertEquals("Browning David", restaurant.getName());

        final List<Restaurant> restaurants = RestaurantHelper.getInstance().getRestaurants();
        Assert.assertEquals(1, restaurants.size());
    }

    @Test
    public void delete() throws SQLException {
        List<Restaurant> restaurants = RestaurantHelper.getInstance().getRestaurants();
        Assert.assertNotNull(restaurants);
        Assert.assertTrue(restaurants.isEmpty());

        try (Connection connection = DbHelper.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO restaurants (NAME, CUISINE, CITY, COUNTRY, URL) VALUES ( 'African Village Centre Restaurant','Ethiopian','Adelaide','Australia','None')");
            statement.execute("INSERT INTO restaurants (NAME, CUISINE, CITY, COUNTRY, URL) VALUES ( 'Hermanos Cubanos Cuban Cafe','Cuban','Adelaide','Australia','None')");
            statement.execute("INSERT INTO restaurants (NAME, CUISINE, CITY, COUNTRY, URL) VALUES ( 'Parwana Afghan Kitchen','Afghani','Adelaide','Australia','None')");

            restaurants = RestaurantHelper.getInstance().getRestaurants();
            Assert.assertNotNull(restaurants);
            Assert.assertEquals(3, restaurants.size());
        }

        final Restaurant restaurant = restaurants.get(1);
        Assert.assertNotEquals(-1, restaurant.getId());
        restaurant.delete();
        Assert.assertEquals(-1, restaurant.getId());

        restaurants = RestaurantHelper.getInstance().getRestaurants();
        Assert.assertEquals(2, restaurants.size());
    }

    @After
    public void tearDown() {
        DbHelper.getInstance().close();
    }
}