package employeedb;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class DataAnalyst extends Employee {

    public DataAnalyst(int id, String name, String surname, int birthYear) {
        super(id, name, surname, birthYear);
    }

    public void runSkill(Map<Integer, Employee> all) {
        System.out.println("Dovednost: spolupracovnik s nejvice spolecnymi vazbami");

        Map<Integer, CollabLevel> myCol = getCollaborators();
        if (myCol.size() == 0) {
            System.out.println("Tento zamestnanec nema zadne spolupracovniky.");
            return;
        }

        int bestId = -1;
        int bestCount = -1;

        Set<Integer> myKeys = myCol.keySet();
        Iterator<Integer> it = myKeys.iterator();
        while (it.hasNext()) {
            Integer cid = it.next();
            Employee col = all.get(cid);
            if (col == null) continue;

            int common = 0;
            Set<Integer> colKeys = col.getCollaborators().keySet();
            Iterator<Integer> it2 = colKeys.iterator();
            while (it2.hasNext()) {
                Integer otherId = it2.next();
                if (otherId != getId() && myCol.containsKey(otherId)) {
                    common++;
                }
            }

            if (common > bestCount) {
                bestCount = common;
                bestId = cid;
            }
        }

        if (bestId == -1) {
            System.out.println("Nepodarilo se najit vhodneho spolupracovnika.");
            return;
        }

        Employee best = all.get(bestId);
        System.out.println("Spolupracovnik: ID " + bestId + " - " + best.getName() + " " + best.getSurname());
        System.out.println("Pocet spolecnych spolupracovniku: " + bestCount);
    }

    public String getGroupName() {
        return "Datovy analytik";
    }

    public char getGroupCode() {
        return 'D';
    }
}
