package com.futurasmus.users_api.infrastructure.repository.spec;

import com.futurasmus.users_api.infrastructure.entity.UserEntity;
import com.futurasmus.users_api.application.dto.RequestUserFilterDto;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<UserEntity> withFilters(RequestUserFilterDto filters) {
        return Specification.allOf(
            emailContains(filters.email()),
            firstNameContains(filters.firstName()),
            lastNameContains(filters.lastName()),
            emailNotContains(filters.notEmail()),
            firstNameNotContains(filters.notFirstName()),
            lastNameNotContains(filters.notLastName()),
            isActive(filters.active()),
            isVerified(filters.verified())
        );
    }

    private static Specification<UserEntity> emailContains(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<UserEntity> emailNotContains(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.notLike(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<UserEntity> firstNameContains(String firstName) {
        return (root, query, cb) ->
                firstName == null ? null : cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    private static Specification<UserEntity> firstNameNotContains(String firstName) {
        return (root, query, cb) ->
                firstName == null ? null : cb.notLike(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    private static Specification<UserEntity> lastNameContains(String lastName) {
        return (root, query, cb) ->
                lastName == null ? null : cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    private static Specification<UserEntity> lastNameNotContains(String lastName) {
        return (root, query, cb) ->
                lastName == null ? null : cb.notLike(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    private static Specification<UserEntity> isActive(Boolean active) {
        return (root, query, cb) ->
                active == null ? null : cb.equal(root.get("active"), active);
    }

    private static Specification<UserEntity> isVerified(Boolean verified) {
        return (root, query, cb) ->
                verified == null ? null : cb.equal(root.get("verified"), verified);
    }
}
