package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RegWithPrefAction<Boolean> extends Action {

    private ArrayList<Integer> gradesList;
    private ArrayList<String> prefCourses;

    /**
     * Constructor
     * @param gradesList
     * @param prefCourses
     */
    public RegWithPrefAction(List<Integer> gradesList, List<String> prefCourses) {
        super();
        setActionName("Register with Preferences");
        this.gradesList = (ArrayList<Integer>)gradesList;
        this.prefCourses = (ArrayList<String>)prefCourses;
    }

    /**
     * runs the action
     * tries to sign to first course,
     * if fails, removes it from the list and retries the 2nd
     *
     * will sign for 1 course at most!
     */
    @Override
    protected void start() {
        if(prefCourses.size()==0){
            complete(false);
            return;
        }
        Action act = new ParticipateAction(actorId, gradesList.get(0));
        List<Action> actions = new LinkedList<>();
        actions.add(act);
        sendMessage(act, prefCourses.get(0), pool.getPrivateState(prefCourses.get(0)));
        then(actions, ()->{
           if((boolean)actions.get(0).getResult().get()) {
               System.out.println(((StudentPrivateState)actorState).getGrades());
               complete(true);
               return;
           }else{
               if(prefCourses.size()==0){
                   complete(false);
                   return;
               }
               gradesList.remove(0);
               prefCourses.remove(0);
               Action toDo = new RegWithPrefAction(gradesList, prefCourses);
               toDo.getResult().subscribe( ()-> this.complete(true));
               sendMessage(toDo, actorId, actorState);
           }
        });

    }
}
