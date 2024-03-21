import entity._Employee_;
import entity._Grupy_;
import entity._Rate_;

import java.io.IOException;
import java.util.Calendar;

public class Main {
    public static void main(String[] args) throws IOException {
        Baza b = new Baza();
        b.connect();
        for(_Grupy_ g : b.getgrupy()){
            System.out.println(g);
        }
        System.out.println();
        for(_Employee_ t : b.getemployee()){
            System.out.println(t);
        }
        System.out.println();
        for(_Rate_ r : b.getrates()){
            System.out.println(r);
        }
        System.out.println();
        for(_Employee_ t : b.sort_teacher_nazwisko()){
            System.out.println(t);
        }
        System.out.println();
        //b.addGrupa("tt",5);
        b.addEmployee("Tdasdasdad","Kowal", EmployeeConditon.chory,2000,2000,1);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR,2023);
        c.add(Calendar.MONTH,12);
        c.add(Calendar.DAY_OF_MONTH,10);
        //b.addRate(1, Date.valueOf("2023-12-10"),2,"");
        b.Grupy_to_CSV();
        b.Employee_to_CSV();
        b.Rate_to_CSV();
        b.Summary();
        b.disconnect();
    }
}
