package chart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import banco.Banco;

public class taskUpdateChart extends Thread {

	public TimeSeriesCollection tm;
	public HashMap<String, TimeSeries> series = new HashMap<>();

	public taskUpdateChart(TimeSeriesCollection tm) {
		this.tm = tm;
	}

	@Override
	public void run() {
		try {

			Connection conn = Banco.abrir();
			Second current = new Second();
			while (true) {

				// the mysql insert statement
				String query = "SELECT h1.nome, h1.percentual, CURRENT_TIMESTAMP dataatual FROM historicoMoeda h1 where h1.vendido <> 'S' and h1.data = (select max(h2.data) from historicoMoeda h2 where h1.nome = h2.nome and h2.data <= CURRENT_TIMESTAMP) ";

				// create the mysql insert preparedstatement
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				ResultSet rs = preparedStmt.executeQuery();

				while (rs.next()) {
					if (series.containsKey(rs.getString("nome"))) {
						series.get(rs.getString("nome")).add(current, rs.getDouble("percentual"));
					} else {
						TimeSeries tt = new TimeSeries(rs.getString("nome"));
						series.put(rs.getString("nome"), tt);
						tm.addSeries(tt);
						;
					}

				}

				preparedStmt.close();

				Thread.sleep(5000);
				current = (Second) current.next();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
