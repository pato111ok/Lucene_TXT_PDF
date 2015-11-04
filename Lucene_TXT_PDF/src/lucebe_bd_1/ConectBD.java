package lucebe_bd_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Patricio Jaramillo
 */
public class ConectBD {

	public static void main(String args[]){

		// Servidor (mÃ¡quina)
		String host = "localhost";

		// Sevidor (puerto): el puerto por defecto es el 3306
		String port = "3306";

		// Usuario
		String user = "root";

		// ContraseÃ±a
		String passwd = "";

		// Base de datos a utilizar
		String db = "scopus_db_2015_10_14";

		// Representa una conexiÃ³n a la base de datos
		Connection conn = null;

		// Permite ejecutar una sentencia o comando en la base de datos
		Statement stmt = null;

		// Representa a los resultados de ejecutar una consulta en la base de datos
		ResultSet rs = null;

		try {

			// Cargamos dinÃ¡micamente la clase del driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			// Abrimos la conexiÃ³n a la base de datos
			String url = "jdbc:mysql://" + host + ":" + port + "/" + db;
			conn = DriverManager.getConnection(url, user, passwd);

			// Queremos ejecutar comandos sobre la base de datos, 
			// asÃ­ que necesitamos un objeto Statement
			stmt = conn.createStatement();

			// executeUpdate: INSERT, DELETE, DROP, ALTER, UPDATE
			// Creamos una tabla
			//int result = stmt.executeUpdate("CREATE TABLE tabla_ejemplo (id INT, nombre VARCHAR(25), primary key(id))");

			// Insertamos dos entradas
			//result = stmt.executeUpdate("INSERT INTO tabla_ejemplo VALUES(1, 'Un nombre cualquiera')");
			//result = stmt.executeUpdate("INSERT INTO tabla_ejemplo VALUES(2, 'Otro nombre')");
			
			// executeQuery: SELECT
			// Leemos todos los datos de la tabla
			rs = stmt.executeQuery("SELECT * FROM triples_prefix");

			// Imprimimos los resultados
			while (rs.next()) {
				System.out.println("s=" + rs.getString("s") + " p=" + rs.getString("p")+ " o=" + rs.getString("o"));
			}
			
			// Contamos el nÃºmero de entradas de la tabla
			rs = stmt.executeQuery("SELECT COUNT(*) as valor FROM triples_prefix");
			
			// Imprimimos el resultado
			/*if (rs.next()) {
				System.out.println("Num de entradas=" + rs.getString("o"));
			}*/

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			// Cerramos todo para liberar recursos de la base de datos
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			catch (SQLException e) {
				System.out.println("Error al liberar recursos");
			}
		}
	}
}
