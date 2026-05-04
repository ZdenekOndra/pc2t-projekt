package employeedb;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

public class SecuritySpecialist extends Employee {

    public SecuritySpecialist(int id, String name, String surname, int birthYear) {
        super(id, name, surname, birthYear);
    }

    public void runSkill(Map<Integer, Employee> all) {
        System.out.println("Dovednost: vyhodnoceni rizikoveho skore spoluprace");

        int n = getCollaborators().size();
        if (n == 0) {
            System.out.println("Zadni spolupracovnici, skore nelze spocitat.");
            return;
        }

        double sum = 0;
        Collection<CollabLevel> hodnoty = getCollaborators().values();
        Iterator<CollabLevel> it = hodnoty.iterator();
        while (it.hasNext()) {
            CollabLevel lvl = it.next();
            sum = sum + lvl.getValue();
        }
        double avgQuality = sum / n;
        double score = n * (4.0 - avgQuality);

        System.out.println("Pocet spolupracovniku: " + n);
        System.out.println("Prumerna kvalita spoluprace: " + avgQuality);
        System.out.println("Rizikove skore: " + score);

        String level;
        if (score < 5) {
            level = "NIZKE";
        } else if (score < 15) {
            level = "STREDNI";
        } else {
            level = "VYSOKE";
        }
        System.out.println("Uroven rizika: " + level);
    }

    public String getGroupName() {
        return "Bezpecnostni specialista";
    }

    public char getGroupCode() {
        return 'S';
    }
}
