package employeedb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmployeeDatabase {

    private final Map<Integer, Employee> employees;
    private int nextId;
    private final String dbFile;

    public EmployeeDatabase() {
        employees = new HashMap<>();
        nextId = 1;
        dbFile = "employees.db";
    }

    public Map<Integer, Employee> getAll() {
        return employees;
    }

    public Employee findById(int id) {
        return employees.get(id);
    }

    public void addEmployee(Employee e) {
        employees.put(e.getId(), e);
        if (e.getId() >= nextId) {
            nextId = e.getId() + 1;
        }
    }

    public void createEmployee(char groupCode, String name, String surname, int birthYear) {
        Employee e;
        if (groupCode == 'D') {
            e = new DataAnalyst(nextId, name, surname, birthYear);
        } else if (groupCode == 'S') {
            e = new SecuritySpecialist(nextId, name, surname, birthYear);
        } else {
            System.out.println("Neznamy kod skupiny.");
            return;
        }
        employees.put(nextId, e);
        System.out.println("Pridan zamestnanec: " + e);
        nextId++;
    }

    public boolean removeEmployee(int id) {
        if (!employees.containsKey(id)) {
            return false;
        }
        employees.remove(id);
        Set<Integer> klice = employees.keySet();
        for (Integer key : klice) {
            employees.get(key).removeCollaborator(id);
        }
        return true;
    }

    public void addCollaboration(int empId, int colId, CollabLevel level) {
        if (empId == colId) {
            System.out.println("Nelze pridat spolupraci sam se sebou.");
            return;
        }
        Employee e = employees.get(empId);
        Employee c = employees.get(colId);
        if (e == null || c == null) {
            System.out.println("Zamestnanec nebo kolega neexistuje.");
            return;
        }
        e.addCollaboration(colId, level);
        c.addCollaboration(empId, level);
        System.out.println("Spoluprace pridana.");
    }

    public void listByGroup() {
        List<Employee> analytici = new ArrayList<>();
        List<Employee> specialiste = new ArrayList<>();

        Set<Integer> klice = employees.keySet();
        for (Integer key : klice) {
            Employee e = employees.get(key);
            if (e.getGroupCode() == 'D') {
                analytici.add(e);
            } else {
                specialiste.add(e);
            }
        }

        Comparator<Employee> dlePrijmeni = (a, b) -> {
            int c = a.getSurname().compareTo(b.getSurname());
            if (c == 0) {
                return a.getName().compareTo(b.getName());
            }
            return c;
        };

        analytici.sort(dlePrijmeni);
        specialiste.sort(dlePrijmeni);

        System.out.println("--- Datovi analytici ---");
        for (Employee employee : analytici) {
            System.out.println(employee);
        }
        System.out.println("--- Bezpecnostni specialiste ---");
        for (Employee employee : specialiste) {
            System.out.println(employee);
        }
    }

    public void showGroupCounts() {
        int analytici = 0;
        int specialiste = 0;

        Set<Integer> klice = employees.keySet();
        for (Integer key : klice) {
            Employee e = employees.get(key);
            if (e.getGroupCode() == 'D') {
                analytici++;
            } else {
                specialiste++;
            }
        }

        System.out.println("Datovi analytici: " + analytici);
        System.out.println("Bezpecnostni specialiste: " + specialiste);
        System.out.println("Celkem: " + employees.size());
    }

    public void showStatistics() {
        if (employees.isEmpty()) {
            System.out.println("Databaze je prazdna.");
            return;
        }

        int bad = 0;
        int avg = 0;
        int good = 0;
        Employee mostConnected = null;
        int maxConn = -1;

        for (Employee e : employees.values()) {
            for (CollabLevel lvl : e.getCollaborators().values()) {
                if (lvl == CollabLevel.BAD) bad++;
                else if (lvl == CollabLevel.AVERAGE) avg++;
                else good++;
            }
            int n = e.getCollaborators().size();
            if (n > maxConn) {
                maxConn = n;
                mostConnected = e;
            }
        }

        bad /= 2;
        avg /= 2;
        good /= 2;

        System.out.println("--- Statistiky ---");
        System.out.println("Spatna spoluprace: " + bad);
        System.out.println("Prumerna spoluprace: " + avg);
        System.out.println("Dobra spoluprace: " + good);

        if (bad + avg + good == 0) {
            System.out.println("Prevazujici kvalita: zadna spoluprace");
        } else if (good >= bad && good >= avg) {
            System.out.println("Prevazujici kvalita: Dobra");
        } else if (avg >= bad) {
            System.out.println("Prevazujici kvalita: Prumerna");
        } else {
            System.out.println("Prevazujici kvalita: Spatna");
        }

        System.out.println("Zamestnanec s nejvice vazbami: " + mostConnected + " (vazeb: " + maxConn + ")");
    }

    public void saveEmployeeToFile(int id, String filename) {
        Employee e = employees.get(id);
        if (e == null) {
            System.out.println("Zamestnanec neexistuje.");
            return;
        }
        try {
            FileWriter fw = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("GROUP=" + e.getGroupCode());
            bw.newLine();
            bw.write("ID=" + e.getId());
            bw.newLine();
            bw.write("NAME=" + e.getName());
            bw.newLine();
            bw.write("SURNAME=" + e.getSurname());
            bw.newLine();
            bw.write("BIRTHYEAR=" + e.getBirthYear());
            bw.newLine();

            Set<Integer> klice = e.getCollaborators().keySet();
            for (Integer cid : klice) {
                CollabLevel lvl = e.getCollaborators().get(cid);
                bw.write("COLLAB=" + cid + ";" + lvl.getLabel());
                bw.newLine();
            }
            bw.close();
            fw.close();
            System.out.println("Zamestnanec ulozen do souboru: " + filename);
        } catch (IOException ex) {
            System.out.println("Chyba pri ukladani: " + ex.getMessage());
        }
    }

    public void loadEmployeeFromFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            char groupCode = ' ';
            int id = -1;
            String name = "";
            String surname = "";
            int birthYear = -1;
            List<Integer> rawCollabs = new ArrayList<>();
            List<CollabLevel> rawLevels = new ArrayList<>();

            String line = br.readLine();
            while (line != null) {
                if (line.startsWith("GROUP=")) {
                    groupCode = line.substring(6).charAt(0);
                } else if (line.startsWith("ID=")) {
                    id = Integer.parseInt(line.substring(3));
                } else if (line.startsWith("NAME=")) {
                    name = line.substring(5);
                } else if (line.startsWith("SURNAME=")) {
                    surname = line.substring(8);
                } else if (line.startsWith("BIRTHYEAR=")) {
                    birthYear = Integer.parseInt(line.substring(10));
                } else if (line.startsWith("COLLAB=")) {
                    String[] parts = line.substring(7).split(";");
                    rawCollabs.add(Integer.parseInt(parts[0]));
                    rawLevels.add(CollabLevel.fromLabel(parts[1]));
                }
                line = br.readLine();
            }
            br.close();

            if ((groupCode != 'D' && groupCode != 'S') || !isYearValid(birthYear)
                    || name.isEmpty() || surname.isEmpty() || id < 1) {
                System.out.println("Soubor obsahuje neplatna data.");
                return;
            }

            Employee e = (groupCode == 'D')
                    ? new DataAnalyst(id, name, surname, birthYear)
                    : new SecuritySpecialist(id, name, surname, birthYear);

            for (int i = 0; i < rawCollabs.size(); i++) {
                int cid = rawCollabs.get(i);
                CollabLevel lvl = rawLevels.get(i);
                if (cid == id || lvl == null) continue;
                e.addCollaboration(cid, lvl);
                Employee colleague = employees.get(cid);
                if (colleague != null) {
                    colleague.addCollaboration(id, lvl);
                }
            }

            addEmployee(e);
            System.out.println("Zamestnanec nacten: " + e);
        } catch (IOException | NumberFormatException | IndexOutOfBoundsException ex) {
            System.out.println("Chyba pri nacitani souboru: " + ex.getMessage());
        }
    }

    public static boolean isYearValid(int year) {
        int current = java.time.Year.now().getValue();
        return year >= 1900 && year <= current;
    }

    public void saveToSQLite() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            Statement st = conn.createStatement();
            st.execute("DROP TABLE IF EXISTS collaborations");
            st.execute("DROP TABLE IF EXISTS employees");
            st.execute("CREATE TABLE employees (id INTEGER PRIMARY KEY, name TEXT, surname TEXT, birth_year INTEGER, group_code TEXT)");
            st.execute("CREATE TABLE collaborations (employee_id INTEGER, colleague_id INTEGER, level INTEGER)");
            st.close();

            String sqlEmp = "INSERT INTO employees VALUES (?, ?, ?, ?, ?)";
            String sqlCol = "INSERT INTO collaborations VALUES (?, ?, ?)";
            PreparedStatement psEmp = conn.prepareStatement(sqlEmp);
            PreparedStatement psCol = conn.prepareStatement(sqlCol);

            Set<Integer> klice = employees.keySet();
            for (Integer key : klice) {
                Employee e = employees.get(key);
                psEmp.setInt(1, e.getId());
                psEmp.setString(2, e.getName());
                psEmp.setString(3, e.getSurname());
                psEmp.setInt(4, e.getBirthYear());
                psEmp.setString(5, String.valueOf(e.getGroupCode()));
                psEmp.executeUpdate();

                Set<Integer> colKeys = e.getCollaborators().keySet();
                for (Integer cid : colKeys) {
                    psCol.setInt(1, e.getId());
                    psCol.setInt(2, cid);
                    psCol.setInt(3, e.getCollaborators().get(cid).getValue());
                    psCol.executeUpdate();
                }
            }

            psEmp.close();
            psCol.close();
            conn.close();
            System.out.println("Data ulozena do databaze: " + dbFile);
        } catch (SQLException ex) {
            System.out.println("Chyba pri ukladani do databaze: " + ex.getMessage());
        }
    }

    public void loadFromSQLite() {
        File f = new File(dbFile);
        if (!f.exists()) {
            return;
        }
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM employees");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                int birthYear = rs.getInt("birth_year");
                char code = rs.getString("group_code").charAt(0);

                Employee e;
                if (code == 'D') {
                    e = new DataAnalyst(id, name, surname, birthYear);
                } else {
                    e = new SecuritySpecialist(id, name, surname, birthYear);
                }
                addEmployee(e);
            }
            rs.close();

            ResultSet rs2 = st.executeQuery("SELECT * FROM collaborations");
            while (rs2.next()) {
                int empId = rs2.getInt("employee_id");
                int colId = rs2.getInt("colleague_id");
                int lvl = rs2.getInt("level");
                Employee e = employees.get(empId);
                if (e != null) {
                    e.addCollaboration(colId, CollabLevel.fromValue(lvl));
                }
            }
            rs2.close();
            st.close();
            conn.close();
            System.out.println("Data nactena z databaze. Zamestnancu: " + employees.size());
        } catch (SQLException ex) {
            System.out.println("Chyba pri nacitani z databaze: " + ex.getMessage());
        }
    }
}
