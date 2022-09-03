package com.bugsby.datalayer.validator;

import com.bugsby.datalayer.model.Entity;

import java.io.Serializable;

public interface Validator<ID extends Serializable, E extends Entity<ID>> {

    /**
     * Validates a given entity
     *
     * @param entity: E, entity to be validated
     * @throws ValidationException if entity is not valid
     */
    void validate(E entity);
}
