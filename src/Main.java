import tasks.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task buyTickets = new Task("Купить билеты", "До 7 июля");
        taskManager.addTask(buyTickets);
        System.out.println(buyTickets);
        System.out.println();

        Task washCage = new Task("Помыть клетку", "Помыть клетку и насыпать свежий корм для попугая");
        taskManager.addTask(washCage);
        System.out.println(washCage);
        System.out.println();

        Epic buyFoods = new Epic("Купить продукты", "Купить продукты на неделю");
        taskManager.addEpic(buyFoods);

        Subtask buyBread = new Subtask("Купить хлеб", "Белый, чёрный и булочки",
                buyFoods.getID());
        taskManager.addSubtask(buyBread);
        Subtask buyMilk = new Subtask("Купить молоко", "Жирность 3,5%",
                buyFoods.getID());
        taskManager.addSubtask(buyMilk);

        System.out.println(buyFoods);
        System.out.println(buyBread);
        System.out.println(buyMilk);
        System.out.println();

        Epic doLaundry = new Epic("Постирать бельё", "На выходных");
        taskManager.addEpic(doLaundry);

        Subtask washBedding = new Subtask("Постирать постельное бельё", "Постирать с новым порошком",
                buyFoods.getID());
        taskManager.addSubtask(washBedding);

        System.out.println(doLaundry);
        System.out.println(washBedding);
        System.out.println();

        washBedding.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(washBedding);
        System.out.println(washBedding);

        taskManager.deleteTaskByID(1);
        taskManager.deleteEpicByID(3);
    }
}