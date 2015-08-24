package common.service.custom;

import common.property.BaseProperty;
import common.property.PropertyManager;
import common.service.base.AbstractService;

import java.util.ArrayList;

/**
 * Created by nikiforov on 24.08.2015.
 */
public class SettingsService extends AbstractService {

    @Override
    protected void customInitialization() {
        listeners = new ArrayList<>();
        PropertyManager.getApplicationSettings();
    }

    public void save() {
        PropertyManager.save();
    }

    public <T> void save(BaseProperty<T> property, T value) {
        PropertyManager.setValue(property.getName(), value);
    }
}
