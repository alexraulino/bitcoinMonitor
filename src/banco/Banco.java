package banco;

import java.sql.Connection;
import java.sql.DriverManager;

public class Banco {
	
	  private static final String USUARIO = "bitcoinmonitor";
	    private static final String SENHA = "afr12481632";
	    private static final String URL = "jdbc:mysql://db4free.net:3306/bitcoinmonitor";
	    private static final String DRIVER = "com.mysql.jdbc.Driver";

	    // Conectar ao banco
	    public static Connection abrir() throws Exception {
	        // Registrar o driver
	        Class.forName(DRIVER);
	        // Capturar a conexão
	        Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
	        // Retorna a conexao aberta
	        return conn;


	    }

}
