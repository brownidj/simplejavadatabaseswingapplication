package org.topository.ui;

import com.google.common.collect.ComparisonChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topository.db.UrlFinder;
import org.topository.restaurant.Restaurant;
import org.topository.restaurant.RestaurantHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.topository.db.UrlFinder.getInstance;

public class RestaurantUI extends JFrame {

    private static final long serialVersionID = 1961691491919165183L;
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantUI.class);

    private JTextField idTextField;
    private JTextField nameTextField;
    private JTextField cuisineTextField;
    private JTextField cityTextField;
    private JTextField countryTextField;
    private JTextField countryCodeTextField;
    private JTextArea urlTextArea;

    private JList<Restaurant> restaurantJList;
    private DefaultListModel<Restaurant> restaurantListModel;
    private List<Restaurant> restaurants;

    private Action refreshAction;
    private Action newAction;
    private Action saveAction;
    private Action deleteAction;
    private Action addListAction;
    private Action sortAction;
    private Action queryAction;

    private Restaurant selected;
    private int sortType = 0;

    public RestaurantUI() {
        initActions();
        initMenu();
        initComponents();
        refreshData(0);
    }

    private void initActions() {
        refreshAction = new AbstractAction("Refresh", load("Refresh")) {
            private static final long serialVersionUID = 7573537222039055715L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                refreshData(sortType);
            }
        };

        newAction = new AbstractAction("New", load("New")) {
            private static final long serialVersionUID = 39402394060879678L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                createNew();
            }
        };

        addListAction = new AbstractAction("Add List", load("Add_List")) {
            private static final long serialVersionUID = -3865627438398974683L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                readDefaultExcelFile();
            }
        };

        saveAction = new AbstractAction("Save", load("Save")) {
            private static final long serialVersionUID = 3151744204386109789L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    save();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };

        deleteAction = new AbstractAction("Delete", load("Delete")) {
            private static final long serialVersionUID = -3865627438398974682L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                delete();
            }
        };

        queryAction = new AbstractAction("Query", load("Query")) {
            private static final long serialVersionUID = 7273530712047255715L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                query();
            }
        };

        sortAction = new AbstractAction("Sort", load("Sort")) {
            private static final long serialVersionUID = 7273537222047255715L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                sort();
            }
        };

        RestaurantUI.LOGGER.info("Finished initiating actions...");
    }

    private ImageIcon load(final String name) {
        return new ImageIcon(getClass().getResource("/icons/" + name + ".png"));
    }

    private void initComponents() {
        RestaurantUI.LOGGER.info("Initiating components...");
        add(createToolBar(), BorderLayout.PAGE_END);
        add(createJListPanel(), BorderLayout.WEST);
        add(createEditor(), BorderLayout.CENTER);
    }

    private void initMenu() {
        final JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        final JMenu editMenu = menuBar.add(new JMenu("Edit"));
        editMenu.add(refreshAction);
        editMenu.add(queryAction);
        editMenu.add(sortAction);
        editMenu.addSeparator();
        editMenu.add(newAction);
        editMenu.add(addListAction);
        editMenu.addSeparator();
        editMenu.add(saveAction);
        editMenu.addSeparator();
        editMenu.add(deleteAction);
        RestaurantUI.LOGGER.info("Finished building menu...");
    }

    private JToolBar createToolBar() {
        final JToolBar toolBar = new JToolBar();
        toolBar.add(refreshAction);
        toolBar.add(queryAction);
        toolBar.add(sortAction);
        toolBar.addSeparator();
        toolBar.add(newAction);
        toolBar.add(addListAction);
        toolBar.addSeparator();
        toolBar.add(saveAction);
        toolBar.addSeparator();
        toolBar.add(deleteAction);
        RestaurantUI.LOGGER.info("Finished building toolbar...");

        return toolBar;
    }

    private Component createJListPanel() {
        restaurantListModel = new DefaultListModel<>();
        restaurantJList = new JList<>(restaurantListModel);
        restaurantJList.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Restaurant selected = restaurantJList.getSelectedValue();
                setSelectedRestaurant(selected);
            }
        });
        RestaurantUI.LOGGER.info("Creating list panel...");
        return new JScrollPane(restaurantJList);
    }

    private JComponent createEditor() {
        final JPanel panel = new JPanel(new GridBagLayout());
        int left = 8;
        int right = 8;
        int top = 8;
//        int bottom = 4;

        // Id
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(top, left, 2, 2);
        panel.add(new JLabel("Id"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(top, 2, 2, right);
        constraints.fill = GridBagConstraints.BOTH;
        idTextField = new JTextField();
        idTextField.setEditable(false);
        panel.add(idTextField, constraints);

        // Name
        constraints = new GridBagConstraints();
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(2, left, 2, 2);
        panel.add(new JLabel("Name"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(2, 2, 2, right);
        constraints.fill = GridBagConstraints.BOTH;
        nameTextField = new JTextField();
        panel.add(nameTextField, constraints);

        // Cuisine
        constraints = new GridBagConstraints();
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(2, left, 2, 2);
        panel.add(new JLabel("Cuisine"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.insets = new Insets(2, 2, 2, right);
        constraints.fill = GridBagConstraints.BOTH;
        cuisineTextField = new JTextField();
        panel.add(cuisineTextField, constraints);

        // City
        constraints = new GridBagConstraints();
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(2, left, 2, 2);
        panel.add(new JLabel("City"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.insets = new Insets(2, 2, 2, right);
        constraints.fill = GridBagConstraints.BOTH;
        cityTextField = new JTextField();
        panel.add(cityTextField, constraints);

        // Country
        constraints = new GridBagConstraints();
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(2, left, 2, 2);
        panel.add(new JLabel("Country"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.weightx = 1;
        constraints.insets = new Insets(2, 2, 2, right);
        constraints.fill = GridBagConstraints.BOTH;
        countryTextField = new JTextField();
        panel.add(countryTextField, constraints);

        // Country Code
        constraints = new GridBagConstraints();
        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(2, left, 2, 2);
        panel.add(new JLabel("Code"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.weightx = 1;
        constraints.insets = new Insets(2, 2, 2, right);
        constraints.fill = GridBagConstraints.BOTH;
        countryCodeTextField = new JTextField();
        panel.add(countryCodeTextField, constraints);

        // Notes
        constraints = new GridBagConstraints();
        constraints.gridy = 6;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.insets = new Insets(2, left, 2, 2);
        panel.add(new JLabel("Notes"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 6;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(2, 2, 0, right);
        constraints.fill = GridBagConstraints.BOTH;
        urlTextArea = new JTextArea();
        panel.add(new JScrollPane(urlTextArea), constraints);

        RestaurantUI.LOGGER.info("Finished building editor...");
        return panel;
    }

    private void setSelectedRestaurant(Restaurant restaurant) {
        selected = restaurant;
        if (restaurant == null) {
            this.setTextfieldsToEmpty();
            RestaurantUI.LOGGER.info("Selected restaurant = null");

        } else {
            idTextField.setText(String.valueOf(restaurant.getId()));
            nameTextField.setText(restaurant.getName());
            cuisineTextField.setText(restaurant.getCuisine());
            cityTextField.setText(restaurant.getCity());
            countryTextField.setText(restaurant.getCountry());
            countryCodeTextField.setText(restaurant.getCountryCode());
            urlTextArea.setText(restaurant.getUrl());
            RestaurantUI.LOGGER.info("Selected restaurant = " + selected.toString());
        }
    }

    private void setTextfieldsToEmpty() {
        String s = "";
        idTextField.setText(s);
        nameTextField.setText(s);
        cuisineTextField.setText(s);
        cityTextField.setText(s);
        countryTextField.setText(s);
        countryCodeTextField.setText(s);
        urlTextArea.setText(s);
    }

    private void refreshData(int sortType) {
        SwingWorker<Void, Restaurant> worker = new SwingWorker<Void, Restaurant>() {
            @Override
            protected Void doInBackground() {
                restaurants = RestaurantHelper.getInstance().getRestaurants();
                if (sortType == 0) {
                    sortDataByCountryAndCity(restaurants);
                } else if(sortType == 1) {
                    sortDataByCity(restaurants);
                } else if(sortType == 2) {
                    sortDataByRestaurantName(restaurants);
                } else if(sortType == 3) {
                sortDataByCusine(restaurants);
            }
                for (Restaurant restaurant : restaurants) publish(restaurant);
                return null;
            }

            protected void process(List<Restaurant> restaurants) {
                restaurantListModel.removeAllElements();
                for (Restaurant restaurant : restaurants) {
                    restaurantListModel.addElement(restaurant);
                }
            }
        };

        worker.execute();
    }

    private void sortDataByCountryAndCity(List<Restaurant> restaurants) {
        Collections.sort(restaurants, (r1, r2) -> ComparisonChain.start()
                .compare(r1.getCountry(), r2.getCountry())
                .compare(r1.getCity(), r2.getCity())
                .compare(r1.getName(), r2.getName())
                .result());
    }

    private void sortDataByCity(List<Restaurant> restaurants) {
        Collections.sort(restaurants, (r1, r2) -> ComparisonChain.start()
                .compare(r1.getCity(), r2.getCity())
                .compare(r1.getName(), r2.getName())
                .result());
    }

    private void sortDataByRestaurantName(List<Restaurant> restaurants) {
        Collections.sort(restaurants, (r1, r2) -> ComparisonChain.start()
                .compare(r1.getName(), r2.getName())
                .result());
    }

    private void sortDataByCusine(List<Restaurant> restaurants) {
        Collections.sort(restaurants, (r1, r2) -> ComparisonChain.start()
                .compare(r1.getCity(), r2.getCity())
                .compare(r1.getCuisine(), r2.getCuisine())
                .result());
    }

    private void createNew() {
        final Restaurant restaurant = new Restaurant("Name",
                "Cuisine", "City", "Country",
                "None", "No URL");
        setSelectedRestaurant(restaurant);
    }

    private void save() throws InterruptedException {
        if (selected != null) {
            selected.setName(nameTextField.getText());
            selected.setCuisine(cuisineTextField.getText());
            selected.setCity(cityTextField.getText());
            selected.setCountry(countryTextField.getText());
            selected.setCountryCode(countryCodeTextField.getText());
            if (urlTextArea.getText().equals("No Url")) {
                RestaurantUI.LOGGER.info(getInstance().findUrl(selected));
            } else {
                selected.setUrl(urlTextArea.getText());
            }
            try {
                selected.save();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to save selected restaurant", "Save", JOptionPane.WARNING_MESSAGE);
            } finally {
                setTextfieldsToEmpty();
                refreshData(sortType);
            }
        }
    }

    private void readDefaultExcelFile() {
        JOptionPane.showMessageDialog(this, "Not yet implemented", "Read Excel file", JOptionPane.WARNING_MESSAGE);
    }

    private void delete() {
        if (selected != null) {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Delete?", "Delete", JOptionPane.YES_NO_OPTION)) {
                try {
                    selected.delete();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Failed to delete selected restaurant", "Save", JOptionPane.WARNING_MESSAGE);
                } finally {
                    setSelectedRestaurant(null);
                    refreshData(sortType);
                }
            }
        }
    }

    private void sort() {
        JOptionPaneWithRadioButtons pane = new JOptionPaneWithRadioButtons();
        pane.init();
        pane.showDialog();
        sortType = pane.getSelectedButton();
        refreshData(sortType);
    }

    private void query() {
        JOptionPane.showMessageDialog(this, "Not yet implemented", "Query", JOptionPane.WARNING_MESSAGE);
    }

}
