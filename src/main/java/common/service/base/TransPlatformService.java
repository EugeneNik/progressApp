package common.service.base;

import data.Task;
import jaxb.HistoriesJAXB;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikiforov on 25.05.2015.
 */
public class TransPlatformService {
    private Map<Class<? extends Service>, Service> serviceMap;
    private Task root;
    private HistoriesJAXB history;

    private static final TransPlatformService self = new TransPlatformService();

    public TransPlatformService() {
        this.serviceMap = new HashMap<>();
    }

    public static TransPlatformService getInstance() {
        return self;
    }

    public <T extends Service> T  getService(Class<T> service) {
        return (T) serviceMap.get(service);
    }

    public void initService(Service service) {
        serviceMap.put(service.getClass(),service);
    }

    public Task getRoot() {
        return root;
    }

    public void setRoot(Task root) {
        this.root = root;
    }

    public HistoriesJAXB getHistory() {
        return history;
    }

    public void setHistory(HistoriesJAXB history) {
        this.history = history;
    }
}
