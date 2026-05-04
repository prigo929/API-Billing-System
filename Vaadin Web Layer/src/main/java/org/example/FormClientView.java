package org.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

// Aceasta pagina este formularul pentru adaugarea sau editarea unui client
// @Route("client") -> accesibila la http://localhost:8080/client
@PageTitle("Form Client")
@Route(value = "client", layout = MainView.class)
public class FormClientView extends VerticalLayout implements HasUrlParameter<Integer> {

    // --- Model ---
    private EntityManager em;
    private ClientAccount client = null; // Obiectul client pe care il editam
    // Binder leaga automat campurile din UI de proprietatile obiectului Java
    private final Binder<ClientAccount> binder = new BeanValidationBinder<>(ClientAccount.class);

    // --- Componente Vizuale (Campurile formularului) ---
    private final H1 titluForm = new H1("Form Client");
    private final IntegerField idField = new IntegerField("ID Client");
    private final TextField nameField = new TextField("Nume Client");
    private final TextField emailField = new TextField("Email");
    
    // ComboBox pentru selectarea Planului
    private final ComboBox<Plan> planComboBox = new ComboBox<>("Plan Abonament");

    // Butoane de actiune
    private final Button cmdSterge = new Button("Sterge");
    private final Button cmdAbandon = new Button("Abandon");
    private final Button cmdSalveaza = new Button("Salveaza");

    public FormClientView() {
        initDataModel();
        initViewLayout();
        initControllerActions();
    }

    // Initializare conexiune DB si legare date
    private void initDataModel() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ApiUsageBillingPU");
        this.em = emf.createEntityManager();

        // Configurare ComboBox Planuri
        List<Plan> planuriDisponibile = em.createQuery("SELECT p FROM Plan p", Plan.class).getResultList();
        planComboBox.setItems(planuriDisponibile);
        // Spunem ComboBox-ului ce sa afiseze pentru fiecare obiect Plan (numele)
        // Folosim reflection pentru a fi siguri ca merge chiar daca nu avem acces direct la metoda getName() la compilare
        planComboBox.setItemLabelGenerator(plan -> {
            try {
                return (String) plan.getClass().getMethod("getName").invoke(plan);
            } catch (Exception e) {
                return "Plan " + plan.toString();
            }
        });

        // Aici spunem Binder-ului: "Campul idField din UI corespunde proprietatii 'idClient' din obiect"
        // Folosim string-uri ("idClient") pentru ca ne bazam pe conventia Java Beans (getIdClient/setIdClient)
        binder.forField(idField).bind("idClient");
        binder.forField(nameField).bind("name");
        binder.forField(emailField).bind("email");
        binder.forField(planComboBox).bind("plan");
    }

    // Aranjarea vizuala a formularului
    private void initViewLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(idField, nameField, emailField, planComboBox);
        formLayout.setMaxWidth("400px");

        // Am scos butonul cmdAdaugare din layout
        HorizontalLayout actionToolbar = new HorizontalLayout(cmdSterge, cmdAbandon, cmdSalveaza);

        add(titluForm, formLayout, actionToolbar);
    }

    // Definirea comportamentului butoanelor
    private void initControllerActions() {
        // Am sters listener-ul pentru cmdAdaugare

        cmdSalveaza.addClickListener(e -> {
            salveazaClient();
            // Dupa salvare, ne intoarcem la lista de clienti
            this.getUI().ifPresent(ui -> ui.navigate(NavigableGridClientiView.class, this.client.getIdClient()));
        });

        cmdAbandon.addClickListener(e -> 
            this.getUI().ifPresent(ui -> ui.navigate(NavigableGridClientiView.class))
        );

        cmdSterge.addClickListener(e -> {
            stergeClient();
            this.getUI().ifPresent(ui -> ui.navigate(NavigableGridClientiView.class));
        });
    }

    // --- Logica Tranzactionala (Salvare/Stergere in DB) ---

    private void salveazaClient() {
        try {
            // writeBeanIfValid ia valorile din campurile UI si le pune in obiectul 'client'
            if(binder.writeBeanIfValid(this.client)) {
                this.em.getTransaction().begin();
                
                // Verificam daca acest client exista deja in baza de date
                ClientAccount existing = em.find(ClientAccount.class, this.client.getIdClient());
                
                if (existing == null) {
                    // Daca nu exista, il inseram (INSERT)
                    this.em.persist(this.client);
                } else {
                    // Daca exista, il actualizam (UPDATE)
                    this.client = this.em.merge(this.client);
                }
                
                this.em.getTransaction().commit();
                System.out.println("Client Salvat");
            }
        } catch (Exception ex) {
            if (this.em.getTransaction().isActive()) this.em.getTransaction().rollback();
            System.err.println("Eroare la salvarea clientului: " + ex.getMessage());
        }
    }

    private void stergeClient() {
        if (this.client != null && this.em.contains(this.client)) {
            this.em.getTransaction().begin();
            this.em.remove(this.client);
            this.em.getTransaction().commit();
        } else if (this.client != null) {
            ClientAccount managed = em.find(ClientAccount.class, client.getIdClient());
            if (managed != null) {
                em.getTransaction().begin();
                em.remove(managed);
                em.getTransaction().commit();
            }
        }
    }

    // Actualizeaza UI-ul cu datele din obiectul curent
    private void refreshForm() {
        if (this.client != null) {
            binder.readBean(this.client); 
        }
    }

    // --- Logica Parametru URL ---
    // Se apeleaza cand intram pe pagina cu un ID (ex: /client/123)
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer id) {
        if (id != null) {
            // Cautam clientul in baza de date
            this.client = em.find(ClientAccount.class, id);

            // Daca nu il gasim sau daca ID-ul e 999 (codul nostru pentru "Nou"), cream unul gol
            if (this.client == null || id == 999) {
                this.client = new ClientAccount();
                this.client.setIdClient(id == 999 ? (int)(System.currentTimeMillis() % 10000) : id);
                this.client.setName("Client Nou");
            }
        }
        refreshForm();
    }
}