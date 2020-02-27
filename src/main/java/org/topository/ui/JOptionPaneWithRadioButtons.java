package org.topository.ui;

import javax.swing.*;
import java.awt.*;

public class JOptionPaneWithRadioButtons {

    private ButtonGroup buttonGroup;
    private JPanel jPanel;
    private JRadioButton[] jRadioButtons;
    private String[] sortNames = {"By country, city",
            "By city", "By restaurant name", "By city and cuisine"};
    private int selectedButton;

    public int getSelectedButton() {
        return selectedButton;
    }

    public void showDialog() {
        JOptionPane.showMessageDialog(null, jPanel);
        for (int i = 0; i < buttonGroup.getButtonCount(); i++) {
            if (jRadioButtons[i].isSelected()) {
                System.out.println("You selected sort: " + sortNames[i] + ".");
                selectedButton = i;
            }
        }
    }

    public void init() {
        jRadioButtons = new JRadioButton[4];
        jPanel = new JPanel(new GridLayout(4, 0));
        buttonGroup = getButtonGroup(jRadioButtons, jPanel);
    }

    private ButtonGroup getButtonGroup(JRadioButton[] jRadioButtons, JPanel jPanel) {
        ButtonGroup buttonGroup = new ButtonGroup();
        for (int i = 0; i < jRadioButtons.length; i++) {
            jRadioButtons[i] = new JRadioButton(sortNames[i]);
            buttonGroup.add(jRadioButtons[i]);
            jPanel.add(jRadioButtons[i]);
        }
        return buttonGroup;
    }

}

