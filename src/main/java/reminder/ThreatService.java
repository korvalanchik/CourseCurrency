package reminder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreatService {
    private final ScheduledExecutorService scheduler;

    public ThreatService() {
        // ������� ScheduledThreadPoolExecutor � 1 �������
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startBackup(long chatId, long state, long period, TimeUnit timeUnit) {
        // ������� ����� ������ ReminderTask
        ReminderTask task = new ReminderTask(chatId, state);

        // ��������� ������ ������ ������ ������� � �������������� ScheduledThreadPoolExecutor
        scheduler.scheduleAtFixedRate(task, 0, period, timeUnit);
    }

    public void stopRemind() {
        // ������������� ���������� ����� � ��������� ��� ������� ����������
        scheduler.shutdown();
    }

    private static class ReminderTask implements Runnable {

        private final long chatId;
        private final long state;
        public ReminderTask(long chatId, long state) {
            this.chatId = chatId;
            this.state = state;
        }


        @Override
        public void run() {
            // ��� ��� ���������� ����������� ������
            System.out.println("Starting remander " + chatId + " in state " + state);
        }
    }

}
