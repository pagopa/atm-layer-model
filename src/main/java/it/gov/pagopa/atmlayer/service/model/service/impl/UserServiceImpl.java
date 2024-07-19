package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.UserMapper;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    @Override
    @WithTransaction
    public Uni<User> insertUser(UserInsertionDTO userInsertionDTO) {
        String userId = userInsertionDTO.getUserId();
        log.info("Inserting user with userId : {}", userId);
        User user = userMapper.toEntityInsertion(userInsertionDTO);
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
    @WithSession
    public Uni<User> findUser(String userId) {
        return this.userRepository.findByIdCustom(userId);
    }

    @Override
    @WithTransaction
    public Uni<User> insertUserWithProfiles(UserInsertionWithProfilesDTO userInsertionWithProfilesDTO) {
        User user = userMapper.toEntityInsertionWithProfiles(userInsertionWithProfilesDTO);
        return this.userRepository.findById(user.getUserId())
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (x != null) {
                        log.error("userId {} already exists", user.getUserId());
                        throw new AtmLayerException(Response.Status.BAD_REQUEST, AppErrorCodeEnum.USER_WITH_SAME_ID_ALREADY_EXIST);
                    }
                    return userRepository.persist(user);
                }));
    }

    @Override
    @WithTransaction
    public Uni<User> updateUser(UserInsertionDTO userInsertionDTO) {
        String userId = userInsertionDTO.getUserId();
        log.info("Updating user with userId : {}", userId);
        return this.getById(userInsertionDTO.getUserId())
                .onItem()
                .transformToUni(Unchecked.function(userFound -> {
                    if (userInsertionDTO.getName().isBlank() && userInsertionDTO.getSurname().isBlank()) {
                        throw new AtmLayerException(Response.Status.BAD_REQUEST, AppErrorCodeEnum.ALL_FIELDS_ARE_BLANK);
                    } else if (userInsertionDTO.getSurname().isBlank()) {
                        userFound.setName(userInsertionDTO.getName());
                    } else if (userInsertionDTO.getName().isBlank()) {
                        userFound.setSurname(userInsertionDTO.getSurname());
                    } else {
                        userFound.setName(userInsertionDTO.getName());
                        userFound.setSurname(userInsertionDTO.getSurname());
                    }
                    userFound.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    return userRepository.persist(userFound);
                }));
    }

    @Override
    @WithTransaction
    public Uni<Boolean> deleteUser(String userId) {
        log.info("Deleting user with userId : {}", userId);
        return this.getById(userId)
                .onItem()
                .transformToUni(x -> this.userRepository.deleteById(userId));
    }

    @Override
    @WithSession
    public Uni<PageInfo<User>> getAllUsers(int pageIndex, int pageSize, String name, String surname, String userId, int profileId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("surname", surname);
        filters.put("userId", userId);
        filters.put("profileId", profileId == 0 ? "" : profileId);
        filters.remove(null);
        filters.values().removeAll(Collections.singleton(null));
        filters.values().removeAll(Collections.singleton(""));
        return this.userRepository.findAllFiltered(filters, pageIndex, pageSize);
    }

    @Override
    public Uni<Long> countUsers() {
        return this.userRepository.count();
    }

    @Override
    @WithSession
    public Uni<User> getById(String userId) {
        return this.userRepository.findByIdCustom(userId)
                .onItem()
                .ifNull()
                .switchTo(() -> {
                    throw new AtmLayerException(Response.Status.NOT_FOUND, AppErrorCodeEnum.NO_USER_FOUND_FOR_ID);
                })
                .onItem()
                .transformToUni(Unchecked.function(x -> Uni.createFrom().item(x)));
    }

    @Override
    @WithTransaction
    public Uni<Void> checkFirstAccess(String userId) {

        return countUsers()
                .onItem()
                .transformToUni(count -> {
                    if (count == 0) {
                        UserInsertionWithProfilesDTO userInsertionWithProfilesDTO = new UserInsertionWithProfilesDTO();
                        List<Integer> profile = new ArrayList<>();
                        profile.add(5);
                        userInsertionWithProfilesDTO.setUserId(userId);
                        userInsertionWithProfilesDTO.setProfileIds(profile);
                        userInsertionWithProfilesDTO.setName("");
                        userInsertionWithProfilesDTO.setSurname("");
                        return insertUserWithProfiles(userInsertionWithProfilesDTO)
                                .onItem()
                                .transformToUni(list -> Uni.createFrom().voidItem());
                        }
                        return Uni.createFrom().voidItem();
                });
    }
}
