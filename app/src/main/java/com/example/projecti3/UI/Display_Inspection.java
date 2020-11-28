package com.example.projecti3.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

import com.example.projecti3.Model.Inspection;
import com.example.projecti3.Model.SingletonInspectionManager;
import com.example.projecti3.R;

/**
 * This class will display the inspection report. The first if and else statements will set the hazard text colour and icon based on the levelof the report.
 * The second if else statements will assign pictures based on the type of violation
 *
 *
 */
public class Display_Inspection extends AppCompatActivity {

    //private  inspectionREPORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__inspection);
        getIntent();

        String name;
        int date;
        String inspectionType;
        int critIssues;
        int nonCritIssues;
        String hazardLevel;
        //List<String> violations;
        Intent intent = getIntent();
        int index = intent.getIntExtra("Index:", -1);
        //Toast.makeText(getApplicationContext(),"" + index,Toast.LENGTH_LONG).show();
        Inspection inspectionREPORT = SingletonInspectionManager.getInstance().get(index);
        inspectionREPORT.setViolationString(index);
        //extractExtras(intent);
        //MRI will copy the singleton object
        //SingletonInspection MRI = new MockResterauntInspection(MockResterauntInspection.getInstance());

        String v = SingletonInspectionManager.getInstance().get(index).getViolations();

        String[] violations = v.split("\\|");


        //name = MRI.getName();
        date = inspectionREPORT.getDate();
        inspectionType = inspectionREPORT.getType();
        critIssues= inspectionREPORT.getCritIssues();
        nonCritIssues = inspectionREPORT.getNoncritIssuses();
        hazardLevel = inspectionREPORT.getHazardLevel();
        //violations = inspectionREPORT.getViolationString();
        //SingletonInspectionManager.getInstance().get(index).getViolations();
        new ArrayList<>(Arrays.asList(violations));
        int year = date / 10000;
        String month = getMonth((date % 10000) / 100);
        int day = date % 100;
        String dateInspec = "" + month + " "  + day + ", " + year;

        ImageView hazardImage = (ImageView) findViewById(R.id.hazardSymbol);
        Drawable hazard_image;
        TextView inspectionDate = (TextView) findViewById(R.id.dateInspection);
        String dateMessage = getString(R.string.inspection_date);
        inspectionDate.setText(dateMessage + " " + dateInspec);

        TextView reportType = (TextView) findViewById(R.id.typeInspection);
        String typeMessage = getString(R.string.inspection_type);
        reportType.setText(typeMessage + " " + inspectionType);

        TextView critCount = (TextView) findViewById(R.id.critIssuesInspection);
        String critMessage = getString(R.string.critical_issues);
        critCount.setText(critMessage + " " + critIssues );

        TextView nonCritCount = (TextView) findViewById(R.id.nonCritIssuesInspection);
        String nonCritMessage = getString(R.string.non_critical_issues);
        nonCritCount.setText(nonCritMessage + " " + nonCritIssues );

        TextView inspectionHazard = (TextView) findViewById(R.id.hazardLevelTrue);
        //inspectionHazard.setText(hazardLevel);
        //View inspecView = findViewById(R.id.)
        //View view = this.getWindow().getDecorView();
        //Toast.makeText(getApplicationContext(),hazardLevel,Toast.LENGTH_LONG).show();
        if(hazardLevel.equals("Low")) {
            inspectionHazard.setText(R.string.lowHazard);
            inspectionHazard.setTextColor(getResources().getColor(R.color.lowLime));
            hazard_image = getResources().getDrawable(R.drawable.greencircle);
            hazardImage.setBackground(hazard_image);
            //setActivityBackgroundColour(getResources().getColor(R.color.colorWhitishGreen));
        } else if (hazardLevel.equals("Moderate")) {
            inspectionHazard.setText(R.string.moderateHazard);
            inspectionHazard.setTextColor(getResources().getColor(R.color.colorModerateOrange));
            hazard_image = getResources().getDrawable(R.drawable.orangecircle);
            hazardImage.setBackground(hazard_image);
            //setActivityBackgroundColour(getResources().getColor(R.color.colorWhitishOrange));
        } else if(hazardLevel.equals("High")) {
            inspectionHazard.setText(R.string.highHazard);
            inspectionHazard.setTextColor(getResources().getColor(R.color.colorDangerRed));
            hazard_image = getResources().getDrawable(R.drawable.redcircle);
            hazardImage.setBackground(hazard_image);
            //setActivityBackgroundColour(getResources().getColor(R.color.colorPink));
        } else {
            inspectionHazard.setText(R.string.noHazard);
            inspectionHazard.setTextColor(getResources().getColor(R.color.colorHazardless));
            //hazardImage.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        }




        ListView violList = (ListView) findViewById(R.id.violationsListView);

        ArrayList<ViolationPerson> arrayList = new ArrayList<>();
        int critLevel;
        int nature;
        for (String S: violations) {
            if(S.contains("Not Critical")) {
                critLevel= R.drawable.greencircle;
            } else {
                critLevel = R.drawable.redcircle;
            }
            if(S.contains("sanit")) {
                nature = R.drawable.germ;
            } else if (S.contains("contaminat") || S.contains("safe to eat") || S.contains("hazardous food") || S.contains("not cooled")) {
                nature = R.drawable.dirtyburger;
            } else if (S.contains("pest")) {
                nature = R.drawable.cockroach;
            } else if (S.contains("wash") && S.contains("hand")) {
                nature = R.drawable.handwash;
            }else if (S.contains("404")) {
                nature = R.drawable.cigarette;
            }else if (S.contains("FOODSAFE")) {
                nature = R.drawable.foodsafelvlone;
            }else if (S.contains("307") || S.contains("308")) {
                nature = R.drawable.wrench;
            } else {
                nature = R.drawable.warning_symbol_in_orange;
            }
            arrayList.add(new ViolationPerson(critLevel,nature,S));
        }

        if(!v.isEmpty()) {
            ViolationsAdapter violationsAdapter = new ViolationsAdapter(this, R.layout.violations_view, arrayList);
            violList.setAdapter(violationsAdapter);
            violList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String violationMain = violations[position];
                    Toast.makeText(getApplicationContext(),violationMain, Toast.LENGTH_LONG).show();
                }
            });
        }



        /**
         Toast.makeText(getApplicationContext(),
         "Name: " + name +
         "\nDate: " + dateInspec ,Toast.LENGTH_LONG).show();*/ /**
         "\nType of Inspection: " + inspectionType +
         "\n# of Critical Issues: " + critIssues +
         "\n# of Non Critical Issues: " + nonCritIssues +
         "\nHazard Level: " + hazardLevel,
         */
    }
    public static Intent makeIntent(Context context) {
        return new Intent(context, Display_Inspection.class);
    }
    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
}
