package com.example.demo.Services;

import com.example.demo.Entities.Client;
import com.example.demo.Entities.Master;
import com.example.demo.Entities.SalonService;
import com.example.demo.Entities.Sex;
import com.example.demo.Repositories.ClientRepository;
import com.example.demo.Repositories.GenericSearch;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.DTO.ClientDTO;
import com.example.demo.Services.DTO.MasterDTO;
import com.example.demo.Services.DTO.ServiceDTO;
import com.example.demo.security.Exceptions.DataNotFound;
import com.example.demo.security.Exceptions.DeleteDataException;
import com.example.demo.security.Exceptions.SaveDataException;
import com.example.demo.security.Exceptions.UpdateDataException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired ClientRepository clientRepository;
    @Autowired UserRepository userRepository;
    @Autowired GenericSearchService<Client> genericSearchService;
    public ClientDTO getClientDTO(String login) {
        Client client = clientRepository.findByClientLogin(login).orElseThrow(() ->
                new DataNotFound("Client not found"));

        return ParseClient(client);
    }
    public Client getClient(String login) {
        return clientRepository.findByClientLogin(login).orElseThrow(() ->
                new DataNotFound("Client not found"));

    }
    public List<ClientDTO> getClients() {
        List<Client> clients =  clientRepository.findAll();
        List<ClientDTO> clientDTOS = new ArrayList<>();
        for(Client client: clients){
            ClientDTO clientDTO =ParseClient(client);

            clientDTOS.add(clientDTO);
        }
        return clientDTOS;
    }

    private ClientDTO ParseClient(Client client) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setLogin(client.getClientLogin());
        clientDTO.setName(client.getName());
        clientDTO.setSex(client.getSex().toString());
        clientDTO.setAvatarPath(client.getAvatarPath());
        clientDTO.setBirthDate(client.getBirthDate());
        clientDTO.setTelephone(client.getTelephone());
        return clientDTO;
    }

    public void update(ClientDTO clientDTO) {
        Client oldClient = clientRepository.findByClientLogin(clientDTO.getLogin())
                .orElseThrow(() -> new DataNotFound("Client not found"));
        if (clientDTO.getName().isEmpty() && clientDTO.getSex() == null &&
                clientDTO.getBirthDate() == null && clientDTO.getTelephone().isEmpty() &&
                clientDTO.getAvatarPath().isEmpty()) {
            throw new UpdateDataException("Ошибка обновления данных клиента!");
        }
        if (!clientDTO.getName().isEmpty()) oldClient.setName(clientDTO.getName());
        if (clientDTO.getSex() != null) oldClient.setSex(Sex.valueOf(clientDTO.getSex()));
        if (clientDTO.getBirthDate() != null) oldClient.setBirthDate(clientDTO.getBirthDate());
        if (!clientDTO.getTelephone().isEmpty()) oldClient.setTelephone(clientDTO.getTelephone());
        if (!clientDTO.getAvatarPath().isEmpty()) oldClient.setAvatarPath(clientDTO.getAvatarPath());
        clientRepository.save(oldClient);
    }
    public void updateAvatar(String login,@NotNull String avatarPath) {
        Client client = clientRepository.findByClientLogin(login).orElseThrow(() ->
                new DataNotFound("Client not found"));
        if(!avatarPath.isEmpty()) client.setAvatarPath(avatarPath);
        clientRepository.save(client);
    }
    public void delete(String login){
        Client client = clientRepository.findByClientLogin(login).orElseThrow(()
                -> new DataNotFound("Client not found!"));
        clientRepository.delete(client);
        userRepository.delete(userRepository.findByLogin(login));
        if(clientRepository.findByClientLogin(login).isEmpty()||userRepository.findByLogin(login)==null){
            throw new DeleteDataException("Error while deleting client!");
        }
    }
    public void save(ClientDTO clientDTO) {
        Client client = new Client();
        client.setClientLogin(clientDTO.getLogin());
        client.setName(clientDTO.getName());
        client.setSex(Sex.valueOf(clientDTO.getSex()));
        client.setAvatarPath(clientDTO.getAvatarPath());
        client.setBirthDate(clientDTO.getBirthDate());
        client.setTelephone(clientDTO.getTelephone());
        clientRepository.save(client);
        if (clientRepository.findByClientLogin(client.getClientLogin()).isEmpty()) {
            throw new SaveDataException("Error while saving client data!"); }
    }
    public List<ClientDTO> searchAndFilterClients(String query, Map<String, Object> criteria) {
        List<ClientDTO> clients = getClients();
        if(query!=null&&!query.isEmpty()) clients = genericSearchService.search(Client.class, query).stream()
                .map(this::ParseClient).toList();
        if(criteria!=null&&!criteria.isEmpty()) clients = GenericFilterUtility.filter(clients, criteria);
        return clients;
    }
}
