package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.LinkedList;
import java.util.List;

public class ParticipateAction<Boolean> extends Action {
    private String student;
    private Integer grade;

    /**
     * Constructor
     * @param student
     * @param grade
     */
    public ParticipateAction(String student, Integer grade) {
        super();
        setActionName("Participate In Course");
        this.student=student;
        this.grade=grade;
    }

    /**
     * runs the action
     * checks if there's room to register,
     * if yes, saves a spot and sends the student an action to try and register
     * if he succeeds fix him in the log, else, release the spot saved
     */
    @Override
    protected void start() {
        if(!((CoursePrivateState)actorState).isThereRoom()){
            complete(false);
            return;
        }
        if((((CoursePrivateState) actorState).getRegStudents().contains(student))){
            sendMessage(this, actorId, actorState);
            return;
        }

        ((CoursePrivateState)actorState).decAvailableNum();
        List<Action> actions = new LinkedList<>();
        Action act = new CheckAndRegister(((CoursePrivateState) actorState).getPrequisites(), actorId, grade);
        actions.add(act);
        then(actions, ()->{
           if(!(boolean)actions.get(0).getResult().get()){
               ((CoursePrivateState)actorState).incAvailableNum();
               complete(false);
               return;
           }
           ((CoursePrivateState)actorState).addStudent(student);
           complete(true);
        });

        sendMessage(act, student, pool.getPrivateState(student));

    }
}
