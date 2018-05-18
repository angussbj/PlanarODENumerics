package angus.planarodenumerics;

import android.os.Build;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class AUtil {
    public static int getNumberOfCores() {
        if (Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            class CpuFilter implements FileFilter {
                @Override
                public boolean accept(File pathname) {
                    // Check if filename is "cpu" followed by a single digit number
                    if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            }

            try {
                // Get the directory that has the CPU info
                File dir = new File("/sys/devices/system/cpu");
                // Filter to only list the devices we care about
                File[] files = dir.listFiles(new CpuFilter());
                // Each file represents a core (so long as #cores <= 9), so
                return files.length;
            } catch (Exception e) {
                return 1;               // default assume 1 core
            }
        }
    }
}
