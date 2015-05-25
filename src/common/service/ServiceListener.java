package common.service;

/**
 * Created by nikiforov on 25.05.2015.
 */
public interface ServiceListener {

    void onStart();

    void updateProgress(double progress);

    double getProgress();

    void onFinish();
}
