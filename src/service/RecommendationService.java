package service;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class RecommendationService {
	
	private final BookMetadataService metadata = new BookMetadataService();
	
	public void recommendRandomBook() throws Exception {
		
		JSONObject edition = metadata.fetchRandomEdition();
		if (edition == null) {
			System.out.print("Could not fetch a random book.");
			return;
		}
		
		String title = edition.optString("title", "Uknown Title");
		
		String authorName = "Unknown Author";
		if (edition.has("authors")) {
			JSONArray authors = edition.getJSONArray("authors");
			if(!authors.isEmpty()) {
				String authorKey = authors.getJSONObject(0).getString("key");
				authorName = metadata.fetchAuthorName(authorKey);
			}
		
		}
		
		String workKey = null;
		if (edition.has("works")) {
			workKey = edition.getJSONArray("works").getJSONObject(0).getString("key");
		}
		
		JSONObject workJson = (workKey != null)
                ? metadata.fetchJson("https://openlibrary.org" + workKey + ".json")
                : null;

        // 4. Extract genres + tags
        String genre = "Unknown";
        String tags = "None";

        if (workJson != null && workJson.has("subjects")) {
            JSONArray subjects = workJson.getJSONArray("subjects");

            if (!subjects.isEmpty()) {
                genre = subjects.getString(0);

                // pick up to 3 tags
                int limit = Math.min(3, subjects.length());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < limit; i++) {
                    sb.append(subjects.getString(i));
                    if (i < limit - 1) sb.append(", ");
                }
                tags = sb.toString();
            }
        }

        // 5. Extract description
        String description = "No description available.";
        if (workJson != null) {
            description = metadata.fetchDescription(workJson);
        }
        
        printRecommendation(title, authorName, genre, tags, description);

        
	}
	private void printRecommendation(String title, String author, String genre, String tags, String description) {
        System.out.println("\n========================================");
        System.out.println("📚  Your Random Book Recommendation");
        System.out.println("========================================");
        System.out.println("Title:       " + title);
        System.out.println("Author:      " + author);
        System.out.println("Genre:       " + genre);
        System.out.println("Tags:        " + tags);
        System.out.println("----------------------------------------");
        System.out.println("Summary:");
        System.out.println(description);
        System.out.println("========================================\n");
    }
	
	private boolean isFiction(JSONArray subjects) {
	    if (subjects == null) return false;

	    String[] fictionKeywords = {
	            "fiction",
	            "science fiction",
	            "fantasy",
	            "romance",
	            "mystery",
	            "thriller",
	            "horror",
	            "young adult",
	            "adventure"
	    };

	    for (Object obj : subjects) {
	        String s = obj.toString().toLowerCase();
	        for (String keyword : fictionKeywords) {
	            if (s.contains(keyword)) {
	                return true;
	            }
	        }
	    }
	    return false;
	}

	
	} // end class
	


