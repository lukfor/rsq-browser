package genepi.r2browser.tasks;

import com.google.gson.Gson;
import genepi.io.FileUtil;
import genepi.r2browser.App;
import genepi.r2browser.model.Report;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private static final Log log = LogFactory.getLog(Scheduler.class);

    private final ScheduledExecutorService scheduler;

    public Scheduler() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startScheduler(int intervalInHours) {
        if (intervalInHours <= 0) {
            throw new IllegalArgumentException("Interval must be greater than 0");
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                cleanUp();
            } catch (Exception e) {
                log.error("Error during cleanup.", e);
            }
        }, 0, intervalInHours, TimeUnit.HOURS);
    }


    private void cleanUp() throws FileNotFoundException {
        log.info("Cleaning up resources at: " + java.time.LocalDateTime.now());

        String workspace = App.getDefault().getConfiguration().getWorkspace();

        String[] jobFiles = FileUtil.getFiles(workspace, "*.json");

        int expired = 0;
        int notExpired = 0;
        int totalJobs = 0;

        Gson gson = new Gson();

        for (String jobFile : jobFiles) {
            Report job = gson.fromJson(new FileReader(jobFile), Report.class);
            Date now = new Date();
            if (now.after(job.getExpiresOn())) {
                File jobPath = new File(FileUtil.path(workspace, job.getId()));
                if (jobPath.exists()) {
                    log.info("Job " + job.getId() + " expired.");
                    FileUtil.deleteDirectory(FileUtil.path(workspace, job.getId()));
                    expired++;
                }
            } else {
                notExpired++;
            }

            totalJobs++;

        }

        log.info("Deleted " + expired + " expired jobs. Unexpired jobs: " + notExpired);

    }

    public void stopScheduler() {
        try {
            log.info("Stopping scheduler...");
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                log.info("Forcing shutdown...");
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.info("Scheduler termination interrupted: " + e.getMessage());
            scheduler.shutdownNow();
        }
    }

}
