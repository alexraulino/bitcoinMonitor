package chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

public class BitCoinMonitorChart extends ApplicationFrame {

	public static TimeSeriesCollection tm;

	public BitCoinMonitorChart(final String title) {
		super(title);
		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		setContentPane(chartPanel);
	}

	private XYDataset createDataset() {
		this.tm = new TimeSeriesCollection();
		return tm;
	}

	private JFreeChart createChart(final XYDataset dataset) {
		return ChartFactory.createTimeSeriesChart("Portifolio BitCoin", "Hora", "% Ganho", dataset, true, true, true);
	}

}