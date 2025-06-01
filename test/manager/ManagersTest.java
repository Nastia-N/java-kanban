package manager;

import org.junit.jupiter.api.Test;
import srs.manager.Managers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    void managersShouldReturnInitializedInstances() {
        assertNotNull(Managers.getDefault(), "Менеджер задач не должен быть null");
        assertNotNull(Managers.getDefaultHistory(), "Менеджер истории не должен быть null");
    }
}
