import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Duke {
    public static void main(String[] args) {

        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";

        System.out.println("Hello from\n" + logo);
        separator();
        System.out.println("Hello! I'm Duke"); //introduction
        System.out.println("What can I do for you?");
        separator();

        String myString = inputCommand(); //get raw input from user
        ArrayList<Task> myList = new ArrayList<>(); //Instantiate an array list of a dynamic size and class Task

        // as long as input is not bye, keep running
        while (!myString.equals("bye")) {
            separator();
            if (myString.equals("list")) { //request for list, output the storage
                getList(myList);
            }
            //if not requesting for list, check for done, event, todos, deadline
            else {
                //splits the input according to white spaces, limit 2 means it only splits 1 space
                String[] bufferArray = myString.split(" ", 2);


                //check if first word is "done", second word should be an integer if true
                if (bufferArray[0].equals("done")) {
                    try {
                        int taskNumber = Integer.parseInt(bufferArray[1]); //get the task number as an integer
                        myList.get(taskNumber - 1).markAsDone(); //marks that task number as done
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println((taskNumber) + "." + myList.get(taskNumber - 1).getStatusIcon());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Error! 'Done' must be followed by a number. Please type 'list' to display " +
                                "the list of tasks and their numbers.");
                    } catch (IndexOutOfBoundsException d) {
                        System.out.println("Error! Task list does not contain that task number.");
                    }
                }

                //First word is not 'done', hence the user is adding a task
                //check if its a todos, adds a standard task description with no timing
                //After adding a new task to list, save this data to a save file
                else {
                    if (bufferArray[0].equals("todo")) {

                        try {
                            Task newTask = new Todo(bufferArray[1]); //Create a new todos with description from user input
                            addToList(newTask, myList);
                            saveData(newTask);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Error! Description of a Todo task must not be empty.");
                        }

                    }
                    //check if its a deadline, there will be a "/by "
                    else if (bufferArray[0].equals("deadline")) {

                        try {
                            //Split the input into 2, before and after /by
                            String[] bufferDeadline = myString.split("/by ", 2);
                            //Now, split the first part to exclude the word "deadline"
                            String[] bufferDescription = bufferDeadline[0].split(" ", 2);
                            //Remove the space after task description and /by for easier formatting
                            bufferDescription[1] = bufferDescription[1].substring(0, bufferDescription[1].length() - 1);

                            //Create a new deadline with description from user input
                            Task newTask = new Deadline(bufferDescription[1], bufferDeadline[1]);
                            addToList(newTask, myList);
                            saveData(newTask);

                            //Error to be displayed when /by is not in deadline input
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Error! Deadline tasks must contain '/by' followed by the deadline.");
                        }
                    }
                    //check if its an event, there should be a "/at "
                    else if (bufferArray[0].equals("event")) {

                        try {
                            //Split the input into 2, before and after /at
                            String[] bufferEvent = myString.split("/at ", 2);
                            //Now, split the first part to exclude the word "event"
                            String[] bufferDescription = bufferEvent[0].split(" ", 2);
                            //Remove the space after task description and /by for easier formatting
                            bufferDescription[1] = bufferDescription[1].substring(0, bufferDescription[1].length() - 1);

                            //Create a new deadline with description from user input
                            Task newTask = new Event(bufferDescription[1], bufferEvent[1]);
                            addToList(newTask, myList);
                            saveData(newTask);

                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Error! Event tasks must contain '/at' followed by the time of event.");
                        }
                    }


                    //Error when first word is not any of the above keywords
                    else {
                        System.out.println("Error! I do not understand what that mean.");
                    }
                }
            }
            separator();
            myString = inputCommand(); //after processing command, wait for new input form user
        }

        //indicates end of duke
        separator();
        System.out.println("Bye. Hope to see you again soon!");
        separator();
    }

    private static String inputCommand() { //read input and returns that input to be processed in main
        Scanner input = new Scanner(System.in);

        return input.nextLine();
    }

    private static void separator() {
        System.out.println("____________________________________________________________");
    }

    //Method to add a task to a list and output the size of list
    private static void addToList(Task taskData, ArrayList<Task> myTasks) {
        myTasks.add(taskData);
        System.out.println("Got it. I've added this task:");
        System.out.println(taskData.getStatusIcon());
        System.out.println("Now you have " + myTasks.size() + " task(s) in the list.");
    }

    //Method to get the tasks in a list
    private static void getList(ArrayList<Task> myTasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < myTasks.size(); i++) { //Standard for-each loop: for (String element: myList)
            System.out.println((i + 1) + "." + myTasks.get(i).getStatusIcon());
        }
    }

    //Method to save new tasks to a persistent storage
    private static void saveData(Task newTask) {
        String fileName = "save.txt"; //name to save file

        try {

            File file = new File("save.txt");
            FileWriter fw = new FileWriter(file, true); //appends incoming task to file
            BufferedWriter bw = new BufferedWriter(fw);

            // Note that write() does not automatically
            // append a newline character.
            bw.write(newTask.getType());
            bw.write("|");
            bw.write(newTask.getStatus());
            bw.write("|");
            bw.write(newTask.getDescription());
            bw.write("|");
            //if incoming data is deadline or event, additional segment for deadline
            if (newTask.getType().equals("[D]") || newTask.getType().equals("[E]")) {
                bw.write(newTask.getBy());
            }
            bw.newLine(); //new line for next input

            // Always close files.
            bw.close();
            fw.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '"+ fileName + "'");
        }
    }

    //method to read data from a persistent storage and output them to a list
    private static void readSave(ArrayList<Task> myTasks) {
        FileReader fr = new File("save.txt");
        BufferedWriter bw = new BufferedWriter(fr);
    }

}


