package pharros.probabilitycalculator;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;

import lombok.Getter;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Getter
public class ProbabilityCalculatorInputArea extends JPanel
{

    private final JTextField uiDropRate;
    private final JTextField uiKillCount;
    private final JTextField uiDropsReceived;

    ProbabilityCalculatorInputArea()
    {
        setLayout(new GridLayout(3, 1, 0, 4));

        uiDropRate = addComponent("Drop Rate:");
        uiKillCount = addComponent("Kill Count:");
        uiDropsReceived = addComponent("Drops Received:");
    }

    double getDropRateInput()
    {
        return getInput(uiDropRate);
    }

    void setDropRateInput(double value)
    {
        setInput(uiDropRate, value);
    }

    double getKillCountInput()
    {
        return getInput(uiKillCount);
    }

    void setKillCountInput(int value)
    {
        setInput(uiKillCount, value);
    }

    double getDropsReceivedInput()
    {
        return getInput(uiDropsReceived);
    }

    void setDropsReceivedInput(int value)
    {
        setInput(uiDropsReceived, value);
    }

    private double getInput(JTextField field)
    {
        try
        {
            if (field.getText().contains("/"))
            {
                String[] fraction = field.getText().split("/");
                return Double.parseDouble(fraction[0]) / Double.parseDouble(fraction[1]);
            }
            return Double.parseDouble(field.getText());
        } catch (NumberFormatException e)
        {
            return 0.1;
        }
    }

    private void setInput(JTextField field, Object value)
    {
        field.setText(String.valueOf(value));
    }

    private JTextField addComponent(String label)
    {
        final JPanel container = new JPanel();
        container.setLayout(new GridLayout(1, 2, 0, 4));

        final JLabel uiLabel = new JLabel(label);
        final FlatTextField uiInput = new FlatTextField();

        uiLabel.setFont(FontManager.getRunescapeSmallFont());
        uiLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        uiLabel.setForeground(Color.WHITE);

        uiInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        uiInput.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        uiInput.setBorder(new EmptyBorder(5, 7, 5, 7));

        container.add(uiLabel, BorderLayout.WEST);
        container.add(uiInput, BorderLayout.EAST);

        add(container);

        return uiInput.getTextField();
    }
}
