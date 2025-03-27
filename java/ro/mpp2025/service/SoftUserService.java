package ro.mpp2025.service;

import ro.mpp2025.domain.SoftUser;
import ro.mpp2025.repository.SoftUserRepository;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class SoftUserService {

    private final SoftUserRepository softUserRepo;

    public SoftUserService(SoftUserRepository softUserRepo) {
        this.softUserRepo = softUserRepo;
    }

    public Optional<SoftUser> login(String username, String password) {
        return StreamSupport.stream(softUserRepo.findAll().spliterator(), false)
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst();
    }
    //login filtrare direct din repo
}
