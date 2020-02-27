package org.topository.db;

import org.topository.restaurant.Restaurant;

public class UrlFinderTester {

    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("African Village Centre Restaurant");
        restaurant.setCity("Adelaide");

        try {
            UrlFinder.getInstance().findUrl(restaurant);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
