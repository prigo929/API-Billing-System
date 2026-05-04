package org.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Pagina care afiseaza istoricul facturilor
@PageTitle("Facturi")
@Route(value = "facturi", layout = MainView.class)
public class NavigableGridFacturiView extends VerticalLayout {

    private EntityManager em;
    private final List<Invoice> facturi = new ArrayList<>();
    private BillingService billingService;

    private final H1 titlu = new H1("Istoric Facturi");
    private final Button cmdGenereaza = new Button("Genereaza Facturi Luna Curenta");
    private final Grid<Invoice> grid = new Grid<>(Invoice.class);

    public NavigableGridFacturiView() {
        initDataModel();
        initViewLayout();
        initControllerActions();
    }

    // Initializare date si servicii
    private void initDataModel() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ApiUsageBillingPU");
        em = emf.createEntityManager();
        this.billingService = new BillingService(em);

        refreshGridData();
    }

    // Reincarca lista de facturi
    private void refreshGridData() {
        this.facturi.clear();
        this.em.getEntityManagerFactory().getCache().evictAll();

        List<Invoice> lst = em.createQuery("SELECT i FROM Invoice i", Invoice.class).getResultList();
        facturi.addAll(lst);

        grid.setItems(this.facturi);
    }

    // Configurare vizuala
    private void initViewLayout() {
        HorizontalLayout toolbar = new HorizontalLayout(cmdGenereaza);

        grid.removeAllColumns();
        grid.addColumn("idInvoice").setHeader("ID");

        // Coloana Client - navigam prin relatia Invoice -> ClientAccount
        // Coloana Client - navigam prin relatia Invoice -> ClientAccount
        grid.addColumn(i -> {
            ClientAccount client = i.getClient();
            if (client != null) {
                return client.getName();
            }
            return "N/A";
        }).setHeader("Client");

        grid.addColumn("periodStart").setHeader("Inceput Perioada");
        grid.addColumn("periodEnd").setHeader("Sfarsit Perioada");
        grid.addColumn("totalAmount").setHeader("Total de Plata");
        grid.addColumn("status").setHeader("Status");

        // Buton de stergere factura
        grid.addComponentColumn(this::createGridActionsButtons).setHeader("Actiuni");

        add(titlu, new VerticalLayout(toolbar, grid));
    }

    private com.vaadin.flow.component.Component createGridActionsButtons(Invoice item) {
        Button cmdDelete = new Button("Sterge", e -> {
            stergeFactura(item);
        });
        return new HorizontalLayout(cmdDelete);
    }

    // Logica de stergere factura
    private void stergeFactura(Invoice invoice) {
        try {
            em.getTransaction().begin();
            if (!em.contains(invoice)) {
                Integer id = invoice.getIdInvoice();
                invoice = em.find(Invoice.class, id);
            }

            if (invoice != null) {
                em.remove(invoice);
                em.getTransaction().commit();
                Notification.show("Factura stearsa cu succes.");
                refreshGridData();
            } else {
                em.getTransaction().rollback();
                Notification.show("Factura nu a fost gasita.");
            }
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            Notification.show("Eroare la stergere: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Actiunea butonului "Genereaza Facturi"
    private void initControllerActions() {
        cmdGenereaza.addClickListener(e -> {
            try {
                List<ClientAccount> clienti = em.createQuery("SELECT c FROM ClientAccount c", ClientAccount.class)
                        .getResultList();

                LocalDate start = LocalDate.now().withDayOfMonth(1);
                LocalDate end = LocalDate.now();

                int generatedCount = 0;
                for (ClientAccount client : clienti) {
                    // Verificam daca clientul are un plan inainte de a genera factura
                    Plan plan = client.getPlan();
                    if (plan != null) {
                        billingService.genereazaFactura(client, start, end);
                        generatedCount++;
                    } else {
                        System.out.println("Skipping client " + client.getName() + " (No Plan assigned)");
                    }
                }

                if (generatedCount > 0) {
                    Notification.show("Au fost generate " + generatedCount + " facturi cu succes!");
                } else {
                    Notification.show("Nu au fost generate facturi. Verificati daca clientii au planuri asignate.");
                }
                refreshGridData();
            } catch (Exception ex) {
                Notification.show("Eroare la generare: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}