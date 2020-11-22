package com.example.projecti3.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *    inspection class --> get info from inspection list
 *    makes a string of each violation from a single report
 */

public class Inspection {
    private String trackingNum;
    private int date;
    private String type;

    private int critIssues;
    private int noncritIssuses;
    private int numIssues;

    private String hazardLevel;
    private String violations;
    List<String> ViolationString = new ArrayList<>();

    private int inspectionIndex;

    public void setDate(int date) {
        this.date = date;
    }

    public int getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCritIssues(int critIssues) {
        this.critIssues = critIssues;
    }

    public int getCritIssues() {
        return critIssues;
    }

    public void setNoncritIssuses(int noncritIssuses) {
        this.noncritIssuses = noncritIssuses;
    }

    public int getNoncritIssuses() {
        return noncritIssuses;
    }

    public void setNumIssues(){
        this.numIssues = critIssues + noncritIssuses;
    }

    public int getNumIssues(){
        return numIssues;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public void setViolations(String violations) {
        this.violations = violations;
    }

    public String getViolations() {
        return violations;
    }

    public List<String> getViolationString(){
        return ViolationString;
    }

    public void setViolationString(int index){
        String v = SingletonInspectionManager.getInstance().getViolations(index);

        String[] Violations = v.split("\\|");
        for (String violation : Violations) {
            this.ViolationString.add(violation);
        }
    }

    public String getTrackingNum() {
        return trackingNum;
    }

    public void setTrackingNum(String trackingNum) {
        this.trackingNum = trackingNum;
    }

    public void setInspectionIndex(int index){
        this.inspectionIndex=index;
    }

    public int getInspectionIndex(){
        return inspectionIndex;
    }

    @Override
    public String toString() {
        return  trackingNum + ", " +
                date + ", " +
                type +
                ", " + critIssues +
                ", " + noncritIssuses +
                ", " + hazardLevel +
                ", " + violations;
    }
}