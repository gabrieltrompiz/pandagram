package handlers;

import models.*;
import utilities.PoolManager;
import utilities.PropertiesReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

public class AdminHandler {
    private static PoolManager poolManager = PoolManager.getPoolManager();
    private static PropertiesReader prop = PropertiesReader.getInstance();


    public static Response<HashMap<String, ArrayList<Post>>> postsByType() {
        Response<HashMap<String, ArrayList<Post>>> response = new Response<>();
        ArrayList<Post> audioPosts = new ArrayList<>();
        ArrayList<Post> textPosts = new ArrayList<>();
        ArrayList<Post> videoPosts = new ArrayList<>();
        ArrayList<Post> imagePosts = new ArrayList<>();
        HashMap<String, ArrayList<Post>> tp = new HashMap<>();
        Connection con = poolManager.getConn();
        String query = prop.getValue("getAllPosts");
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Post post = new Post();
                User user = new User();
                post.setIdPost(rs.getInt(1));
                post.setTypePost(rs.getInt(2));
                post.setPostText(rs.getString(3));
                post.setCreationTime(rs.getTimestamp(4));
                user.setUsername(rs.getString(5));
                user.setName(rs.getString(6));
                user.setLastName(rs.getString(7));
                user.setId(rs.getInt(8));
                post.setLikes(null);
                post.setComments(null);
                post.setUser(user);
                if(post.getTypePost() == 1) { textPosts.add(post); }
                if(post.getTypePost() == 2) { imagePosts.add(post); }
                if(post.getTypePost() == 3) { videoPosts.add(post); }
                if(post.getTypePost() == 4) { audioPosts.add(post); }
            }
            response.setStatus(200);
            tp.put("text", textPosts);
            tp.put("audio", audioPosts);
            tp.put("video", videoPosts);
            tp.put("image", imagePosts);
            response.setMessage("Stats returned.");
            response.setData(tp);

        } catch(SQLException e) {
            response.setStatus(500);
            response.setMessage("DB Connection Error");
        }
        finally {
            poolManager.returnConn(con);
        }
        return response;
    }

    public static Response<HashMap<String, ArrayList<User>>> usersByGenre() {
        Response<HashMap<String, ArrayList<User>>> response = new Response<>();
        ArrayList<User> male = new ArrayList<>();
        ArrayList<User> female = new ArrayList<>();
        HashMap<String, ArrayList<User>> ug = new HashMap<>();
        Connection con = poolManager.getConn();
        String query = prop.getValue("getAllUsers");
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                User user = new User();
                user.setId(rs.getInt(1));
                user.setUsername(rs.getString(2));
                user.setName(rs.getString(3));
                user.setLastName(rs.getString(4));
                user.setBirthday(rs.getDate(5));
                user.setCreationTime(rs.getTimestamp(6));
                user.setSex(rs.getBoolean(7));
                user.setEnabled(rs.getBoolean(8));
                if(user.getSex()) { male.add(user); }
                else { female.add(user); }
            }
            response.setStatus(200);
            response.setMessage("Stats returned.");
            ug.put("male", male);
            ug.put("female", female);
            response.setData(ug);
        } catch(SQLException e) {
            e.printStackTrace();
            response.setStatus(500);
            response.setMessage("DB Connection Error");
        }
        finally {
            poolManager.returnConn(con);
        }
        return response;
    }

    public static Response<LinkedHashMap<String, ArrayList<Post>>> usersByPosts() {
        Response<LinkedHashMap<String, ArrayList<Post>>> response = new Response<>();
        HashMap<String, ArrayList<Post>> map = new HashMap<>();
        HashMap<String, Integer> quantity = new HashMap<>();
        LinkedHashMap<String, ArrayList<Post>> data = new LinkedHashMap<>();
        Connection con = poolManager.getConn();
        String query = prop.getValue("getAllPosts");
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Post post = new Post();
                User user = new User();
                post.setIdPost(rs.getInt(1));
                post.setTypePost(rs.getInt(2));
                post.setPostText(rs.getString(3));
                post.setCreationTime(rs.getTimestamp(4));
                user.setUsername(rs.getString(5));
                user.setName(rs.getString(6));
                user.setLastName(rs.getString(7));
                user.setId(rs.getInt(8));
                post.setLikes(null);
                post.setComments(null);
                post.setUser(user);
                if(map.containsKey(user.getUsername())) {
                    map.get(user.getUsername()).add(post);
                    quantity.put(user.getUsername(), quantity.get(user.getUsername()) + 1);
                }
                else {
                    ArrayList<Post> posts = new ArrayList<>();
                    posts.add(post);
                    map.put(user.getUsername(), posts);
                    quantity.put(user.getUsername(), 1);
                }
            }
            HashMap<String, Integer> sorted = quantity
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(comparingByValue()))
                    .limit(10)
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
            for(Map.Entry me : sorted.entrySet()) {
                String key = me.getKey().toString();
                data.put(key, map.get(key));
            }
            response.setStatus(200);
            response.setMessage("Stats Returned");
            response.setData(data);

        } catch(SQLException e) {
            response.setStatus(500);
            response.setMessage("DB Connection Error");
        }
        return response;
    }

}