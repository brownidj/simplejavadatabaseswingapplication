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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class RestaurantHelperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantHelperTest.class);

    @Before
    public void setUp() throws SQLException {
        DbHelper.getInstance().init();
        try (Connection connection = DbHelper.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE RESTAURANTS");
            statement.execute("ALTER TABLE RESTAURANTS ALTER COLUMN ID RESTART WITH 1");
            RestaurantHelperTest.LOGGER.debug("Truncated table");
        }
    }

    @Test
    public void load() throws SQLException {
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

            Restaurant restaurant = restaurants.get(0);
            Assert.assertNotNull(restaurants);
            Assert.assertEquals(1, restaurant.getId());
            Assert.assertEquals("African Village Centre Restaurant", restaurant.getName());

            restaurant = restaurants.get(2);
            Assert.assertNotNull(restaurants);
            Assert.assertEquals(3, restaurant.getId());
            Assert.assertEquals("Parwana Afghan Kitchen", restaurant.getName());
        }
    }

    @After
    public void tearDown() {
        DbHelper.getInstance().close();
    }

}
