/**
 * 
 */
package es.ucm.artivism.service;

/**
 * @author Ivan
 *
 */
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.imgscalr.Scalr;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import es.ucm.artivism.data.PostVO;


@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
        
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String filepath = Paths.get(System.getProperty("user.home"), "uploads").toString(); 

	@Override
	public void init() throws ServletException {
		super.init();
		Path path = Paths.get(System.getProperty("user.home"), "uploads");
		
    	try {
    		if(!Files.exists(path)){
    			Files.createDirectory(path);
    		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
        * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
        * 
        */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        
        if (request.getParameter("getfile") != null && !request.getParameter("getfile").isEmpty()) {
            File file = new File(filepath + File.separator +request.getParameter("getfile"));
            if (file.exists()) {
                int bytes = 0;
                ServletOutputStream op = response.getOutputStream();

                response.setContentType(getMimeType(file));
                response.setContentLength((int) file.length());
                response.setHeader( "Content-Disposition", "inline; filename=\""+ File.separator + "img"+ File.separator + file.getName() + "\"" );

                byte[] bbuf = new byte[1024];
                DataInputStream in = new DataInputStream(new FileInputStream(file));

                while ((in != null) && ((bytes = in.read(bbuf)) != -1)) {
                    op.write(bbuf, 0, bytes);
                }

                in.close();
                op.flush();
                op.close();
            }
        } else if (request.getParameter("delfile") != null && !request.getParameter("delfile").isEmpty()) {
            File file = new File(filepath + File.separator + request.getParameter("delfile"));
            if (file.exists()) {
                file.delete(); // TODO:check and report success
            } 
        } else if (request.getParameter("getthumb") != null && !request.getParameter("getthumb").isEmpty()) {
            File file = new File(filepath + File.separator + request.getParameter("getthumb"));
                if (file.exists()) {
                    System.out.println(file.getAbsolutePath());
                    String mimetype = getMimeType(file);
                    if (mimetype.endsWith("png") || mimetype.endsWith("jpeg")|| mimetype.endsWith("jpg") || mimetype.endsWith("gif")) {
                        BufferedImage im = ImageIO.read(file);
                        if (im != null) {
                            BufferedImage thumb = Scalr.resize(im, 75); 
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            if (mimetype.endsWith("png")) {
                                ImageIO.write(thumb, "PNG" , os);
                                response.setContentType("image/png");
                            } else if (mimetype.endsWith("jpeg")) {
                                ImageIO.write(thumb, "jpg" , os);
                                response.setContentType("image/jpeg");
                            } else if (mimetype.endsWith("jpg")) {
                                ImageIO.write(thumb, "jpg" , os);
                                response.setContentType("image/jpeg");
                            } else {
                                ImageIO.write(thumb, "GIF" , os);
                                response.setContentType("image/gif");
                            }
                            ServletOutputStream srvos = response.getOutputStream();
                            response.setContentLength(os.size());
                            response.setHeader( "Content-Disposition", "inline; filename=\"" + file.getName() + "\"" );
                            os.writeTo(srvos);
                            srvos.flush();
                            srvos.close();
                        }
                    }
            } // TODO: check and report success
        } else {
//          Path path = Paths.get(System.getProperty("user.home"), "uploads");
//  		File folder = new File(path.toString());
//  		File[] listOfFiles = folder.listFiles();
//  		List<File> infoFiles = new ArrayList<>();
//  		for (int i = 0; i < listOfFiles.length; i++) {
//  			if (listOfFiles[i].isFile()) {
//  				File file = listOfFiles[i];
//  				String fileName = file.getName();
//  				if (fileName.endsWith("_info.txt")){
//  					infoFiles.add(file);
//  				}
//  				System.out.println("File " + fileName);
//  			} else if (listOfFiles[i].isDirectory()) {
//  				System.out.println("Directory " + listOfFiles[i].getName());
//  			}
//  	    }
//  		Gson gson = new Gson();
//  		List<PostVO> posts = new ArrayList<>();
//		for(File f : infoFiles){
//			try {
//				FileReader reader = new FileReader(f);
//				List<String> lines = Files.readAllLines(Paths.get(f.getAbsolutePath()));
//				for(String line : lines){
//					if(line != null && !line.isEmpty()){
//						System.out.println(line);
//						PostVO post = gson.fromJson(line, PostVO.class);
//						posts.add(post);
//					}
//				}
//			} catch (FileNotFoundException e) {
//				System.out.println(e);
//				e.printStackTrace();
//			} catch (IOException e) {
//				System.out.println(e);
//				e.printStackTrace();
//			}
//		}
//		String aux = "{\"files\": %s }";
//        	{"files": [
//        	           {
//        	             "name": "picture1.jpg",
//        	             "size": 902604,
//        	             "url": "http:\/\/example.org\/files\/picture1.jpg",
//        	             "thumbnailUrl": "http:\/\/example.org\/files\/thumbnail\/picture1.jpg",
//        	             "deleteUrl": "http:\/\/example.org\/files\/picture1.jpg",
//        	             "deleteType": "DELETE"
//        	           },
//        	           {
//        	             "name": "picture2.jpg",
//        	             "size": 841946,
//        	             "url": "http:\/\/example.org\/files\/picture2.jpg",
//        	             "thumbnailUrl": "http:\/\/example.org\/files\/thumbnail\/picture2.jpg",
//        	             "deleteUrl": "http:\/\/example.org\/files\/picture2.jpg",
//        	             "deleteType": "DELETE"
//        	           }
//        	         ]}
            PrintWriter writer = response.getWriter();
            writer.write("call POST with multipart form data");
        }
    }
    
    /**
        * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
        * 
        */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }
        String contextPath = request.getContextPath().toString();
        System.out.println(contextPath);
        ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");
        JSONArray json = new JSONArray();
        Map<String, Map<String,String>> postData = Collections.synchronizedMap(new HashMap<String, Map<String, String>>()); // image name as index
        try {
        	
            List<FileItem> items = uploadHandler.parseRequest(request);
            for (FileItem item : items) {
            	if (item.isFormField()) {
            		String name = item.getFieldName();
            		String value = item.getString();
            		System.out.println(String.format("%s : %s ", name, value));
            		/* in order to have the correct assignment between images and values, I am using a special
            		 * character to add the name of the picture to the data: <imgae name>#<field name>
            		 * In order to extract the data, the use of the tokenizer and a map
            		*/
            		StringTokenizer tk = new StringTokenizer(name, "#");
            		String imageName = tk.nextToken();
            		String field = tk.nextToken();
            		Map<String, String> fieldData = postData.get(imageName);
            		if(fieldData == null){
            			fieldData = Collections.synchronizedMap(new HashMap<String, String>());
            		}
            		fieldData.put(field, value);
            		fieldData.put("URL", "UploadServlet?getfile=" + imageName);
            		fieldData.put("filePath", filepath + File.separator + imageName);
            		fieldData.put("id", Calendar.getInstance().getTimeInMillis() + "_" + imageName);
            		postData.put(imageName, fieldData);
            	}
                if (!item.isFormField()) {
                		String itemName = item.getName();
                        File file = new File(filepath, itemName);
//                        File fileInfo = new File(filepath, item.getName()+"_info.txt");
                        item.write(file);
                        JSONObject jsono = new JSONObject();
                        jsono.put("name", item.getName());
                        jsono.put("size", item.getSize());
                        jsono.put("url", baseUrl + File.separator + itemName);
                        jsono.put("thumbnail_url", "UploadServlet?getthumb=" + itemName);
                        jsono.put("delete_url", "UploadServlet?delfile=" + itemName);
                        jsono.put("delete_type", "GET");
                        jsono.put("author", "Ivan");
                        json.put(jsono);
                        System.out.println(json.toString());
                        System.out.println(itemName);
                        
                        Map<String, String> fieldData = postData.get(itemName);
                		if(fieldData == null){
                			fieldData = Collections.synchronizedMap(new HashMap<String, String>());
                		}
                		fieldData.put("URL", "UploadServlet?getfile=" + itemName);
                		fieldData.put("filePath", filepath + File.separator + itemName);
                		fieldData.put("id", Calendar.getInstance().getTimeInMillis() + "_" + itemName);
                		postData.put(itemName, fieldData);
//                        try (FileWriter fileInfo = new FileWriter(filepath + File.separator + item.getName() +  "_info.txt")) {
//                        	fileInfo.write(jsono.toString());
//                			System.out.println("Successfully Copied JSON Object to File...");
//                			System.out.println("\nJSON Object: " + jsono);
//                		}
                }
            }
        } catch (FileUploadException e) {
                throw new RuntimeException(e);
        } catch (Exception e) {
                throw new RuntimeException(e);
        } finally {
            writer.write(json.toString());
            writer.close();
        }
        serializeData(postData);
    }

    private void serializeData(final Map<String, Map<String, String>> postData) {
    	
    	for(String image: postData.keySet()){
    		Map<String, String> fieldData = postData.get(image);
    		System.out.println(fieldData);
    		String id = fieldData.get("id").toString();
    		String title = fieldData.get("title").toString();
    		String imgUrl = fieldData.get("URL").toString();
    		String description = fieldData.get("description").toString();
    		String location = fieldData.get("location").toString();
    		String author = fieldData.get("author").toString();
    		Long uploadedTime = Calendar.getInstance().getTimeInMillis();
    		PostVO post = new PostVO(id, title, imgUrl, description, location, author, uploadedTime, null, null);
    		try (FileWriter fileInfo = new FileWriter(filepath + File.separator + image +  "_info.txt")) {
        		fileInfo.write(post.toString());
        		System.out.println("Successfully Copied JSON Object to File...");
        		System.out.println("\nJSON Object: " + post);
        		fileInfo.close();
        	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
    
    
    
	private String getMimeType(File file) {
        String mimetype = "";
        if (file.exists()) {
            if (getSuffix(file.getName()).equalsIgnoreCase("png")) {
                mimetype = "image/png";
            }else if(getSuffix(file.getName()).equalsIgnoreCase("jpg")){
                mimetype = "image/jpg";
            }else if(getSuffix(file.getName()).equalsIgnoreCase("jpeg")){
                mimetype = "image/jpeg";
            }else if(getSuffix(file.getName()).equalsIgnoreCase("gif")){
                mimetype = "image/gif";
            }else {
                javax.activation.MimetypesFileTypeMap mtMap = new javax.activation.MimetypesFileTypeMap();
                mimetype  = mtMap.getContentType(file);
            }
        }
        return mimetype;
    }



    private String getSuffix(String filename) {
        String suffix = "";
        int pos = filename.lastIndexOf('.');
        if (pos > 0 && pos < filename.length() - 1) {
            suffix = filename.substring(pos + 1);
        }
        return suffix;
    }
}
