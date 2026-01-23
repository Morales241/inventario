package Mappers;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Clase genérica para transformar objetos.
 * @param <E> Tipo de la Entidad (Entity)
 * @param <D> Tipo del DTO
 */
public class Mapper<E, D> {

    private final Function<E, D> toDto;
    private final Function<D, E> toEntity;

    public Mapper(Function<E, D> toDto, Function<D, E> toEntity) {
        this.toDto = toDto;
        this.toEntity = toEntity;
    }

    public D mapToDto(E entity) {
        if (entity == null) return null;
        return toDto.apply(entity);
    }

    public E mapToEntity(D dto) {
        if (dto == null) return null;
        return toEntity.apply(dto);
    }

    public List<D> mapToDtoList(List<E> entities) {
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<E> mapToEntityList(List<D> dtos) {
        if (dtos == null || dtos.isEmpty()) return Collections.emptyList();
        return dtos.stream().map(this::mapToEntity).collect(Collectors.toList());
    }
}