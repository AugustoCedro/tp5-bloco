package org.example.service;

import org.example.exception.ClientNotFoundException;
import org.example.model.Client;
import org.example.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

public class ClientService {

    private final ClientRepository repository;

    public ClientService() {
        this.repository = new ClientRepository();
    }

    public List<Client> getClients(){
        return repository.getRepository();
    }

    public Client getClientById(int id) {
        if(isClientFound(id)){
            return repository.findClientById(id);
        }
        throw new ClientNotFoundException("Cliente com ID: " + id + " não encontrado");
    }

    public void createClient(Client client) {
        if(isClientDataValid(client)){
            repository.addClient(client);
        }else{
            throw new IllegalArgumentException("Dados de criação não devem ser nulos");
        }
    }

    public void updateClient(Client client) {
        if(isClientFound(client.getId())){
            if(isClientDataValid(client)){
                repository.updateClient(client);
            }else{
                throw new IllegalArgumentException("Dados de alteração não devem ser nulos");
            }
        }
    }

    public void deleteClientById(int id) {
        if(isClientFound(id)){
            repository.deleteClient(id);
        }
        else{
            throw new ClientNotFoundException("Cliente com ID: " + id + " não encontrado");
        }
    }

    public boolean isClientFound(int id) {
        Client client = repository.findClientById(id);
        if (client != null) {
            return true;
        }
        throw new ClientNotFoundException("Cliente com ID: " + id + " não encontrado");
    }

    private boolean isClientDataValid(Client client) {

        if (client.getName() == null || client.getName().isBlank())
            throw new IllegalArgumentException("Nome não deve ser nulo");

        if (client.getEmail() == null || client.getEmail().isBlank())
            throw new IllegalArgumentException("Email não deve ser nulo");

        isEmailValid(client.getEmail(), client.getId());

        String name = client.getName().toLowerCase();
        if (name.contains("'") || name.contains(";") || name.contains("drop") || name.contains("--"))
            throw new IllegalArgumentException("Dados maliciosos foram encontrados");

        return true;
    }

    public void simulateNetworkCall() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException("Network interrupted");
        }
    }
    public boolean isEmailValid(String email, Integer currentId) {

        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email não deve ser nulo");

        String regex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(regex))
            throw new IllegalArgumentException("Email inválido");

        Client existing = repository.findClientByEmail(email);

        if (existing != null && !existing.getId().equals(currentId)) {
            throw new IllegalArgumentException("Email já está sendo utilizado");
        }

        return true;
    }


}
