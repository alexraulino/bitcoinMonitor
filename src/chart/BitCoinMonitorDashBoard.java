package chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

public class BitCoinMonitorDashBoard extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static TimeSeriesCollection tm;
	public static PanelPortifolio pn = new PanelPortifolio();

	public BitCoinMonitorDashBoard(final String title) {
		super(title);
		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

		// setContentPane(chartPanel);
		getContentPane().setLayout(null);
		getContentPane().add(pn);
		getContentPane().setPreferredSize(new java.awt.Dimension(510, 350));

	}

	private XYDataset createDataset() {
		BitCoinMonitorDashBoard.tm = new TimeSeriesCollection();
		return tm;
	}

	private JFreeChart createChart(final XYDataset dataset) {
		return ChartFactory.createTimeSeriesChart("Portifolio BitCoin", "Hora", "% Ganho", dataset, true, true, true);
	}

}