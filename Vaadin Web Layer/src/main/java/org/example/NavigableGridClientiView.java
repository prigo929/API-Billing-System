package org.example;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Aceasta pagina afiseaza lista de clienti intr-un tabel (Grid)
// @Route("clienti") inseamna ca pagina este accesibila la http://localhost:8080/clienti
// layout = MainView.class inseamna ca va folosi meniul definit in MainView
@PageTitle("Clienti")
@Route(value = "clienti", layout = MainView.class)
public class NavigableGridClientiView extends VerticalLayout implements HasUrlParameter<Integer> {

    // --- Date Model ---
    private EntityManager em; // Managerul JPA pentru interactiunea cu baza de date
    private final List<ClientAccount> clienti = new ArrayList<>(); // Lista locala de clienti afisata in tabel
    private ClientAccount client = null; // Clientul selectat curent (daca exista)

    // --- Componente Vizuale ---
    private final H1 titluForm = new H1("Lista Clienti");
    private final TextField filterText = new TextField(); // Campul de cautare
    private final Button cmdAdaugaClient = new Button("Adauga client...");
    private final Grid<ClientAccount> grid = new Grid<>(ClientAccount.class); // Tabelul Vaadin

    public NavigableGridClientiView() {
        initDataModel();
        initViewLayout();
        initControllerActions();
    }

    // 1. Initializare Date - Conectare la DB si incarcare initiala
    private void initDataModel() {
        // Creeaza conexiunea cu baza de date folosind unitatea de persistenta definita
        // in persistence.xml
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ApiUsageBillingPU");
        em = emf.createEntityManager();
        refreshGridData();
    }

    // Reincarca datele din baza de date in lista locala si actualizeaza tabelul
    private void refreshGridData() {
        this.clienti.clear();
        // Executa query JPQL pentru a lua toti clientii
        List<ClientAccount> lst = em.createQuery("SELECT c FROM ClientAccount c", ClientAccount.class).getResultList();
        clienti.addAll(lst);

        if (!clienti.isEmpty()) {
            // Sorteaza clientii dupa ID pentru o afisare ordonata
            clienti.sort(Comparator.comparing(ClientAccount::getIdClient));
            // Selecteaza implicit primul client daca nu e niciunul selectat
            if (this.client == null) {
                this.client = clienti.get(0);
            }
        }
        grid.setItems(this.clienti); // Trimite datele catre componenta Grid
    }

    // 2. Initializare Layout - Aranjarea componentelor in pagina
    private void initViewLayout() {
        // Configurare camp filtrare: LAZY inseamna ca asteapta putin dupa ce tastezi
        // inainte sa declanseze evenimentul
        filterText.setPlaceholder("Filtreaza dupa nume...");
        filterText.setClearButtonVisible(true);
         filterText.setValueChangeMode(ValueChangeMode.LAZY);

        HorizontalLayout toolbar = new HorizontalLayout(filterText, cmdAdaugaClient);

        // Configurare coloane tabel
        grid.removeAllColumns();

        // Adaugam coloane cu sortare activata
        grid.addColumn(ClientAccount::getIdClient)
                .setHeader("ID")
                .setSortable(true);

        grid.addColumn(ClientAccount::getName)
                .setHeader("Nume")
                .setSortable(true);

        grid.addColumn(ClientAccount::getEmail)
                .setHeader("Email")
                .setSortable(true);

        // Adaugam coloana pentru Plan cu sortare custom
        grid.addColumn(client -> {
            Plan plan = client.getPlan();
            if (plan != null) {
                return plan.getName();
            }
            return "Fara Plan";
        }).setHeader("Plan").setSortable(true);

        // Adauga o coloana speciala "Actiuni" care contine butoane pentru fiecare rand
        grid.addComponentColumn(this::createGridActionsButtons).setHeader("Actiuni");

        add(titluForm, new VerticalLayout(toolbar, grid));
    }

    // Creeaza butoanele de Editare si Stergere pentru fiecare rand din tabel
    private Component createGridActionsButtons(ClientAccount item) {
        Button cmdEdit = new Button("Edit", e -> {
            grid.asSingleSelect().setValue(item);
            editClient();
        });
        Button cmdDelete = new Button("Sterge", e -> {
            grid.asSingleSelect().setValue(item);
            stergeClient();
            refreshGridData();
        });
        return new HorizontalLayout(cmdEdit, cmdDelete);
    }

    // 3. Actiuni Controller - Ce se intampla cand utilizatorul interactioneaza cu
    // butoanele
    private void initControllerActions() {
        filterText.addValueChangeListener(e -> updateList());
        // Am eliminat listenerii pentru butoanele sterse din toolbar
        cmdAdaugaClient.addClickListener(e -> adaugaClient());
    }

    // --- Logica CRUD (Create, Read, Update, Delete) ---

    // Filtreaza lista de clienti in functie de textul introdus
    private void updateList() {
        List<ClientAccount> filtered = clienti;
        if (filterText.getValue() != null && !filterText.getValue().isEmpty()) {
            filtered = clienti.stream()
                    .filter(c -> c.getName().toLowerCase().contains(filterText.getValue().toLowerCase()))
                    .toList();
        }
        grid.setItems(filtered);
    }

    // Navigheaza catre pagina de formular pentru a adauga un client nou (ID 999 e
    // un semnal pentru "Nou")
    private void adaugaClient() {
        this.getUI().ifPresent(ui -> ui.navigate(FormClientView.class, 999));
    }

    // Navigheaza catre pagina de formular pentru a edita clientul selectat
    private void editClient() {
        this.client = this.grid.asSingleSelect().getValue();
        if (this.client != null) {
            this.getUI().ifPresent(ui -> ui.navigate(FormClientView.class, this.client.getIdClient()));
        }
    }

    // Sterge clientul selectat din baza de date
    private void stergeClient() {
        this.client = this.grid.asSingleSelect().getValue();
        if (this.client != null) {
            // Verifica daca entitatea este "managed" (conectata la sesiunea curenta
            // Hibernate/JPA)
            if (this.em.contains(this.client)) {
                this.em.getTransaction().begin();
                this.em.remove(this.client);
                this.em.getTransaction().commit();
            } else {
                // Daca nu e managed, o cautam din nou in DB si o stergem pe cea gasita
                ClientAccount managed = em.find(ClientAccount.class, client.getIdClient());
                if (managed != null) {
                    em.getTransaction().begin();
                    em.remove(managed);
                    em.getTransaction().commit();
                }
            }
            this.clienti.remove(this.client); // Scoate si din lista locala
        }
    }

    // --- Gestionare Parametru Navigare ---
    // Aceasta metoda este apelata automat de Vaadin cand navigam catre aceasta
    // pagina
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer id) {
        // Reimprospatam datele pentru a fi siguri ca vedem modificarile facute in
        // Formular
        refreshGridData();

        // Curatam cache-ul JPA pentru a evita datele vechi (stale data)
        this.em.getEntityManagerFactory().getCache().evictAll();

        if (id != null) {
            // Daca am primit un ID in URL, incercam sa selectam acel client in tabel
            this.client = em.find(ClientAccount.class, id);
            if (this.client != null) {
                grid.select(this.client);
            }
        }
    }
}