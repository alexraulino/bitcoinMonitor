package dashboard;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelPortifolio extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1724820878296252505L;

	private JLabel lb_dEnt, lb_vEnt, lb_lAtu, lb_vAtu, lb_vPer, lb_cPer, lb_gif_load;
	private JPanel panelMoedas;

	private Double valuePercentual = 0.0;

	private static Color cl_greenForest = new Color(34, 139, 34);

	private HashMap<String, PanelMoeda> moedas = new HashMap<>();
	private ArrayList<String> status = new ArrayList<>();

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public void setEntrada(Double value) {
		lb_vEnt.setText(String.format("%-15.8f", value));
	}

	public void setAtual(Double value) {
		lb_vAtu.setText(String.format("%-15.8f", value));
	}

	public void setMoeda(String nome, Double compra, Double venda, Double diferenca, Double percentual) {
		if (moedas.containsKey(nome)) {
			moedas.get(nome).setAtualizar(nome, compra, venda, percentual);
		} else {
			PanelMoeda pm = new PanelMoeda();
			pm.setAtualizar(nome, compra, venda, percentual);
			moedas.put(nome, pm);
			panelMoedas.add(pm);
		}
	}

	public void setPercentual(Double value) {

		value = round(value, 2);

		lb_vPer.setText(String.format("%-15.2f", value));
		if (value < 0.0) {
			lb_vPer.setForeground(Color.red);

		} else {
			lb_vPer.setForeground(cl_greenForest);
		}

		if (value > valuePercentual) {
			status.add("<FONT COLOR=green>▲</FONT>");

		} else

		if (value < valuePercentual) {
			status.add("<FONT COLOR=RED>▼</FONT>");
		}

		if (status.size() == 25) {
			status.remove(0);
		}

		String aux = "";
		for (String x : status) {
			aux = x + aux;
		}

		lb_cPer.setText("<html>" + aux + "</html>");

		valuePercentual = value;
	}

	public PanelPortifolio() {
		setLayout(null);

		lb_dEnt = new JLabel("E:");
		add(lb_dEnt);
		lb_dEnt.setBounds(5, 5, 20, 15);

		lb_vEnt = new JLabel("0,0");
		add(lb_vEnt);
		lb_vEnt.setBounds(23, 5, 100, 15);

		lb_lAtu = new JLabel("A:");
		add(lb_lAtu);
		lb_lAtu.setBounds(101, 5, 20, 15);

		lb_vAtu = new JLabel("0,0");
		add(lb_vAtu);
		lb_vAtu.setBounds(115, 5, 100, 15);

		lb_vPer = new JLabel("0.0");
		add(lb_vPer);
		lb_vPer.setBounds(202, 5, 100, 15);

		lb_cPer = new JLabel("");
		lb_cPer.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(lb_cPer);
		lb_cPer.setBounds(239, 5, 700, 15);

		setBounds(0, 0, 660, 450);
		setVisible(true);
		setBorder(BorderFactory.createLoweredBevelBorder());

		panelMoedas = new JPanel();
		panelMoedas.setBorder(BorderFactory.createLoweredBevelBorder());
		panelMoedas.setBounds(5, 40, 650, 200);
		panelMoedas.setLayout(new BoxLayout(panelMoedas, BoxLayout.Y_AXIS));
		add(panelMoedas);

		ImageIcon imageIcon = new ImageIcon("loading.gif");
		lb_gif_load = new JLabel(imageIcon);
		lb_gif_load.setBounds(626, 5, 30, 30);
		add(lb_gif_load);

	}

	public void mostraLoad(boolean visible) {
		lb_gif_load.setVisible(visible);
	}

}
