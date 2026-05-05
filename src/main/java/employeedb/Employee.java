package employeedb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Employee {

    private final int id;
    private final String name;
    private final String surname;
    private final int birthYear;
    private final Map<Integer, CollabLevel> collaborators;

    public Employee(int id, String name, String surname, int birthYear) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthYear = birthYear;
        this.collaborators = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public Map<Integer, CollabLevel> getCollaborators() {
        return collaborators;
    }

    public void addCollaboration(int colId, CollabLevel level) {
        if (level == null) {
            System.out.println("Neplatna uroven spoluprace.");
            return;
        }
        
        if (colId == id) {
            System.out.println("Nelze pridat sebe sama.");
            return;
        }
        collaborators.put(colId, level);
    }

    public void removeCollaborator(int colId) {
        collaborators.remove(colId);
    }

    public void printInfo(Map<Integer, Employee> all) {
        System.out.println("--- Zamestnanec ---");
        System.out.println("ID: " + id);
        System.out.println("Jmeno: " + name + " " + surname);
        System.out.println("Rok narozeni: " + birthYear);
        System.out.println("Skupina: " + getGroupName());
        System.out.println("Pocet spolupraci: " + collaborators.size());

        if (!collaborators.isEmpty()) {
            System.out.println("Spolupracovnici:");
            int bad = 0;
            int avg = 0;
            int good = 0;
            Set<Integer> klice = collaborators.keySet();
            for (Integer cid : klice) {
                CollabLevel lvl = collaborators.get(cid);
                Employee col = all.get(cid);
                String fullName = "(neznamy)";
                if (col != null) {
                    fullName = col.getName() + " " + col.getSurname();
                }
                System.out.println("  ID " + cid + " - " + fullName + " [" + lvl.getLabel() + "]");
                if (lvl == CollabLevel.BAD) bad++;
                else if (lvl == CollabLevel.AVERAGE) avg++;
                else good++;
            }
            System.out.println("Souhrn: spatna=" + bad + ", prumerna=" + avg + ", dobra=" + good);
        }
    }

    public abstract void runSkill(Map<Integer, Employee> all);

    public abstract String getGroupName();

    public abstract char getGroupCode();

    public String toString() {
        return "ID=" + id + " " + surname + " " + name + " (" + birthYear + ") [" + getGroupName() + "]";
    }
}
