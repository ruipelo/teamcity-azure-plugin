/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.clouds.azure;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.SystemInfo;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergey.Pak
 *         Date: 4/23/2014
 *         Time: 6:40 PM
 */
public class AzurePropertiesReader {

    private static final Logger LOG = Logger.getInstance(AzurePropertiesReader.class.getName());
    private final UnixConfigReader myUnixConfigReader;
    private final WindowsConfigReader myWindowsConfigReader;

    public AzurePropertiesReader(@NotNull final EventDispatcher<AgentLifeCycleListener> events,
                                 @NotNull final UnixConfigReader unixConfigReader,
                                 @NotNull final WindowsConfigReader windowsConfigReader) {
        myUnixConfigReader = unixConfigReader;
        myWindowsConfigReader = windowsConfigReader;

        LOG.info("Azure plugin initializing...");

        events.addListener(new AgentLifeCycleAdapter() {
            @Override
            public void afterAgentConfigurationLoaded(@NotNull final BuildAgent agent) {
                if (SystemInfo.isUnix) {
                    myUnixConfigReader.process();
                } else if (SystemInfo.isWindows) {
                    myWindowsConfigReader.process();
                } else {
                    LOG.warn(String.format("Azure integration is disabled: unsupported OS family %s(%s)", SystemInfo.OS_ARCH, SystemInfo.OS_VERSION));
                }
            }
        });
    }
}
