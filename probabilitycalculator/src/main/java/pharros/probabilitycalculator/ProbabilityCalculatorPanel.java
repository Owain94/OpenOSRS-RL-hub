package pharros.probabilitycalculator;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.border.EmptyBorder;
import java.awt.*;

class ProbabilityCalculatorPanel extends PluginPanel
{

    private ProbabilityCalculatorInputArea inputArea;
    private ProbabilityCalculatorOutputArea outputArea;

    ProbabilityCalculatorPanel(ProbabilityCalculatorInputArea inputArea, ProbabilityCalculatorOutputArea outputArea)
    {
        super();
        this.inputArea = inputArea;
        this.outputArea = outputArea;
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        inputArea.setBorder(new EmptyBorder(12, 0, 12, 0));
        inputArea.setBackground(ColorScheme.DARK_GRAY_COLOR);

        add(inputArea, c);
        c.gridy++;
        add(outputArea, c);
        c.gridy++;
    }

    /*
    void init(ProbabilityCalculatorConfig config) {

    }
    */

}
