/**
 * Copyright (C) 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.flyway.maven.largetest;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Large Test for the Flyway Maven Plugin.
 */
public class MavenLargeTest {
    /**
     * The installation directory for the test POMs.
     */
    private String installDir = System.getProperty("installDir");

    @Test
    public void regular() throws Exception {
        String stdOut = runMaven("regular");
        assertTrue(stdOut.contains("<< Flyway Init >>"));
    }

    @Test
    public void settings() throws Exception {
        String stdOut = runMaven("settings", "-s", installDir + "/settings/settings.xml");
        assertTrue(stdOut.contains("<< Flyway Init >>"));
    }

    /**
     * Runs Maven in this directory with these extra arguments.
     *
     * @param dir The directory below src/test/resources to run maven in.
     * @param extraArgs The extra arguments (if any) for Maven.
     * @return The standard output.
     * @throws Exception When the execution failed.
     */
    public String runMaven(String dir, String... extraArgs) throws Exception {
        String m2home = System.getenv("M2_HOME");
        String flywayVersion = System.getProperty("flywayVersion");

        String extension = "";
        if (System.getProperty("os.name").startsWith("Windows")) {
            extension = ".bat";
        }

        List<String> args = new ArrayList<String>();
        args.add(m2home + "/bin/mvn" + extension);
        args.add("-Dflyway.version=" + flywayVersion);
        args.add("flyway:init");
        args.add("flyway:status");
        for (String extraArg : extraArgs) {
            args.add(extraArg);
        }

        ProcessBuilder builder = new ProcessBuilder(args);
        builder.directory(new File(installDir + "/" + dir));
        builder.redirectErrorStream(true);

        Process process = builder.start();
        String stdOut = FileCopyUtils.copyToString(new InputStreamReader(process.getInputStream(), "UTF-8"));
        int returnCode = process.waitFor();

        System.out.print(stdOut);

        assertEquals(0, returnCode);

        return stdOut;
    }

}
