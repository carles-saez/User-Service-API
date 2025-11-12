package com.futurasmus.users_api.infrastructure.repository.spec;

import com.futurasmus.users_api.infrastructure.entity.UserEntity;
import com.futurasmus.users_api.application.dto.RequestUserFilterDto;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<UserEntity> withFilters(RequestUserFilterDto filters) {
        return Specification.allOf(
            emailContains(filters.email()),
            emailNotContains(filters.notEmail()),
            firstNameContains(filters.firstName()),
            firstNameNotContains(filters.notFirstName()),
            lastNameContains(filters.lastName()),
            lastNameNotContains(filters.notLastName()),
            createdAtBefore(filters.createdBefore()),
            createdAtAfter(filters.createdAfter()),
            updatedAtBefore(filters.updatedBefore()),
            updatedAtAfter(filters.updatedAfter()),
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

    private static Specification<UserEntity> createdAtBefore(LocalDateTime createdAt) {
        return (root, query, cb) ->
                createdAt == null ? null : cb.lessThan(root.get("createdAt"), createdAt);
    }

    private static Specification<UserEntity> createdAtAfter(LocalDateTime createdAt) {
        return (root, query, cb) ->
                createdAt == null ? null : cb.greaterThan(root.get("createdAt"), createdAt);
    }

    private static Specification<UserEntity> updatedAtBefore(LocalDateTime updatedAt) {
        return (root, query, cb) ->
                updatedAt == null ? null : cb.lessThan(root.get("updatedAt"), updatedAt);
    }

    private static Specification<UserEntity> updatedAtAfter(LocalDateTime updatedAt) {
        return (root, query, cb) ->
                updatedAt == null ? null : cb.greaterThan(root.get("updatedAt"), updatedAt);
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
