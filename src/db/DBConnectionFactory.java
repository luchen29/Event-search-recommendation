package db;
import db.mysql.MySQLConnection;

public class DBConnectionFactory {
	// This should change based on the pipeline.
	// 可以在这里进行选择 mysql或者mongo
	private static final String DEFAULT_DB = "mysql";
	
	public static DBConnection getConnection(String db) {
		switch (db) {
		case "mysql":
//			返回connection object -- Factory patern
			return new MySQLConnection();
//			return null;
		case "mongodb":
			// return new MongoDBConnection();
			return null;
		default:
			throw new IllegalArgumentException("Invalid db:" + db);
		}

	}

	public static DBConnection getConnection() {
		return getConnection(DEFAULT_DB);
	}
}
