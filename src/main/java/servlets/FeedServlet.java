package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import handlers.SessionHandler;
import models.Post;
import models.Response;
import models.User;
import utilities.Encryptor;

@WebServlet(urlPatterns = "feed", name = "Feed Servlet")
public class FeedServlet extends HttpServlet {

	//Gettea los posts del feed
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String username = req.getParameter("user").toLowerCase();
		Response<ArrayList<Post>> response = SessionHandler.getPosts(username);
		resp.getWriter().print(mapper.writeValueAsString(response));
	}
}