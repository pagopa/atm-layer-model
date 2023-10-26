package it.gov.pagopa.atmlayer.service.model.utils;

import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;

import java.util.List;
import java.util.Set;

@Singleton
public class ConstraintViolationMappingUtilsImpl implements ConstraintViolationMappingUtils {

    public List<String> extractErrorMessages(Set<ConstraintViolation<?>> errors) {
        return errors.stream().map(this::extractErrorMessage).toList();
    }

    public String extractErrorMessage(ConstraintViolation<?> error) {
        PathImpl errorNodes = (PathImpl) error.getPropertyPath();
        NodeImpl leafNode = errorNodes.getLeafNode();
        NodeImpl field = leafNode.isInIterable() ? leafNode.getParent() : leafNode;
        return field.asString().concat(" ").concat(error.getMessage());

    }
}
