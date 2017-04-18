/**
 * 
 */
package es.ucm.artivism.service;

import java.util.List;

import javax.servlet.ServletContext;

import es.ucm.artivism.data.PostVO;

/**
 * @author Ivan
 *
 */
public class PostGetter {
	
	private DirectoryGetter localDirectory;
	private TwitterGetter twitter;
	
	public PostGetter() {
		localDirectory = new DirectoryGetter();
		twitter = new TwitterGetter();
	}

	public List<PostVO> getPostsFromSources(final Integer maxPosts, final String baseUrl) {
		System.out.println("Looking for the path: "+ baseUrl + "img/");
		List<PostVO> uploadedPosts = localDirectory.getPosts(maxPosts, baseUrl);
		List<PostVO> twitterPosts = twitter.getPosts(maxPosts);
//		File file = new File(servletContext.getRealPath("/")+"imgs/");
		return uploadedPosts;
	}

}
