/**
 * 
 */
package es.ucm.artivism.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import es.ucm.artivism.data.PostVO;

/**
 * @author Ivan
 *
 */
public class DirectoryGetter {

	public List<PostVO> getPosts(final Integer maxPosts, final String baseUrl) {
		ArrayList<PostVO> result = new ArrayList<PostVO>();
		Path path = Paths.get(System.getProperty("user.home"), "uploads");
		File folder = new File(path.toString());
		File[] listOfFiles = folder.listFiles();
		List<File> infoFiles = new ArrayList<>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				File file = listOfFiles[i];
				String fileName = file.getName();
				if (fileName.endsWith("_info.txt")){
					infoFiles.add(file);
				}
				System.out.println("File " + fileName);
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
	    }
		Gson gson = new Gson();
		for(File f : infoFiles){
			try {
				FileReader reader = new FileReader(f);
				List<String> lines = Files.readAllLines(Paths.get(f.getAbsolutePath()));
				for(String line : lines){
					if(line != null && !line.isEmpty()){
						System.out.println(line);
						PostVO post = gson.fromJson(line, PostVO.class);
						Float longitude = post.getLongitude();
						Float latitude = post.getLatitude();
						String location = post.getLocation();
						if((longitude == null || latitude == null) && location != null && !location.isEmpty()){
							try {
								Map<String, Float> geoData = invokeGeoService(location);
								longitude = geoData.get("longitude");
								latitude = geoData.get("latitude");
							} catch (IOException e) {
								System.out.println(e);
								e.printStackTrace();
							}
						}
						
						result.add(post);
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println(e);
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
//		String id = "GRUMPY";
//		String title = "Grumpy cat";
//		String imgUrl = baseUrl+"/img/"+ "grumpyCatIco.jpg";
//		String description = "This is an example, programmed post, for demo and debugging purposes.";
//		String location = "Pastoor Peterstraat 127, Eindhoven";
//		String author = "Ivan Mikovski";
//		Long uploadedTime = Calendar.getInstance().getTimeInMillis();
//		Float longitude = null;
//		Float latitude = null;
//		if((longitude == null || latitude == null) && location != null && !location.isEmpty()){
//			try {
//				Map<String, Float> geoData = invokeGeoService(location);
//				longitude = geoData.get("longitude");
//				latitude = geoData.get("latitude");
//			} catch (IOException e) {
//				System.out.println(e);
//				e.printStackTrace();
//			}
//		}
//		PostVO example = new PostVO(id, title, imgUrl, description, location, author, uploadedTime, longitude, latitude);
			
		
//		result.add(example);
		return result;
	}
	
	
	private String urlEncode(final String value){
        try{
            return URLEncoder.encode(value, "UTF-8");
        }catch (UnsupportedEncodingException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
	
	protected Map<String, Float> invokeGeoService(final String address) throws IOException{
		String apikey = "AIzaSyA6XC8KV0_zapNHH6KEsII4fs7YbwsMHUc";
		String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + urlEncode(address) + "&key=" + apikey;
		
		String url2 = " http://nominatim.openstreetmap.org/search/" + urlEncode(address) + "?format=json&addressdetails=1&limit=1";
		
		URL website = new URL(url2);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);
        
        in.close();
        
        System.out.println(response.toString());
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(response.toString()).getAsJsonArray();
        System.out.println("geo data response "+ array);
        JsonObject obj = null;
        if(!array.isJsonNull()){
        	obj = array.get(0).getAsJsonObject();
        }
        Map<String, Float> result = new HashMap<String, Float>();
        if(obj != null){
	        String longitude = obj.get("lon").getAsString(); // works for url2
	        String latitude = obj.get("lat").getAsString();
	        result.put("longitude", Float.parseFloat(longitude));
	        result.put("latitude", Float.parseFloat(latitude));
        }else{//40.447694, -3.726656
        	result.put("longitude", 40.447694);
	        result.put("latitude", -3.726656);
        }
        

        
        
        
        return result;
    }
}
