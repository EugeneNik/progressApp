package common.service.custom;

import common.service.base.AbstractService;

import java.util.ArrayList;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class AchievementService extends AbstractService {

    protected void customInitialization() {
        listeners = new ArrayList<>();
    }
}
