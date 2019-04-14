package handlers;

import models.*;
import utilities.PropertiesReader;
import utilities.PoolManager;
import utilities.Pool;

import javax.websocket.Session;
import java.io.File;
import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ptthappy
 */

@SuppressWarnings("Duplicates")
public class UserHandler {
	private static PoolManager poolManager = PoolManager.getPoolManager();
	private static PropertiesReader prop = PropertiesReader.getInstance();

	public static Response<User> login(User user) {
		Connection con = poolManager.getConn();
		Response<User> response = new Response<>();
		String query = prop.getValue("login");
		try {
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, user.getLowercaseUsername());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				getUserData(rs, user);
				response.setStatus(200);
				response.setMessage("Access granted");
				response.setData(user);
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
		poolManager.returnConn(con);
		return response;
	}

	public static Response<User> register(User user) throws IOException {
		Connection con = poolManager.getConn();
		Response<User> response = new Response<>();
		String query = prop.getValue("registerUser");
		if(checkLowercaseUsername(user.getLowercaseUsername())) {
			response.setStatus(409);
			response.setMessage("Username already registered");
			poolManager.returnConn(con);
			return response;
		}
		if(checkEmail(user.getEmail().toLowerCase())) {
			response.setStatus(409);
			response.setMessage("Email already in use");
			poolManager.returnConn(con);
			return response;
		}
		try {
			PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getLowercaseUsername());
			pstmt.setString(3, user.getPassword());
			pstmt.setString(4, user.getName());
			pstmt.setString(5, user.getLastName());
			pstmt.setString(6, user.getEmail().toLowerCase());
			pstmt.setDate(7, user.getBirthday());
			pstmt.setTimestamp(8, user.getCreationTime());
			pstmt.setString(9, user.getAvatar());
			pstmt.setInt(10, user.getTypeId());
			pstmt.setBoolean(11, user.getSex());
			pstmt.setBoolean(12, user.isEnabled());
			pstmt.execute();
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			user.setId(rs.getInt(1));
			response.setStatus(200);
			response.setMessage("User registered successfully");
			user.setPassword(null);
			response.setData(user);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(500);
			response.setMessage("DB connection error");
		}
		poolManager.returnConn(con);
		return response;
	}

	public static Response<User> modifyUser(User user) {
		Connection con = poolManager.getConn();
		Response<User> response = new Response<>();
		String query = prop.getValue("updateUser");
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, user.getName());
			ps.setString(2, user.getLastName());
			ps.setString(3, user.getEmail());
			ps.setDate(4, user.getBirthday());
			ps.setBoolean(5, user.getSex());
			ps.setInt(6, user.getId());
			ps.setString(7, user.getPassword());
			int affectedRows = ps.executeUpdate();
			if(affectedRows == 1) {
				response.setStatus(200);
				response.setMessage("User Update Successfully");
				response.setData(user);
			  } else {
			    response.setStatus(401);
			    response.setMessage("Bad credentials");
			    response.setData(null);
      		}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(500);
			response.setMessage("DB connection error");
			response.setData(user);
		}
		poolManager.returnConn(con);
		return response;
	}

	public static Response<ArrayList<User>> searchUsers(String search) {
		Response<ArrayList<User>> response = new Response<>();
		ArrayList<User> users = new ArrayList<>();
		Connection con = poolManager.getConn();
		String query = prop.getValue("searchUsers");
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, "%" + search + "%");
			ps.setString(2, "%" + search + "%");
			ps.setString(3, "%" + search + "%");
			ps.setString(4, "%" + search + "%");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.setId(rs.getInt(1));
				user.setUsername(rs.getString(2));
				user.setName(rs.getString(3));
				user.setLastName(rs.getString(4));
				user.setAvatar(rs.getString(5));
				user.setBirthday(rs.getDate(6));
				users.add(user);
			}

			response.setData(users);
			response.setMessage("List Returned");
			response.setStatus(200);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setMessage("DB Connection Error");
			response.setStatus(500);
		} finally {
			poolManager.returnConn(con);
		}

		return response;
	}

	public static Response<ArrayList<Post>> getUserPosts(int user_id) {
		Response<ArrayList<Post>> response = new Response<>();
		ArrayList<Post> posts = new ArrayList<>();
		Connection con = poolManager.getConn();
		String query = prop.getValue("getUserPosts");
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, user_id);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Post post = new Post();
				post.setIdPost(rs.getInt(1));
				post.setTypePost(rs.getInt(2));
				post.setPostText(rs.getString(3));
				post.setUrl(rs.getString(4));
				post.setCreationTime(rs.getTimestamp(5));
				post.setFileCount(PostsHandler.getFileCount(String.valueOf(user_id), post.getIdPost()));

				posts.add(post);
			}
			response.setData(posts);
			response.setMessage("User Posts Returned");
			response.setStatus(200);

		} catch (SQLException e) {
			e.printStackTrace();
			response.setMessage("DB Connection Error");
			response.setStatus(500);
		} finally {
			poolManager.returnConn(con);
		}

		return response;
	}

	private static void getUserData(ResultSet rs, User user) throws SQLException {
	    user.setId(rs.getInt(1));
		user.setUsername(rs.getString(2));
		user.setLowercaseUsername(rs.getString(3));
		user.setName(rs.getString(5));
		user.setLastName(rs.getString(6));
		user.setEmail(rs.getString(7));
		user.setBirthday(rs.getDate(8));
		user.setCreationTime(rs.getTimestamp(9));
		user.setAvatar(rs.getString(10));
		user.setTypeId(rs.getInt(11));
		user.setSex(rs.getBoolean(12));
		user.setEnabled(rs.getBoolean(13));
		user.setPassword(null);
	}

	public static boolean checkEmail(String email){
		Connection con = poolManager.getConn();
		String query = prop.getValue("checkEmail");
		try {
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, email.toLowerCase());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				poolManager.returnConn(con);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			poolManager.returnConn(con);
			return true;
		}
		poolManager.returnConn(con);
		return false;
	}

	public static boolean checkLowercaseUsername(String username) {
		Connection con = poolManager.getConn();
		String query = prop.getValue("checkLowercaseUsername");
		try {
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				poolManager.returnConn(con);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			poolManager.returnConn(con);
			return true;
		}
		poolManager.returnConn(con);
		return false;
	}

	public static Response<Like> likePost(Like like) {
		Response<Like> response = new Response<>();
		Connection con = poolManager.getConn();
		String query = prop.getValue("insertLike");
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, like.getType_like_id());
			ps.setInt(2, like.getPost_id());
			ps.setInt(3, like.getUser_id());
			ps.execute();

			query = prop.getValue("getMyLike");
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			rs.next();
			like.setLike_id(rs.getInt(1));

			response.setStatus(200);
			response.setMessage("Post Liked");
			response.setData(like);

		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(500);
			response.setMessage("DB Connection Error");
		}

		return response;
	}

	public static Response<?> dislikePost(Like like) {
		Response<?> response = new Response<>();
		Connection con = poolManager.getConn();
		String query = prop.getValue("deleteLike");
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, like.getUser_id());
			ps.setInt(2, like.getLike_id());

			ps.execute();
			response.setStatus(200);
			response.setMessage("Post Disliked");

		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(500);
			response.setMessage("DB Connection Error");
		}

		return response;
	}

	public static Response<?> updateLike(Like like) {
		Response<?> response = new Response<>();
		Connection con = poolManager.getConn();
		String query = prop.getValue("updateLike");

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, like.getType_like_id());
			ps.setInt(2, like.getUser_id());
			ps.setInt(3, like.getLike_id());

			ps.execute();
			response.setStatus(200);
			response.setMessage("Like Updated");

		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(500);
			response.setMessage("DB Connection Error");
		}
		return response;
	}

	public static Response<?> addComment(Comment comment) {
		Response<?> response = new Response<>();
		Connection con = poolManager.getConn();
		String query = prop.getValue("insertComment");

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, comment.getComment_text());
			ps.setString(2, comment.getComment_url());
			ps.setInt(3, comment.getPost_id());
			ps.setInt(4, comment.getUser_id());

			ps.execute();
			response.setStatus(200);
			response.setMessage("Comment Done");

		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(500);
			response.setMessage("DB Connection Error");
		}
		return response;
	}

	public static Response<?> deleteComment(Comment comment) {
		Response<?> response = new Response<>();
		Connection con = poolManager.getConn();
		String query = prop.getValue("deleteComment");

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, comment.getUser_id());
			ps.setInt(2, comment.getComment_id());

			ps.execute();
			response.setStatus(200);
			response.setMessage("Comment Deleted");

		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(500);
			response.setMessage("DB Connection Error");
		}
		return response;
	}
}
