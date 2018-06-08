/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	public static Warehouse warehouse;
	public static ActorThreadPool actorThreadPool;
    private static ArrayList<String> p1actors;
    private static ArrayList<String> p2actors;
    private static ArrayList<String> p3actors;
    private static ArrayList<Action> p1actions;
    private static ArrayList<Action> p2actions;
    private static ArrayList<Action> p3actions;
    private static CountDownLatch cd1;
    private static CountDownLatch cd2;
    private static CountDownLatch cd3;
	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start(){
        actorThreadPool.start();
        runPhase(1);
        try {
            cd1.await();
        } catch (InterruptedException ignored) {}
        runPhase(2);
        try {
            cd2.await();
        } catch (InterruptedException ignored) {}
        runPhase(3);
        try {
            cd3.await();
        } catch (InterruptedException ignored) {}
    }

    /**
     * Runs the i-th phase of the simulation
     * @param i
     */
	private static void runPhase(int i){
        ArrayList<String> actorList = null;
        ArrayList<Action> actionsList = null;
        switch(i){
            case 1: {
                actorList = p1actors;
                actionsList = p1actions;
                break;
            }
            case 2: {
                actorList = p2actors;
                actionsList = p2actions;
                break;
            }
            case 3: {
                actorList = p3actors;
                actionsList = p3actions;
                break;
            }
        }
        for(int j=0; j<actorList.size(); j++){
            if(!actorThreadPool.getActors().containsKey(actorList.get(j))) { //if pool doesn't have given actor
                DepartmentPrivateState dps = new DepartmentPrivateState();
                actorThreadPool.submit(new VoidAction(), actorList.get(j), dps);
            }
            actorThreadPool.submit(actionsList.get(j),
                    actorList.get(j), actorThreadPool.getPrivateState(actorList.get(j)));

        }
    }
	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorThreadPool=myActorThreadPool;
	}
	
	/**
	* shut down the simulation
	* returns list of private states
	*/
	public static HashMap<String,PrivateState> end(){
        try {
            actorThreadPool.shutdown();
        } catch (InterruptedException e){}
        return (HashMap<String,PrivateState>)actorThreadPool.getActors();
	}

    /**
     * Parses JSON input, initializes thread pool, warehouse and action lists
     * once finished, running the simulation
     * when finished, exporting result to "result.ser"
     * @param args
     */
	public static void main(String [] args){
	    try(JsonReader reader = new JsonReader(new FileReader(args[0]))){
            Decoder decoder = (new Gson()).fromJson(reader, Decoder.class);

            //Initialize pool and warehouse
            warehouse=new Warehouse();
            ActorThreadPool pool = new ActorThreadPool(decoder.threads);
            attachActorThreadPool(pool);
            for(Computer comp: decoder.computers)
                warehouse.addComputerByName(comp);


            //Initialize Phase 1
            p1actors = decoder.getActorsList(1);
            p1actions = decoder.getPhaseList(1);
            cd1 = new CountDownLatch(p1actions.size());
            for(Action act: p1actions)
                act.getResult().subscribe(()->cd1.countDown());
            //Initialize Phase 2
            p2actors = decoder.getActorsList(2);
            p2actions = decoder.getPhaseList(2);
            cd2 = new CountDownLatch(p2actions.size());
            for(Action act: p2actions)
                act.getResult().subscribe(()->cd2.countDown());
            //Initialize Phase 3
            p3actors = decoder.getActorsList(3);
            p3actions = decoder.getPhaseList(3);
            cd3 = new CountDownLatch(p3actions.size());
            for(Action act: p3actions)
                act.getResult().subscribe(()->cd3.countDown());

            //Simulate
            start();

            //End simulation export log to result.ser
            output(end());

        } catch (IOException e1) {
            e1.printStackTrace();
        }
	}

    /**
     * Exports simulation result into "result.ser"
     * @param simResult
     */
    private static void output(HashMap<String, PrivateState> simResult){
        try (FileOutputStream fout = new FileOutputStream("result.ser")) {
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(simResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * JSON parser classes
     * main class: Decoder
     * secondary class: PhaseClass
     */
    private static class Decoder {
        protected Integer threads;
        @SerializedName("Computers")
        protected ArrayList<Computer> computers;
        @SerializedName("Phase 1")
        protected ArrayList<PhaseClass> phase1;
        @SerializedName("Phase 2")
        protected ArrayList<PhaseClass> phase2;
        @SerializedName("Phase 3")
        protected ArrayList<PhaseClass> phase3;

        protected ArrayList<Action> getPhaseList(int i){
            ArrayList<PhaseClass> p;
            switch(i){
                case 1: {p=phase1;
                    break;}
                case 2: {p=phase2;
                    break;}
                default: {p=phase3;
                    break;}
            }

            ArrayList<Action> actions = new ArrayList<>();
            for(PhaseClass pC: p){
                actions.add(pC.generateAction());
            }
            return actions;
        }
        protected ArrayList<String> getActorsList(int i){
            ArrayList<PhaseClass> p;
            switch(i){
                case 1: {p=phase1;
                    break;}
                case 2: {p=phase2;
                    break;}
                default: {p=phase3;
                    break;}
            }
            ArrayList<String> actorsList = new ArrayList<>();
            for(PhaseClass pC: p){
                actorsList.add(pC.getActorId());
            }
            return actorsList;
        }

    }
    private static class PhaseClass {
        @SerializedName("Action")
        private String action;
        @SerializedName("Department")
        private String department;
        @SerializedName("Course")
        private String course;
        @SerializedName("Space")
        private String space;
        @SerializedName("Student")
        private String student;
        @SerializedName("Computer")
        private String computer;
        @SerializedName("Grade")
        private ArrayList<String> grade;
        @SerializedName("Prerequisites")
        private ArrayList<String> prerequisites;
        @SerializedName("Students")
        private ArrayList<String> students;
        @SerializedName("Conditions")
        private ArrayList<String> conditions;
        @SerializedName("Preferences")
        private ArrayList<String> preferences;
        @SerializedName("Number")
        private String number;

        /**
         * generates action according to JSON input
         * @return Action
         */
        protected Action<?> generateAction(){
            switch(action){
                case "Open Course": return new OpenCourseAction
                        (Integer.parseInt(space), prerequisites, course);
                case "Add Student": return new AddStudentAction
                        (student);
                case "Participate In Course": {
                    Integer value;
                    if(grade.get(0).equals("-"))
                        value = -1;
                    else value= Integer.parseInt(grade.get(0));
                    return new ParticipateAction(student, value);
                }
                case "Add Spaces": return new AddSpacesAction
                        (Integer.parseInt(number), false);
                case "Register With Preferences": {
                    ArrayList<Integer> gradesList = new ArrayList<>();
                    for(String currGrade:grade){
                        if(currGrade.equals("-"))
                            gradesList.add(-1);
                        else
                            gradesList.add(Integer.parseInt(currGrade));
                    }
                    return new RegWithPrefAction(gradesList, preferences);
                }
                case "Unregister": return new UnregisterAction
                        (student);
                case "Close Course": return new CloseCourseAction
                        (course);
                case "Administrative Check": return new AdministrativeCheckAction
                        (students, computer, conditions);
                default: return null;
            }
        }

        /**
         *
         * @return String actorId to given action
         */
        protected String getActorId(){
            switch(action){
                case "Open Course": return department;
                case "Add Student": return department;
                case "Participate In Course": return course;
                case "Add Spaces": return course;
                case "Register With Preferences": return student;
                case "Unregister": return course;
                case "Close Course": return department;
                case "Administrative Check": return department;
                default: return null;
            }
        }

    }
}
