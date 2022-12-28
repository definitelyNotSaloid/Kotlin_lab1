import lab34.*
import java.sql.DriverManager


suspend fun main() {
    Class.forName("com.mysql.cj.jdbc.Driver")

    val connection =
    DriverManager.getConnection(
        "jdbc:sqlserver://192.168.0.33:1433;database=kotel;encrypt=false;",
        "salo",
        "admin"
    )

    initBot(connection, readln())
}
