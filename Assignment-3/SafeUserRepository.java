// SafeUserRepository.java
import java.sql.*;
import javax.sql.DataSource;

/** Parameterized queries prevent SQL injection at the application layer. */
public class SafeUserRepository {
  private final DataSource ds;
  public SafeUserRepository(DataSource ds) { this.ds = ds; }

  public User findByEmail(String email) throws SQLException {
    String sql = "SELECT id, email, display_name FROM users WHERE email = ?";
    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, email);             // <-- NO string concatenation
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          User u = new User();
          u.id = rs.getLong("id");
          u.email = rs.getString("email");
          u.displayName = rs.getString("display_name");
          return u;
        }
        return null;
      }
    }
  }

  public static class User {
    public long id;
    public String email;
    public String displayName;
  }
}
