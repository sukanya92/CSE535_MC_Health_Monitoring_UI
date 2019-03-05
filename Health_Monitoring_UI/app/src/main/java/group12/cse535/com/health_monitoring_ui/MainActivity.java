package group12.cse535.com.health_monitoring_ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main Activity to handle the overall functionality and events
 */
public class MainActivity extends AppCompatActivity {

    //The variable declaration
    LineGraphSeries<DataPoint> series;
    double x = 0;
    int j = 0;
    GraphView graph;
    EditText editPatientId, editAge, editName;
    RadioGroup radioSex;
    Button btnRun, btnStop;
    String mostRecentValid = "";
    String getMostRecentValid2 = "";
    boolean isPatientChanged = false;
    boolean isStarted = false;
    int interval = 20;
    DataPoint[] d;

    /**
     * The first overriden function in the activity life cycle which contains all the variable
     * declaration and event handling
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Data point is initialized to (0,0) in the graph.
        d = new DataPoint[]{new DataPoint(0d, 0d)};

        //All UI elements are defined
        graph = (GraphView)findViewById(R.id.graphView);
        editPatientId = (EditText)findViewById(R.id.editPatientId);
        editAge = (EditText)findViewById(R.id.editAge);
        editName = (EditText)findViewById(R.id.editName);
        radioSex = (RadioGroup)findViewById(R.id.radioSex);
        btnRun = (Button)findViewById(R.id.btnRun);
        btnStop = (Button)findViewById(R.id.btnStop);

        //Series and View port are defined
        series = new LineGraphSeries<DataPoint>();
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setBackgroundColor(Color.WHITE);
        viewport.setScalable(true);
        viewport.setScrollable(true);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Voltage");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");

        /**
         * Event listener for Run button
         */
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((!isStarted) && isRecordValid()) {
                    if (isPatientChanged) {
                        series = new LineGraphSeries<DataPoint>();
                        series.resetData(d);
                        graph.removeCallbacks(action);
                        graph.removeAllSeries();
                        x = 0;
                        j = 0;
                    }
                    graph.addSeries(series);
                    graph.post(action);
                    isStarted = true;
                }
            }
        });

        /**
         * Event listener for Start button
         */
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStarted){
                    graph.removeCallbacks(action);
                    graph.removeAllSeries();
                    isStarted= false;
                }
            }
        });


    }

    /**
     * Runnable/ thread responsible for plotting the graph every 200 milli second
     */
    Runnable action = new Runnable() {
        @Override
        public void run() {
            coreRun(interval);
            graph.postDelayed(this, 200);
        }
    };

    /**
     * The function decides how the graph will look like; such as when it will be flat, when it will be spiked
     * @param n - the interval ; how wide/ flat the graph will be
     */
    private void coreRun(int n){
        x+=0.01;
        double y=0.0;
        if(j%n<=n-6){
            y = 7d+new Random().nextDouble()*(10d-7d);
            addEntry(x,y );j++;
        }else if((j%n>=n-5) && (j%n<=n-2)){
            y = 10d+new Random().nextDouble()*(13d-10d);
            addEntry(x,y );j++;
        }
        else{
            y = 80d+new Random().nextDouble()*(10d);
            addEntry(x, y);j++;
        }
        Log.d("MA", "run: " + x + y);
    }

    /**
     * This function makes sure that new graph data/ point is appended with the continuing graph
     * @param b - X coordinate of the new point to be appended
     * @param d - Y coordinate of the new point to be appended
     */
    private void addEntry(double b,double d) {
        series.appendData(new DataPoint(b, d), true, 100);

    }

    /**
     * Gets the ID value entered by the user
     * @return - ID
     */
    private int getID(){
        int id1 = -1;
        String id2 = editPatientId.getText().toString();
        id1 = (id2.trim().equals(""))?-1: Integer.parseInt(editPatientId.getText().toString());
        Log.d("ID", "run: " +id1);
        return id1;
    }
    /**
     * Gets the NAme value entered by the user
     * @return - Name
     */
    private String getName(){
        String name = "";
        name = editName.getText().toString();
        Log.d("NAME", "run: " +name);
        return name.trim();
    }

    /**
     * Gets the Age value entered by the user
     * @return - Age
     */
    private int getAge(){
        int age1 = 0;
        String age2 = editAge.getText().toString();
        age1 = (age2.trim().equals(""))?0: Integer.parseInt(editAge.getText().toString());
        Log.d("AGE", "run: " +age1);
        return age1;
    }

    /**
     * Gets the Sex value selected by the user
     * @return - Sex
     */
    private String getSex(){
        String sex = "";
        sex = ((RadioButton)findViewById(radioSex.getCheckedRadioButtonId())).getText().toString();
        Log.d("SEX", "run: " +sex);
        return sex;
    }

    /**
     * Checks if the set of input record is valid and if new user entry has happened
     * @return - True/ false
     */
    private boolean isRecordValid(){
        int id = getID();
        String name = getName();
        int age = getAge();
        String sex = getSex();
        boolean b =false;
        boolean isIDValid = (getID()!=-1);
        boolean isNameValid = (!getName().equals(""));
        boolean isAgeValid = (getAge()!=0);

        b = (isIDValid && isNameValid && isAgeValid);// && isSexValid && isMatching);
        if(b){
            interval = 20;
            mostRecentValid = id+"_"+name+"_"+age+"_"+sex;
            isPatientChanged = !mostRecentValid.equals(getMostRecentValid2);
            getMostRecentValid2 = mostRecentValid;
        }
        return b;
    }
}
