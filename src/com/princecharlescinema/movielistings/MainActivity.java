package com.princecharlescinema.movielistings;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.princecharlescinema.movielistings.adapter.TabsPagerAdapter;

public class MainActivity extends FragmentActivity implements TabListener {

// URL Address
    static String url = "http://m.princecharlescinema.com";
    
    ProgressDialog mProgressDialog;
    static Table t;
	private DatePicker date_picker;
	private Button datebutton;

	private int year;
	private int month;
	private int day;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;	
    // Tab titles
    private String[] tabs = { "Yesterday", "Today", "Tomorrow" };
 
	

	static final int DATE_DIALOG_ID = 100;    
    
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    
     // Initilization
//        viewPager = (ViewPager) findViewById(R.id.pager);
//        actionBar = getActionBar();
//        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
//        
//        viewPager.setAdapter(mAdapter);
//        actionBar.setHomeButtonEnabled(false);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
//        // Adding Tabs
//        for (String tab_name : tabs) {
//            actionBar.addTab(actionBar.newTab().setText(tab_name)
//                    .setTabListener(this));
//        }       
//        actionBar.setSelectedNavigationItem(1);
//        /**
//         * on swiping the viewpager make respective tab selected
//         * */
//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//         
//            @Override
//            public void onPageSelected(int position) {
//                // on changing the page
//                // make respected tab selected
//                actionBar.setSelectedNavigationItem(position);
//            }
//         
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//            }
//         
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//            }
//        });        
        
        // Locate the Buttons in activity_main.xml
        //Button datebutton = (Button) findViewById(R.id.datebutton);
//        Button descbutton = (Button) findViewById(R.id.descbutton);
        setCurrentDate();
        setDate(day, month, year);
        
        t=new Table();
        t.execute();
 
        // Capture button click
        addButtonListener();
 
    }
    
    public void setDate(int day, int month, int year){
//    	SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
//    	Date d = new Date(year, month, day);	
//    	String dayOfTheWeek = sdf.format(d);
    	SimpleDateFormat sdf  = new SimpleDateFormat("EEEEE dd/MMM/yyyy");
    	Calendar c = Calendar.getInstance();
    	c.set(year, month, day);
    	//c.add(Calendar.DAY_OF_MONTH, -1);
    	Date date = c.getTime();    	
        TextView txtdate = (TextView) findViewById(R.id.datetxt);
        txtdate.setText(sdf.format(date));      	
    }
	public void addButtonListener() {

		datebutton = (Button) findViewById(R.id.datebutton);

		datebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID);

			}

		});

	}
	public void setCurrentDate() {

		final Calendar calendar = Calendar.getInstance();

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

	}	
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DATE_DIALOG_ID:
		   // set date picker as current date
		   return new DatePickerDialog(this, datePickerListener, year, month,day);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay) {
			if (day!=selectedDay||month!=selectedMonth||year!=selectedYear){
				year = selectedYear;
				month = selectedMonth;
				day = selectedDay;
				url = "http://m.princecharlescinema.com/index.php?date="+year+":"+month+":"+day+"&year="+year+"&month="+month+"&day="+day;
				new Table().execute();
			}
			setDate(day, month, year);
		}
	};

    // Table AsyncTask
    private class Table extends AsyncTask<Void, Void, Void> {
        String movie;
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Prince Charles Cinema");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(url).get();
                // Using Elements to get the Meta data
                //Get times
//                Elements description = document
//                        .select("tr[class=odd] td[width=40]");
                //Get titles
                Elements titles = document
                        .select("a[target=_top]");
              //Get prices  
              Elements description = document
              .select("span[class=pinkParagraphSmall]");       
              //Get extra description if any
//              Elements description = document
//              .select("a[class=buylink]");                 
                // Locate the content attribute
                movie = titles.first().text();
                for (Element e:titles){
                	movie=movie.concat("\n");
                	movie=movie.concat(e.text());
                	movie = movie.concat(e.text());
                	movie=movie.concat("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            TextView txttitle = (TextView) findViewById(R.id.desctxt);
            txttitle.setText(movie);
            mProgressDialog.dismiss();
        }        
    }
    

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());
		
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	

}