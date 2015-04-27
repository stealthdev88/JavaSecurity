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
package de.dominikschadow.javasecurity.services;

import de.dominikschadow.javasecurity.domain.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author Dominik Schadow
 */
@Service
public class ContactService {
    private JdbcTemplate jdbcTemplate;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostAuthorize("returnObject.username == principal.username")
    public Contact getContact(int contactId) {
        return jdbcTemplate.queryForObject("SELECT * FROM contacts WHERE contact_id = ?",
                new Object[]{contactId},
                (rs, rowNum) -> {
                    Contact contact = new Contact();
                    contact.setContactId(rs.getInt("contact_id"));
                    contact.setUsername(rs.getString("username"));
                    contact.setFirstname(rs.getString("firstname"));
                    contact.setLastname(rs.getString("lastname"));
                    contact.setComment(rs.getString("comment"));
                    return contact;
                });
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("filterObject.username == principal.username")
    public List<Contact> getContacts() {
        return jdbcTemplate.query("SELECT * FROM contacts",
                (rs, rowNum) -> {
                    Contact contact = new Contact();
                    contact.setContactId(rs.getInt("contact_id"));
                    contact.setUsername(rs.getString("username"));
                    contact.setFirstname(rs.getString("firstname"));
                    contact.setLastname(rs.getString("lastname"));
                    contact.setComment(rs.getString("comment"));
                    return contact;
                });
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
