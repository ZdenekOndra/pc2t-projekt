package employeedb;

import java.util.Map;
import java.util.Collection;

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
        for (CollabLevel lvl : hodnoty) {
            sum = sum + lvl.getValue();
        }
        double avgQuality = sum / n;
        double score = n * (4.0 - avgQuality);

        System.out.println("Pocet spolupracovniku: " + n);
        System.out.println("Prumerna kvalita spoluprace: " + avgQuality);
        System.out.println("Rizikove skore: " + score);

      
