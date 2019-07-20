import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.*;

public class GC {

    private static final String logsDirName = "hw05-gc"+File.separator+"customLogs";
    private static long sleepPeriod = 45L;
    private static String gcName="";

    public enum GCEventType {
        YANG_GEN("Young Generation"),
        OLD_GEN("Old generation");
        private String name;

        GCEventType(String name) {
            this.name = name;
        }
    }

    public static class GcStatistic {
        long count = 0;
        long duration = 0;
    }

    private static final long startAppTime = System.currentTimeMillis();

    private static final Map<GCEventType,GcStatistic> gcStatisticMap = new HashMap<>(2);

    public static void init() {
        gcStatisticMap.put(GCEventType.YANG_GEN, new GcStatistic());
        gcStatisticMap.put(GCEventType.OLD_GEN, new GcStatistic());
        monitoring();
        File logsDir = new File(logsDirName);
        if (!logsDir.exists() || !logsDir.isDirectory())
            logsDir.mkdirs();
    }

    public static void main(String[] args) throws InterruptedException {
        long localSleepPeriod = -1;
        try {
            if (args.length > 0 && !args[0].isEmpty() && (localSleepPeriod = Long.parseLong(args[0])) >= 0 )
                GC.sleepPeriod = localSleepPeriod;
        } catch (Throwable th) {}
        System.out.println("start with sleep period : "+sleepPeriod);
        init();
        fillMemory();
    }


    public static void fillMemory() throws InterruptedException {
        long lastIterationTime = startAppTime;
        long maxMemory = Runtime.getRuntime().maxMemory();
        try(FileWriter writer = new FileWriter(logsDirName+File.separator+(gcName+"_gc-"+new Date()+".log").replaceAll(" ",""), false)) {
            LinkedList<Object> objects = new LinkedList<>();
            for (int i = 0; true; i++) {
                Date date = new Date();
                String info = (date.getTime() - startAppTime) / 1000 + ","
                        + (maxMemory-Runtime.getRuntime().freeMemory())/1024/1024+","
                        + gcStatisticMap.get(GCEventType.YANG_GEN).count + ","
                        + gcStatisticMap.get(GCEventType.YANG_GEN).duration + ","
                        + gcStatisticMap.get(GCEventType.OLD_GEN).count + ","
                        + gcStatisticMap.get(GCEventType.OLD_GEN).duration+ ","
                        + (gcStatisticMap.get(GCEventType.YANG_GEN).duration + gcStatisticMap.get(GCEventType.OLD_GEN).duration)+","
                        +i+"\n";

                if (date.getTime()-lastIterationTime>=1000) {
                    System.out.print(info);
                    writer.write(info);
                    writer.flush();
                    lastIterationTime = date.getTime();
                }
                objects.add(date);
                if (i % 2 == 0 )
                    objects.poll();
                if (i%sleepPeriod == 0) {
                    Thread.sleep(1);
                }
                //if (i%2 == 0)
                //Thread.sleep(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void monitoring() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            if (gcName.isEmpty())
                gcName = gcBean.getName();
            NotificationEmitter emitter = (NotificationEmitter) gcBean;
            NotificationListener listener = (notification, handback) -> {
                if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                    GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                    String gcName = info.getGcName();
                    String gcAction = info.getGcAction();
                    String gcCause = info.getGcCause();

                    long startTime = info.getGcInfo().getStartTime();
                    long duration = info.getGcInfo().getDuration();

                    GcStatistic statistic = getStatistic(gcAction);

                    if (statistic != null) {
                        statistic.count++;
                        statistic.duration+=duration;
                    }


                }
            };
            emitter.addNotificationListener(listener, null, null);
        }

    }

    public static GcStatistic getStatistic(String gcAction) {
        if (gcAction.contains("minor"))
            return gcStatisticMap.get(GCEventType.YANG_GEN);
        else if (gcAction.contains("major"))
            return gcStatisticMap.get(GCEventType.OLD_GEN);
        return null;
    }
}