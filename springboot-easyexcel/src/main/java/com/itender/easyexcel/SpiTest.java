package com.itender.easyexcel;

import com.itender.spi.ProcessSpiInterface;

import java.util.ServiceLoader;

/**
 * @author analytics
 * @date 2024/10/17 20:35
 * @description
 */
public class SpiTest {
    public static void main(String[] args) {
        new SpiTest().process();
    }

    public void process( ) {
        ServiceLoader<ProcessSpiInterface> searchServiceLoader = ServiceLoader.load(ProcessSpiInterface.class);
        for (ProcessSpiInterface process : searchServiceLoader) {
            String result = process.process();
            System.out.println(result);
        }
    }

}
