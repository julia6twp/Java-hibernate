import entity._Employee_;
import entity._Grupy_;
import entity._Rate_;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class Baza {

    EntityManagerFactory factory;
    EntityManager manager;
    EntityTransaction transaction;
    void connect(){
        factory = Persistence.createEntityManagerFactory("default");
        manager = factory.createEntityManager();
        transaction = manager.getTransaction();

    }
    void disconnect(){
        if(transaction.isActive())
        {
            transaction.rollback();
        }
        manager.close();
        factory.close();
    }
    List<_Grupy_> getgrupy(){
            transaction.begin();
            TypedQuery<_Grupy_> gtq = manager.createNamedQuery("getgrupy",_Grupy_.class);
            List<_Grupy_> pom = gtq.getResultList();
            transaction.commit();
            return pom;
    }
    List<_Employee_> getemployee(){
            transaction.begin();
            TypedQuery<_Employee_> gtq = manager.createNamedQuery("getteacher", _Employee_.class);
            List<_Employee_> pom = gtq.getResultList();
            transaction.commit();
            return pom;
        }
    List<_Rate_> getrates(){
        transaction.begin();
        TypedQuery<_Rate_> gtq = manager.createNamedQuery("getrates",_Rate_.class);
        List<_Rate_> pom = gtq.getResultList();
        transaction.commit();
        return pom;
    }

    void addGrupa(String nazwa, int pojemność){
        transaction.begin();
        _Grupy_ g = new _Grupy_();
        g.setId(0);
        g.setNazwa(nazwa);
        g.setPojemnść(pojemność);
        manager.persist(g);
        transaction.commit();
    }
    void addEmployee(String imie, String nazwisko, EmployeeConditon stan, int rok_urodzenia, double wynagrodzenie, int id_grupy){
        transaction.begin();
        _Employee_ t = new _Employee_();
        t.setImie(imie);
        t.setNazwisko(nazwisko);
        t.setStan(stan.toString());
        t.setRokUrodzenia(rok_urodzenia);
        t.setWynagrodzenie(wynagrodzenie);
        _Grupy_ g = manager.find(_Grupy_.class,id_grupy);
        t.setGrupyByGrupa(g);
        manager.persist(t);
        transaction.commit();
    }
    void addRate(int ocena, Date data_wystawienia,int id_grupy, String komentarz) {
        if(ocena < 1 || ocena > 6)
        {
            System.out.println("Błędna ocena");
            return;
        }
        transaction.begin();
        _Rate_ r = new _Rate_();
        r.setOcena(ocena);
        r.setDataWystawieniaOceny(data_wystawienia);
        r.setKomentarz(komentarz);
        _Grupy_ g = manager.find(_Grupy_.class,id_grupy);
        r.setGrupyByGrupy(g);
        manager.persist(r);
        transaction.commit();
    }
    void Grupy_to_CSV() throws IOException {
        File file = new File("csv/grupy.csv");
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        transaction.begin();
        TypedQuery<_Grupy_> gtq = manager.createNamedQuery("getgrupy",_Grupy_.class);
        List<_Grupy_> pom = gtq.getResultList();
        transaction.commit();
        bw.write("id,nazwa,pojemność");
        bw.newLine();
        for(_Grupy_ g : pom){
            bw.write(g.getId()+","+g.getNazwa()+","+g.getPojemnść());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
    void Employee_to_CSV() throws IOException {
        File file = new File("csv/employee.csv");
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        transaction.begin();
        TypedQuery<_Employee_> gtq = manager.createNamedQuery("getteacher", _Employee_.class);
        List<_Employee_> pom = gtq.getResultList();
        transaction.commit();
        bw.write("id,imie,nazwisko,stan,rok_urodzenia,wynagrodzenie,grupa");
        bw.newLine();
        for(_Employee_ g : pom){
            bw.write(g.getId()+","+g.getImie()+","+g.getNazwisko()+","+g.getStan()+","+g.getRokUrodzenia()+","+g.getWynagrodzenie()+","+g.getGrupyByGrupa().getId());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
    void Rate_to_CSV() throws IOException {
        File file = new File("csv/rate.csv");
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        transaction.begin();
        TypedQuery<_Rate_> gtq = manager.createNamedQuery("getrates",_Rate_.class);
        List<_Rate_> pom = gtq.getResultList();
        transaction.commit();
        bw.write("id,ocena,grupy,data,komentarz");
        bw.newLine();
        for(_Rate_ g : pom){
            bw.write(g.getId()+","+g.getOcena()+","+g.getGrupyByGrupy().getId()+','+g.getDataWystawieniaOceny()+','+g.getKomentarz());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
    List<_Employee_> sort_teacher_salary(){
        transaction.begin();
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<_Employee_> criteria = builder.createQuery(_Employee_.class);
        Root<_Employee_> root = criteria.from(_Employee_.class);
        criteria.select(root);
        criteria.groupBy(root.get("wynagrodzenie"),root.get("id"));
        List<_Employee_> pom = manager.createQuery(criteria).getResultList();
        transaction.commit();
        return pom;
    }

    List<_Employee_> sort_teacher_nazwisko(){
        transaction.begin();
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<_Employee_> criteria = builder.createQuery(_Employee_.class);
        Root<_Employee_> root = criteria.from(_Employee_.class);
        criteria.select(root);
        criteria.groupBy(root.get("nazwisko"),root.get("id"));
        List<_Employee_> pom = manager.createQuery(criteria).getResultList();
        transaction.commit();
        return pom;
    }

    void Summary(){
        transaction.begin();
        Query query = manager.createQuery("select grupyByGrupy.nazwa, count(ocena), avg(ocena) from _Rate_ group by grupyByGrupy.nazwa");
        List<Object[]> result = query.getResultList();
        transaction.commit();
        for(Object[] o : result){
            System.out.println("Nazwa: " + (String)o[0] + ", Liczba ocen: "+(Long)o[1] +",  Średnia: "+(Double)o[2]);
        }
    }

}
