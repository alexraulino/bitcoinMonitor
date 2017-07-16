package dashboard;

import org.jfree.ui.ApplicationFrame;

public class BitCoinMonitorDashBoard extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static PanelPortifolio pn = new PanelPortifolio();

	public BitCoinMonitorDashBoard(final String title) {
		super(title);
		getContentPane().setLayout(null);
		getContentPane().add(pn);
		getContentPane().setPreferredSize(new java.awt.Dimension(660, 350));
		setResizable(false);

	}

}