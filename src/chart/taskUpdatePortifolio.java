package chart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import banco.Banco;

public class taskUpdatePortifolio extends Thread {

	public PanelPortifolio pn;

	public taskUpdatePortifolio(PanelPortifolio pn) {
		this.pn = pn;
	}

	@Override
	public void run() {

		try {
			Connection conn = Banco.abrir();
			while (true) {

				// the mysql insert statement
				String query3 = "SELECT h1.* FROM historicoMoeda h1 where h1.vendido <> 'S' and h1.data = (select max(h2.data) from historicoMoeda h2 where h1.nome = h2.nome and h2.data <= CURRENT_TIMESTAMP) ";

				// create the mysql insert preparedstatement
				PreparedStatement preparedStmt3 = conn.prepareStatement(query3);
				try {

					ResultSet rs3 = preparedStmt3.executeQuery();
					try {

						while (rs3.next()) {
							pn.setMoeda(rs3.getString("nome"), rs3.getDouble("compra"), rs3.getDouble("venda"),
									rs3.getDouble("diferenca"), rs3.getDouble("percentual"));

						}
					} finally {
						rs3.close();
					}

				} finally {
					preparedStmt3.close();
				}

				// the mysql insert statement
				String query = "SELECT h1.* FROM historicoPortifolio h1 where h1.data = (select max(h2.data) from historicoPortifolio h2 where h2.data <= CURRENT_TIMESTAMP) ";

				// create the mysql insert preparedstatement
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				try {

					ResultSet rs = preparedStmt.executeQuery();
					try {

						pn.setPercentual(rs.getDouble("percentualGanho"));
						pn.setAtual(rs.getDouble("BTC_atual"));
					} finally {
						rs.close();
					}
				} finally {
					preparedStmt.close();
				}

				String query2 = "SELECT * from portifolio ";

				// create the mysql insert preparedstatement
				PreparedStatement preparedStmt2 = conn.prepareStatement(query2);
				try {
					ResultSet rs2 = preparedStmt2.executeQuery();
					try {
						pn.setEntrada(rs2.getDouble("BTC_entrada"));
					} finally {
						rs2.close();
					}
				} finally {
					preparedStmt2.close();
				}

				Thread.sleep(3000);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
