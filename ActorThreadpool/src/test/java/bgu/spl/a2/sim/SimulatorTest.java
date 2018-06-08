package bgu.spl.a2.sim;

import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class SimulatorTest {
    //@Rule
    public Timeout globalTimeout = Timeout.seconds(3); // 10 seconds max per method tested
    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[5000][0]);
    }

    @Test
    public void main() {
        Simulator.main(new String[]{"input.json"});
        try (InputStream fin = new FileInputStream("result.ser");
             ObjectInputStream ois = new ObjectInputStream(fin)) {
            HashMap<String, PrivateState> data = (HashMap<String, PrivateState>) ois.readObject();
            data.forEach((String actor, PrivateState state) -> {
                System.out.println(actor + ": ");
                System.out.print("History: ");
                state.getLogger().forEach((String s) -> {
                    System.out.print(s + ", ");
                });
                System.out.println("");
                if (state instanceof DepartmentPrivateState) {
                    printDepartment((DepartmentPrivateState) state);
                    switch (actor) {
                        case "CS": {
                            testCS(actor, (DepartmentPrivateState) state);
                            break;
                        }
                        case "Math": {
                            testMath(actor, state);
                            break;
                        }
                    }
                } else if (state instanceof StudentPrivateState) {
                    printStudent((StudentPrivateState) state);
                    testStudent(actor, (StudentPrivateState) state);
                } else {
                    printCourse((CoursePrivateState) state);
                    testCourse(actor, (CoursePrivateState) state);
                }
                System.out.println("----------------");
            });

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();

        }
        System.out.println("-----------------Test End------------------");
    }

    private void testCourse(String actor, CoursePrivateState state) {
        switch (actor) {
            case "Intro To CS": {
                assertTrue("Course " + actor + ": should have 2 Participate In Course action in logger", Collections.frequency(state.getLogger(), "Participate In Course") == 2);
                assertTrue("Course " + actor + ": availableSpots should be 198", state.getAvailableSpots() == 198);
                assertTrue("Course " + actor + ": should have 2 registered students", state.getRegistered() == 2);
                assertTrue("Course " + actor + ": not all students appear in regStudents list " +
                                " should have: 123456789, 5959595959",
                        state.getRegStudents().containsAll(new ArrayList<>(Arrays.asList("123456789", "5959595959"))));
                assertTrue("Course " + actor + ": should have no prerequisites",
                        state.getPrequisites().isEmpty());
                break;
            }case "SPL": {
                assertTrue("Course " + actor + ": should have 2 Participate In Course action in logger", Collections.frequency(state.getLogger(), "Participate In Course") == 2);
                assertTrue("Course " + actor + ": availableSpots should be 0", state.getAvailableSpots() == 0);
                assertTrue("Course " + actor + ": should have 1 registered students", state.getRegistered() == 1);
                assertTrue("Course" + actor + ": not all prerequisites appear, should have Intro To CS",
                        state.getPrequisites().contains("Intro To CS"));
                assertFalse("both students have been registered to SPL- only one of them should",
                        state.getRegStudents().containsAll(new ArrayList<>(Arrays.asList("123456789", "5959595959"))));
                assertFalse("both students have not been registered to SPL one of them should",
                        (!state.getRegStudents().contains("123456789"))&(!state.getRegStudents().contains("5959595959")));
                break;
            }case "Data Structures": {
                assertTrue("Course " + actor + ": should have 1 Participate In Course action and" +
                        " 1 Unregister in logger", state.getLogger().containsAll(new ArrayList<>(Arrays.asList("Participate In Course", "Unregister"))));
                assertTrue("Course " + actor + ": availableSpots should be 100", state.getAvailableSpots() == 100);
                assertTrue("Course " + actor + ": should have 0 registered students", state.getRegistered() == 0);
                assertTrue("Course" + actor + ": should have no regStudents",
                        state.getRegStudents().isEmpty());
                assertTrue("Course" + actor + ": not all prerequisites appear, should have Intro To CS",
                        state.getPrequisites().contains("Intro To CS"));
                break;
            }
        }
    }

    private void printCourse(CoursePrivateState state) {
        System.out.print("prequisites: ");
        state.getPrequisites().forEach((String s) -> {
            System.out.print(s + ", ");
        });
        System.out.print('\n' + "students: ");
        state.getRegStudents().forEach((String s) -> {
            System.out.print(s + ", ");
        });
        System.out.print('\n' + "Registered: ");
        System.out.println(state.getRegistered());
        System.out.print("available spaces: ");
        System.out.println(state.getAvailableSpots());
    }

    private void testStudent(String actor, StudentPrivateState state) {
        boolean registered5959595959 = false;

        switch (actor) {
            case "123456789": {
                assertTrue("student " + actor + ": should be registered to Intro To CS with grade 77",
                        state.getGrades().containsKey("Intro To CS") && state.getGrades().get("Intro To CS") == 77);
                assertTrue("student " + actor + ": signature should be 999283 or 1234666",
                        state.getSignature() == 999283||state.getSignature()==1234666);
                break;
            }
            case "5959595959": {
                assertTrue("student " + actor + ": should be registered to Intro To CS with grade 94",
                        state.getGrades().containsKey("Intro To CS") && state.getGrades().get("Intro To CS") == 94);
                registered5959595959 = state.getGrades().containsKey("SPL");
                if (registered5959595959)
                    assertTrue("student " + actor + ": should be registered to SPL with grade 100",
                            state.getGrades().get("SPL") == 100);
                assertTrue("student " + actor + ": signature should be 999283 or 1234666",
                        state.getSignature() == 999283||state.getSignature()==1234666);
                break;
            }
            case "132424353": {
                assertTrue("student " + actor + ": grades should be empty",
                        state.getGrades().isEmpty());
                assertTrue("student " + actor + ": signature should be 0", state.getSignature() == 0);
                break;
            }
        }
    }

    private void printStudent(StudentPrivateState state) {
        System.out.print("Grades: ");
        state.getGrades().forEach((String s, Integer grade) -> {
            System.out.print(s + ": " + grade + ", ");
        });
        System.out.print('\n' + "Signature: ");
        System.out.println(state.getSignature());
    }

    private void testMath(String actor, PrivateState state) {
        assertTrue("Department" + actor + ": should have 1 Add student action in logger", Collections.frequency(state.getLogger(), "Add Student") == 1);
        assertTrue("Department" + actor + ": courselist should be empty",
                ((DepartmentPrivateState) state).getCourseList().isEmpty());
        assertTrue("Department" + actor + ": not all students appear in studentslist " +
                        " should have: 132424353",
                ((DepartmentPrivateState) state).getStudentList().containsAll(new ArrayList<String>(Arrays.asList("132424353"))));
    }

    private void testCS(Object actor, DepartmentPrivateState state) {
        assertTrue("Department" + actor + ": should have 3 open course action in logger", Collections.frequency(state.getLogger(), "Open Course") == 3);
        assertTrue("Department" + actor + ": should have 2 Add student action in logger", Collections.frequency(state.getLogger(), "Add Student") == 2);
        assertTrue("Department" + actor + ": should have 1 Administrative Check in logger", Collections.frequency(state.getLogger(), "Administrative Check") == 1);
        assertTrue("Department" + actor + ": not all courses appear in courselist " +
                        " should have: Intro To CS, Data Structures, SPL",
                state.getCourseList().containsAll(new ArrayList<>(Arrays.asList("Intro To CS", "Data Structures", "SPL"))));
        assertTrue("Department" + actor + ": not all students appear in studentslist " +
                        " should have: 123456789,5959595959",
                state.getStudentList().containsAll(new ArrayList<>(Arrays.asList("123456789", "5959595959"))));
    }

    private void printDepartment(DepartmentPrivateState state) {
        System.out.print("Courses: ");
        state.getCourseList().forEach((String s) -> {
            System.out.print(s + ", ");
        });
        System.out.print('\n' + "Students: ");
        state.getStudentList().forEach((String s) -> {
            System.out.print(s + ", ");
        });
        System.out.println("");
    }
}