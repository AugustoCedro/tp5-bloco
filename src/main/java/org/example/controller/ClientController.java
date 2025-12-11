package org.example.controller;

import io.javalin.Javalin;
import org.example.exception.ClientNotFoundException;
import org.example.model.Client;
import org.example.service.ClientService;
import org.example.view.ClientView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientController {

    private final ClientService service;

    public ClientController(Javalin app) {
        this.service = new ClientService();
        app.get("/clients",ctx -> ctx.html(ClientView.renderList(service.getClients())));
        app.get("/clients/new",ctx -> ctx.html(ClientView.renderForm(new HashMap<>())));
        app.post("/clients", ctx -> {
            try {
                String name = ctx.formParam("name");
                String email = ctx.formParam("email");

                service.createClient(new Client(name, email));

                ctx.redirect("/clients");
            } catch (IllegalArgumentException e) {
                Map<String, Object> model = new HashMap<>();
                model.put("name", ctx.formParam("name"));
                model.put("email", ctx.formParam("email"));


                if (e.getMessage().toLowerCase().contains("nome")) {
                    model.put("nameError", e.getMessage());
                } else if (e.getMessage().toLowerCase().contains("email")) {
                    model.put("emailError", e.getMessage());
                } else {
                    model.put("generalError", e.getMessage());
                }

                ctx.status(400).html(ClientView.renderForm(model));
            }

        });
        app.get("/clients/edit/{id}", ctx -> {
            try {
                int id = ctx.pathParamAsClass("id", Integer.class).get();
                Client client = service.getClientById(id);

                Map<String, Object> model = new HashMap<>();
                model.put("id", client.getId());
                model.put("name", client.getName());
                model.put("email", client.getEmail());

                ctx.html(ClientView.renderForm(model));
            } catch (ClientNotFoundException e) {
                ctx.status(404).result(e.getMessage());
            } catch (Exception e) {
                ctx.status(500).result("Erro interno inesperado");
            }
        });
        app.post("/clients/edit/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();

            try {
                String name = ctx.formParam("name");
                String email = ctx.formParam("email");

                service.updateClient(new Client(id, name, email));
                ctx.redirect("/clients");

            } catch (IllegalArgumentException e) {

                Map<String, Object> model = new HashMap<>();
                model.put("id", id);
                model.put("name", ctx.formParam("name"));
                model.put("email", ctx.formParam("email"));

                if (e.getMessage().toLowerCase().contains("nome")) {
                    model.put("nameError", e.getMessage());
                } else if (e.getMessage().toLowerCase().contains("email")) {
                    model.put("emailError", e.getMessage());
                } else {
                    model.put("generalError", e.getMessage());
                }

                ctx.status(400).html(ClientView.renderForm(model));
            }
        });

        app.post("/clients/delete/{id}",ctx -> {
            int id = ctx.pathParamAsClass("id",Integer.class).get();
            service.deleteClientById(id);
            ctx.redirect("/clients");
        });

    }

    public ClientController() {
        this.service = new ClientService();
    }

    public List<Client> getClients(){
        return service.getClients();
    }

    public Client getClientById(int id){
        return service.getClientById(id);
    }

    public void createClient(Client client){
        service.createClient(client);
    }

    public void updateClient(Client client){
        service.updateClient(client);
    }


    public void deleteClientById(int id) {
        service.deleteClientById(id);
    }
}
