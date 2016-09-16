package com.github.rlf.littlebits.async.generic;

import com.github.rlf.littlebits.async.Scheduler;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class SchedulerExecutorServiceTest {
    @Test
    public void scheduler0() throws Exception {
        final Scheduler scheduler = new SchedulerExecutorService(0);
        final List<String> actual = new ArrayList<>();
        scheduler.async(new Runnable() {
            @Override
            public void run() {
                actual.add("async(" + scheduler.isSync() + ")");
            }
        });
        scheduler.sync(new Runnable() {
            @Override
            public void run() {
                actual.add("sync(" + scheduler.isSync() + ")");
            }
        });
        while (actual.size() < 2) {
            Thread.sleep(100);
        }
        assertThat(actual, is(Arrays.asList("async(true)", "sync(true)")));
    }

    @Test
    public void scheduler1() throws Exception {
        final Scheduler scheduler = new SchedulerExecutorService(1);
        final List<String> actual = new ArrayList<>();
        scheduler.async(new Runnable() {
            @Override
            public void run() {
                actual.add("async(" + scheduler.isSync() + ")");
            }
        });
        scheduler.sync(new Runnable() {
            @Override
            public void run() {
                actual.add("sync(" + scheduler.isSync() + ")");
            }
        });
        while (actual.size() < 2) {
            Thread.sleep(100);
        }
        assertThat(actual, is(Arrays.asList("async(false)", "sync(true)")));
    }

}