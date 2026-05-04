package org.example;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

// @Route("") defineste aceasta clasa ca pagina principala (root) a aplicatiei (http://localhost:8080/)
// RouterLayout inseamna ca aceasta clasa serveste ca "rama" sau layout principal pentru alte pagini
@Route("") 
public class MainView extends VerticalLayout implements RouterLayout {

    public MainView() {
        setMenuBar();
        add(new H1("API Usage and Billing")); // Titlul aplicatiei pe homepage
    }

    private void setMenuBar() {
        // Bara de meniu principala care va aparea sus pe toate paginile
        MenuBar mainMenu = new MenuBar();

        // Meniu Acasa - navigheaza inapoi la pagina principala
        MenuItem homeMenu = mainMenu.addItem("Acasa");
        homeMenu.addClickListener(event -> UI.getCurrent().navigate(MainView.class));

        // Meniu Clienti - contine sub-meniuri pentru actiuni specifice
        MenuItem clientsMenu = mainMenu.addItem("Clienti");
        SubMenu clientsSubMenu = clientsMenu.getSubMenu();
        clientsSubMenu.addItem("Lista Clienti...", event -> UI.getCurrent().navigate(NavigableGridClientiView.class));
        clientsSubMenu.addItem("Adauga Client...", event -> UI.getCurrent().navigate(FormClientView.class));

        // Meniu Abonamente (Plans)
        MenuItem plansMenu = mainMenu.addItem("Abonamente");
        SubMenu plansSubMenu = plansMenu.getSubMenu();
        plansSubMenu.addItem("Lista Abonamente...", event -> UI.getCurrent().navigate(NavigableGridPlanuriView.class));
        plansSubMenu.addItem("Adauga Abonament...", event -> UI.getCurrent().navigate(FormPlanView.class));

        // Meniu Facturi (Invoices)
        MenuItem invoicesMenu = mainMenu.addItem("Facturi");
        SubMenu invoicesSubMenu = invoicesMenu.getSubMenu();
        invoicesSubMenu.addItem("Istoric Facturi...", event -> UI.getCurrent().navigate(NavigableGridFacturiView.class));

        add(new HorizontalLayout(mainMenu));
    }
}