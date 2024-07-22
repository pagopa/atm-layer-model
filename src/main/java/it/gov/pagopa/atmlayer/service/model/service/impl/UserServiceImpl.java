package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.UserMapper;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserProfilesService;
import it.gov.pagopa.atmlayer.service.model.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.*;

@ApplicationScoped
@Slf4j
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    @Inject
    UserProfilesService userProfilesService;

    @Override
    @WithTransaction
    public Uni<User> insertUser(UserInsertionDTO userInsertionDTO) {
        String userId = userInsertionDTO.getUserId();
        User user = userMapper.toEntityInsertion(userInsertionDTO);
        return this.userRepository.findById(user.getUserId())
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (x != null) {
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
                        throw new AtmLayerException(Response.Status.BAD_REQUEST, AppErrorCodeEnum.USER_WITH_SAME_ID_ALREADY_EXIST);
                    }
                    return userRepository.persist(user);
                }));
    }

    @Override
    @WithTransaction
    public Uni<User> updateUser(@NotBlank String userId, @NotBlank String name, @NotBlank String surname) {
        return this.getById(userId)
                .onItem()
                .transformToUni(Unchecked.function(userFound -> {
                        userFound.setName(name);
                        userFound.setSurname(surname);
                    return userRepository.persist(userFound);
                }));
    }

    @Override
    @WithTransaction
    public Uni<User> updateWithProfiles(UserInsertionWithProfilesDTO input) {
        return userProfilesService.updateUserProfiles(new UserProfilesInsertionDTO(input.getUserId(), input.getProfileIds()))
                .onItem()
                .transformToUni(updatedProfiles ->
                        this.updateUser(input.getUserId(), input.getName(), input.getSurname())
                );
    }

    @Override
    @WithTransaction
    public Uni<Boolean> deleteUser(String userId) {
        return this.getById(userId)
                .onItem()
                .transformToUni(x -> this.userRepository.deleteById(userId));
    }

    @Override
    @WithSession
    public Uni<List<User>> getAllUsers() {
        return this.userRepository.findAllCustom().list();
    }

    @Override
    public Uni<PageInfo<User>> getUserFiltered(int pageIndex, int pageSize, String name, String surname, String userId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("surname", surname);
        filters.put("userId", userId);
        filters.remove(null);
        filters.values().removeAll(Collections.singleton(null));
        filters.values().removeAll(Collections.singleton(""));
        return userRepository.findByFilters(filters, pageIndex, pageSize);
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
