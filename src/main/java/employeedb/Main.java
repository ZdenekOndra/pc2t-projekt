package employeedb;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        EmployeeDatabase db = new EmployeeDatabase();
        Scanner sc = new Scanner(System.in);

        db.loadFromSQLite();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    addEmployee(db, sc);
                    break;
                case "2":
                    addCollaboration(db, sc);
                    break;
                case "3":
                    removeEmployee(db, sc);
                    break;
                case "4":
                    findEmployee(db, sc);
                    break;
                case "5":
                    runSkill(db, sc);
                    break;
                case "6":
                    db.listByGroup();
                    break;
                case "7":
                    db.showStatistics();
                    break;
                case "8":
                    db.showGroupCounts();
                    break;
                case "9":
                    saveEmployeeToFile(db, sc);
                    break;
                case "10":
                    loadEmployeeFromFile(db, sc);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Neplatna volba.");
                    break;
            }
            if (running) {
                pauseForUser(sc);
            }
        }

        db.saveToSQLite();
        System.out.println("Konec programu.");
        sc.close();
    }

    public static void printMenu() {
        System.out.println("===== Databazovy system zamestnancu =====");
        System.out.println("1  Pridat zamestnance");
        System.out.println("2  Pridat spolupraci");
        System.out.println("3  Odebrat zamestnance");
        System.out.println("4  Vyhledat zamestnance dle ID");
        System.out.println("5  Spustit dovednost zamestnance");
        System.out.println("6  Abecedni vypis dle skupin");
        System.out.println("7  Statistiky");
        System.out.println("8  Pocty zamestnancu ve skupinach");
        System.out.println("9  Ulozit zamestnance do souboru");
        System.out.println("10 Nacist zamestnance ze souboru");
        System.out.println("0  Konec");
        System.out.println("Volba:");
    }

    public static void pauseForUser(Scanner sc) {
        System.out.println();
        System.out.println("[Stiskni Enter pro pokracovani]");
        sc.nextLine();
        System.out.println();
    }

    public static int readInt(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("Neplatne cislo.");
            return -1;
        }
    }

    public static void addEmployee(EmployeeDatabase db, Scanner sc) {
        System.out.println("Skupina: D = datovy analytik, S = bezpecnostni specialista");
        System.out.println("Kod skupiny:");
        String input = sc.nextLine().trim().toUpperCase();
        if (input.isEmpty() || (input.charAt(0) != 'D' && input.charAt(0) != 'S')) {
            System.out.println("Neznamy kod skupiny (povoleno D nebo S).");
            return;
        }
        char code = input.charAt(0);

        System.out.println("Jmeno:");
        String name = sc.nextLine().trim();
        System.out.println("Prijmeni:");
        String surname = sc.nextLine().trim();
        if (name.isEmpty() || surname.isEmpty()) {
            System.out.println("Jmeno a prijmeni nesmi byt prazdne.");
            return;
        }

        System.out.println("Rok narozeni:");
        int year = readInt(sc);
        if (!EmployeeDatabase.isYearValid(year)) {
            System.out.println("Neplatny rok narozeni.");
            return;
        }

        db.createEmployee(code, name, surname, year);
    }

    public static void addCollaboration(EmployeeDatabase db, Scanner sc) {
        System.out.println("ID zamestnance:");
        int empId = readInt(sc);
        if (empId < 0) return;
        System.out.println("ID kolegy:");
        int colId = readInt(sc);
        if (colId < 0) return;

        System.out.println("Uroven spoluprace: 1 = spatna, 2 = prumerna, 3 = dobra");
        System.out.println("Volba:");
        int v = readInt(sc);
        if (v < 1 || v > 3) {
            System.out.println("Neplatna uroven.");
            return;
        }

        db.addCollaboration(empId, colId, CollabLevel.fromValue(v));
    }

    public static void removeEmployee(EmployeeDatabase db, Scanner sc) {
        System.out.println("ID zamestnance k odebrani:");
        int id = readInt(sc);
        if (id < 0) return;
        if (db.removeEmployee(id)) {
            System.out.println("Zamestnanec ID " + id + " odebran.");
        } else {
            System.out.println("Zamestnanec ID " + id + " nenalezen.");
        }
    }

    public static void findEmployee(EmployeeDatabase db, Scanner sc) {
        System.out.println("ID zamestnance:");
        int id = readInt(sc);
        if (id < 0) return;
        Employee e = db.findById(id);
        if (e == null) {
            System.out.println("Zamestnanec nenalezen.");
            return;
        }
        e.printInfo(db.getAll());
    }

    public static void runSkill(EmployeeDatabase db, Scanner sc) {
        System.out.println("ID zamestnance:");
        int id = readInt(sc);
        if (id < 0) return;
        Employee e = db.findById(id);
        if (e == null) {
            System.out.println("Zamestnanec nenalezen.");
            return;
        }
        e.runSkill(db.getAll());
    }

    public static void saveEmployeeToFile(EmployeeDatabase db, Scanner sc) {
        System.out.println("ID zamestnance:");
        int id = readInt(sc);
        if (id < 0) return;
        System.out.println("Nazev souboru:");
        String filename = sc.nextLine().trim();
        if (filename.isEmpty()) {
            System.out.println("Nazev souboru nesmi byt prazdny.");
            return;
        }
        db.saveEmployeeToFile(id, filename);
    }

    public static void loadEmployeeFromFile(EmployeeDatabase db, Scanner sc) {
        System.out.println("Nazev souboru:");
        String filename = sc.nextLine().trim();
        db.loadEmployeeFromFile(filename);
    }
}
