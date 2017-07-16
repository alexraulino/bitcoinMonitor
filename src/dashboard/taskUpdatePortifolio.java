package dashboard;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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

							if (rs3.getDouble("percentual") >= 10.0) {
								String msg = rs3.getString("nome") + rs3.getDouble("percentual");
								String url = "http://gatemonito.000webhostapp.com/GCM/send_push_notification_message.php?regId=APA91bFvaROHsLNEHE33oTPvfyu5T9aGeRCvSeD0g5jl-6AzpEpNXhPdeBUwcqcfTs0YIfoidfKVWDqfM6jzlr42pkOBanz6bWV_SonD_p97QPVe6EfikSVUCMdcMWo9VnbSP931wuoH&message="
										+ msg;
								try {
									CloseableHttpClient httpClient = HttpClients.createDefault();
									HttpPost post = new HttpPost(url);
									CloseableHttpResponse response = httpClient.execute(post);
									System.out.println(EntityUtils.toString(response.getEntity()));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

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

				pn.mostraLoad(false);
				Thread.sleep(3000);
				pn.mostraLoad(true);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
