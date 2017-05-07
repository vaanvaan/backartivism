/**
 * 
 */
package es.ucm.artivism.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import es.ucm.artivism.data.PostVO;

/**
 * @author Ivan
 *
 */
@WebServlet("/GetPostsServlet")
public class GatherPostsServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<PostVO> postsInMemory;
	private static final Integer MAX_POSTS = 1000;
	private Long lastUpdate;
	private PostGetter getter;
	
	public GatherPostsServlet() {
		super();
		postsInMemory = new ArrayList<PostVO>();
		getter = new PostGetter();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 *  TODO: validate the origin of the request, if it is a valid one
		 */
//		String tQuery = request.getParameter("country").trim();
//		if(tQuery == null || "".equals(tQuery)){
//			tQuery = "No DATA";
//		}
		String baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
		List<PostVO> posts = obtainPosts(baseUrl);
		Gson gson = new Gson();
		String responseData = gson.toJson(posts);
		response.setContentType("application/json");
		response.getWriter().write(responseData);
	}

	private List<PostVO> obtainPosts(final String baseUrl) {
		if(postsInMemory.isEmpty() || lastUpdate == null){
			postsInMemory = getter.getPostsFromSources(MAX_POSTS, baseUrl);
			lastUpdate = Calendar.getInstance().getTimeInMillis();
		}else{
			Long diff = Calendar.getInstance().getTimeInMillis() - lastUpdate; 
			long diffMinutes = diff / (60 * 1000) % 60; 
			if (diffMinutes > 360*4/480){ //24h -> 3 min
				postsInMemory = getter.getPostsFromSources(MAX_POSTS, baseUrl);
				lastUpdate = Calendar.getInstance().getTimeInMillis();
			}
		}
		return postsInMemory;
	}

}
