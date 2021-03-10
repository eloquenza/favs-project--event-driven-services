package edu.hsh.favs.project.escqrs.services.commons;

import static org.javers.core.diff.ListCompareAlgorithm.LEVENSHTEIN_DISTANCE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.util.StringUtils;
import reactor.util.Logger;

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
                  Method method =
                      oldEntity
                          .getClass()
                          .getDeclaredMethod(
                              "set" + StringUtils.capitalize(valueChange.getPropertyName()),
                              valueChange.getRight().getClass());
                  method.invoke(oldEntity, valueChange.getRight());
                }
              } catch (IllegalAccessException
                  | InvocationTargetException
                  | NoSuchMethodException e) {
                throw new RuntimeException(e);
              }
            });
    return oldEntity;
  }
}
