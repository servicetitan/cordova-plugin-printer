/*
    Copyright 2013-2016 appPlant GmbH

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
 */

package de.appplant.cordova.plugin.printer.ext;

import android.content.Context;
import android.os.Build;
import android.print.PrintJob;
import android.print.PrintJobId;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.appplant.cordova.plugin.printer.reflect.Meta;

public final class PrintManager {

    public interface OnPrintJobStateChangeListener {
        /**
         * Callback notifying that a print job state changed.
         *
         * @param job The print job.
         */
        void onPrintJobStateChanged(PrintJob job);
    }

    /**
     * The application context.
     */
    private WeakReference<Context> ctx;

    /**
     * The registered listener for the state change event.
     */
    private WeakReference<OnPrintJobStateChangeListener> listener;

    /**
     * The proxy wrapper of the listener.
     */
    private Object proxy;

    /**
     * Constructor
     *
     * @param context The context where to look for.
     */
    public PrintManager (Context context) {
        this.ctx = new WeakReference<Context>(context);
    }

    /**
     * Get an instance from PrintManager service.
     *
     * @return A PrintManager instance.
     */
    public final android.print.PrintManager getInstance () {
        return (android.print.PrintManager)
                ctx.get().getSystemService(Context.PRINT_SERVICE);
    }

    /**
     * Gets the list of installed print services.
     *
     * @return The found service list or an empty list.
     */
    public final List<PrintServiceInfo> getInstalledPrintServices () {
        List printers;

        if (Build.VERSION.SDK_INT < 24) {
            printers = (List) Meta.invokeMethod(getInstance(),
                    "getInstalledPrintServices");
        } else {
            Method getPrintServicesMethod = Meta.getMethod(
                    getInstance().getClass(), "getPrintServices", int.class);

            printers = (List) Meta.invokeMethod(getInstance(),
                    getPrintServicesMethod, 3);
        }

        ArrayList<PrintServiceInfo> services =
                new ArrayList<PrintServiceInfo>();

        if (printers == null)
            return Collections.emptyList();

        for (Object printer : printers) {
            services.add(new PrintServiceInfo(printer));
        }

        return services;
    }

    /**
     * Gets the list of enabled print services.
     *
     * @return The found service list or an empty list.
     */
    public final List<PrintServiceInfo> getEnabledPrintServices () {
        List printers;

        if (Build.VERSION.SDK_INT < 24) {
            printers = (List) Meta.invokeMethod(getInstance(),
                    "getEnabledPrintServices");
        } else {
            Method getPrintServicesMethod = Meta.getMethod(
                    getInstance().getClass(), "getPrintServices", int.class);

            printers = (List) Meta.invokeMethod(getInstance(),
                    getPrintServicesMethod, 1);
        }

        ArrayList<PrintServiceInfo> services =
                new ArrayList<PrintServiceInfo>();

        if (printers == null)
            return Collections.emptyList();

        for (Object printer : printers) {
            services.add(new PrintServiceInfo(printer));
        }

        return services;
    }

    /**
     * Creates an session object to discover all printer services. To do so
     * you need to register a listener object and start the discovery process.
     *
     * @return An instance of class PrinterDiscoverySession.
     */
    public final PrinterDiscoverySession createPrinterDiscoverySession () {
        Object session = Meta.invokeMethod(getInstance(),
                "createPrinterDiscoverySession");

        return new PrinterDiscoverySession(session);
    }
}