/*
 * #%L
 * Restaurant DB
 * %%
 * Copyright (C) 2020 topository
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.topository.db;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topository.restaurant.Restaurant;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class RestaurantsHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantsHelper.class);

    private static final RestaurantsHelper INSTANCE = new RestaurantsHelper();

    private String stem = "./resources/raw_data/restaurants.";
    private Row row;

    private List<Restaurant> restaurants;


    private RestaurantsHelper() {
    }

    public static RestaurantsHelper getInstance() {
        return RestaurantsHelper.INSTANCE;
    }

    public List<Restaurant> getRestaurants() throws SQLException {
        return buildList("Loading restaurants", "Loaded {} restaurants");
    }

    private List<Restaurant> buildList(String message1, String message2) throws SQLException {
        RestaurantsHelper.LOGGER.debug("Message1 -> " + message1);

        final List<Restaurant> restaurants = new ArrayList<>();

        final String sql = "SELECT * FROM restaurants ORDER BY country";
        try (Connection connection = DbHelper.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {

            while (rs.next()) {
                final Restaurant restaurant = new Restaurant();
                restaurant.setId(rs.getLong("id"));
                restaurant.setName(rs.getString("name"));
                restaurant.setCuisine(rs.getString("cuisine"));
                restaurant.setCity(rs.getString("city"));
                restaurant.setCountry(rs.getString("country"));
                restaurant.setUrl(rs.getString("url"));
//                if (restaurant.getUrl() == "No URL") {
//                    restaurant.setUrl(UrlFinder.getInstance().getRestaurantUrls(restaurant));
//                }
                restaurants.add(restaurant);
            }
        }
        RestaurantsHelper.LOGGER.debug("Message2 " + message2, restaurants.size());
        return restaurants;
    }

//    public List<Restaurant> getRestaurantsFromTsv() {
//        restaurants = new ArrayList<>();
//        try {
//            RestaurantsHelper.LOGGER.info("Trying to load tsv");
//            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
//            CSVReader reader = new CSVReaderBuilder(new FileReader(stem + "tsv"))
//                    .withCSVParser(parser)
//                    .build();
//            RestaurantsHelper.LOGGER.info("Loaded tsv");
//            List<String[]> lines = reader.readAll();
//            for (String[] line : lines) {
//                restaurants.add(new Restaurant(line));
//            }
//        } catch (IOException | CsvException e) {
//            e.printStackTrace();
//        }
//        RestaurantsHelper.LOGGER.debug("Loaded {} restaurants from TSV file", restaurants.size());
//        return restaurants;
//    }

//    public List<Restaurant> getRestaurantsFromXlsx() {
//        RestaurantsHelper.LOGGER.debug("Trying xlsx ");
//        restaurants = new ArrayList<>();
//        int rows = 0;
//
//        try (FileInputStream fileIn = new FileInputStream(stem + "xlsx")) {
//            Workbook wb = new XSSFWorkbook(fileIn);
//            for (Sheet sheet : wb) {
//                for (Row row : sheet) {
//                    restaurants.add(new Restaurant(row));
//                    rows += 1;
//                }
//            }
//            RestaurantsHelper.LOGGER.debug("Rows: " + rows);
//        } catch (IOException e) {
//            RestaurantsHelper.LOGGER.debug("Failed to load xlsx file", e);
//        }
//        RestaurantsHelper.LOGGER.debug("Loaded {} restaurants from xlsx file", restaurants.size());
//        return restaurants;
//    }

    public List<Restaurant> getRestaurantsFromXls() {
        RestaurantsHelper.LOGGER.debug("Trying xls ");
        restaurants = new ArrayList<>();
        int rows = 0;

        try (FileInputStream fileIn = new FileInputStream(stem + "xls")) {
            Workbook wb = new HSSFWorkbook(fileIn);
            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    Restaurant restaurant = new Restaurant(row);
//                    restaurant.setUrlFromGoogle(restaurant);
                    restaurants.add(restaurant);
                    rows += 1;
                }
            }
            RestaurantsHelper.LOGGER.debug("Rows: " + rows);
        } catch (IOException e) {
            RestaurantsHelper.LOGGER.debug("Failed to load xlsx file", e);
        }
        RestaurantsHelper.LOGGER.debug("Loaded {} restaurants from xlsx file", restaurants.size());
        return restaurants;
    }

}
