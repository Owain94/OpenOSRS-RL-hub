/*
 * Copyright (c) 2020, RKGman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.music.firebeats;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public
class FireBeatsPanel extends PluginPanel implements ChangeListener, ActionListener
{
    FireBeatsPlugin fireBeatsPlugin;



    public FireBeatsPanel(FireBeatsPlugin fireBeatsPlugin)
    {
        super(false);

        this.fireBeatsPlugin = fireBeatsPlugin;

        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titlePanel.setLayout(new BorderLayout());

        JLabel title = new JLabel();
        title.setText("Fire Beats Controls");
        title.setForeground(Color.WHITE);
        titlePanel.add(title, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // End Title Panel

        JPanel volumePanel = new JPanel();
        volumePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        volumePanel.setLayout(new FlowLayout());

        // Volume
        JLabel volumeLabel = new JLabel();
        volumeLabel.setText("Volume");
        volumeLabel.setForeground(Color.WHITE);
        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100,
                fireBeatsPlugin.getMusicConfig().volume());
        volumeSlider.setBackground(Color.LIGHT_GRAY);
        volumeSlider.setName("volume");
        volumeSlider.addChangeListener((ChangeListener) this);
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);

        // Remix Offset
        JLabel remixOffsetLabel = new JLabel();
        remixOffsetLabel.setText("Remix Offset");
        remixOffsetLabel.setForeground(Color.WHITE);
        JSlider remixOffsetSlider = new JSlider(JSlider.HORIZONTAL, 0, 100,
                fireBeatsPlugin.getMusicConfig().remixVolumeOffset());
        remixOffsetSlider.setBackground(Color.LIGHT_GRAY);
        remixOffsetSlider.setName("remixOffset");
        remixOffsetSlider.addChangeListener((ChangeListener) this);
        volumePanel.add(remixOffsetLabel);
        volumePanel.add(remixOffsetSlider);

        JPanel togglePanel = new JPanel();
        togglePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        togglePanel.setLayout(new GridLayout(0, 1));

        // Mute
        JLabel muteLabel = new JLabel();
        muteLabel.setText("Mute:");
        muteLabel.setForeground(Color.WHITE);
        JCheckBox muteCheckBox = new JCheckBox();
        muteCheckBox.setSelected(fireBeatsPlugin.getMusicConfig().mute());
        muteCheckBox.setForeground(Color.WHITE);
        muteCheckBox.setName("mute");
        muteCheckBox.addActionListener((ActionListener) this);
        togglePanel.add(new JSeparator());
        togglePanel.add(muteLabel);
        togglePanel.add(muteCheckBox);

        ButtonGroup bg = new ButtonGroup();

        // Loop
        JLabel loopLabel = new JLabel();
        loopLabel.setText("Loop - Normal Mode:");
        loopLabel.setForeground(Color.WHITE);
        JRadioButton loopRadioButton = new JRadioButton();
        loopRadioButton.setSelected(fireBeatsPlugin.getMusicConfig().loop());
        loopRadioButton.setForeground(Color.WHITE);
        loopRadioButton.setName("loop");
        loopRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton source = (JRadioButton)e.getSource();
                fireBeatsPlugin.getMusicConfig().setLoop(source.isSelected());

                if (source.isSelected() == true)
                {
                    fireBeatsPlugin.getMusicConfig().setShuffleMode(false);
                }
            }
        });
        bg.add(loopRadioButton);
        togglePanel.add(new JSeparator());
        togglePanel.add(loopLabel);
        togglePanel.add(loopRadioButton);

        // Shuffle Mode
        JLabel shuffleLabel = new JLabel();
        shuffleLabel.setText("Shuffle Mode - (WIP):");
        shuffleLabel.setForeground(Color.WHITE);
        JRadioButton shuffleRadioButton = new JRadioButton();
        shuffleRadioButton.setSelected(fireBeatsPlugin.getMusicConfig().shuffleMode());
        shuffleRadioButton.setForeground(Color.WHITE);
        shuffleRadioButton.setName("shuffle");
        shuffleRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton source = (JRadioButton)e.getSource();
                fireBeatsPlugin.getMusicConfig().setShuffleMode(source.isSelected());

                if (source.isSelected() == true)
                {
                    fireBeatsPlugin.getMusicConfig().setLoop(false);
                    // loop.setSelected(false);
                }
            }
        });
        togglePanel.add(new JSeparator());
        togglePanel.add(shuffleLabel);
        togglePanel.add(shuffleRadioButton);
        bg.add(shuffleRadioButton);
        togglePanel.add(new JSeparator());

        // Shuffle Next Button
        JButton shuffleNextTrackButton = new JButton("Shuffle to Next Track  >>");
        shuffleNextTrackButton.setName("shuffleButton");
        shuffleNextTrackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireBeatsPlugin.getMusicConfig().setLoop(false);
                loopRadioButton.setSelected(false);
                fireBeatsPlugin.getMusicConfig().setShuffleMode(true);
                shuffleRadioButton.setSelected(true);
                fireBeatsPlugin.shuffleNextTrack();
            }
        });
        togglePanel.add(shuffleNextTrackButton);
        togglePanel.add(new JSeparator());

        // Show Track Name
        JLabel showTrackLabel = new JLabel();
        showTrackLabel.setText("Show Area's Track Name:");
        showTrackLabel.setForeground(Color.WHITE);
        JCheckBox showTrackCheckBox = new JCheckBox();
        showTrackCheckBox.setSelected(fireBeatsPlugin.getMusicConfig().showCurrentTrackName());
        showTrackCheckBox.setForeground(Color.WHITE);
        showTrackCheckBox.setName("showTrackName");
        showTrackCheckBox.addActionListener((ActionListener) this);
        togglePanel.add(showTrackLabel);
        togglePanel.add(showTrackCheckBox);
        togglePanel.add(new JSeparator());

        // Play Original Music
        JLabel playOriginalLabel = new JLabel();
        playOriginalLabel.setText("Play Original When No Remix:");
        playOriginalLabel.setForeground(Color.WHITE);
        JCheckBox playOriginalCheckBox = new JCheckBox();
        playOriginalCheckBox.setSelected(fireBeatsPlugin.getMusicConfig().playOriginalIfNoRemix());
        playOriginalCheckBox.setForeground(Color.WHITE);
        playOriginalCheckBox.setName("playOriginal");
        playOriginalCheckBox.addActionListener((ActionListener) this);
        togglePanel.add(playOriginalLabel);
        togglePanel.add(playOriginalCheckBox);
        togglePanel.add(new JSeparator());

        // Update Button
        JButton updateFromRepoButton = new JButton("Update Track List");
        updateFromRepoButton.setName("updateButton");
        updateFromRepoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // log.info("Button pushed to update CSV.");
                // method to update CSV list.
                fireBeatsPlugin.updateListFromRepo(true);
            }
        });
        togglePanel.add(updateFromRepoButton);
        togglePanel.add(new JSeparator());

        // Automatically Pull List From Repository
        JLabel updateFromRepoLabel = new JLabel();
        updateFromRepoLabel.setText("Auto Update List From Repo:");
        updateFromRepoLabel.setForeground(Color.WHITE);
        JCheckBox updateFromRepoCheckBox = new JCheckBox();
        updateFromRepoCheckBox.setSelected(fireBeatsPlugin.getMusicConfig().updateFromRepo());
        updateFromRepoCheckBox.setForeground(Color.WHITE);
        updateFromRepoCheckBox.setName("updateFromRepo");
        updateFromRepoCheckBox.addActionListener((ActionListener) this);
        togglePanel.add(updateFromRepoLabel);
        togglePanel.add(updateFromRepoCheckBox);
        togglePanel.add(new JSeparator());

        volumePanel.add(togglePanel);

        add(volumePanel, BorderLayout.CENTER);
    }

    public void stateChanged(ChangeEvent e)
    {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            if (source.getName() == "volume")
            {
                // log.info("Volume is " + source.getValue());
                if (source.getValue() < fireBeatsPlugin.getMusicConfig().remixVolumeOffset())
                {
                    fireBeatsPlugin.getMusicConfig().setVolume(fireBeatsPlugin.getMusicConfig().remixVolumeOffset());
                }
                else
                {
                    fireBeatsPlugin.getMusicConfig().setVolume(source.getValue());
                }
            }
            else if (source.getName() == "remixOffset")
            {
                // log.info("Remix offset is " + source.getValue());
                fireBeatsPlugin.getMusicConfig().setRemixVolumeOffset(source.getValue());
            }

        }
    }

    public void actionPerformed(ActionEvent e)
    {
        JCheckBox source = (JCheckBox)e.getSource();
        if (source.getName() == "mute")
        {
            // log.info("Value of mute is " + source.isSelected());
            fireBeatsPlugin.getMusicConfig().setMute(source.isSelected());
        }
        else if (source.getName() == "showTrackName")
        {
            // log.info("Value of showTrackName is " + source.isSelected());
            fireBeatsPlugin.getMusicConfig().setShowCurrentTrackName(source.isSelected());
        }
        else if (source.getName() == "playOriginal")
        {
            // log.info("Value of playOriginal is " + source.isSelected());
            fireBeatsPlugin.getMusicConfig().setPlayOriginalIfNoRemix(source.isSelected());
        }
        else if (source.getName() == "updateFromRepo")
        {
            // log.info("Value of updateFromRepo is " + source.isSelected());
            fireBeatsPlugin.getMusicConfig().setUpdateFromRepo(source.isSelected());
        }
    }
}
