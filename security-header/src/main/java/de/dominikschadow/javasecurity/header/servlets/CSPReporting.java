/*
 * Copyright (C) 2015 Dominik Schadow, dominikschadow@gmail.com
 *
 * This file is part of the Java Security project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dominikschadow.javasecurity.header.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Simple CSP-Reporting servlet to receive and print out any JSON style CSP report with violations.
 *
 * @author Dominik Schadow
 */
@WebServlet(name = "CSPReporting", urlPatterns = {"/csp/CSPReporting"})
public class CSPReporting extends HttpServlet {
    private static final long serialVersionUID = 5150026442855960085L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CSPReporting.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            StringBuilder responseBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = reader.readLine()) != null) {
                responseBuilder.append(inputStr);
            }

            Gson gs = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            JsonParser parser = new JsonParser();
            JsonElement je = parser.parse(responseBuilder.toString());
            LOGGER.info("\n{}", gs.toJson(je));
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
