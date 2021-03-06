package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;

import handlers.UserHandler;

import models.Like;
import models.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.stream.Collectors;

/**
 * @author Ptthappy
 */

@WebServlet(urlPatterns = "/likes", name = "Likes Management Servlet")
public class LikesServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = req.getReader().lines().collect(Collectors.joining());
		Like like = mapper.readValue(json, Like.class);
		like.setUserId(Integer.parseInt(req.getSession(false).getAttribute("user_id").toString()));

		Response<Like> response = UserHandler.likePost(like);

		resp.getWriter().print(mapper.writeValueAsString(response));
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Integer likeId = Integer.parseInt(req.getParameter("id"));
		Integer typeLikeId = Integer.parseInt(req.getParameter("type"));
		Integer userId = Integer.parseInt(req.getSession(false).getAttribute("user_id").toString());
		Response<?> response = UserHandler.updateLike(userId, likeId, typeLikeId);

		resp.getWriter().print(mapper.writeValueAsString(response));
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Integer likeId = Integer.parseInt(req.getParameter("likeId"));
		Integer userId = Integer.parseInt(req.getSession(false).getAttribute("user_id").toString());
		Response<?> response = UserHandler.dislikePost(userId, likeId);

		resp.getWriter().print(mapper.writeValueAsString(response));
	}
}
