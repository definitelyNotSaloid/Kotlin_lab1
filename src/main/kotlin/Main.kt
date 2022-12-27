import lab34.*
import java.sql.DriverManager


suspend fun main() {
    Class.forName("com.mysql.cj.jdbc.Driver")

    val connection =
    DriverManager.getConnection(
        "jdbc:sqlserver://127.0.0.1:1434;database=kotel;encrypt=false;",
        "salo",
        "admin"
    )

    initBot(connection, "")
}
