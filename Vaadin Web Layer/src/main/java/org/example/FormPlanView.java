package org.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// Formular pentru editarea sau crearea unui Abonament (Plan)
// Accesibil la /plan sau /plan/{id}
@PageTitle("Form Plan")
@Route(value = "plan", layout = MainView.class)
public class FormPlanView extends VerticalLayout implements HasUrlParameter<Integer> {

    private EntityManager em;
    private Plan plan = null; // Obiectul Plan curent
    private final Binder<Plan> binder = new BeanValidationBinder<>(Plan.class);

    // --- Componente Vizuale ---
    private final H1 titluForm = new H1("Editare Abonament");
    private final IntegerField idField = new IntegerField("ID Plan");
    private final TextField nameField = new TextField("Nume Abonament");
    private final NumberField monthlyFeeField = new NumberField("Taxa Lunara");

    // Folosim TextField cu convertor pentru Long, deoarece IntegerField nu suporta Long direct
    private final TextField requestsIncludedField = new TextField("Request-uri Incluse");
    
    private final NumberField costPerRequestField = new NumberField("Cost per Request Extra");

    // Actiuni
    private final Button cmdSterge = new Button("Sterge");
    private final Button cmdAbandon = new Button("Abandon");
    private final Button cmdSalveaza = new Button("Salveaza");

    public FormPlanView() {
        initDataModel();
        initViewLayout();
        initControllerActions();
    }

    // Initializare conexiune DB si legare date
    private void initDataModel() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ApiUsageBillingPU");
        this.em = emf.createEntityManager();

        // Adaugam sufixul "RON" pentru claritate vizuala
        monthlyFeeField.setSuffixComponent(new com.vaadin.flow.component.html.Span("RON"));
        costPerRequestField.setSuffixComponent(new com.vaadin.flow.component.html.Span("RON"));

        // Legare date (Binding)
        // Folosim string-uri pentru a referi proprietatile din clasa Plan (ex: "idPlan", "name")
        binder.forField(idField).bind("idPlan");
        binder.forField(nameField).bind("name");
        binder.forField(monthlyFeeField).bind("monthlyFee");
        
        // Convertim valoarea din TextField (String) in Long pentru proprietatea 'includedRequestsPerMonth'
        // withNullRepresentation("") asigura ca null in DB apare ca text gol, nu arunca eroare
        binder.forField(requestsIncludedField)
                .withNullRepresentation("")
                .withConverter(
                        new com.vaadin.flow.data.converter.StringToLongConverter("Trebuie sa fie un numar intreg"))
                .bind("includedRequestsPerMonth");
                
        binder.forField(costPerRequestField).bind("pricePerExtraRequest");
    }

    // Aranjarea vizuala a formularului
    private void initViewLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(idField, nameField, monthlyFeeField, requestsIncludedField, costPerRequestField);
        formLayout.setMaxWidth("400px");

        HorizontalLayout actionToolbar = new HorizontalLayout(cmdSterge, cmdAbandon, cmdSalveaza);
        add(titluForm, formLayout, actionToolbar);
    }

    // Definirea comportamentului butoanelor
    private void initControllerActions() {
        cmdSalveaza.addClickListener(e -> {
            salveazaPlan();
            this.getUI().ifPresent(ui -> ui.navigate(NavigableGridPlanuriView.class));
        });

        cmdAbandon.addClickListener(e -> 
            this.getUI().ifPresent(ui -> ui.navigate(NavigableGridPlanuriView.class))
        );

        cmdSterge.addClickListener(e -> {
            stergePlan();
            this.getUI().ifPresent(ui -> ui.navigate(NavigableGridPlanuriView.class));
        });
    }

    // Salveaza modificarile in baza de date
    private void salveazaPlan() {
        try {
            if(binder.writeBeanIfValid(this.plan)) {
                this.em.getTransaction().begin();
                // Folosim reflection pentru a lua ID-ul, deoarece clasa Plan vine dintr-o dependinta externa
                Integer id = (Integer) this.plan.getClass().getMethod("getIdPlan").invoke(this.plan);
                
                Plan existing = em.find(Plan.class, id);
                if (existing == null) {
                    this.em.persist(this.plan); // Insert
                } else {
                    this.plan = this.em.merge(this.plan); // Update
                }
                this.em.getTransaction().commit();
            }
        } catch (Exception ex) {
            if (this.em.getTransaction().isActive()) this.em.getTransaction().rollback();
            System.err.println("Eroare la salvare: " + ex.getMessage());
        }
    }

    // Sterge planul curent
    private void stergePlan() {
        if (this.plan != null) {
            try {
                this.em.getTransaction().begin();
                if (this.em.contains(this.plan)) {
                    this.em.remove(this.plan);
                } else {
                    Integer id = (Integer) this.plan.getClass().getMethod("getIdPlan").invoke(this.plan);
                    Plan managed = em.find(Plan.class, id);
                    if (managed != null) em.remove(managed);
                }
                this.em.getTransaction().commit();
            } catch (Exception e) {
                if (this.em.getTransaction().isActive()) this.em.getTransaction().rollback();
                e.printStackTrace();
            }
        }
    }

    // Reincarca datele in formular
    private void refreshForm() {
        if (this.plan != null) {
            binder.readBean(this.plan);
        }
    }

    // Se apeleaza la navigare (/plan/123)
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer id) {
        if (id != null) {
            this.plan = em.find(Plan.class, id);
            
            // Daca nu gasim planul sau ID-ul e 999, cream unul nou
            if (this.plan == null || id == 999) {
                this.plan = new Plan();
                try {
                    // Generam un ID aleatoriu pentru noul plan
                    int newId = id == 999 ? (int)(System.currentTimeMillis() % 10000) : id;
                    this.plan.getClass().getMethod("setIdPlan", Integer.class).invoke(this.plan, newId);
                    this.plan.getClass().getMethod("setName", String.class).invoke(this.plan, "Abonament Nou");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Fallback: daca nu avem ID in URL, presupunem ca e nou
            this.plan = new Plan();
             try {
                int newId = (int)(System.currentTimeMillis() % 10000);
                this.plan.getClass().getMethod("setIdPlan", Integer.class).invoke(this.plan, newId);
                this.plan.getClass().getMethod("setName", String.class).invoke(this.plan, "Abonament Nou");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        refreshForm();
    }
}