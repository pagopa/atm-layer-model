package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Override
    @WithTransaction
    public Uni<User> insertUser(User user) {
        String userId = user.getUserId();
        log.info("Inserting user with userId : {}", userId);
        return this.userRepository.findById(user.getUserId())
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (x != null) {
                        log.error("userId {} already exists", userId);
                        throw new AtmLayerException(Response.Status.BAD_REQUEST, AppErrorCodeEnum.USER_WITH_SAME_ID_ALREADY_EXIST);
                    }
                    return userRepository.persist(user);
                }));
    }

    @Override
    @WithTransaction
    public Uni<Boolean> deleteUser(String userId) {
        log.info("Deleting user with userId : {}", userId);
        return this.findById(userId)
                .onItem()
                .transformToUni(x -> this.userRepository.deleteById(userId));
    }

    @Override
    @WithSession
    public Uni<List<User>> getAllUsers() {
        return this.userRepository.findAll().list();
    }

    @Override
    @WithSession
    public Uni<User> findById(String userId) {
        return this.userRepository.findById(userId)
                .onItem()
                .ifNull()
                .switchTo(() -> {
                    throw new AtmLayerException(Response.Status.NOT_FOUND, AppErrorCodeEnum.NO_USER_FOUND_FOR_ID);
                })
                .onItem()
                .transformToUni(Unchecked.function(x -> Uni.createFrom().item(x)));
    }
}
