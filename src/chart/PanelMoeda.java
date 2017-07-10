package chart;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelMoeda extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1724820878296252505L;

	private JLabel lb_dEnt, lb_vEnt, lb_lAtu, lb_vAtu, lb_vPer, lb_cPer, lb_nome;

	private Double valuePercentual = 0.0;

	private static Color cl_greenForest = new Color(34, 139, 34);

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public void setAtualizar(String nome, Double compra, Double venda, Double percentual) {
		lb_nome.setText(nome);
		setCompra(compra);
		setVenda(venda);
		setPercentual(percentual);
	}

	public void setCompra(Double value) {
		lb_vEnt.setText(String.format("%-15.8f", value));
	}

	public void setVenda(Double value) {
		lb_vAtu.setText(String.format("%-15.8f", value));
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

	public PanelMoeda() {
		setLayout(null);

		lb_nome = new JLabel("DOGE");
		add(lb_nome);
		lb_nome.setBounds(5, 5, 40, 15);

		lb_dEnt = new JLabel("E:");
		add(lb_dEnt);
		lb_dEnt.setBounds(45, 5, 20, 15);

		lb_vEnt = new JLabel("0,01334577");
		add(lb_vEnt);
		lb_vEnt.setBounds(58, 5, 100, 15);

		lb_lAtu = new JLabel("V:");
		add(lb_lAtu);
		lb_lAtu.setBounds(141, 5, 20, 15);

		lb_vAtu = new JLabel("0,00927512");
		add(lb_vAtu);
		lb_vAtu.setBounds(155, 5, 100, 15);

		lb_vPer = new JLabel("-30,57");
		add(lb_vPer);
		lb_vPer.setBounds(242, 5, 100, 15);

		lb_cPer = new JLabel("");
		add(lb_cPer);
		lb_cPer.setBounds(279, 5, 100, 15);

		setBounds(0, 0, 20, 100);
		setVisible(true);
		setBorder(BorderFactory.createRaisedBevelBorder());
	}
}
