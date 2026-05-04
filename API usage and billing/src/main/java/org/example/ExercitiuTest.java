package org.example;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;

public class ExercitiuTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            // Inițializăm EntityManagerFactory folosind unitatea de persistență definită în persistence.xml
            emf = Persistence.createEntityManagerFactory("ApiUsageBillingPU");
            em = emf.createEntityManager();

            // 1. Configurare Perioadă: Setăm intervalul de timp pentru facturare (luna curentă)
            // 'startPeriod' este prima zi a lunii curente
            LocalDate startPeriod = LocalDate.now().withDayOfMonth(1);
            // 'endPeriod' este ultima zi a lunii curente
            LocalDate endPeriod = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

            // Începem o tranzacție pentru a salva sau actualiza datele în baza de date
            em.getTransaction().begin();

            // 2. Creare Plan și Client (Verificăm mai întâi dacă există deja în baza de date)
            // Căutăm planul cu ID-ul 1
            Plan pro = em.find(Plan.class, 1);
            if (pro == null) {
                // Dacă nu există, îl creăm: Plan PRO, $49.99/lună, 100 cereri incluse, $0.50 pentru fiecare cerere extra
                pro = new Plan(1, "PRO Plan", 49.99, 100L, 0.50);
                em.persist(pro); // Salvăm planul în baza de date
            }

            // Căutăm clientul cu ID-ul 100
            ClientAccount client = em.find(ClientAccount.class, 100);
            if (client == null) {
                // Dacă nu există, îl creăm și îl asociem cu planul PRO
                client = new ClientAccount(100, "Acme Corp", "ceo@acme.com", "Acme Inc", "US", pro);
                em.persist(client); // Salvăm clientul
            }
            
            // Adăugăm fonduri în contul clientului pentru a testa funcționalitatea de plată automată
            // Această sumă se adaugă la soldul existent
            client.addFunds(100.00);
            // Folosim 'merge' pentru a ne asigura că modificarea soldului este salvată, chiar dacă clientul exista deja
            client = em.merge(client); 
            System.out.println("Balanța de credit a clientului: " + client.getCreditBalance());

            // 3. Creare API Key (Cheia de acces pentru API)
            // Căutăm cheia cu ID-ul 1000
            ApiKey key1 = em.find(ApiKey.class, 1000);
            if (key1 == null) {
                // Dacă nu există, o creăm și o asociem clientului
                key1 = new ApiKey(1000, "sk-proj-12345", ApiKeyStatus.ACTIVE, client);
                em.persist(key1);
            }

            // 4. Simulare Consum (UsageRecord)
            // Verificăm și creăm înregistrări de utilizare a API-ului
            
            // U1: 80 de unități consumate (se încadrează în limita de 100 a planului)
            UsageRecord u1 = em.find(UsageRecord.class, 1);
            if (u1 == null) {
                u1 = new UsageRecord(1, client, key1, "/v1/chat", 80L, true, null);
                em.persist(u1);
            }
            
            // U2: 50 de unități consumate
            // Total consumat: 80 + 50 = 130 unități.
            // Limita planului este 100, deci avem 30 de unități extra care vor fi taxate suplimentar.
            UsageRecord u2 = em.find(UsageRecord.class, 2);
            if (u2 == null) {
                u2 = new UsageRecord(2, client, key1, "/v1/image", 50L, true, null);
                em.persist(u2);
            }

            // Confirmăm tranzacția pentru a salva toate modificările în baza de date
            em.getTransaction().commit();

            System.out.println("=== Date populate/verificate. Se rulează logica de Billing... ===");

            // 5. APELARE SERVICE: Generăm factura automat pe baza consumului înregistrat
            BillingService billingService = new BillingService(em);
            
            // Calcul estimat al facturii:
            // Abonament lunar: $49.99
            // Cost extra: (130 total - 100 incluse) * $0.50 = 30 * $0.50 = $15.00
            // Total Factură așteptat: $49.99 + $15.00 = $64.99
            Invoice generatedInvoice = billingService.genereazaFactura(client, startPeriod, endPeriod);

            // Afișăm detaliile facturii generate
            System.out.println("Factură generată ID: " + generatedInvoice.getIdInvoice());
            System.out.println("Total de plată: $" + generatedInvoice.getTotalAmount());
            System.out.println("Status Factură: " + generatedInvoice.getStatus()); // Ar trebui să fie PAID dacă clientul a avut suficient credit
            System.out.println("Linii factură:");
            for (InvoiceLine line : generatedInvoice.getLines()) {
                System.out.println(" - " + line.getDescription() + 
                                   " | Cantitate: " + line.getQuantity() + 
                                   " | Preț unitar: " + line.getUnitPrice() + 
                                   " | Total linie: " + line.getLineTotal());
            }
            
            // Reîmprospătăm obiectul client din baza de date pentru a vedea balanța actualizată după plata facturii
            em.refresh(client);
            System.out.println("Balanța de credit a clientului după factură: " + client.getCreditBalance());


            // 6. Verificare Rapoarte
            // Calculăm și afișăm totalul cererilor înregistrate în baza de date pentru perioada respectivă
            Long totalReq = totalRequestsForClientInPeriod(em, client.getIdClient(), startPeriod, endPeriod);
            System.out.println("\nTotal requests în baza de date pentru perioadă: " + totalReq);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Închidem resursele (EntityManager și EntityManagerFactory)
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }
    
    // Metodă ajutătoare pentru a calcula totalul unităților consumate de un client într-o perioadă dată
    private static Long totalRequestsForClientInPeriod(EntityManager em,
                                                       Integer idClient,
                                                       LocalDate startDate,
                                                       LocalDate endDate) {
        // Convertim datele (LocalDate) în momente de timp (LocalDateTime) pentru interogare
        LocalDateTime start = startDate.atStartOfDay(); // Începutul zilei de start (00:00:00)
        LocalDateTime end = endDate.atTime(23, 59, 59); // Sfârșitul zilei de final (23:59:59)

        // Interogare JPQL pentru a suma unitățile de cerere (requestUnits)
        String jpql = "SELECT SUM(u.requestUnits) " +
                "FROM UsageRecord u " +
                "WHERE u.client.idClient = :idClient " +
                "  AND u.timestamp BETWEEN :start AND :end";

        Long result = em.createQuery(jpql, Long.class)
                .setParameter("idClient", idClient)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();

        // Returnăm rezultatul sau 0 dacă nu există înregistrări
        return result != null ? result : 0L;
    }
}