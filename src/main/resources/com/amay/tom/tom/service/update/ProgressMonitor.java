package com.amay.tom.service.update;

import com.jcraft.jsch.SftpProgressMonitor;

import java.util.function.BiConsumer;

public class ProgressMonitor implements SftpProgressMonitor {
    private final BiConsumer<Double, String> uiUpdater;
    private long totalBytes = 0, transferred = 0, lastPercent = 0;

    public ProgressMonitor(BiConsumer<Double, String> uiUpdater) {
        this.uiUpdater = uiUpdater;
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        this.totalBytes = max;
        uiUpdater.accept(0.0, "Starting " + src);
    }

    @Override
    public boolean count(long count) {
        transferred += count;
        if (totalBytes > 0) {
            long percent = transferred * 100 / totalBytes;
            if (percent != lastPercent) {
                lastPercent = percent;
                uiUpdater.accept(percent / 100.0, "Software Update👩‍🚀⬇️ : "+ percent + " %");
            }
        }
        return true;
    }

    @Override
    public void end() {
        uiUpdater.accept(1.0, "Completed " + transferred + "/" + totalBytes);
    }
}
