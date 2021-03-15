package edu.hsh.favs.project.escqrs.services.commons.transactions;

import static org.javers.core.diff.ListCompareAlgorithm.LEVENSHTEIN_DISTANCE;

import java.lang.reflect.Field;
import java.util.Optional;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import reactor.util.Logger;

/** TODO */
public class EntityUpdater<EntityT> {

  private final Logger log;
  private final Javers javers;

  public EntityUpdater(Logger log) {
    this.log = log;
    this.javers = JaversBuilder.javers().withListCompareAlgorithm(LEVENSHTEIN_DISTANCE).build();
  }

  public EntityT update(EntityT oldEntity, EntityT newEntity) throws RuntimeException {
    Diff diff = javers.compare(oldEntity, newEntity);
    this.log.info(String.format("Logging difference between objects: %s", diff));
    diff.getChangesByType(ValueChange.class)
        .forEach(
            valueChange -> {
              try {
                Optional<?> opt = Optional.ofNullable(valueChange.getRight());
                if (opt.isPresent()) {
                  Field field =
                      oldEntity.getClass().getDeclaredField(valueChange.getPropertyName());
                  field.setAccessible(true);
                  if (field.getType().isEnum()) {
                    field.set(
                        oldEntity,
                        Enum.valueOf(
                            (Class<Enum>) field.getType(), valueChange.getRight().toString()));
                  } else {
                    field.set(oldEntity, valueChange.getRight());
                  }
                }
              } catch (IllegalAccessException
                  | IllegalArgumentException
                  | NoSuchFieldException
                  | NullPointerException e) {
                throw new RuntimeException(e);
              }
            });
    return oldEntity;
  }
}
