import managers.InMemoryTaskManager;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    private static final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    public static void main(String[] args) {
        addTasks();
        printAllTasks();
    }

    private static void addTasks() {
        Task buyTickets = new Task("Купить билеты", "До 7 августа",
                LocalDateTime.of(2024,8,25,10,0, 0), Duration.ofMinutes(20));
        taskManager.addTask(buyTickets);
        taskManager.getTaskByID(buyTickets.getID());

        Task washCage = new Task("Помыть клетку", "Помыть клетку и насыпать свежий корм для попугая",
                LocalDateTime.of(2024,8,25,10,30, 0), Duration.ofMinutes(20));
        taskManager.addTask(washCage);
        taskManager.getTaskByID(washCage.getID());

        Epic buyFoods = new Epic("Купить продукты", "Купить продукты на неделю");
        taskManager.addEpic(buyFoods);
        taskManager.getEpicByID(buyFoods.getID());

        Subtask buyBread = new Subtask("Купить хлеб", "Белый, чёрный и булочки", buyFoods.getID(),
                LocalDateTime.of(2024,8,25,11,0, 0), Duration.ofMinutes(20));
        taskManager.addSubtask(buyBread);
        taskManager.getSubtaskByID(buyBread.getID());
        Subtask buyMilk = new Subtask("Купить молоко", "Жирность 3,5%", buyFoods.getID(),
                LocalDateTime.of(2024,8,25,11,30, 0), Duration.ofMinutes(20));
        taskManager.addSubtask(buyMilk);
        taskManager.getSubtaskByID(buyMilk.getID());

        Epic doLaundry = new Epic("Постирать бельё", "На выходных");
        taskManager.addEpic(doLaundry);
        taskManager.getEpicByID(doLaundry.getID());

        Subtask washBedding = new Subtask("Постирать постельное бельё", "Постирать с новым порошком",
                doLaundry.getID(), LocalDateTime.of(2024,8,25,12,0, 0),
                Duration.ofMinutes(20));
        taskManager.addSubtask(washBedding);
        taskManager.getSubtaskByID(washBedding.getID());
        taskManager.getSubtaskByID(washBedding.getID());
        taskManager.getSubtaskByID(washBedding.getID());
        taskManager.getSubtaskByID(washBedding.getID());
        taskManager.getSubtaskByID(washBedding.getID());
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);

            for (Task task : taskManager.getEpicSubtasks(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}