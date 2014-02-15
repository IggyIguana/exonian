package sparkjolt.exonian;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Menu;
import android.widget.TextView;

public class Article extends FragmentActivity {
	
	TextView title;
	TextView author;
	TextView date;
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article);
	    
		// Allow network access in the main thread
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        // Access the TextViews
        content = (TextView) findViewById(R.id.article_content);
        title = (TextView) findViewById(R.id.article_title);
        author = (TextView) findViewById(R.id.article_author);
        
        // Read the JSON into a string
        String feed = readExonianFeed();
        
        try {
            // Construct a JSON object and get the content of the post
            JSONObject jsonObject = new JSONObject(feed);
            String contentText = (String) ((JSONObject) jsonObject.get("post")).get("content");
            String titleText = (String) ((JSONObject) jsonObject.get("post")).get("title");
            String authorText = (String) ((JSONObject) ((JSONObject) jsonObject.get("post")).get("author")).get("name");
            
            // Do not get the CSS after the </p> tag for the content text
            contentText = contentText.substring(0, contentText.indexOf("</p>"));
            content.setText(Html.fromHtml(contentText));
            title.setText(titleText);
            author.setText(authorText);
            
            // Set the font to Ebrima
            Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/ebrima.ttf");
            content.setTypeface(tf);
            title.setTypeface(tf, Typeface.BOLD);
            author.setTypeface(tf, Typeface.ITALIC);
        } catch (Exception e) {
            e.printStackTrace();
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	// Using the StringBuilder class, returns the JSON as a string
	public String readExonianFeed() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://theexonian.com/new/2014/02/13/boys-track-wins-ea-girls-fall-close-behind/?json=1");
		
		// Uses a BufferedReader to read all of the content from the URL
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
}
