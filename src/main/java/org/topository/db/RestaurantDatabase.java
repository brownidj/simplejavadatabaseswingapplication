package org.topository.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topository.ui.RestaurantUI;

import javax.swing.*;

public class RestaurantDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantDatabase.class);

    public static void main(String[] args) {
        DbHelper.getInstance().init();
        DbHelper.getInstance().registerShutdownHook();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RestaurantDatabase.LOGGER.info("Running application");
                final RestaurantUI restaurantUI = new RestaurantUI();
                restaurantUI.setTitle("Restaurants");
                restaurantUI.setSize(1024, 600);
                restaurantUI.setLocationRelativeTo(null);
                restaurantUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                restaurantUI.setVisible(true);
            }
        });

        RestaurantDatabase.LOGGER.info("Done");
    }
}


