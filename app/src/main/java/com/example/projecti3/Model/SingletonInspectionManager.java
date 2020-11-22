package com.example.projecti3.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *    singleton inspection manager --> makes inspection list iterable
 */

public class SingletonInspectionManager implements Iterable<Inspection> {

    private List<Inspection> inspectionManager = new ArrayList<>();

    private static SingletonInspectionManager instance;

    public static SingletonInspectionManager getInstance() {
        if (instance == null) {
            instance = new SingletonInspectionManager();
        }
        return instance;
    }

    private SingletonInspectionManager() {
        // Nothing: ensure this is a singleton.
    }

    public void add(Inspection inspection) {
        this.inspectionManager.add(inspection);
    }

    public void remove(Inspection inspection) {
        this.inspectionManager.remove(inspection);
    }

    public Inspection get(int i) {
        return inspectionManager.get(i);
    }

    public List<Inspection> getAll(){
        return inspectionManager;
    }

    public int getDate(int i) {
        return inspectionManager.get(i).getDate();
    }

    public int getNumIssues(int i) {
        return inspectionManager.get(i).getNumIssues();
    }

    public int getCritIssues(int i) {
        return inspectionManager.get(i).getCritIssues();
    }

    public int getNonCritIssues(int i) {
        return inspectionManager.get(i).getNoncritIssuses();
    }

    public String getHazardLevel(int i) {
        return inspectionManager.get(i).getHazardLevel();
    }

    public String getViolations(int i){
        return inspectionManager.get(i).getViolations();
    }

    @Override
    public Iterator<Inspection> iterator() {
        return inspectionManager.iterator();
    }

    public int getNumInspections() {
        return inspectionManager.size();
    }
    public String getStoreDataKey() {
        //String stuff;

        return "InspectionsDetailsKey";
    }
    public String getDownloadDateKey() {

        return "InspectionsDownloadsDateKey";
    }
    public String getLastModifiedDateKey() {

        return "InspectionLastModified";
    }
    @Override
    public String toString() {
        return inspectionManager +
                "";
    }
}