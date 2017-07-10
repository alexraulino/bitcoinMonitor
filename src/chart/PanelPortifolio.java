package chart;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelPortifolio extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1724820878296252505L;

	private JLabel lb_dEnt, lb_vEnt, lb_lAtu, lb_vAtu, lb_vPer, lb_cPer;
	private JPanel panelMoedas;

	private Double valuePercentual = 0.0;

	private static Color cl_greenForest = new Color(34, 139, 34);

	private HashMap<String, PanelMoeda> moedas = new HashMap<>();

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
			lb_cPer.setText("▲");
			lb_cPer.setForeground(cl_greenForest);
		} else

		if (value < valuePercentual) {
			lb_cPer.setText("▼");
			lb_cPer.setForeground(Color.red);
		}

		valuePercentual = value;
	}

	public PanelPortifolio() {
		setLayout(null);

		lb_dEnt = new JLabel("E:");
		add(lb_dEnt);
		lb_dEnt.setBounds(5, 5, 20, 15);

		lb_vEnt = new JLabel("0,01334577");
		add(lb_vEnt);
		lb_vEnt.setBounds(18, 5, 100, 15);

		lb_lAtu = new JLabel("A:");
		add(lb_lAtu);
		lb_lAtu.setBounds(101, 5, 20, 15);

		lb_vAtu = new JLabel("0,00927512");
		add(lb_vAtu);
		lb_vAtu.setBounds(115, 5, 100, 15);

		lb_vPer = new JLabel("-30,57");
		add(lb_vPer);
		lb_vPer.setBounds(202, 5, 100, 15);

		lb_cPer = new JLabel("");
		add(lb_cPer);
		lb_cPer.setBounds(239, 5, 100, 15);

		setBounds(0, 0, 410, 450);
		setVisible(true);
		setBorder(BorderFactory.createLoweredBevelBorder());

		panelMoedas = new JPanel();
		panelMoedas.setBorder(BorderFactory.createLoweredBevelBorder());
		panelMoedas.setBounds(5, 40, 400, 140);
		panelMoedas.setLayout(new BoxLayout(panelMoedas, BoxLayout.Y_AXIS));
		add(panelMoedas);
	}

	public static void main(String[] args) {

	}

}
