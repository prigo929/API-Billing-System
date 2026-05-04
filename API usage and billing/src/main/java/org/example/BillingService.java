package org.example;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BillingService {

    private final EntityManager em;

    public BillingService(EntityManager em) {
        this.em = em;
    }

    /**
     * Generează o factură pentru un client pe o perioadă dată.
     * Calculează costurile pe baza planului tarifar și a consumului înregistrat.
     * Încearcă să plătească automat factura din soldul clientului.
     */
    public Invoice genereazaFactura(ClientAccount client,
                                    LocalDate start,
                                    LocalDate end) {
        // 1. Obține consumul total (requestUnits) pentru perioada respectivă
        // Se interoghează baza de date pentru a suma toate unitățile consumate de client între datele specificate
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        String jpql = "SELECT SUM(u.requestUnits) FROM UsageRecord u " +
                "WHERE u.client.idClient = :clientId " +
                "AND u.timestamp BETWEEN :start AND :end";

        Long totalUsage = em.createQuery(jpql, Long.class)
                .setParameter("clientId", client.getIdClient())
                .setParameter("start", startDateTime)
                .setParameter("end", endDateTime)
                .getSingleResult();

        // Dacă nu există consum, considerăm 0
        if (totalUsage == null) {
            totalUsage = 0L;
        }

        // 2. Calculează costurile pe baza planului clientului
        Plan plan = client.getPlan();
        Double monthlyFee = plan.getMonthlyFee(); // Abonamentul lunar fix
        Long includedRequests = plan.getIncludedRequestsPerMonth(); // Limita de cereri incluse în abonament
        Double pricePerExtraRequest = plan.getPricePerExtraRequest(); // Prețul pentru fiecare cerere peste limită

        // Calculăm câte cereri sunt extra (peste limita inclusă)
        long extraRequests = 0;
        if (totalUsage > includedRequests) {
            extraRequests = totalUsage - includedRequests;
        }

        // Calculăm costul total: abonament + costul pentru cererile extra
        Double extraCost = extraRequests * pricePerExtraRequest;
        Double totalAmount = monthlyFee + extraCost;

        // 3. Creează obiectul Factură (Invoice)
        Invoice invoice = new Invoice();
        
        // Generăm un ID temporar (în producție ar trebui folosit @GeneratedValue)
        int dummyId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        
        invoice.setIdInvoice(dummyId);
        invoice.setClient(client);
        invoice.setPeriodStart(start);
        invoice.setPeriodEnd(end);
        invoice.setStatus(InvoiceStatus.DRAFT); // Inițial factura este DRAFT (ciornă)
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setTotalAmount(totalAmount);

        // 4. Creează Liniile Facturii (detaliile costurilor)
        
        // Linia 1: Abonamentul lunar
        InvoiceLine lineFee = new InvoiceLine();
        lineFee.setIdLine(dummyId + 1); 
        lineFee.setInvoice(invoice);
        lineFee.setDescription("Monthly fee " + plan.getName());
        lineFee.setQuantity(1L);
        lineFee.setUnitPrice(monthlyFee);

        // Linia 2: Costuri extra (dacă există)
        InvoiceLine lineExtra = new InvoiceLine();
        lineExtra.setIdLine(dummyId + 2); 
        lineExtra.setInvoice(invoice);
        lineExtra.setDescription("Overage API requests");
        lineExtra.setQuantity(extraRequests);
        lineExtra.setUnitPrice(pricePerExtraRequest);

        // Adăugăm liniile la factură
        invoice.adaugaLinie(lineFee);
        invoice.adaugaLinie(lineExtra);


        // Logica de plată automată din soldul clientului (Credit Balance)
        if (client.getCreditBalance() > 0) {
            if (client.getCreditBalance() >= totalAmount) {
                // Cazul 1: Clientul are suficienți bani pentru a plăti integral
                // Scădem suma din soldul clientului
                client.setCreditBalance(client.getCreditBalance() - totalAmount);
                // Marcăm factura ca plătită
                invoice.setStatus(InvoiceStatus.PAID);

                // Înregistrăm plata în sistem
                Payment creditPayment = new Payment(dummyId + 5, invoice, totalAmount, "CREDIT_BALANCE", "WALLET-TX");
                invoice.adaugaPayment(creditPayment);
                em.persist(creditPayment);
            } else {
                // Cazul 2: Clientul are bani, dar nu suficienți pentru toată factura (plată parțială)
                Double partial = client.getCreditBalance();
                // Folosim tot soldul disponibil
                client.setCreditBalance(0.0);

                // Înregistrăm plata parțială
                Payment partialPayment = new Payment(dummyId + 5, invoice, partial, "CREDIT_BALANCE", "WALLET-TX");
                invoice.adaugaPayment(partialPayment);
                em.persist(partialPayment);
                // Factura rămâne DRAFT (sau ar putea fi PARTIAL_PAID dacă am avea acest status)
            }
        }

        // 5. Persist (Salvează totul în baza de date)
        // Deschidem o tranzacție pentru a salva factura și liniile ei
        em.getTransaction().begin();
        em.persist(invoice);
        em.persist(lineFee);
        em.persist(lineExtra);
        em.getTransaction().commit();

        return invoice;
    }
    
    public EntityManager getEm() {
        return em;
    }
}
