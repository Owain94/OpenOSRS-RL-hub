package com.xpgrapher;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

import java.awt.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XpGrapherOverlay extends OverlayPanel {

    private final Client client;
    private XpGrapherPlugin grapherPlugin;

    private int borderThickness = 2;
    private int divisionLineThickness = 1;

    private Color textColor = Color.WHITE;
    private Color graphLineColor;
    private Color backgroundColor;
    private int backgroundTransparency;

    private int marginGraphLeft = 43;
    private int marginGraphTop = 11;
    private int marginGraphRight = 24;
    private int marginGraphBottom = 30;
    private int marginTimeLabelTop = 7;

    private int marginOverlayRight = 5;

    //private int marginStartMessageTop = 12;
    private int marginStartMessageBottom = 8;

    private int marginLegendRight = 12;
    private int marginLegendTop = 8;
    private int marginLegendLeft = 10;
    private int marginLegendBoxBottom = 2;
    private int marginLegendBoxRight = 4;
    private int marginLegendBoxLeft = 4;
    private int marginLegendBoxTop = 2;
    private int marginLegendBottom = 3;
    private int legendBoxSize = 10;

    private int legendWidth = 93;
    private int legendHeight = 20;

    private int marginVertAxisValueRight = 4;

    private int bottomAxisTickMarkLength = 6;

    @Inject
    private XpGrapherOverlay(Client client, XpGrapherPlugin grapherPlugin) {
        this.client = client;
        this.grapherPlugin = grapherPlugin;
        graphLineColor = grapherPlugin.config.graphColor();
        backgroundColor = grapherPlugin.config.graphBackgroundColor();
        backgroundTransparency = grapherPlugin.config.graphBackgroundTransparency();
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.BOTTOM_LEFT);

    }

    private int convertTransparency(int configTransparency) {
        return (int)(255*((double)configTransparency/(double)100));
    }

    private String formatTime(long timePassed) {
        int secondsPassed = (int)timePassed/1000;
        int hours = secondsPassed/(60*60);
        int secondsLeft = secondsPassed%(60*60);
        int minutes = secondsLeft/60;
        int seconds = secondsLeft%60;

        String result = "";
        if (hours > 0)
            result += hours + ":";
        if (minutes > 0 || hours > 0) {
            if (minutes < 10 && hours > 0)
                result += "0";
            result += minutes + ":";
        }
        if (seconds < 10 && (minutes > 0|| hours > 0))
            result += "0";
        result += seconds;
        return result;
    }

    private String formatXpString(int xpToFormat) {

        String result;

        if (xpToFormat < 1000)
            result = Integer.toString(xpToFormat);
        else if (xpToFormat < 1000000) {

            int xpInK = xpToFormat/1000;
            int decimalPart = xpToFormat%1000;
            //System.out.println(xpToFormat + ", " + xpInK + ", " + decimalPart);
            result = Integer.toString(xpInK);
            if (decimalPart > 0) {
                result = result + "." + decimalPart;
                while (result.charAt(result.length()-1) == '0')
                    result = result.substring(0, result.length()-1);
            }


            result = result + "K";

        }
        else {
            int xpInM = xpToFormat/1000000;
            int decimalPart = xpToFormat%1000000;
            //System.out.println(xpToFormat + ", " + xpInK + ", " + decimalPart);
            result = Integer.toString(xpInM);
            if (decimalPart > 0) {
                result = result + "." + decimalPart;
                while (result.charAt(result.length()-1) == '0')
                    result = result.substring(0, result.length()-1);
            }


            result = result + "M";
        }

        return result;
    }

    public void printFormattedXp() {
        for (int i = 0; i < grapherPlugin.xpGraphPointManager.xpGraphMaxValues.length; i++) {
            System.out.println(formatXpString(grapherPlugin.xpGraphPointManager.xpGraphMaxValues[i]));
        }
    }



    LayoutableRenderableEntity graphEntity = new LayoutableRenderableEntity() {

        @Override
        public Dimension render(Graphics2D graphics) {

            graphLineColor = grapherPlugin.config.graphColor();
            backgroundColor = grapherPlugin.config.graphBackgroundColor();
            backgroundTransparency = convertTransparency(grapherPlugin.config.graphBackgroundTransparency());

            if (!grapherPlugin.config.displayKey()) {
                int currentWidth = this.getBounds().width;
                int currentHeight = this.getBounds().height;
                //this.setBounds(new Rectangle(currentWidth - legendWidth, currentHeight));
                //this.setPreferredSize(new Dimension(currentWidth - legendWidth, currentHeight));
                //setBounds(new Rectangle(50, 50));

            }

            //overlay background box
            int r = backgroundColor.getRed();
            int g = backgroundColor.getGreen();
            int b = backgroundColor.getBlue();
            Color backgroundColorTrans = new Color(r, g, b, backgroundTransparency);

            graphics.setColor(backgroundColorTrans);
            //graphics.fillRect(0, 0, this.getBounds().width, this.getBounds().height);
            graphics.fillRect(0, 0, marginGraphLeft+grapherPlugin.graphWidth+marginGraphRight+marginOverlayRight, this.getBounds().height);


            //overlay border box
            graphics.setColor(graphLineColor);
            int borderX = 0;
            int borderY = 0;
            graphics.drawRect(borderX, borderY, marginGraphLeft+grapherPlugin.graphWidth+marginGraphRight+marginOverlayRight, this.getBounds().height);
            graphics.drawRect(borderX+1, borderY+1, marginGraphLeft+grapherPlugin.graphWidth+marginGraphRight+marginOverlayRight-2, this.getBounds().height-2);

            //graph border box
            borderX = marginGraphLeft;
            borderY = marginGraphTop;
            graphics.drawRect(borderX, borderY, grapherPlugin.graphWidth, grapherPlugin.graphHeight);
            graphics.drawRect(borderX-1, borderY-1, grapherPlugin.graphWidth+2, grapherPlugin.graphHeight+2);

            //vertical divisions
            double verticalIncrement = (double)grapherPlugin.graphHeight/(double)grapherPlugin.numVerticalDivisions;
            for (int i = 0; i <= 5; i++) {

                int x = marginGraphLeft;
                int y = (int)(marginGraphTop+verticalIncrement*i);
                graphics.drawLine(x, y, x+grapherPlugin.graphWidth, y);

            }

            //vertical axis values
            int vertAxisMaxValue = grapherPlugin.xpGraphPointManager.maxVertAxisValue;
            int vertAxisXpInc = vertAxisMaxValue/grapherPlugin.numVerticalDivisions;

            for (int i = 0; i <= 5; i++) {
                int xpAtVerticalDiv = vertAxisMaxValue - i*vertAxisXpInc;

                String xpAtVerticalDivString = formatXpString(xpAtVerticalDiv);

                int stringWidth = graphics.getFontMetrics().stringWidth(xpAtVerticalDivString);
                int stringHeight = graphics.getFontMetrics().getHeight();

                if (xpAtVerticalDiv != 0 || i == 5)
                    graphics.drawString(xpAtVerticalDivString, marginGraphLeft-stringWidth-marginVertAxisValueRight, (int)(marginGraphTop+verticalIncrement*i+stringHeight/2-1));


            }

            //actual xp data lines
            for (int i = 0; i < grapherPlugin.skillList.length; i++) {

                Skill skillToGraph = grapherPlugin.skillList[i];
                Color skillColor = grapherPlugin.xpGraphColorManager.getSkillColor(skillToGraph);
                graphics.setColor(skillColor);

                if (grapherPlugin.isSkillShown(skillToGraph)) {

                    int oldX = -1;
                    int oldY = -1;
                    for (int x = 0; x < grapherPlugin.graphWidth; x++) {

                        int y = grapherPlugin.xpGraphPointManager.getGraphPointData(skillToGraph, x);
                        if (y >= 0) {
                            graphics.drawLine(marginGraphLeft+x, marginGraphTop+y, marginGraphLeft+x, marginGraphTop+y);
                            graphics.drawLine(marginGraphLeft+x, marginGraphTop+y+1, marginGraphLeft+x, marginGraphTop+y+1);
                        }

                        if (oldX != -1 && y >= 0) {
                            graphics.drawLine(marginGraphLeft+oldX, marginGraphTop+oldY, marginGraphLeft+x-1, marginGraphTop+y);
                            graphics.drawLine(marginGraphLeft+oldX-1, marginGraphTop+oldY, marginGraphLeft+x-2, marginGraphTop+y);
                        }
                        oldX = x;
                        oldY = y;
                    }

                }

            }

            int legendSkillCount =  grapherPlugin.currentlyGraphedSkills.size();

            if (grapherPlugin.config.displayKey() && legendSkillCount > 0) {

                //legendHeight = legendSkillCount*(legendBoxSize + marginLegendBoxTop + marginLegendBoxBottom) + marginLegendBottom;
                legendHeight = 12*legendSkillCount + 6;
                int legendX = marginGraphLeft+grapherPlugin.graphWidth+marginGraphRight+marginLegendLeft;
                int legendY = marginGraphTop;

                //legend background box
                graphics.setColor(backgroundColorTrans);
                graphics.fillRect(legendX, legendY, legendWidth, legendHeight);

                //legend border box
                graphics.setColor(graphLineColor);
                graphics.drawRect(legendX, legendY, legendWidth, legendHeight);
                graphics.drawRect(legendX-1, legendY-1, legendWidth+2, legendHeight+2);


                //legend boxes and labels
                int legendYOffset = marginGraphTop+2*marginLegendBoxTop-1;
                for (int i = 0; i < grapherPlugin.skillList.length; i++) {
                    Skill theSkill = grapherPlugin.skillList[i];
                    if (grapherPlugin.isSkillShown(theSkill)) {
                        //graphics.setColor(dataLineColors[i]);
                        Color skillColor = grapherPlugin.xpGraphColorManager.getSkillColor(theSkill);
                        graphics.setColor(skillColor);
                        int legendBoxX = legendX + marginLegendBoxLeft;
                        graphics.fillRect(legendBoxX, legendYOffset, legendBoxSize, legendBoxSize);
                        graphics.setColor(Color.BLACK);
                        graphics.drawRect(legendBoxX, legendYOffset, legendBoxSize, legendBoxSize);
                        legendYOffset += legendBoxSize + marginLegendBoxBottom;

                        graphics.setColor(graphLineColor);
                        String skillName = theSkill.getName();
                        int skillNameHeight = graphics.getFontMetrics().getHeight();
                        int skillNameX = legendBoxX + legendBoxSize + marginLegendBoxRight;
                        int skillNameY = legendYOffset;
                        graphics.drawString(skillName, skillNameX, skillNameY);
                    }
                }
            }



            //bottom axis tick marks
            graphics.setColor(graphLineColor);
            int bottomLeftGraphX = marginGraphLeft;
            int bottomLeftGraphY = marginGraphTop+grapherPlugin.graphHeight;
            graphics.drawLine(bottomLeftGraphX, bottomLeftGraphY, bottomLeftGraphX, bottomLeftGraphY + bottomAxisTickMarkLength);
            graphics.drawLine(bottomLeftGraphX-1, bottomLeftGraphY, bottomLeftGraphX-1, bottomLeftGraphY + bottomAxisTickMarkLength);
            int bottomRightGraphX = bottomLeftGraphX + grapherPlugin.graphWidth;
            graphics.drawLine(bottomRightGraphX, bottomLeftGraphY, bottomRightGraphX, bottomLeftGraphY + bottomAxisTickMarkLength);
            graphics.drawLine(bottomRightGraphX+1, bottomLeftGraphY, bottomRightGraphX+1, bottomLeftGraphY + bottomAxisTickMarkLength);

            long timePassed = grapherPlugin.currentTime - grapherPlugin.startTime;
            String timeStartLabel = "0";
            int timeStartLabelWidth = graphics.getFontMetrics().stringWidth(timeStartLabel);
            int timeStartLabelHeight = graphics.getFontMetrics().getHeight();
            graphics.drawString(timeStartLabel, bottomLeftGraphX-timeStartLabelWidth/2, bottomLeftGraphY+timeStartLabelHeight+marginTimeLabelTop);

            //int secondsPassed = (int)timePassed/1000;
            //int timePassedHours = secondsPassed/(60*60);
            //int secondsLeft = secondsPassed%(60*60);
            //int timePassedMinutes = secondsLeft/60;
            //int timePassedSeconds = secondsLeft%60;



            //String timeEndLabel = timePassedHoursString + ":" + timePassedMinutesString + ":" + timePassedSecondsString;

            //String timeEndLabel = formatTime(timePassedHours, timePassedMinutes, timePassedSeconds);
            String timeEndLabel = formatTime(timePassed);
            /*
            if (timePassedHours > 0)
                timeEndLabel += timePassedHours + ":";
            if (timePassedMinutes > 0 || timePassedHours > 0) {
                if (timePassedMinutes < 10 && timePassedHours > 0)
                    timeEndLabel += "0";
                timeEndLabel += timePassedMinutes + ":";
            }
            if (timePassedSeconds < 10 && (timePassedMinutes > 0|| timePassedHours > 0))
                timeEndLabel += "0";
            timeEndLabel += timePassedSeconds;
            */

            /*
            int secondsPassed = (int)(timePassed/1000);
            int timePassedMinutes = secondsPassed/60;
            int secondsLeft = secondsPassed%60;
            String secondsLeftString = Integer.toString(secondsLeft);
            if (secondsLeft < 10) {
                secondsLeftString = "0" + secondsLeftString;
            }
            String timeEndLabel = timePassedMinutes + ":" + secondsLeftString;
            */

            int timeEndLabelWidth = graphics.getFontMetrics().stringWidth(timeEndLabel);
            int timeEndLabelHeight = graphics.getFontMetrics().getHeight();
            graphics.drawString(timeEndLabel, bottomRightGraphX-timeEndLabelWidth/2, bottomLeftGraphY+timeEndLabelHeight+marginTimeLabelTop);


            //xp rate data
            if (grapherPlugin.config.displayXpRate() && grapherPlugin.currentlyGraphedSkills.size() > 0) {
                int endingSkillXp;
                if (grapherPlugin.tickCount > 0)
                    endingSkillXp = grapherPlugin.xpDataManager.getXpData(grapherPlugin.mostRecentSkillGained, grapherPlugin.tickCount-1);
                else
                    endingSkillXp = grapherPlugin.xpDataManager.getXpData(grapherPlugin.mostRecentSkillGained, 0);
                int startingSkillXp = grapherPlugin.xpDataManager.getXpData(grapherPlugin.mostRecentSkillGained, 0);
                int xpGained = endingSkillXp - startingSkillXp;
                long msPassed = System.currentTimeMillis() - grapherPlugin.startTime;
                long secPassed = msPassed/1000;
                double xpPerSecond = (double)xpGained/secPassed;
                double xpPerHour = xpPerSecond*60*60;
                String skillName = grapherPlugin.mostRecentSkillGained.getName();
                DecimalFormat formatter = new DecimalFormat("###,###,###");
                String skillInfoXpRate = formatter.format((int)xpPerHour);
                String skillInfo = skillName + " XP/hr: " + skillInfoXpRate;
                int skillInfoWidth = graphics.getFontMetrics().stringWidth(skillInfo);
                int skillInfoHeight = graphics.getFontMetrics().getHeight();
                //int skillInfoY = grapherPlugin.graphHeight+marginGraphTop+marginGraphBottom+bottomAxisTickMarkLength-skillInfoHeight/2;
                int skillInfoY = bottomLeftGraphY+timeEndLabelHeight+marginTimeLabelTop;
                int skillInfoX = marginGraphLeft+grapherPlugin.graphWidth/2-skillInfoWidth/2;
                graphics.drawString(skillInfo, skillInfoX, skillInfoY);
            }

            //show message to start skilling to start the graph
            //if (legendYOffset == marginGraphTop+2*marginLegendBoxTop-1) {
            if (grapherPlugin.currentlyGraphedSkills.size() == 0) {

                grapherPlugin.startMessageDisplaying = true;

                //0, 0, marginGraphLeft+grapherPlugin.graphWidth+marginGraphRight+marginOverlayRight, this.getBounds().height
                //int overlayWidth = grapherPlugin.graphWidth+marginGraphRight+marginGraphLeft+legendWidth+marginLegendRight;
                //int overlayHeight = marginGraphTop+grapherPlugin.graphHeight+marginGraphBottom;
                int overlayWidth = marginGraphLeft+grapherPlugin.graphWidth+marginGraphRight+marginOverlayRight;
                int overlayHeight = this.getBounds().height;

                int messageHeight = graphics.getFontMetrics().getHeight();

                String message1 = "Gain xp to start";
                int message1Width = graphics.getFontMetrics().stringWidth(message1);
                //int message1x = marginGraphLeft+grapherPlugin.graphWidth/2-message1Width/2;
                int message1x = overlayWidth/2-message1Width/2;

                int totalMessageHeight = 3*messageHeight+3*marginStartMessageBottom;
                int startingYOffset = this.getBounds().height/2 - totalMessageHeight/2;
                int message1y = marginGraphTop+startingYOffset;

                String message2 = "Alt+drag to move";
                int message2Width = graphics.getFontMetrics().stringWidth(message2);
                //int message2x = marginGraphLeft+grapherPlugin.graphWidth/2-message2Width/2;
                int message2x = overlayWidth/2-message2Width/2;
                int message2y = message1y+messageHeight+marginStartMessageBottom;

                String message3 = "Change graph size in settings";
                int message3Width = graphics.getFontMetrics().stringWidth(message3);
                //int message3x = marginGraphLeft+grapherPlugin.graphWidth/2-message3Width/2;
                int message3x = overlayWidth/2-message3Width/2;
                int message3y = message2y+messageHeight+marginStartMessageBottom;

                graphics.setColor(new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), 200));
                //graphics.fillRect(marginGraphLeft+1, marginGraphRight, grapherPlugin.graphWidth+marginGraphRight+legendWidth-1, grapherPlugin.graphHeight-1);
                graphics.fillRect(1, 1,
                        overlayWidth-1,
                        overlayHeight-1
                );


                graphics.setColor(graphLineColor);
                graphics.drawString(message1, message1x, message1y);
                graphics.drawString(message2, message2x, message2y);
                graphics.drawString(message3, message3x, message3y);

            } else {
                grapherPlugin.startMessageDisplaying = false;
            }



            return new Dimension(this.getBounds().width, this.getBounds().height);

        }

        @Override
        public Rectangle getBounds() {
            int boundsWidth = marginGraphLeft+grapherPlugin.graphWidth+marginGraphRight+legendWidth+marginLegendRight;
            int boundsHeight = marginGraphTop+grapherPlugin.graphHeight+marginGraphBottom;
            return new Rectangle(boundsWidth, boundsHeight);
        }

        @Override
        public void setPreferredLocation(Point position) {
            return;
        }

        @Override
        public void setPreferredSize(Dimension dimension) {
            return;
        }

    };

    @Override
    public Dimension render(Graphics2D graphics)
    {

        panelComponent.getChildren().add(graphEntity);
        panelComponent.setBackgroundColor(new Color(0, 0, 0, 0));

        return super.render(graphics);
    }

}

