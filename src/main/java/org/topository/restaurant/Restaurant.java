package org.topository.restaurant;

import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topository.db.DbHelper;
import org.topository.db.UrlFinder;

import java.sql.*;
import java.util.StringJoiner;

public class Restaurant {

    private static final Logger LOGGER = LoggerFactory.getLogger(Restaurant.class);
    private long id = -1;
    private String name;
    private String cuisine;
    private String city;
    private String country;
    private String countryCode;
    private String url;

    public Restaurant() {
    }

    public Restaurant(String name, String cuisine, String city, String country, String countryCode, String url) {
        this.name = name;
        this.cuisine = cuisine;
        this.city = city;
        this.country = country;
        this.countryCode = countryCode;
        this.url = url;
    }

    public Restaurant(Row row) {
        this.setName(row.getCell(0).getStringCellValue());
        this.setCuisine(row.getCell(1).getStringCellValue());
        this.setCity(row.getCell(2).getStringCellValue());
        this.setCountry(row.getCell(3).getStringCellValue());
        this.setUrl(row.getCell(4).getStringCellValue());
//        getUrls();
    }

    private void getUrls() {
        if (this.getUrl().equals("No Url")) {
            UrlFinder.getInstance().getRestaurantUrls(this);
            UrlFinder.getInstance().getResults();
        }
    }

    public Restaurant(long id, String name, String cuisine, String city, String country, String countryCode, String url) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.city = city;
        this.country = country;
        this.countryCode = countryCode;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Restaurant.LOGGER.debug("Setting name: " + name);
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
//        Restaurant.LOGGER.debug("Setting cuisine: " + cuisine);
        this.cuisine = cuisine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
//        Restaurant.LOGGER.debug("Setting city: " + city);
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
//        Restaurant.LOGGER.debug("Setting country: " + country);
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
//        Restaurant.LOGGER.debug("Setting countryCode: " + countryCode);
        this.countryCode = countryCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
//        Restaurant.LOGGER.debug("Setting countryCode: " + url);
        this.url = url;
    }

    public void delete() throws SQLException {
        if (id != -1) {
            final String sql = "DELETE from RESTAURANTS where ID=?";
            try (Connection connection = DbHelper.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);
                preparedStatement.execute();
                id = -1;
            }
        }
    }

    public void save() throws SQLException {
        try (Connection connection = DbHelper.getConnection();) {
            if (id == -1) {
                final String sql = "INSERT INTO RESTAURANTS (NAME, CUISINE, CITY, COUNTRY, COUNTRYCODE, URL) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, cuisine);
                    preparedStatement.setString(3, city);
                    preparedStatement.setString(4, country);
                    preparedStatement.setString(5, countryCode);
                    preparedStatement.setString(6, url);
                    preparedStatement.execute();

                    try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                        resultSet.next();
                        id = resultSet.getLong("ID");
                    }
                }
            } else {
                final String sql = "UPDATE RESTAURANTS SET NAME=?, " +
                        "CUISINE=?, CITY=?, COUNTRY=?, COUNTRYCODE=?, URL=? WHERE ID=?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, cuisine);
                    preparedStatement.setString(3, city);
                    preparedStatement.setString(4, country);
                    preparedStatement.setString(5, countryCode);
                    preparedStatement.setString(6, url);
                    preparedStatement.setLong(7, id);
                    preparedStatement.execute();
                }
            }

        }
    }

//    public void setUrlFromGoogle(Restaurant restaurant) {
//        try {
//            this.url = UrlFinder.getInstance().getRestaurantUrls(restaurant);
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            Restaurant.LOGGER.debug("Failed to find URL", e);
//        }
//    }

    @Override
    public String toString() {
        return new StringJoiner(" ")
                .add(name)
                .add("[" + cuisine + "]")
                .add(city)
                .add(countryCode)
                .toString();
    }
}
