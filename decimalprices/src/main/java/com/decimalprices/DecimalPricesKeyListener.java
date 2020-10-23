package com.decimalprices;

import net.runelite.api.Client;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.client.input.KeyListener;

import java.awt.event.KeyEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
class DecimalPricesKeyListener implements KeyListener {

    @Inject
    private Client client;

    private boolean isQuantityInput() {
        /*
        Determine user is typing into a quantity input field.
        Known types:
        2 Add friend input
        3 Delete friend input
        6 Send private message input
        7 Enter a quantity input (ge, bank, trade, coffer etc.)
         */
        return client.getVar(VarClientInt.INPUT_TYPE) == 7;
    }

    private void convertQuantity() {
        final String rawInputText = client.getVar(VarClientStr.INPUT_TEXT);
        // convert to lowercase for validation
        final String lowerInputText = rawInputText.toLowerCase();
        // ensure input matches exactly (any amount of numbers)period(any amount of numbers)[one of only k, m or b]
        if(!lowerInputText.matches("[0-9]+\\.[0-9]+[kmb]")) {
            return;
        }
        // get the unit from the end of string, k (thousands), m (millions) or b (billions)
        char unit = lowerInputText.charAt(lowerInputText.length() - 1);
        // get the number xx.xx without the unit and parse as a double
        double amount = Double.parseDouble(lowerInputText.substring(0, lowerInputText.length() - 1));
        // multiply the number and the unit
        double product;
        switch(unit) {
            case 'k':
                product = amount * 1000;
                break;
            case 'm':
                product = amount * 1000000;
                break;
            case 'b':
                product = amount * 1000000000;
                break;
            default:
                product = 0;
                break;
        }
        // cast the double to an int, truncating anything after the decimal in the process
        int truncProduct = (int) product;
        // set the newly converted integer before it is sent to the server
        client.setVar(VarClientStr.INPUT_TEXT, String.valueOf(truncProduct));
    }

    private void addDecimalToInputText() {
        // take current input text and append a period (decimal)
        final String currentInputText = client.getVar(VarClientStr.INPUT_TEXT);
        if(currentInputText.equals("")) {
            return;
        }
        String newInputText = currentInputText + ".";
        client.setVar(VarClientStr.INPUT_TEXT, newInputText);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER && isQuantityInput()) {
            // intercept quantity entry before it is sent to the server
            convertQuantity();
        } else if(e.getKeyCode() == KeyEvent.VK_PERIOD && isQuantityInput()) {
            // allow typing of decimal in quantity input field which is otherwise not possible to do
            addDecimalToInputText();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
