package reminder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreatService {
    private final ScheduledExecutorService scheduler;

    public ThreatService() {
        // Создаем ScheduledThreadPoolExecutor с 1 потоком
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startBackup(long chatId, long state, long period, TimeUnit timeUnit) {
        // Создаем новую задачу ReminderTask
        ReminderTask task = new ReminderTask(chatId, state);

        // Запускаем задачу каждый период времени с использованием ScheduledThreadPoolExecutor
        scheduler.scheduleAtFixedRate(task, 0, period, timeUnit);
    }

    public void stopRemind() {
        // Останавливаем выполнение задач и закрываем пул потоков исполнения
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
            // Код для резервного копирования файлов
            System.out.println("Starting remander " + chatId + " in state " + state);
        }
    }

}
