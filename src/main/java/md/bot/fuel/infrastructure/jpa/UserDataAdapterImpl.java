package md.bot.fuel.infrastructure.jpa;

import lombok.RequiredArgsConstructor;
import md.bot.fuel.domain.UserData;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class UserDataAdapterImpl implements UserDataAdapter {

    private final UserDataRepository userDataRepository;
    private final UserDataJpaMapper mapper;

    @Override
    public UserData getUserData(long userId) {
        final UserDataJpa userDataJpa = getOrCreateUser(userId);
        return mapper.toEntity(userDataJpa);
    }

    @Override
    public void save(UserData userData) {
        final UserDataJpa userDataJpa = userDataRepository.findById(userData.getId());

        if (isNull(userDataJpa)) {
            final UserDataJpa newUser = mapper.toJpa(userData);
            userDataRepository.save(newUser);
        } else {
            final UserDataJpa updatedUser = mapper.update(userDataJpa, userData);
            userDataRepository.save(updatedUser);
        }
    }

    private UserDataJpa getOrCreateUser(long userId) {
        return isNull(userDataRepository.findById(userId)) ? userDataRepository.save(mapper.toJpa(userId)) :
                userDataRepository.findById(userId);
    }
}
