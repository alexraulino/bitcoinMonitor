package banco;

import java.sql.Connection;
import java.sql.DriverManager;

public class Banco {

	private static final String URL = "jdbc:sqlite:bitcoinmonitor.db";
	private static final String DRIVER = "org.sqlite.JDBC";
	private static Connection conn = null;

	// Conectar ao banco
	public static Connection abrir() throws Exception {

		if (conn == null) {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL);
		}

		return conn;

	}

}
