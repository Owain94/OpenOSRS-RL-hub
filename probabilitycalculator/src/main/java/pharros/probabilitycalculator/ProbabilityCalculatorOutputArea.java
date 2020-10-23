package pharros.probabilitycalculator;

import lombok.Setter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import java.awt.*;
import java.text.DecimalFormat;

@Setter
public class ProbabilityCalculatorOutputArea extends JPanel
{

    private JTextArea textArea;
    private double atLeastChance;
    private double zeroChance;
    private double exactChance;
    private long killCount;
    private long dropsReceived;
    private double dropRate;
    private String strAtLeastChance;
    private String strExactChance;
    private String strZeroChance;
    private String outputMsg;
    private DecimalFormat df;
    private String dfPattern;
    private final ProbabilityCalculatorConfig config;
    private StringBuilder strBldr;

    ProbabilityCalculatorOutputArea(double dropRate, int killCount, int dropsReceived, ProbabilityCalculatorConfig config)
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR, 5));
        setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);

        this.killCount = killCount;
        this.dropsReceived = dropsReceived;
        this.dropRate = dropRate;
        this.config = config;

        updatedfPattern();

        calculateProbabilities();

        textArea = new JTextArea(outputMsg);
        textArea.setEditable(false);
        textArea.setFont(FontManager.getRunescapeBoldFont());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(textArea);

    }

    private double nCx(double n, double x)
    {

        if (x > n / 2)
        {
            x = n - x;
        }

        double ret = 1.0;
        for (int i = 1; i <= x; i++)
        {
            ret *= (n - x + i);
            ret /= i;
        }

        return ret;
         /*
         //alternative method of computing nCx
        if (x < 0 || x > n) {
            return 0;
        }
        if (x == 0 || x == n) {
            return 1;
        }
        x = Math.min(x, n-x);
        double c = 1.0;
        for (int i = 0; i <= x-1; i++) {
            c = c * (n - i)/(i + 1);
        }
        return c;
        */
    }

    private double binomialProb(double n, double x, double p)
    {
        return nCx(n, x) * Math.pow(p, x) * (Math.pow(1.0 - p, n - x));
    }

    void calculateProbabilities()
    {
        if (killCount < dropsReceived)
        {
            outputMsg = "You've somehow cheated the RNG gods and managed to get more drops than you got kills. What is this sorcery?!";
        }
        else if (dropRate >= 1.0 || dropRate <= 0.0)
        {
            outputMsg = "Please use a drop rate value that is between 0.0 and 1.0, exclusively.";
        }
        else
        {
            exactChance = binomialProb(killCount, dropsReceived, dropRate);
            if (Double.isNaN(exactChance))
            {
                outputMsg = "Your input for kill count or drops received is too large for this calculator. Please user smaller amounts.";
                return;
            }
            zeroChance = Math.pow(1.0 - dropRate, killCount);
            if (Double.isNaN(zeroChance))
            {
                outputMsg = "Your input for kill count or drops received is too large for this calculator. Please user smaller amounts.";
                return;
            }
            if (dropsReceived == 1.0)
            {
                atLeastChance = 1.0 - zeroChance;
            }
            else
            {
                atLeastChance = 0.0;
                for (int i = 0; i < dropsReceived; i++)
                {
                    atLeastChance += binomialProb(killCount, i, dropRate);
                }
                atLeastChance = 1.0 - atLeastChance;
                if (Double.isNaN(atLeastChance))
                {
                    outputMsg = "Your input for kill count or drops received is too large for this calculator. Please user smaller amounts.";
                    return;
                }
            }

            strAtLeastChance = df.format(Math.abs(atLeastChance * 100.0));
            if (strAtLeastChance.equals("0") || strAtLeastChance.equals("100"))
            {
                strAtLeastChance = "~" + strAtLeastChance;
            }
            strExactChance = df.format(Math.abs(exactChance * 100.0));
            if (strExactChance.equals("0") || strExactChance.equals("100"))
            {
                strExactChance = "~" + strExactChance;
            }
            strZeroChance = df.format(Math.abs(zeroChance * 100.0));
            if (strZeroChance.equals("0") || strZeroChance.equals("100"))
            {
                strZeroChance = "~" + strZeroChance;
            }

            outputMsg = "At " + killCount + " kills, " + dropsReceived + " drop(s), and a drop rate of " + dropRate + ", your chances are:\n\n" +
                "Chance to get at least " + dropsReceived + " drop(s):\n" + strAtLeastChance + "%\n\n" +
                "Chance to get exactly " + dropsReceived + " drop(s):\n" + strExactChance + "%\n\n" +
                "Chance to get zero drops:\n" + strZeroChance + "%\n\n" + "==========================\n\n" + "Expected drop(s) for given input:\n" + (int)Math.round(dropRate*killCount) +
                "\n\nExpected kill count for given input:\n" + (int)Math.ceil(dropsReceived/dropRate);
        }
    }

    void updateTextArea()
    {
        remove(textArea);
        updatedfPattern();
        calculateProbabilities();
        textArea = new JTextArea(outputMsg);
        textArea.setEditable(false);
        textArea.setFont(FontManager.getRunescapeBoldFont());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(textArea);
        revalidate();
        repaint();
    }

    private void updatedfPattern()
    {
        if (config.getDecimalPlaces() > 0)
        {
            strBldr = new StringBuilder();
            strBldr.append("#.");
            for (int i = 0; i < config.getDecimalPlaces(); i++)
            {
                strBldr.append("#");
            }
            dfPattern = strBldr.toString();
        }
        else
        {
            dfPattern = "#";
        }
        df = new DecimalFormat(dfPattern);
    }

}
