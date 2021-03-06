/*******************************************************************************
 * TurtleKit 3 - Agent Based and Artificial Life Simulation Platform
 * Copyright (C) 2011-2014 Fabien Michel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package turtlekit.murmuration;


import java.util.logging.Level;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import madkit.simulation.SimulationException;
import madkit.simulation.probe.PropertyProbe;
import turtlekit.agr.TKOrganization;
import turtlekit.gui.util.ChartsUtil;
import turtlekit.viewer.AbstractObserver;

public class SpeedChecker extends AbstractObserver {
	

	private PropertyProbe<AbstractStarling,Double> probeSpeed;
	private XYSeries speed;
	private int index = 0;
	
	public SpeedChecker() {
		createGUIOnStartUp(); //prevent inappropriate launching and thus null pointer
	}

	/**
	 * Just to do some initialization work
	 */
	@Override
	protected void activate() {
		setLogLevel(Level.ALL);
		super.activate();
		probeSpeed = new PropertyProbe<AbstractStarling,Double>(getCommunity(), TKOrganization.TURTLES_GROUP, TKOrganization.TURTLE_ROLE, "speed");
		addProbe(probeSpeed);
	}

	@Override
	public void setupFrame(JFrame frame) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		final ChartPanel chartPanel = ChartsUtil.createChartPanel(dataset, "Moyenne", null, null);
		chartPanel.setPreferredSize(new java.awt.Dimension(550, 250));
		speed = new XYSeries("Speed");
		dataset.addSeries(speed);
		frame.setContentPane(chartPanel);
		frame.setLocation(50, 0);
//		XYSeries s = dataset.getSeries("Total");
	}
	
	@Override
	protected void observe() {
		double averageHeading = 0;
		double averageSpeed = 0;
		for (AbstractStarling a : probeSpeed.getCurrentAgentsList()) {
			averageSpeed += probeSpeed.getPropertyValue(a);
		}
		averageSpeed /= (float) probeSpeed.size();
		
		if(index % 10000 == 0){
			speed.clear();
		}

		try {
//			heading.add(index, averageHeading);
			speed.add(index, averageSpeed);
		} catch (SimulationException | NullPointerException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		index++;
	}
	

}
