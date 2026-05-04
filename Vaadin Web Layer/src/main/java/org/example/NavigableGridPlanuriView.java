package org.example;

import com.vaadin.flow.component.Component;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Pagina care afiseaza lista de abonamente (Planuri)
@PageTitle("Abonamente")
@Route(value = "planuri", layout = MainView.class)
public class NavigableGridPlanuriView extends VerticalLayout {

    private EntityManager em;
    private final List<Plan> planuri = new ArrayList<>();

    private final H1 titlu = new H1("Lista Abonamente");
    private final Button cmdAdauga = new Button("Adauga Abonament...");
    private final Grid<Plan> grid = new Grid<>(Plan.class);

    public NavigableGridPlanuriView() {
        initDataModel();
        initViewLayout();
        initControllerActions();
    }

    // Initializare date din DB
    private void initDataModel() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ApiUsageBillingPU");
        em = emf.createEntityManager();
        refreshGridData();
    }

    // Reincarca lista de planuri
    private void refreshGridData() {
        this.planuri.clear();
        this.em.getEntityManagerFactory().getCache().evictAll(); // Evita datele vechi din cache
        List<Plan> lst = em.createQuery("SELECT p FROM Plan p", Plan.class).getResultList();
        planuri.addAll(lst);

        // Sortarea este comentata temporar pentru a evita erori de compilare daca
        // getterii nu sunt vizibili
        if (!planuri.isEmpty()) {
            planuri.sort(Comparator.comparing(Plan::getIdPlan));
        }
        grid.setItems(this.planuri);
    }

    // Configurare vizuala
    private void initViewLayout() {
        HorizontalLayout toolbar = new HorizontalLayout(cmdAdauga);

        grid.removeAllColumns();
        // Definim coloanele folosind numele proprietatilor (string-uri)
        grid.addColumn("idPlan").setHeader("ID");
        grid.addColumn("name").setHeader("Nume");
        grid.addColumn("monthlyFee").setHeader("Taxa Lunara");
        grid.addColumn("includedRequestsPerMonth").setHeader("Request-uri Incluse");

        // Coloana de actiuni (buton Edit si Sterge)
        grid.addComponentColumn(this::createGridActionsButtons).setHeader("Actiuni");

        add(titlu, new VerticalLayout(toolbar, grid));
    }

    // Creeaza butoanele de Editare si Stergere pentru fiecare rand
    private Component createGridActionsButtons(Plan item) {
        Button cmdEdit = new Button("Edit", e -> {
            // Folosim reflection pentru a lua ID-ul in siguranta
            Integer id = item.getIdPlan();
            this.getUI().ifPresent(ui -> ui.navigate(FormPlanView.class, id));
        });

        Button cmdDelete = new Button("Sterge", e -> {
            stergePlan(item);
        });

        return new HorizontalLayout(cmdEdit, cmdDelete);
    }

    private void stergePlan(Plan plan) {
        try {
            em.getTransaction().begin();
            if (!em.contains(plan)) {
                Integer id = plan.getIdPlan();
                plan = em.find(Plan.class, id);
            }

            if (plan != null) {
                em.remove(plan);
                em.getTransaction().commit();
                Notification.show("Abonament sters cu succes.");
                refreshGridData();
            } else {
                em.getTransaction().rollback();
                Notification.show("Abonamentul nu a fost gasit.");
            }
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            Notification.show("Eroare la stergere: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Actiunea butonului "Adauga Abonament"
    private void initControllerActions() {
        cmdAdauga.addClickListener(e -> {
            // Navigheaza la formular cu ID 999 (semnal pentru "Nou")
            this.getUI().ifPresent(ui -> ui.navigate(FormPlanView.class, 999));
        });
    }
}