package dev.valente.user_service.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.RecordComponent;

public class SingleFieldNotNullValidator implements ConstraintValidator<SingleFieldNotNull, Object> {
    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Objeto nulo é válido
        }

        // Obtém os componentes do record
        RecordComponent[] components = dto.getClass().getRecordComponents();

        if (components == null) {
            return false; // Não é um record ou não há campos
        }

        boolean idIsSet = false;
        int nonEmptyFieldCount = 0;

        for (RecordComponent component : components) {
            try {
                Object value = component.getAccessor().invoke(dto); // Obtém o valor do campo

                if (component.getName().equals("id")) {
                    idIsSet = value != null; // O ID pode estar preenchido
                } else if (isNotEmpty(value)) {
                    nonEmptyFieldCount++;
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro ao acessar o campo: " + component.getName(), e);
            }
        }

        // O ID pode estar preenchido e apenas mais um campo não vazio/nulo
        return idIsSet && nonEmptyFieldCount == 1;
    }

    private boolean isNotEmpty(Object value) {
        if (value == null) {
            return false; // Valor nulo não é considerado preenchido
        }
        if (value instanceof String str) {
            return !str.trim().isEmpty(); // Strings vazias ou em branco são consideradas não preenchidas
        }
        return true; // Para outros tipos, considerar como preenchido se não for nulo
    }
}
