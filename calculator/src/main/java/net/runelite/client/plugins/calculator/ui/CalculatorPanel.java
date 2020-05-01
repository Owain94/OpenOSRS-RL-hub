/*
 * Copyright (c) 2020, Spedwards <https://github.com/Spedwards>
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
package net.runelite.client.plugins.calculator.ui;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import net.runelite.client.plugins.calculator.CalculatorPlugin;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.StringUtils;

public class CalculatorPanel extends JPanel
{
	private static final ImageIcon PLUS_MINUS_ICON;

	static
	{
		final BufferedImage plusMinusIcon = ImageUtil.resizeImage(ImageUtil.getResourceStreamFromClass(CalculatorPlugin.class, "plus_minus_icon.png"), 25, 25);
		PLUS_MINUS_ICON = new ImageIcon(plusMinusIcon);
	}

	private final Map<String, CalculatorButton> buttonMap = new HashMap<>();

	private final CalculatorPluginPanel panel;
	private final DisplayField displayField;

	protected CalculatorPanel(CalculatorPluginPanel panel)
	{
		super();

		this.panel = panel;
		this.displayField = panel.getDisplayField();

		setLayout(new GridLayout(4, 4, 4, 4));

		CalculatorButton plusMinus = new CalculatorButton(PLUS_MINUS_ICON);

		addButton("+");
		addButton("7");
		addButton("8");
		addButton("9");

		addButton("-");
		addButton("4");
		addButton("5");
		addButton("6");

		addButton("*");
		addButton("1");
		addButton("2");
		addButton("3");

		addButton("/");
		addButton("0");
		add(plusMinus);
		buttonMap.put("plusMinus", plusMinus);
		addButton("=");

		plusMinus.addActionListener(e ->
		{
			if (displayField.isFinished())
			{
				displayField.reset();
				if (displayField.getPreviousResult() != null)
				{
					displayField.setNum1(displayField.getPreviousResult());
				}
				else
				{
					displayField.setNum1(0);
				}
				displayField.setNum1(displayField.getNum1() * -1);
				displayField.setFinished(false);
			}
			else
			{
				if (displayField.getCalculatorAction() == null)
				{
					displayField.setNum1(displayField.getNum1() * -1);
				}
				else
				{
					Integer num2 = displayField.getNum2();
					if (num2 == null)
					{
						num2 = 0;
					}
					displayField.setNum2(num2 * -1);
				}
			}
			displayField.update();
		});
	}

	private void addButton(String key)
	{
		CalculatorButton btn = new CalculatorButton(key);
		btn.addActionListener(e ->
		{
			String text = btn.getText();
			if (text.equals("="))
			{
				displayField.calculateResult();
				if (displayField.getResult() == null)
				{
					// Divide by 0 error occured
					return;
				}
				panel.getHistoryPanel().addHistoryItem(displayField.getText() + " =", displayField.getResult().toString());
			}
			else if (StringUtils.isNumeric(text))
			{
				int num = Integer.parseInt(text);
				if (displayField.isFinished())
				{
					displayField.reset();
					displayField.setNum1(num);
					displayField.setFinished(false);
				}
				else
				{
					if (displayField.getCalculatorAction() == null)
					{
						Integer num1 = displayField.getNum1();
						if (num1 == 0)
						{
							if (displayField.num1IsNegativeZero())
							{
								num *= -1;
							}
							displayField.setNum1(num);
						}
						else
						{
							if (num1 < 0)
							{
								displayField.setNum1(num1 * 10 - num);
							}
							else
							{
								displayField.setNum1(num1 * 10 + num);
							}
						}
					}
					else
					{
						Integer num2 = displayField.getNum2();
						if (num2 == null || num2 == 0)
						{
							if (displayField.num2IsNegativeZero())
							{
								num *= -1;
							}
							displayField.setNum2(num);
						}
						else
						{
							if (num2 < 0)
							{
								displayField.setNum2(num2 * 10 - num);
							}
							else
							{
								displayField.setNum2(num2 * 10 + num);
							}
						}
					}
				}
			}
			else
			{
				if (displayField.isFinished() && displayField.getPreviousResult() != null)
				{
					displayField.reset();
					displayField.setNum1(displayField.getPreviousResult());
					displayField.setFinished(false);
					displayField.update();
				}
				if (displayField.getNum1() != null)
				{
					switch (text)
					{
						case "+":
							displayField.setCalculatorAction(DisplayField.Action.ADDITION);
							break;
						case "-":
							displayField.setCalculatorAction(DisplayField.Action.SUBTRACTION);
							break;
						case "*":
							displayField.setCalculatorAction(DisplayField.Action.MULTIPLICATION);
							break;
						case "/":
							displayField.setCalculatorAction(DisplayField.Action.DIVISION);
							break;
					}
				}
				displayField.setNum2(null);
			}
			displayField.update();
		});
		buttonMap.put(key, btn);
		add(btn);
	}
}