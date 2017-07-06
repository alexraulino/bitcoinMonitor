package banco;

import java.sql.Connection;
import java.sql.DriverManager;

public class Banco {

	private static final String URL = "jdbc:sqlite:bitcoinmonitor.db";
	private static final String DRIVER = "org.sqlite.JDBC";

	// Conectar ao banco
	public static Connection abrir() throws Exception {

		// Registrar o driver
		Class.forName(DRIVER);
		// Capturar a conexão
		Connection conn = DriverManager.getConnection(URL);
		// Retorna a conexao aberta
		return conn;

	}

}
