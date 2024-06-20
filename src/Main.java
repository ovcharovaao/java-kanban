import managers.InMemoryTaskManager;
import tasks.*;

public class Main {

    private static final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    public static void main(String[] args) {
        addTasks();
        printAllTasks();
    }

    private static void addTasks() {
        Task buyTickets = new Task("Купить билеты", "До 7 июля");
        taskManager.addTask(buyTickets);
        taskManager.getTaskByID(buyTickets.getID());

        Task washCage = new Task("Помыть клетку", "Помыть клетку и насыпать свежий корм для попугая");
        taskManager.addTask(washCage);
        taskManager.getTaskByID(washCage.getID());

        Epic buyFoods = new Epic("Купить продукты", "Купить продукты на неделю");
        taskManager.addEpic(buyFoods);
        taskManager.getEpicByID(buyFoods.getID());

        Subtask buyBread = new Subtask("Купить хлеб", "Белый, чёрный и булочки",
                buyFoods.getID());
        taskManager.addSubtask(buyBread);
        taskManager.getSubtaskByID(buyBread.getID());
        Subtask buyMilk = new Subtask("Купить молоко", "Жирность 3,5%",
                buyFoods.getID());
        taskManager.addSubtask(buyMilk);
        taskManager.getSubtaskByID(buyMilk.getID());

        Epic doLaundry = new Epic("Постирать бельё", "На выходных");
        taskManager.addEpic(doLaundry);
        taskManager.getEpicByID(doLaundry.getID());

        Subtask washBedding = new Subtask("Постирать постельное бельё", "Постирать с новым порошком",
                doLaundry.getID());
        taskManager.addSubtask(washBedding);
        taskManager.getSubtaskByID(washBedding.getID());
        taskManager.getSubtaskByID(washBedding.getID());
        taskManager.getSubtaskByID(washBedding.getID());
        taskManager.getSubtaskByID(washBedding.getID());
        taskManager.getSubtaskByID(washBedding.getID());
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : taskManager.printTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : taskManager.printEpics()) {
            System.out.println(epic);

            for (Task task : taskManager.printEpicSubtasks(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.printSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}