package proj.peer.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SchedulerPrinter implements Runnable{

    private ScheduledThreadPoolExecutor executor;

    public SchedulerPrinter(ScheduledThreadPoolExecutor executor) {

        this.executor = executor;
    }
    @Override
    public void run() {
        System.out.println(this.executor.getQueue().size());
    }
}
