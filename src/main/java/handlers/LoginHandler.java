package handlers;

import models.Response;
import models.User;
import utilities.ConnManager;
import utilities.PropertiesReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginHandler {
  private static Connection connection = ConnManager.getConnection();
  private static PropertiesReader prop = PropertiesReader.getInstance();

  public static Response<?> login(User user) {
    Response<?> response = new Response<>();
    String query = prop.getValue("login");
    try {
      PreparedStatement pstmt = connection.prepareStatement(query);
      pstmt.setString(1, user.getUserName());
      pstmt.setString(2, user.getPassword());
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        response.setStatus(200);
        response.setMessage("Access granted");
      }
      else {
        response.setStatus(401);
        response.setMessage("Unauthorized, bad credentials");
      }
    } catch (SQLException e) {
      response.setStatus(500);
      response.setMessage("DB connection error");
      e.printStackTrace();
    }
    return response;
  }

}